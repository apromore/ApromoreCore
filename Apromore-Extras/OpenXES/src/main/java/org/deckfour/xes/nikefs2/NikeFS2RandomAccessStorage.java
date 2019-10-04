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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This interface specifies a random acess data storage container,
 * pretty much the same as <code>java.util.RandomAccessFile</code>.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public interface NikeFS2RandomAccessStorage extends DataOutput, DataInput {

	/**
	 * Closes the data storage container.
	 * After this method has been invoked, no further access
	 * to the represented instance is allowed.
	 */
	public void close() 
		throws IOException;
	
	/**
	 * Returns the current file pointer of the storage container.
	 * A file pointer is the offset in bytes, from the beginning of the
	 * sequential byte storage, at which the next read or write operation
	 * would occur.
	 * 
	 * @return Offset in bytes from beginning of storage.
	 */
	public long getFilePointer() 
		throws IOException;
	
	/**
	 * Returns the length, or size, in number of bytes currently
	 * used by this instance.
	 * 
	 * @return Number of bytes currently allocated.
	 */
	public long length() 
		throws IOException;
	
	/**
	 * Repositions the offset, or file pointer, at which the next read
	 * or write operation will occur.
	 * 
	 * @param pos The offset in bytes, at which the next operation 
	 * will occur.
	 */
	public void seek(long pos) 
		throws IOException;
	
	/**
	 * Moves the offset, or file pointer, a specified number of bytes
	 * towards the end of the storage container.
	 */
	public int skipBytes(int n) 
		throws IOException;
	
	/**
	 * Creates a clone, or copy, of this storage, having the exact same
	 * contents and the file pointer reset to zero.
	 */
	public NikeFS2RandomAccessStorage copy()
		throws IOException;
	
}
