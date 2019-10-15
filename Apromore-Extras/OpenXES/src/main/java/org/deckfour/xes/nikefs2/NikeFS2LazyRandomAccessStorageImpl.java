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
package org.deckfour.xes.nikefs2;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Lazy implementation of the random access storage in NikeFS2: Blocks are
 * copied as late as possible (soft copies), while retaining soft links
 * otherwise.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class NikeFS2LazyRandomAccessStorageImpl extends
		NikeFS2RandomAccessStorageImpl {

	/**
	 * Parent storage, containing the original data.
	 */
	protected NikeFS2LazyRandomAccessStorageImpl parent;
	/**
	 * Whether this instance is still a soft copy (i.e., actually empty).
	 */
	protected boolean isSoftCopy = true;
	/**
	 * Child storages, which are soft copies of this instance.
	 */
	protected ArrayList<NikeFS2LazyRandomAccessStorageImpl> softCopies;

	/**
	 * Creates a new instance.
	 * 
	 * @param virtualFileSystem
	 *            Virtual file system to store data to.
	 */
	public NikeFS2LazyRandomAccessStorageImpl(
			NikeFS2VirtualFileSystem virtualFileSystem) {
		super(virtualFileSystem);
		synchronized (NikeFS2RandomAccessStorageImpl.class) {
			isSoftCopy = false;
			parent = null;
			softCopies = new ArrayList<NikeFS2LazyRandomAccessStorageImpl>();
		}
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param template
	 *            Storage of which this instance is a soft copy.
	 */
	public NikeFS2LazyRandomAccessStorageImpl(
			NikeFS2LazyRandomAccessStorageImpl template) {
		super(template.vfs);
		synchronized (NikeFS2RandomAccessStorageImpl.class) {
			isSoftCopy = true;
			softCopies = new ArrayList<NikeFS2LazyRandomAccessStorageImpl>();
			size = template.size;
			pointer = template.pointer;
			blocks = template.blocks;
			parent = template;
			template.registerSoftCopy(this);
		}
	}

	/**
	 * This method is used by child copies to register with their parent.
	 * 
	 * @param copycat
	 *            The child soft copy to register.
	 */
	public synchronized void registerSoftCopy(
			NikeFS2LazyRandomAccessStorageImpl copycat) {
		softCopies.add(copycat);
	}

	/**
	 * This method is used by child copies to deregister with their parent.
	 * 
	 * @param copycat
	 *            The child soft copy to deregister.
	 */
	public synchronized void deregisterSoftCopy(
			NikeFS2LazyRandomAccessStorageImpl copycat) {
		softCopies.remove(copycat);
	}

	/**
	 * This method alerts all child soft copies of this storage to consolidate;
	 * called prior to modification of this instance. The child soft copies so
	 * alerted will detach from this instance consequently.
	 */
	public synchronized void alertSoftCopies() throws IOException {
		// make a copy of the list of soft copies, as they will deregister
		// within the loop (removing themselves from our internal list)
		NikeFS2LazyRandomAccessStorageImpl[] copies = softCopies
				.toArray(new NikeFS2LazyRandomAccessStorageImpl[softCopies
						.size()]);
		for (NikeFS2LazyRandomAccessStorageImpl copy : copies) {
			if (copy.isSoftCopy) {
				copy.consolidateSoftCopy();
				copy.alertSoftCopies(); // HV
			}
		}
	}

	/**
	 * Consolidates this soft copy prior to modification. This will detach this
	 * instance from its parent, creating a true copy of its current data.
	 */
	public synchronized void consolidateSoftCopy() throws IOException {
		if (isSoftCopy == true) {
			ArrayList<NikeFS2Block> copyBlocks = new ArrayList<NikeFS2Block>();
			if (blocks.size() > 0) {
				// make copies of all contained blocks
				byte[] buffer = new byte[blocks.get(0).size()];
				for (NikeFS2Block block : blocks) {
					NikeFS2Block copyBlock = vfs.allocateBlock();
					block.read(0, buffer);
					copyBlock.write(0, buffer);
					copyBlocks.add(copyBlock);
				}
			}
			// replace blocks list
			blocks = copyBlocks;
			isSoftCopy = false;
			// deregister from template
			parent.deregisterSoftCopy(this);
			parent = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		alertSoftCopies();
		if (parent != null) {
			parent.deregisterSoftCopy(this);
		}
		if (isSoftCopy == false) {
			// frees our rightfully owned blocks
			super.close();
		} else {
			// shared blocks must not be freed (soft copy)
			blocks = null;
			size = 0;
			pointer = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#copy()
	 */
	@Override
	public synchronized NikeFS2RandomAccessStorage copy() throws IOException {
		return (NikeFS2RandomAccessStorage) (new NikeFS2LazyRandomAccessStorageImpl(
				this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#write
	 * (byte[], int, int)
	 */
	@Override
	public synchronized void write(byte[] b, int off, int len)
			throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.write(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#write
	 * (byte[])
	 */
	@Override
	public synchronized void write(byte[] b) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#write
	 * (int)
	 */
	@Override
	public synchronized void write(int b) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeBoolean
	 * (boolean)
	 */
	@Override
	public synchronized void writeBoolean(boolean v) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeBoolean(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeByte
	 * (int)
	 */
	@Override
	public synchronized void writeByte(int b) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeByte(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeBytes
	 * (java.lang.String)
	 */
	@Override
	public synchronized void writeBytes(String str) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeBytes(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeChar
	 * (int)
	 */
	@Override
	public synchronized void writeChar(int c) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeChar(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeChars
	 * (java.lang.String)
	 */
	@Override
	public synchronized void writeChars(String str) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeChars(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeDouble
	 * (double)
	 */
	@Override
	public synchronized void writeDouble(double d) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeDouble(d);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeFloat
	 * (float)
	 */
	@Override
	public synchronized void writeFloat(float f) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeFloat(f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeInt
	 * (int)
	 */
	@Override
	public synchronized void writeInt(int i) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeInt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeLong
	 * (long)
	 */
	@Override
	public synchronized void writeLong(long l) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeLong(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeShort
	 * (int)
	 */
	@Override
	public synchronized void writeShort(int s) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeShort(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.fsio.FS2RandomAccessStorage#writeUTF
	 * (java.lang.String)
	 */
	@Override
	public synchronized void writeUTF(String str) throws IOException {
		consolidateSoftCopy();
		alertSoftCopies();
		super.writeUTF(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		close();
	}

}
