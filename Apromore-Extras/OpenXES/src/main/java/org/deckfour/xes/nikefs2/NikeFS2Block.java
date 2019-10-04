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

/**
 * This class implements the abstraction of a storage block for the
 * NikeFS2 virtual file systems. Blocks have a defined size, in number
 * of bytes, and are the basic building blocks for virtual files.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class NikeFS2Block {
	
	/**
	 * The provider of this block, i.e. where it is physically stored.
	 */
	private final NikeFS2BlockProvider provider;
	/**
	 * The internal number of this block within its provider.
	 */
	private final int blockNumber;
	
	/**
	 * Creates a new block.
	 * 
	 * @param provider The provider of this block.
	 * @param blockNumber The block number.
	 */
	public NikeFS2Block(NikeFS2BlockProvider provider, int blockNumber) {
		this.provider = provider;
		this.blockNumber = blockNumber;
	}
	
	/**
	 * Returns the size of this block in bytes.
	 */
	public int size() {
		return provider.blockSize();
	}
	
	/**
	 * Returns the index number of this block within
	 * its block provider.
	 */
	public int blockNumber() {
		return blockNumber;
	}
	
	/**
	 * Closes this block, which frees all associated resources.
	 */
	public void close() {
		provider.freeBlock(this);
	}
	
	/**
	 * Read a number of bytes from this block.
	 * 
	 * @param blockOffset Offset, in bytes, within this block.
	 * @param buffer Buffer to store read data in.
	 * @param offset Offset within the buffer to write to.
	 * @param length Number of bytes to be read.
	 * @return The number of read bytes.
	 */
	public synchronized int read(int blockOffset, byte[] buffer, int offset, int length) 
			throws IOException {
		return provider.read(blockNumber, blockOffset, buffer, offset, length);
	}
	
	/**
	 * Read a number of bytes from this block.
	 * 
	 * @param blockOffset Offset, in bytes, within this block.
	 * @param buffer Buffer to store read data in.
	 */
	public synchronized int read(int blockOffset, byte[] buffer) 
			throws IOException {
		return provider.read(blockNumber, blockOffset, buffer);
	}
	
	/**
	 * Read a single byte from this block.
	 * 
	 * @param blockOffset Offset, in bytes, within this block.
	 * @return The read byte.
	 */
	public synchronized int read(int blockOffset) 
			throws IOException {
		return provider.read(blockNumber, blockOffset);
	}
	
	/**
	 * Writes a number of bytes to this block.
	 * 
	 * @param blockOffset Offset within the block to commence writing at.
	 * @param buffer Buffer storing the data to be written.
	 * @param offset Offset within the buffer from where to read.
	 * @param length Number of bytes to be written.
	 */
	public synchronized void write(int blockOffset, byte[] buffer, int offset, int length)
			throws IOException {
		provider.write(blockNumber, blockOffset, buffer, offset, length);
	}
	
	/**
	 * Writes a number of bytes to this block.
	 * 
	 * @param blockOffset Offset within the block to commence writing at.
	 * @param buffer Buffer storing the data to be written.
	 */
	public synchronized void write(int blockOffset, byte[] buffer)
			throws IOException {
		provider.write(blockNumber, blockOffset, buffer);
	}
	
	/**
	 * Writes a single byte to this block.
	 * 
	 * @param blockOffset Offset within the block to commence writing at.
	 * @param value The byte to be written.
	 */
	public synchronized void write(int blockOffset, int value)
			throws IOException {
		provider.write(blockNumber, blockOffset, value);
	}
	
	

}
