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

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.deckfour.xes.model.XEvent;

/**
 * This class implements an iterator over the buffered implementation for the
 * XTrace interface.
 * 
 * @see java.util.Iterator
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XTraceIterator implements ListIterator<XEvent> {

	/**
	 * The trace to be iterated over.
	 */
	protected XTraceBufferedImpl list = null;
	/**
	 * Current position of the iterator.
	 */
	protected int position = 0;

	/**
	 * Constructs a new iterator on the specified
	 * <code>XTraceBufferedImpl</code>.
	 * 
	 * @param aList
	 *            XTraeBufferedImpl, over which the created iterator iterates.
	 */
	public XTraceIterator(XTraceBufferedImpl aList) {
		this(aList, 0);
	}

	/**
	 * Constructs a new iterator on the specified
	 * <code>XTraceBufferedImpl</code>.
	 * 
	 * @param aList
	 *            XTraeBufferedImpl, over which the created iterator iterates.
	 * @param aPosition
	 *            The starting position of the iterator.
	 */
	public XTraceIterator(XTraceBufferedImpl aList, int aPosition) {
		list = aList;
		position = aPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return (position < list.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public XEvent next() {
		XEvent result = null;
		try {
			result = list.get(position);
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchElementException("There is no next event in this trace");
		} finally {
			position++;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		position--;
		list.remove(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	public void add(XEvent o) {
		list.add(position, o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return (position > 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#previous()
	 */
	public XEvent previous() {
		position--;
		return list.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return (position - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	public void set(XEvent o) {
		list.remove(position);
		list.add(position, o);
	}

}
