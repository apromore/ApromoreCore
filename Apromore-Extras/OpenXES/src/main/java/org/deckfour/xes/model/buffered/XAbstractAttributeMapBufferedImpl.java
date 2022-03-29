/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
import java.lang.ref.WeakReference;
import java.util.Collection;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.nikefs2.NikeFS2RandomAccessStorage;
import org.deckfour.xes.nikefs2.NikeFS2StorageProvider;
import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;

/**
 * NikeFS2 virtual file system storage-based implementation for
 * the XAttributeMap interface. Makes it possible to store maps
 * of attributes on disk memory, transparent to the user and the
 * using application.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public abstract class XAbstractAttributeMapBufferedImpl implements XAttributeMap {
	
	/**
	 * The number of attributes contained in a buffer.
	 */
	private int size = 0;
	/**
	 * The random access storage to back the buffer
	 * of attributes.
	 */
	private NikeFS2RandomAccessStorage storage = null;
	/**
	 * Storage provider which is used to allocate new buffer storages.
	 */
	private NikeFS2StorageProvider provider = null;
	/**
	 * Attribute map serializer.
	 */
	private XAttributeMapSerializer serializer = null;
	/**
	 * Weak reference to cache in-heap attribute map.
	 */
	private WeakReference<XAttributeMap> cacheMap = null;
	
	
	/**
	 * Creates a new attribute map.
	 * 
	 * @param serializer The serializer used to serialize this attribute map.
	 */
	protected XAbstractAttributeMapBufferedImpl(XAttributeMapSerializer serializer) {
		this(NikeFS2VirtualFileSystem.instance(), serializer);
	}
	
	/**
	 * Creates a new attribute map.
	 * 
	 * @param provider Storage provider to use for serialization.
	 * @param serializer The serializer used to serialize this attribute map.
	 */
	protected XAbstractAttributeMapBufferedImpl(NikeFS2StorageProvider provider, 
			XAttributeMapSerializer serializer) {
		synchronized(this) {
			this.size = 0;
			this.provider = provider;
			this.serializer = serializer;
			try {
				this.storage = provider.createStorage();
			} catch (IOException e) {
				// major fuckup
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retrieves a quick-access representation of this attribute map
	 * for actual usage. De-buffers the attribute map and creates an
	 * in-memory representation which should be discarded after use
	 * to free memory.
	 * 
	 * @return In-memory copy of this attribute map.
	 */
	protected synchronized XAttributeMap deserialize() throws IOException {
		if(this.size == 0) {
			return new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class);
		} else {
			if(cacheMap != null && cacheMap.get() != null) {
				return cacheMap.get();
			} else {
				storage.seek(0);
				XAttributeMap deserialized = this.serializer.deserialize(storage);
				cacheMap = new WeakReference<XAttributeMap>(deserialized);
				return deserialized;
			}
		}
	}
	
	/**
	 * Serializes the given attribute map to a disk-buffered representation.
	 * 
	 * @param map Attribute map to be serialized.
	 */
	protected synchronized void serialize(XAttributeMap map) throws IOException {
		storage.seek(0);
		serializer.serialize(map, storage);
		cacheMap = new WeakReference<XAttributeMap>(map);
		this.size = map.size();
		map = null;
	}
		
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public synchronized void clear() {
		try {
			this.storage.close();
			this.storage = provider.createStorage();
		} catch (IOException e) {
			// major fuckup
			e.printStackTrace();
		}
		this.size = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key) {
		if(size == 0) {
			return false;
		} else {
			try {
				XAttributeMap map = deserialize();
				return map.containsKey(key);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public synchronized boolean containsValue(Object value) {
		if(size == 0) {
			return false;
		} else {
			try {
				XAttributeMap map = deserialize();
				return map.containsValue(value);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public synchronized Set<java.util.Map.Entry<String, XAttribute>> entrySet() {
		try {
			return deserialize().entrySet();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public synchronized XAttribute get(Object key) {
		if(size == 0) {
			return null;
		} else {
			try {
				XAttributeMap map = deserialize();
				return map.get(key);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public synchronized boolean isEmpty() {
		return (size == 0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public synchronized Set<String> keySet() {
		if(size == 0) {
			return new UnifiedSet<String>(0);
		} else {
			try {
				XAttributeMap map = deserialize();
				return map.keySet();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized XAttribute put(String key, XAttribute value) {
		try {
			XAttributeMap map = deserialize();
			map.put(key, value);
			serialize(map);
			return value;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public synchronized void putAll(Map<? extends String, ? extends XAttribute> t) {
		try {
			XAttributeMap map = deserialize();
			map.putAll(t);
			serialize(map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public synchronized XAttribute remove(Object key) {
		try {
			XAttributeMap map = deserialize();
			XAttribute retVal = map.remove(key);
			serialize(map);
			return retVal;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public synchronized int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public synchronized Collection<XAttribute> values() {
		try {
			XAttributeMap map = deserialize();
			return map.values();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Creates a clone, i.e. deep copy, of this attribute map.
	 */
	public synchronized Object clone() {
		try {
			XAbstractAttributeMapBufferedImpl clone = (XAbstractAttributeMapBufferedImpl)super.clone();
			clone.storage = storage.copy();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// object equality here (no use for expensive tests)
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		// clean up
		storage.close();
		storage = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// no sensible implementation
		// TODO: rethink later on!
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "XAttributeMap buffered implementation, size: " + size;
	}

}
