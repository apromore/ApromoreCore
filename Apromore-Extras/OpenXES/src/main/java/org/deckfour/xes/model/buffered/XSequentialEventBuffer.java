/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.model.buffered;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.nikefs2.NikeFS2RandomAccessStorage;
import org.deckfour.xes.nikefs2.NikeFS2StorageProvider;

/**
 * This class provides a random-access interface to a sequential set of events.
 * These events are buffered not in heap space, but in a binary buffer file,
 * whose encoding is implemented in this class as well.
 * <p>
 * The structure of a buffer file is a sequence of binary records, one for each
 * events, where records are encoded as follows:
 * <ul>
 * <li>Offset in bytes to next record / size of record (4-byte integer)</li>
 * <li>Offset in bytes to previous record (4-byte integer)</li>
 * <li>ID of the event (serialization provided by XID class)</li>
 * </ul>
 * This fixed part is followed by the attribute map serialization for this
 * event. Note that attribute maps may be recursive, i.e. attributes in an
 * attribute map each have their own attribute map with meta-events. The depth
 * of this recursion is not limited. An attribute map is serialized as follows:
 * <ul>
 * <li>Number of attributes (as 4-byte integer)</li>
 * <li>A corresponding number of attribute serializations</li>
 * </ul>
 * Attribute serializations are composed as follows:
 * <ul>
 * <li>Attribute key (UTF-8 encoded String)</li>
 * <li>Attribute value (UTF-8 encoded String)</li>
 * <li>Attribute type (UTF-8 encoded String)</li>
 * <li>Attribute extension URI (UTF-8 encoded String)</li>
 * <li>Serialization of the attribute's attribute map (meta-attributes)</li>
 * </ul>
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XSequentialEventBuffer implements Cloneable {

	/**
	 * Encoding for non-existent extension (generic attributes)
	 */
	protected static final int EXTENSION_GENERIC = -1;

	/**
	 * The number of events contained in a buffer.
	 */
	private int size = 0;
	/**
	 * The current logical index (in number of events) in the buffer.
	 */
	private int index = 0;
	/**
	 * The current actual position in the backing buffer storage, in bytes
	 * offset from the beginning of the storage.
	 */
	private long position = 0;
	/**
	 * The position at which the last event entry was inserted into the backing
	 * buffer storage. Initialized with -1.
	 */
	private long lastInsertPosition = -1;
	/**
	 * The random access storage to back the buffer of events.
	 */
	private NikeFS2RandomAccessStorage storage = null;
	/**
	 * Storage provider which is used to allocate new buffer storages.
	 */
	private NikeFS2StorageProvider provider = null;
	/**
	 * Attribute map serializer.
	 */
	private XAttributeMapSerializer attributeMapSerializer = null;
	/**
	 * Factory for model elements.
	 */
	private XFactory factory = null;

	/**
	 * Creates and initializes a new instance of this class.
	 * 
	 * @param aProvider
	 *            Storage provider used for backing this buffer.
	 * @param attributeMapSerializer
	 *            Attribute map serializer to be used.
	 * @throws IOException
	 */
	public XSequentialEventBuffer(NikeFS2StorageProvider aProvider,
			XAttributeMapSerializer attributeMapSerializer) throws IOException {
		this.provider = aProvider;
		this.attributeMapSerializer = attributeMapSerializer;
		this.size = 0;
		this.index = 0;
		this.position = 0;
		this.lastInsertPosition = -1;
		this.storage = provider.createStorage();
		this.factory = XFactoryRegistry.instance().currentDefault();
	}

	protected XSequentialEventBuffer() {
		// implicit constructor reserved for derived classes
	}

	/**
	 * Returns the storage provider used by this instance
	 */
	public NikeFS2StorageProvider getProvider() {
		return provider;
	}

	/**
	 * Returns the current position of this instance
	 */
	public long position() {
		return position;
	}

	/**
	 * Returns the last insert position of this instance
	 */
	public long lastInsert() {
		return lastInsertPosition;
	}

	/**
	 * Returns the random access storage this instance is based on
	 */
	public NikeFS2RandomAccessStorage getStorage() {
		return storage;
	}

	/**
	 * Retrieves the number of events recorded in this instance.
	 * 
	 * @return number of audit trail entries recorded in this instance
	 */
	public synchronized int size() {
		return size;
	}

	/**
	 * Retrieves the current internal, logical position of this collection.
	 */
	public synchronized int index() {
		return index;
	}

	/**
	 * Appends a new event to the end of this collection.
	 * <p>
	 * Notice that a call to this method does not affect the current position
	 * events are read from.
	 * 
	 * @param ate
	 *            The event to append
	 */
	public synchronized void append(XEvent event) throws IOException {
		// remember insert position
		long insertPosition = storage.length();
		// position storage pointer at end of storage
		storage.seek(insertPosition);
		// encode event to byte array
		byte evtEnc[] = encode(event);
		// compute segment length: add some slack to accomodate for later,
		// larger versions of this entry
		int segmentPaddingSize = evtEnc.length / 4;
		int segmentSize = evtEnc.length + segmentPaddingSize;
		byte segmentPadding[] = new byte[segmentPaddingSize];
		Arrays.fill(segmentPadding, (byte) 0);
		// record offset to subsequent audit trail entry for forward
		// skips; as size of event encoding + 12 bytes (for forward and
		// backward offset marker and payload size)
		storage.writeInt(segmentSize + 12);
		// record offset to previous event (for backward skips)
		storage.writeInt((int) (insertPosition - lastInsertPosition));
		// record actual payload size
		storage.writeInt(evtEnc.length);
		// record event encoding data
		storage.write(evtEnc);
		// record padding data
		storage.write(segmentPadding);
		// update last position pointer to this entry
		lastInsertPosition = insertPosition;
		// update collection size
		size++;
	}

	/**
	 * Replaces an event at the given position.
	 * 
	 * @param event
	 *            The new event to be inserted.
	 * @param index
	 *            Index to replace at.
	 * @return The former event, having been replaced.
	 */
	public synchronized boolean replace(XEvent event, int index)
			throws IOException {
		// check for index sanity
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}
		// determine and set appropriate file pointer position
		navigateToIndex(index);
		storage.seek(position);
		long atePosition = position;
		// read navigation data
		int fwd = storage.readInt();
		// skip backwards pointer and payload size, not relevant
		storage.skipBytes(8);
		int segmentSize = fwd - 12;
		// encode event
		byte[] evtEnc = encode(event);
		boolean success = false;
		if (evtEnc.length <= segmentSize) {
			// overwrite event
			storage.seek(atePosition + 8);
			storage.writeInt(evtEnc.length);
			// insert new padding
			byte segmentPadding[] = new byte[segmentSize - evtEnc.length];
			Arrays.fill(segmentPadding, (byte) 0);
			storage.write(evtEnc);
			storage.write(segmentPadding);
			success = true;
		} else {
			success = false;
		}
		// return to prior position
		this.position = atePosition;
		storage.seek(this.position);
		return success;
	}

	/**
	 * Retrieves the event recorded at the specified position
	 * 
	 * @param eventIndex
	 *            Position of the requested event, defined to be within
	 *            <code>[0, size()-1]</code>.
	 * @return The requested event.
	 */
	public synchronized XEvent get(int eventIndex) throws IOException,
			IndexOutOfBoundsException {
		// check for index sanity
		if (eventIndex < 0 || eventIndex >= size) {
			throw new IndexOutOfBoundsException();
		}
		// determine and set appropriate file pointer position
		navigateToIndex(eventIndex);
		// read and return requested audit trail entry
		return read();
	}

	/**
	 * Cleans up any non-volatile resources (e.g. temporary files) associated
	 * with this instance and resets the instance to an initial state.
	 */
	public synchronized void cleanup() throws IOException {
		// close and delete the underlying storage
		storage.close();
		size = 0;
		index = 0;
		position = 0;
		lastInsertPosition = -1;
	}

	/**
	 * Repositions the low-level layer to read from the specified index.
	 * 
	 * @param reqIndex
	 *            Index to position the file pointer to.
	 */
	protected synchronized void navigateToIndex(int reqIndex)
			throws IOException {
		// determine if navigation is necessary
		if (reqIndex != index) {
			// ensure that the requested index is valid
			if (reqIndex < 0 || reqIndex >= size) {
				throw new IndexOutOfBoundsException();
			}
			// navigate to requested index in file
			if (reqIndex > index) {
				// forward navigation
				skipForward(reqIndex - index);
			} else {
				// backward navigation
				int backSkips = index - reqIndex;
				if (backSkips < (index / 2)) {
					// check if current index is beyond valid list
					if (index == size) {
						// reset current position to last element in
						// set and adjust index and skip counter.
						index = (size - 1);
						position = lastInsertPosition;
						backSkips = index - reqIndex;
					}
					// move in backward direction
					skipBackward(backSkips);
				} else {
					// it is faster to reset position to the beginning
					// of the file and move forward from there to the
					// requested index
					resetPosition();
					skipForward(reqIndex);
				}
			}
		}
		if (reqIndex != index) {
			throw new IOException("Navigation fault! (required: " + reqIndex
					+ ", yielded: " + index + ")");
		}
	}

	/**
	 * Resets the position of the data access layer to read the next event from
	 * the first position.
	 */
	protected synchronized void resetPosition() {
		index = 0;
		position = 0;
	}

	/**
	 * Repositions the position of the data access layer to skip the specified
	 * number of records towards the end of the file.
	 * 
	 * @param eventsToSkip
	 *            Number of records to be skipped.
	 */
	protected synchronized void skipForward(int eventsToSkip)
			throws IOException {
		int offset = 0;
		for (int i = 0; i < eventsToSkip; i++) {
			// adjust position for reading offset
			storage.seek(position);
			// read forward skip offset
			offset = storage.readInt();
			// set file pointer to next event position
			position += offset;
			// adjust index
			index++;
		}
	}

	/**
	 * Repositions the position of the data access layer to skip the specified
	 * number of records towards the beginning of the file.
	 * 
	 * @param eventsToSkip
	 *            Number of records to be skipped.
	 */
	protected synchronized void skipBackward(int eventsToSkip)
			throws IOException {
		int offset = 0;
		for (int i = 0; i < eventsToSkip; i++) {
			// position file pointer at current backward offset marker
			storage.seek(position + 4);
			// read backward offset to previous event
			offset = storage.readInt();
			// adjust file pointer position
			position -= offset;
			// adjust index
			index--;
		}
	}

	/**
	 * Reads an event from the current position of the data access layer.
	 * Calling this method implies the advancement of the data access layer, so
	 * that the next call will yield the subsequent event.
	 */
	protected synchronized XEvent read() throws IOException {
		// reset file pointer position
		storage.seek(position);
		// compute next position from forward offset
		long nextPosition = position + storage.readInt();
		// skip backward offset (4 bytes)
		storage.skipBytes(4);
		// read payload size
		int eventSize = storage.readInt();
		// buffered implementation: reads the byte array representing the
		// event and interprets it from that buffer subsequently.
		byte[] eventData = new byte[eventSize];
		storage.readFully(eventData);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
				eventData));
		// read event data attributes in specified order from file
		XID id = XID.read(dis);
		// read event attribute set
		XAttributeMap attributes = this.attributeMapSerializer.deserialize(dis);
		// assemble event
		XEvent event = factory.createEvent(id, attributes);
		// adjust position of data access layer
		position = nextPosition;
		index++;
		return event;
	}

	/**
	 * Encodes the given event into a sequence of bytes. This byte array
	 * corresponds to the structure of an event record, as specified in the
	 * beginning of this document, excluding the back-/forward offsets used for
	 * navigation.
	 * 
	 * @param ate
	 *            The event to be encoded.
	 * @return byte Array representing the event without navigation offsets.
	 */
	protected byte[] encode(XEvent event) throws IOException {
		// prepare output stream for encoding
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		// write event id to output stream
		XID.write(event.getID(), dos);
		// encode attributes
		this.attributeMapSerializer.serialize(event.getAttributes(), dos);
		// flush and serialize output stream result
		dos.flush();
		return baos.toByteArray();
	}

	/**
	 * Creates an identical clone of this buffer.
	 */
	public Object clone() {
		XSequentialEventBuffer clone = null;
		// class-exclusive access
		synchronized (XSequentialEventBuffer.class) {
			try {
				clone = (XSequentialEventBuffer) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
			try {
				clone.storage = storage.copy();
			} catch (IOException e) {
				e.printStackTrace();
				clone.storage = null;
			}
		}
		return clone;
	}

	/**
	 * Remove buffer file when this instance is garbage collected.
	 */
	protected void finalize() throws Throwable {
		// clean buffer file from disk
		cleanup();
	}

}
