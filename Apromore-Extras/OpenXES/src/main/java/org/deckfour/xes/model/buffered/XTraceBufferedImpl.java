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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.util.XAttributeUtils;

/**
 * Soft-buffered implementation of the XTrace interface. Uses the virtual NikeFS
 * filesystem for event log data for transparently storing the data on disk, so
 * that main memory is freed for other tasks.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XTraceBufferedImpl implements XTrace {

	/**
	 * ID prefix used for this implementation.
	 */
	public static final String ID_PREFIX = "TRACE";

	/**
	 * The attribute map of this trace.
	 */
	private XAttributeMap attributes;
	/**
	 * Attribute map serializer.
	 */
	private XAttributeMapSerializer attributeMapSerializer;
	/**
	 * Fast event list, backed by NikeFS2, storing the events in this trace.
	 */
	private XFastEventList events;

	/**
	 * Creates a new trace.
	 * 
	 * @param attributeMap
	 *            Map to store the attributes of this trace.
	 * @param attributeMapSerializer
	 *            Serializer used to serialize the attribute maps of events in
	 *            this buffered trace.
	 */
	public XTraceBufferedImpl(XAttributeMap attributeMap,
			XAttributeMapSerializer attributeMapSerializer) {
		this.attributeMapSerializer = attributeMapSerializer;
		this.attributes = attributeMap;
		try {
			this.events = new XFastEventList(attributeMapSerializer);
		} catch (IOException e) {
			// oh la la..
			e.printStackTrace();
		}
	}

	/**
	 * List equality for XTrace
	 * 
	 * Equality as defined in the List interface and as implemented in the AbstractList class, 
	 * with a small tweak for efficiency.
	 * 
	 * @param o Object to be compared with this XTrace implementation.
	 * 
	 * @return Returns true if both objects are lists and the elements of both lists are equal. 
	 */
	public boolean equals(Object o) { 
		if (o == this) {
			return true;
		}
		if (!(o instanceof XTrace)) {
			return false;
		}
		XTrace other = (XTrace)o;
		// First compare the size of the lists, since iterating through them can be expensive,
		// because of on-disk caching operations being performed in the background. 
		if (size() != other.size()) {
			return false;
		}
		ListIterator<XEvent> i1 = listIterator();
		ListIterator<XEvent> i2 = other.listIterator();
		while (i1.hasNext() && i2.hasNext()) {
			XEvent e1 = i1.next();
			XEvent e2 = i2.next();
			if (!(e1 == null ? e2 == null : e1.equals(e2))) {
				return false;
			}
		}
		// i1 empty or i2 empty, hence i1 empty and i2 empty (as #i1 == #i2).
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.XAttributable#getAttributes()
	 */
	public XAttributeMap getAttributes() {
		return attributes;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XAttributable#hasAttributes()
	 */
	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.XAttributable#getExtensions()
	 */
	public Set<XExtension> getExtensions() {
		return XAttributeUtils.extractExtensions(attributes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.XAttributable#setAttributes(java.util.Map)
	 */
	public void setAttributes(XAttributeMap attributes) {
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(XEvent event) {
		try {
			events.append(event);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, XEvent event) {
		try {
			events.insert(event, index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends XEvent> c) {
		return addAll(events.size(), c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends XEvent> c) {
		for (XEvent event : c) {
			try {
				events.insert(event, index);
			} catch (IOException e) {
				e.printStackTrace();
			}
			index++;
		}
		return (c.size() > 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		try {
			events.cleanup();
			events = new XFastEventList(this.attributeMapSerializer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		for (int i = 0; i < events.size(); i++) {
			try {
				if (events.get(i).equals(o)) {
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (contains(e) == false) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#get(int)
	 */
	public XEvent get(int index) {
		try {
			XEvent event = events.get(index);
			return event;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		try {
			for (int i = 0; i < events.size(); i++) {
				if (events.get(i).equals(o)) {
					return i;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return (events.size() == 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#iterator()
	 */
	public Iterator<XEvent> iterator() {
		return new XTraceIterator(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		int index = -1;
		try {
			for (int i = 0; i < events.size(); i++) {
				if (events.get(i).equals(o)) {
					index = i;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<XEvent> listIterator() {
		return new XTraceIterator(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<XEvent> listIterator(int index) {
		return new XTraceIterator(this, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index >= 0) {
			try {
				events.remove(index);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#remove(int)
	 */
	public XEvent remove(int index) {
		try {
			XEvent result = events.remove(index);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			modified |= remove(o);
		}
		return modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		for (int i = 0; i < events.size(); i++) {
			try {
				if (c.contains(events.get(i)) == false) {
					events.remove(i);
					modified = true;
					i--;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return modified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public XEvent set(int index, XEvent event) {
		try {
			XEvent result = events.replace(event, index);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#size()
	 */
	public int size() {
		return events.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#subList(int, int)
	 */
	public List<XEvent> subList(int fromIndex, int toIndex) {
		XTraceBufferedImpl sublist = (XTraceBufferedImpl) this.clone();
		this.clear();
		for (int i = fromIndex; i < toIndex; i++) {
			try {
				sublist.add(events.get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sublist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		XEvent array[] = new XEvent[events.size()];
		for (int i = 0; i < events.size(); i++) {
			try {
				array[i] = events.get(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.List#toArray(T[])
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < events.size()) {
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), events.size());
		}
		for (int i = 0; i < events.size(); i++) {
			try {
				a[i] = (T) events.get(i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	/**
	 * Creates an identical clone of this trace.
	 */
	public Object clone() {
		XTraceBufferedImpl clone;
		try {
			clone = (XTraceBufferedImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		clone.attributes = (XAttributeMap) attributes.clone();
		clone.events = (XFastEventList) events.clone();
		return clone;
	}

	/**
	 * Trigger consolidation of this trace.
	 * 
	 * @return whether consolidation has been performed.
	 */
	public boolean consolidate() {
		try {
			return events.consolidate();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Insert the event in an ordered manner, if timestamp information is
	 * available in this trace.
	 * 
	 * @param event
	 *            the event to be inserted.
	 * @return index of the inserted event.
	 */
	public int insertOrdered(XEvent event) {
		try {
			return events.insertOrdered(event);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		events.cleanup();
	}

	/*
	 * Runs the given visitor for the given log on this trace.
	 * 
	 * (non-Javadoc)
	 * @see org.deckfour.xes.model.XTrace#accept(org.deckfour.xes.model.XVisitor, org.deckfour.xes.model.XLog)
	 */
	public void accept(XVisitor visitor, XLog log) {
		/*
		 * First call.
		 */
		visitor.visitTracePre(this, log);
		/*
		 * Visit the attributes.
		 */
		for (XAttribute attribute: attributes.values()) {
			attribute.accept(visitor, this);
		}
		/*
		 * Visit the events.
		 */
		for (XEvent event: this) {
			event.accept(visitor, this);
		}
		/*
		 * Last call.
		 */
		visitor.visitTracePost(this, log);
	}

}
