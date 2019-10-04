/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
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
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.logging.XLogging.Importance;

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class NikeFS2FileAccessMonitor {
	
	private static NikeFS2FileAccessMonitor singleton = null;
	
	public synchronized static NikeFS2FileAccessMonitor instance() {
		return instance(4);
	}
	
	public synchronized static NikeFS2FileAccessMonitor instance(int shadowSize) {
		if(singleton == null) {
			singleton = new NikeFS2FileAccessMonitor(shadowSize);
		}
		return singleton;
	}
	
	/**
	 * Maximum number of currently open backing files.
	 */
	private int shadowSize = 4;
	/**
	 * Number of currently open backing files.
	 */
	private int currentShadowSize = 0;
	/**
	 * Index of the last-accessed backing file.
	 */
	private int lastRequestIndex = 0;
	/**
	 * Array of mapped byte buffers, which grant
	 * fast access to currently open backing files.
	 */
	private MappedByteBuffer[] centralMaps = null;
	/**
	 * Array of block providers, which hold the byte buffers
	 * at their corresponding index in the latter array.
	 */
	private NikeFS2BlockProvider[] currentMapOwners = null;
	/**
	 * Array of random access file wrappers for map owners
	 */
	private RandomAccessFile[] currentMapRandomAccessFiles = null;
	/**
	 * Array of file channels for map owners
	 */
	private FileChannel[] currentMapFileChannels = null;
	
	
	public NikeFS2FileAccessMonitor(int shadowSize) {
		this.shadowSize = shadowSize;
		this.centralMaps = new MappedByteBuffer[shadowSize];
		this.currentMapOwners = new NikeFS2BlockProvider[shadowSize];
		this.currentMapRandomAccessFiles = new RandomAccessFile[shadowSize];
		this.currentMapFileChannels = new FileChannel[shadowSize];
	}
	
	/**
	 * Grants access to the mapped byte buffer on the backing file for
	 * a block provider. This static method implements the backing file
	 * manager, which ensures that only a limited number of backing files
	 * and associated mapped byte buffers are concurrently in use.
	 * 
	 * @param requester Block provider requesting access.
	 * @return The mapped byte buffer over the requester's backing file.
	 */
	public synchronized MappedByteBuffer requestMap(NikeFS2BlockProvider requester) 
			throws IOException {
		// check if requested map is already in shadow
		for(int i=0; i<currentShadowSize; i++) {
			if(currentMapOwners[i] == requester) {
				// requester found in shadow; return shadowed map
				lastRequestIndex = i;
				return centralMaps[i];
			}
		}
		// check if we can create another shadow map
		if(currentShadowSize < shadowSize) {
			// create new map in shadow in pristine place
			currentMapOwners[currentShadowSize] = requester;
			currentMapRandomAccessFiles[currentShadowSize] = new RandomAccessFile(requester.getFile(), "rw");
			currentMapFileChannels[currentShadowSize] = currentMapRandomAccessFiles[currentShadowSize].getChannel();
			MappedByteBuffer map = currentMapFileChannels[currentShadowSize].map(FileChannel.MapMode.READ_WRITE, 0, requester.size());
			centralMaps[currentShadowSize] = map;
			lastRequestIndex = currentShadowSize;
			currentShadowSize++;
			XLogging.log("NikeFS2: Populating shadow map " + currentShadowSize + " (of " + shadowSize + " max.)", Importance.DEBUG);
			return map;
		} else {
			// we need to displace one shadow to make place
			int kickIndex = lastRequestIndex + 1;
			if(kickIndex == shadowSize) {
				kickIndex = 0;
			}
			centralMaps[kickIndex].force();
			centralMaps[kickIndex] = null;
			currentMapFileChannels[kickIndex].close();
			currentMapFileChannels[kickIndex] = null;
			currentMapRandomAccessFiles[kickIndex].close();
			currentMapRandomAccessFiles[kickIndex] = null;
			System.gc();
			currentMapOwners[kickIndex] = requester;
			currentMapRandomAccessFiles[kickIndex] = new RandomAccessFile(requester.getFile(), "rw");
			currentMapFileChannels[kickIndex] = currentMapRandomAccessFiles[kickIndex].getChannel();
			MappedByteBuffer map = currentMapFileChannels[kickIndex].map(FileChannel.MapMode.READ_WRITE, 0, requester.size());
			centralMaps[kickIndex] = map;
			lastRequestIndex = kickIndex;
			XLogging.log("NikeFS2: Displacing shadow map " + (kickIndex + 1), 
					Importance.DEBUG);
			return map;
		}
	}

}
