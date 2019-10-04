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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.BitSet;

/**
 * This class implements a block provider for the NikeFS2 virtual
 * file system. It is backed by an OS-level random-access file, which
 * stores the actual contents of the blocks.
 * 
 * Also, this class contains static facilities for managing the
 * current set of open files, thereby reducing the number of
 * concurrently open and used file handles to a sensible minimum.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class NikeFS2BlockProvider {
	
	/**
	 * Flag indicating whether this block provider is using
	 * memory mapping for faster access to its contents
	 * (default and recommended is true).
	 */
	protected final boolean mapped;
	/**
	 * Backing file for this block provider.
	 */
	protected final File file;
	/**
	 * Random-access file wrapper, if non-mapped
	 */
	protected final RandomAccessFile rafile;
	/**
	 * Size (in bytes) of this block provider, i.e. of its backing file.
	 */
	protected final int size;
	/**
	 * Size (in bytes) of each block in this block provider.
	 */
	protected final int blockSize;
	/**
	 * Number of blocks provided by this block provider.
	 */
	protected final int numberOfBlocks;
	/**
	 * Bit set indicating which blocks served by this provider
	 * are still free, i.e. not yet allocated. Every block is
	 * addressed by its unique index within this provider. For
	 * each block, a bit set to <code>true</code>, at the block's
	 * index in this bit set, indicates that the block is free.
	 * Blocks whose index's bit is set to <code>false</code> are
	 * currently allocated.
	 */
	protected final BitSet blockAllocationMap;
	
	
	/**
	 * Creates a new block provider.
	 * 
	 * @param storage Backing file to store actual contents in.
	 * @param size Size of the backing file in bytes.
	 * @param blockSize Size of blocks in bytes.
	 * @param mapped Whether to use memory mapping for this block
	 * provider (it is recommended to set this flag to <code>true</code>).
	 */
	public NikeFS2BlockProvider(File storage, int size, int blockSize, boolean mapped) 
			throws IOException {
		synchronized(this) {
			// initialize
			this.mapped = mapped;
			this.size = size;
			this.blockSize = blockSize;
			// create backing file, if not present yet
			if(storage.exists()==false) {
				storage.createNewFile();
			}
			// wrap backing file in random access file
			this.file = storage;
			if(this.mapped) {
				this.rafile = new RandomAccessFile(file, "rw");
			} else {
				this.rafile = null;
			}
			// compute number of available blocks
			numberOfBlocks = size / blockSize;
			// allocate block allocation map.
			blockAllocationMap = new BitSet(numberOfBlocks);
			// initialize all fields to true, i.e. free blocks
			blockAllocationMap.set(0, numberOfBlocks);
		}
	}
	
	/**
	 * Provides direct access to the backing file.
	 * 
	 * @return The random access file backing this block provider.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Returns the size, in bytes, of this block provider in total.
	 * 
	 * @return Number of bytes totally provided by this instance.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Returns the number of blocks provided by this instance.
	 * 
	 * @return The number of blocks provided by this instance.
	 */
	public int numberOfBlocks() {
		return numberOfBlocks;
	}
	
	/**
	 * Returns the number of non-allocated blocks in this instance.
	 * 
	 * @return The number of non-allocated blocks in this instance.
	 */
	public synchronized int numberOfFreeBlocks() {
		// The number of free blocks equals the number of bits
		// set to true in the allocation map.
		return blockAllocationMap.cardinality();
	}
	
	/**
	 * Returns the size of blocks provided by this instance.
	 * 
	 * @return The size of blocks (in bytes) provided by this instance.
	 */
	public int blockSize() {
		return blockSize;
	}
	
	/**
	 * Allocates a new block from this block provider.
	 * 
	 * @return A newly allocated block from this provider.
	 * May return <code>null</code>, if no free blocks are
	 * currently available.
	 */
	public synchronized NikeFS2Block allocateBlock() {
		// look for free block, i.e. first block whose index
		// bit is set to true in the allocation map.
		int freeBlockIndex = blockAllocationMap.nextSetBit(0);
		if(freeBlockIndex < 0) {
			// no free blocks left in this provider.
			return null;
		} else {
			// set index bit to false in allocation map,
			// and return in block wrapper.
			blockAllocationMap.set(freeBlockIndex, false);
			return new NikeFS2Block(this, freeBlockIndex);
		}
	}
	
	/**
	 * De-allocates the specified block in this provider.
	 * 
	 * @param block The block to be freed.
	 */
	public synchronized void freeBlock(NikeFS2Block block) {
		// set block index to true in allocation map.
		blockAllocationMap.set(block.blockNumber(), true);
	}
	
	/**
	 * Returns the internal offset of the specified block in
	 * the backing file (in bytes).
	 * 
	 * @param blockNumber The internal number of the block in question.
	 * @return Internal offset of the block, in bytes, in the backing file.
	 */
	public int getBlockOffset(int blockNumber) {
		return blockNumber * blockSize;
	}
	
	/**
	 * Reads from the specified block.
	 * 
	 * @param blockNumber Internal number of the block in question.
	 * @param blockOffset Offset, in bytes, within this block.
	 * @param buffer Buffer to store read data in.
	 * @return The number of read bytes.
	 */
	public synchronized int read(int blockNumber, int blockOffset, byte[] buffer) 
			throws IOException {
		return read(blockNumber, blockOffset, buffer, 0, buffer.length);
	}
	
	/**
	 * Reads from the specified block.
	 * 
	 * @param blockNumber Internal number of the block in question.
	 * @param blockOffset Offset, in bytes, within this block.
	 * @param buffer Buffer to store read data in.
	 * @param bufferOffset Offset in the given buffer to start writing at.
	 * @param length Number of bytes to be read.
	 * @return The number of read bytes.
	 */
	public synchronized int read(int blockNumber, int blockOffset, byte[] buffer, int bufferOffset, int length) 
			throws IOException {
		long pointer = getBlockOffset(blockNumber) + blockOffset;
		int readable = blockSize - blockOffset;
		int readLength = length;
		if(readable < length) {
			readLength = readable;
		}
		if(mapped == true) {
			MappedByteBuffer map = NikeFS2FileAccessMonitor.instance().requestMap(this);
			map.position((int)pointer);
			map.get(buffer, bufferOffset, readLength);
			return readLength;
		} else {
			rafile.seek(pointer);
			return rafile.read(buffer, bufferOffset, readLength);
		}
	}
	
	/**
	 * Reads from the specified block.
	 * 
	 * @param blockNumber Internal number of the block in question.
	 * @param blockOffset Offset, in bytes, within this block.
	 * @return The read byte.
	 */
	public synchronized int read(int blockNumber, int blockOffset) 
			throws IOException {
		long pointer = getBlockOffset(blockNumber) + blockOffset;
		if(mapped == true) {
			MappedByteBuffer map = NikeFS2FileAccessMonitor.instance().requestMap(this);
			map.position((int)pointer);
			int result = map.get();
			return result + 128;
		} else {
			rafile.seek(pointer);
			return rafile.read();
		}
	}
	
	/**
	 * Writes to this block.
	 * 
	 * @param blockNumber Internal number of the block in question.
	 * @param blockOffset Offset within the block to commence writing at.
	 * @param buffer Buffer storing the data to be written.
	 */
	public synchronized void write(int blockNumber, int blockOffset, byte[] buffer) 
			throws IOException {
		write(blockNumber, blockOffset, buffer, 0, buffer.length);
	}
	
	/**
	 * Writes to this block.
	 * 
	 * @param blockNumber Internal number of the block in question.
	 * @param blockOffset Offset within the block to commence writing at.
	 * @param buffer Buffer storing the data to be written.
	 * @param bufferOffset Offset within the buffer from where to read.
	 * @param length Number of bytes to be written.
	 */
	public synchronized void write(int blockNumber, int blockOffset, byte[] buffer, int bufferOffset, int length) 
			throws IOException {
		long pointer = getBlockOffset(blockNumber) + blockOffset;
		int writable = blockSize - blockOffset;
		int writeLength = length;
		if(writable < length) {
			writeLength = writable;
		}
		if(mapped == true) {
			MappedByteBuffer map = NikeFS2FileAccessMonitor.instance().requestMap(this);
			map.position((int)pointer);
			map.put(buffer, bufferOffset, writeLength);
		} else {
			rafile.seek(pointer);
			rafile.write(buffer, bufferOffset, writeLength);
		}
	}
	
	/**
	 * Writes to this block.
	 * 
	 * @param blockNumber Internal number of the block in question.
	 * @param blockOffset Offset within the block to commence writing at.
	 * @param value Byte value to be written.
	 */
	public synchronized void write(int blockNumber, int blockOffset, int value) 
			throws IOException {
		long pointer = getBlockOffset(blockNumber) + blockOffset;
		if(mapped == true) {
			MappedByteBuffer map = NikeFS2FileAccessMonitor.instance().requestMap(this);
			map.position((int)pointer);
			map.put((byte)(value - 128));
		} else {
			rafile.seek(pointer);
			rafile.write(value);
		}
	}

}
