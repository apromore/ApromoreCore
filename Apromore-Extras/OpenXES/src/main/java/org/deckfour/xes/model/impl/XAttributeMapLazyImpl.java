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
package org.deckfour.xes.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;

/**
 * Lazy implementation of the XAttributeMap interface.
 * 
 * This implementation serves as a proxy for an XAttributeMapImpl instance,
 * which is initially not present. Once the attribute map is to be filled
 * with values, the true backing XAttributeMapImpl instance will be created
 * on the fly, and used for storing and accessing data transparently.
 * 
 * This lazy instantiation prevents lots of initializations of real
 * attribute maps, since a large amount of attributes do not have
 * any meta-attributes.
 * 
 * This class is a generic, and can be parametrized with the actual
 * implementation for the backing storage, which will then be instantiated
 * on demand. Note that you will also have to pass the Class object of
 * this implementation to the constructor, since this is required for
 * instantiation (by Java).
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XAttributeMapLazyImpl<T extends XAttributeMap> implements XAttributeMap {
	
	/**
	 * Default empty entry set used for lazy operation.
	 */
	private static final Set<java.util.Map.Entry<String, XAttribute>> EMPTY_ENTRYSET 
		= Collections.unmodifiableSet(new UnifiedSet<java.util.Map.Entry<String, XAttribute>>(0));
	
	/**
	 * Default empty key set used for lazy operation.
	 */
	private static final Set<String> EMPTY_KEYSET = 
		Collections.unmodifiableSet(new UnifiedSet<String>(0));
	
	/**
	 * Default empty entries used for lazy operation.
	 */
	private static final Collection<XAttribute> EMPTY_ENTRIES = 
		Collections.unmodifiableCollection(new ArrayList<XAttribute>(0));
	
	/**
	 * Class implementing the backing store; this is needed for initialization
	 * of generic classes, as of the Java language.
	 */
	private Class<T> backingStoreClass;
	
	
	/**
	 * Backing store, initialized lazily, i.e. on the fly.
	 */
	private T backingStore = null;
	
	/**
	 * Creates a new lazy attribute map instance.
	 * 
	 * @param implementingClass Class which should be used for
	 * 		instantiating the backing storage.
	 */
	public XAttributeMapLazyImpl(Class<T> implementingClass) {
		backingStoreClass = implementingClass;
		backingStore = null;
	}
	
	/**
	 * Returns the class used for implementing the
	 * backing store.
	 * 
	 * @return The class used for implementing the
	 * backing store.
	 */
	public Class<T> getBackingStoreClass() {
		return backingStoreClass;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public synchronized void clear() {
		backingStore = null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key) {
		if(backingStore != null) {
			return backingStore.containsKey(key);
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public synchronized boolean containsValue(Object value) {
		if(backingStore != null) {
			return backingStore.containsValue(value);
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public synchronized Set<java.util.Map.Entry<String, XAttribute>> entrySet() {
		if(backingStore != null) {
			return backingStore.entrySet();
		} else {
			return EMPTY_ENTRYSET;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public synchronized XAttribute get(Object key) {
		if(backingStore != null) {
			return backingStore.get(key);
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public synchronized boolean isEmpty() {
		if(backingStore != null) {
			return backingStore.isEmpty();
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public synchronized Set<String> keySet() {
		if(backingStore != null) {
			return backingStore.keySet();
		} else {
			return EMPTY_KEYSET;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized XAttribute put(String key, XAttribute value) {
		if(backingStore == null) {
			try {
				backingStore = backingStoreClass.newInstance();
			} catch (Exception e) {
				// Fuckup
				e.printStackTrace();
			}
		}
		return backingStore.put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public synchronized void putAll(Map<? extends String, ? extends XAttribute> t) {
		if(t.size() > 0) {
			if(backingStore == null) {
				try {
					backingStore = backingStoreClass.newInstance();
				} catch (Exception e) {
					// Fuckup
					e.printStackTrace();
				}
			}
			backingStore.putAll(t);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public synchronized XAttribute remove(Object key) {
		if(backingStore != null) {
			return backingStore.remove(key);
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public synchronized int size() {
		if(backingStore != null) {
			return backingStore.size();
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public synchronized Collection<XAttribute> values() {
		if(backingStore != null) {
			return backingStore.values();
		} else {
			return EMPTY_ENTRIES;
		}
	}
	
	/**
	 * Creates a clone, i.e. deep copy, of this lazy attribute map.
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		XAttributeMapLazyImpl<T> clone;
		try {
			clone = (XAttributeMapLazyImpl<T>)super.clone();
			if(backingStore != null) {
				clone.backingStore = (T)backingStore.clone();
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			// Fuckup!
			e.printStackTrace();
			return null;
		}
	}

}
