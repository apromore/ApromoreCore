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

import java.io.IOException;
import java.util.BitSet;
import java.util.Date;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;

/**
 * Implements a fast list of events stored in disk buffers, by using the means
 * of the NikeFS2 virtual file system for event logs. Frees main memory for
 * other tasks, while guaranteeing quick sequential and random access to event
 * log data.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XFastEventList implements Cloneable {

	/**
	 * Maximal number of buffered edit operations on the list, before it is
	 * consolidated to the disk buffer.
	 */
	public static int OVERFLOW_LIMIT = 100;

	/**
	 * Size of the list.
	 */
	protected int size = 0;
	/**
	 * Sequential event buffer used for raw buffered storage.
	 */
	protected XSequentialEventBuffer buffer;
	/**
	 * Attribute map serializer.
	 */
	protected XAttributeMapSerializer attributeMapSerializer;
	/**
	 * Indicates the positions in the list which are no longer valid, i.e. need
	 * to be skipped.
	 */
	protected BitSet holeFlags;
	/**
	 * Array of list indices for additional overflow entries.
	 */
	protected int[] overflowIndices;
	/**
	 * Array of additional overflow entries.
	 */
	protected XEvent[] overflowEntries;
	/**
	 * Current, actual size of the overflow data structures.
	 */
	protected int overflowSize;

	/**
	 * Creates a new fast event list.
	 * 
	 * @param attributeMapSerializer
	 *            The attribute map serializer to be used.
	 */
	public XFastEventList(XAttributeMapSerializer attributeMapSerializer)
			throws IOException {
		this.size = 0;
		this.attributeMapSerializer = attributeMapSerializer;
		this.buffer = new XSequentialEventBuffer(NikeFS2VirtualFileSystem
				.instance(), attributeMapSerializer);
		this.holeFlags = new BitSet();
		this.overflowIndices = new int[OVERFLOW_LIMIT];
		this.overflowEntries = new XEvent[OVERFLOW_LIMIT];
		this.overflowSize = 0;
	}

	/**
	 * Appends the given event to the end of this fast event list.
	 * 
	 * @param event
	 *            Event to be added.
	 * @return Index of the added event.
	 */
	public synchronized int append(XEvent event) throws IOException {
		buffer.append(event);
		size++;
		return size - 1;
	}

	/**
	 * Cleans up this fast event list after use, frees all associated resources.
	 */
	public synchronized void cleanup() throws IOException {
		buffer.cleanup();
		this.holeFlags = null;
	}

	/**
	 * Consolidates this fast event list. Consolidation implies, that all
	 * overflow and skipping data structures are freed, and the buffered
	 * representation is brought completely in-line with the virtual current
	 * contents of the list.
	 * 
	 * The actual consolidation will be skipped, if no need for it is detected
	 * by the algorithm.
	 * 
	 * @return Whether consolidation has been performed.
	 */
	public synchronized boolean consolidate() throws IOException {
		if (isTainted()) {
			// proceed with consolidation
			XSequentialEventBuffer nBuffer = new XSequentialEventBuffer(buffer
					.getProvider(), this.attributeMapSerializer);
			int overflowIndex = 0;
			int fileBufferIndex = 0;
			for (int i = 0; i < size; i++) {
				if (overflowIndex < overflowSize
						&& overflowIndices[overflowIndex] == i) {
					nBuffer.append(overflowEntries[overflowIndex]);
					overflowIndex++;
				} else {
					while (holeFlags.get(fileBufferIndex) == true) {
						fileBufferIndex++;
					}
					nBuffer.append(buffer.get(fileBufferIndex));
					fileBufferIndex++;
				}
			}
			buffer.cleanup();
			buffer = nBuffer;
			overflowSize = 0;
			holeFlags.clear();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Retrieves an event at a specific index in the list.
	 * 
	 * @param index
	 *            Index of the required event in the list.
	 * @return The requested event.
	 */
	public synchronized XEvent get(int index) throws IndexOutOfBoundsException,
			IOException {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}
		int bufferIndex = index;
		// correct buffer index from overflow
		for (int i = 0; i < overflowSize; i++) {
			if (overflowIndices[i] == index) {
				return overflowEntries[i];
			} else if (overflowIndices[i] < index) {
				bufferIndex--;
			} else {
				break;
			}
		}
		// determine deleted offset
		// step over flagged indices and adjust buffer index upwards
		// respectively
		for (int hole = holeFlags.nextSetBit(0); hole >= 0
				&& hole <= bufferIndex; hole = holeFlags.nextSetBit(hole + 1)) {
			bufferIndex++;
		}
		// buffer index should now point to the corresponding index
		// within the file buffer, so return it
		return buffer.get(bufferIndex);
	}

	/**
	 * Inserts an event at a given index into the list.
	 * 
	 * @param event
	 *            The event to be inserted.
	 * @param index
	 *            Requested index of the inserted event.
	 */
	public synchronized void insert(XEvent event, int index)
			throws IndexOutOfBoundsException, IOException {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException();
		}
		// check if we can append
		if (index == size) {
			append(event);
			return;
		}
		// adjust size and overflow size
		size++;
		overflowSize++;
		// add to overflow set
		for (int i = overflowSize - 2; i >= 0; i--) {
			if (overflowIndices[i] >= index) {
				overflowIndices[i + 1] = overflowIndices[i] + 1;
				overflowEntries[i + 1] = overflowEntries[i];
			} else {
				overflowIndices[i + 1] = index;
				overflowEntries[i + 1] = event;
				if (overflowSize == overflowIndices.length) {
					consolidate();
				}
				return;
			}
		}
		// if we arrive here, we must insert at zero
		overflowIndices[0] = index;
		overflowEntries[0] = event;
		if (overflowSize == overflowIndices.length) {
			consolidate();
		}
	}

	/**
	 * Inserts the given event at its logical position in the list. The logical
	 * position is determined from timestamp information, if available.
	 * Otherwise, the event is appended to the end of the list.
	 * 
	 * @param event
	 *            Event to be inserted.
	 * @return Position of the event after insertion.
	 */
	public synchronized int insertOrdered(XEvent event) throws IOException {
		if (this.size() == 0) {
			// append if list is empty
			append(event);
			return 0;
		}
		XAttribute insTsAttr = event.getAttributes().get(
				XTimeExtension.KEY_TIMESTAMP);
		if (insTsAttr == null) {
			// append if event has no timestamp
			append(event);
			return (size() - 1);
		}
		Date insTs = ((XAttributeTimestamp) insTsAttr).getValue();
		for (int i = (size() - 1); i >= 0; i--) {
			XAttribute refTsAttr = get(i).getAttributes().get(
					XTimeExtension.KEY_TIMESTAMP);
			if (refTsAttr == null) {
				// trace contains events w/o timestamps, append.
				append(event);
				return (size() - 1);
			}
			Date refTs = ((XAttributeTimestamp) refTsAttr).getValue();
			if (insTs.before(refTs) == false) {
				// insert position reached
				insert(event, i + 1);
				return (i + 1);
			}
		}
		// beginning reached, insert at head
		insert(event, 0);
		return 0;
	}

	/**
	 * Checks whether this list needs consolidation, i.e. whether the overflow
	 * and skipping structures have any content.
	 * 
	 * @return Whether this list is tainted.
	 */
	public synchronized boolean isTainted() {
		return (overflowSize > 0) || (holeFlags.cardinality() > 0);
	}

	/**
	 * Removes the event at the given index from this list.
	 * 
	 * @param index
	 *            Index of the event to be removed.
	 * @return The removed event.
	 */
	public synchronized XEvent remove(int index)
			throws IndexOutOfBoundsException, IOException {
		// check overflow list and adjust indices
		XEvent removed = null;
		int smallerOverflow = 0;
		for (int i = 0; i < overflowSize; i++) {
			if (overflowIndices[i] == index) {
				removed = overflowEntries[i];
			} else if (overflowIndices[i] > index) {
				overflowIndices[i] = overflowIndices[i] - 1;
				if (removed != null) {
					// move left
					overflowIndices[i - 1] = overflowIndices[i];
					overflowEntries[i - 1] = overflowEntries[i];
				}
			} else if (overflowIndices[i] < index) {
				smallerOverflow++;
			}
		}
		if (removed != null) {
			// adjust overflow size
			overflowSize--;
			// invalidate entry in overflow set
			overflowIndices[overflowSize] = -1;
			overflowEntries[overflowSize] = null;
		} else {
			int bufferIndex = index - smallerOverflow;
			for (int hole = holeFlags.nextSetBit(0); hole >= 0
					&& hole <= bufferIndex; hole = holeFlags
					.nextSetBit(hole + 1)) {
				bufferIndex++;
			}
			removed = buffer.get(bufferIndex);
			holeFlags.set(bufferIndex, true);
		}
		size--;
		return removed;
	}

	/**
	 * Replaces the event at the given index with another event.
	 * 
	 * @param event
	 *            Event to be inserted at the given position.
	 * @param index
	 *            Position to replace event at.
	 * @return The removed event, which has been replaced.
	 */
	public synchronized XEvent replace(XEvent event, int index)
			throws IndexOutOfBoundsException, IOException {
		// check overflow list and adjust indices
		XEvent replaced = null;
		int smallerOverflow = 0;
		for (int i = 0; i < overflowSize; i++) {
			if (overflowIndices[i] == index) {
				replaced = overflowEntries[i];
				overflowEntries[i] = event;
				return replaced;
			} else if (overflowIndices[i] > index) {
				// done
				break;
			} else if (overflowIndices[i] < index) {
				smallerOverflow++;
			}
		}
		// still here: we must look in file buffer
		int bufferIndex = index - smallerOverflow;
		for (int hole = holeFlags.nextSetBit(0); hole >= 0
				&& hole <= bufferIndex; hole = holeFlags.nextSetBit(hole + 1)) {
			bufferIndex++;
		}
		replaced = buffer.get(bufferIndex);
		if (buffer.replace(event, bufferIndex) == false) {
			remove(index);
			insert(event, index);
		}
		return replaced;
	}

	/**
	 * Returns the size of this event list.
	 * 
	 * @return The number of currently contained events.
	 */
	public synchronized int size() {
		return size;
	}

	/**
	 * Creates a clone of this list.
	 */
	public synchronized Object clone() {
		// consolidate first
		try {
			this.consolidate();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		// start cloning
		XFastEventList clone = null;
		try {
			clone = (XFastEventList) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		// clone back buffer explicitly
		clone.buffer = (XSequentialEventBuffer) buffer.clone();
		clone.holeFlags= (BitSet) holeFlags.clone();
		clone.overflowEntries = overflowEntries.clone();
		clone.overflowIndices = overflowIndices.clone();
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		cleanup();
	}

}
