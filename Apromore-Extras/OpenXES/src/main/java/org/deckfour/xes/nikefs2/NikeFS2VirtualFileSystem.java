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
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.logging.XLogging.Importance;

/**
 * Virtual file system authority, managing swap files and virtual file
 * abstractions. Implements the storage provider interface for use by file
 * abstractions, and performs cleanup after a shutdown of the host application.
 * 
 * This class must be used as a singleton. There is no need for multiple virtual
 * file systems per application. A single VFS instance operates with maximum
 * efficiency, and guarantees that no data corruption or swap loss will occur.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class NikeFS2VirtualFileSystem implements NikeFS2StorageProvider {

	/**
	 * The singleton VFS instance.
	 */
	protected static NikeFS2VirtualFileSystem instance = null;

	/**
	 * Singleton access method.
	 * 
	 * @return The singleton instance.
	 */
	public synchronized static NikeFS2VirtualFileSystem instance() {
		if (instance == null) {
			instance = new NikeFS2VirtualFileSystem();
		}
		return instance;
	}

	/**
	 * Block size, in bytes, of virtual storage blocks. Default value is 2048,
	 * i.e. 2kB.
	 */
	protected int blockSize = 2048;
	/**
	 * Size of swap files which are used to provide blocks. Default is 67108864,
	 * i.e. 64 MB.
	 */
	protected int swapFileSize = 67108864;
	/**
	 * Flag determining whether to use soft copies for virtual file duplication.
	 * This greatly enhances performance with negligible memory penalties, thus
	 * the default value is <code>true</code>.
	 */
	protected boolean useLazyCopies = true;
	/**
	 * List of block providers, i.e. currently used swap files that serve for
	 * providing storage blocks to virtual file abstractions.
	 */
	protected List<NikeFS2BlockProvider> blockProviders;

	/**
	 * Creates a new virtual file system instance. (Hidden private constructor,
	 * use singleton accessor method!)
	 */
	private NikeFS2VirtualFileSystem() {
		blockProviders = new ArrayList<NikeFS2BlockProvider>();
	}

	/**
	 * Sets this VFS's property of whether to use soft copies for virtual file
	 * duplication. This will only affect newly created virtual files.
	 * 
	 * @param useLazyCopies
	 *            Whether to use soft copies for virtual file duplication.
	 */
	public synchronized void setUseLazyCopies(boolean useLazyCopies) {
		this.useLazyCopies = useLazyCopies;
	}

	/**
	 * Sets the swap file size of the virtual file system. (used henceforth
	 * until successive change).
	 * 
	 * @param bytes
	 *            Size of swap files, in bytes.
	 */
	public synchronized void setSwapFileSize(int bytes) {
		this.swapFileSize = bytes;
	}

	/**
	 * Sets the block size of the virtual file system. (used henceforth until
	 * successive change).
	 * 
	 * @param bytes
	 *            Size of blocks, in bytes.
	 */
	public synchronized void setBlockSize(int bytes) {
		this.blockSize = bytes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.framework.log.rfb.io.StorageProvider#createStorage()
	 */
	public NikeFS2RandomAccessStorage createStorage() throws IOException {
		if (useLazyCopies == true) {
			return new NikeFS2LazyRandomAccessStorageImpl(this);
		} else {
			return new NikeFS2RandomAccessStorageImpl(this);
		}
	}

	/**
	 * Returns the block size of this VFS.
	 * 
	 * @return The size of virtual storage blocks served by this VFS, in bytes.
	 */
	public int blockSize() {
		return blockSize;
	}

	/**
	 * Allocates a new virtual storage block from this virtual file system
	 * instance. If no currently allocated swap file can provide any more
	 * storage blocks, a new swap file will be allocated.
	 * 
	 * @return An empty storage block abstraction.
	 */
	public NikeFS2Block allocateBlock() throws IOException {
//		synchronized (this) {
//			// try to allocate from already created providers first
//			for (NikeFS2BlockProvider provider : blockProviders) {
//				if (provider.numberOfFreeBlocks() > 0) {
//					NikeFS2Block block = provider.allocateBlock();
//					if (block != null) {
//						return block;
//					}
//				}
//			}
//		}
//		// force garbage collection and try again (stale files still around?)
//		// Running the finalization might cause the Finalizer thread to call the allocateBlock method again,
//		// hence the lock on "this" should be removed before calling System.runFinalization
//		System.gc();
//		System.runFinalization();
//		Thread.yield();
		synchronized (this) {
			for (NikeFS2BlockProvider provider : blockProviders) {
				if (provider.numberOfFreeBlocks() > 0) {
					NikeFS2Block block = provider.allocateBlock();
					if (block != null) {
						return block;
					}
				}
			}
			// ok, we give up:
			// create new swap file and provider, and allocate from there
			XLogging.log("NikeFS2: Allocating new swap file. (#"
					+ (blockProviders.size() + 1) + ": " + swapFileSize
					+ " bytes)", Importance.DEBUG);
			File swapFile = NikeFS2SwapFileManager.createSwapFile();
			NikeFS2BlockProvider addedProvider = new NikeFS2BlockProvider(
					swapFile, swapFileSize, blockSize, true);
			blockProviders.add(addedProvider);
			return addedProvider.allocateBlock();
		}
	}

}
