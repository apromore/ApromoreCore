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
package org.deckfour.xes.info.impl;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.info.XAttributeInfo;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.util.XAttributeUtils;

/**
 * This class provides aggregate information about attributes
 * within one container in the log type hierarchy. For example,
 * it may store information about all event attributes in a 
 * log.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XAttributeInfoImpl implements XAttributeInfo {
	
	/**
	 * Mapping from attribute keys to attribute prototypes.
	 */
	private Map<String, XAttribute> keyMap;
	/**
	 * Mapping from attribute types to attribute prototypes.
	 */
	private Map<Class<? extends XAttribute>, Set<XAttribute>> typeMap;
	/**
	 * Mapping from attribute extensions to attribute prototypes.
	 */
	private Map<XExtension, Set<XAttribute>> extensionMap;
	/**
	 * Attribute prototypes for non-extension attributes.
	 */
	private Set<XAttribute> noExtensionSet;
	/**
	 * Mapping from attribute keys to absolute frequency.
	 */
	private Map<String, Integer> frequencies;
	/**
	 * Total absolute frequency of all registered attributes.
	 */
	private int totalFrequency;
	
	/**
	 * Creates a new attribute information registry.
	 */
	public XAttributeInfoImpl() {
		keyMap = new UnifiedMap<String, XAttribute>();
		frequencies = new UnifiedMap<String, Integer>();
		typeMap = new UnifiedMap<Class<? extends XAttribute>, Set<XAttribute>>();
		extensionMap = new UnifiedMap<XExtension, Set<XAttribute>>();
		noExtensionSet = new UnifiedSet<XAttribute>();
		totalFrequency = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getAttributes()
	 */
	public Collection<XAttribute> getAttributes() {
		return Collections.unmodifiableCollection(keyMap.values());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getAttributeKeys()
	 */
	public Collection<String> getAttributeKeys() {
		return Collections.unmodifiableCollection(keyMap.keySet());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getFrequency(java.lang.String)
	 */
	public int getFrequency(String key) {
		return frequencies.get(key);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getFrequency(org.deckfour.xes.model.XAttribute)
	 */
	public int getFrequency(XAttribute attribute) {
		return getFrequency(attribute.getKey());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getRelativeFrequency(java.lang.String)
	 */
	public double getRelativeFrequency(String key) {
		return (double)frequencies.get(key) / (double)totalFrequency;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getRelativeFrequency(org.deckfour.xes.model.XAttribute)
	 */
	public double getRelativeFrequency(XAttribute attribute) {
		return getRelativeFrequency(attribute.getKey());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getAttributesForType(org.deckfour.xes.model.XAttribute.Type)
	 */
	public Collection<XAttribute> getAttributesForType(Class<? extends XAttribute> type) {
		Set<XAttribute> typeSet = typeMap.get(type);
		if(typeSet == null) {
			typeSet = new UnifiedSet<XAttribute>(0);
		}
		return Collections.unmodifiableCollection(typeSet);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getKeysForType(org.deckfour.xes.model.XAttribute.Type)
	 */
	public Collection<String> getKeysForType(Class<? extends XAttribute> type) {
		Collection<XAttribute> typeCollection = getAttributesForType(type);
		Set<String> keySet = new UnifiedSet<String>();
		for(XAttribute attribute : typeCollection) {
			keySet.add(attribute.getKey());
		}
		return Collections.unmodifiableCollection(keySet);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getAttributesForExtension(org.deckfour.xes.extension.XExtension)
	 */
	public Collection<XAttribute> getAttributesForExtension(XExtension extension) {
		if(extension == null) {
			return getAttributesWithoutExtension();
		} else {
			Set<XAttribute> extensionSet = extensionMap.get(extension);
			if(extensionSet == null) {
				extensionSet = new UnifiedSet<XAttribute>(0);
			}
			return Collections.unmodifiableCollection(extensionSet);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getKeysForExtension(org.deckfour.xes.extension.XExtension)
	 */
	public Collection<String> getKeysForExtension(XExtension extension) {
		Collection<XAttribute> extensionCollection = getAttributesForExtension(extension);
		Set<String> keySet = new UnifiedSet<String>();
		for(XAttribute attribute : extensionCollection) {
			keySet.add(attribute.getKey());
		}
		return Collections.unmodifiableCollection(keySet);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getAttributesWithoutExtension()
	 */
	public Collection<XAttribute> getAttributesWithoutExtension() {
		return Collections.unmodifiableCollection(noExtensionSet);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XAttributeInfo#getKeysWithoutExtension()
	 */
	public Collection<String> getKeysWithoutExtension() {
		return getKeysForExtension(null);
	}
	
	/**
	 * Registers a concrete attribute with this registry.
	 * 
	 * @param attribute Attribute to be registered.
	 */
	public void register(XAttribute attribute) {
		if(keyMap.containsKey(attribute.getKey()) == false) {
			// create new attribute prototype
			XAttribute prototype = XAttributeUtils.derivePrototype(attribute);
			// add to main map
			keyMap.put(attribute.getKey(), prototype);
			// initialize frequency
			frequencies.put(attribute.getKey(), 1);
			// register with type map
			Set<XAttribute> typeSet = typeMap.get(XAttributeUtils.getType(prototype));
			if(typeSet == null) {
				typeSet = new UnifiedSet<XAttribute>();
				typeMap.put(XAttributeUtils.getType(prototype), typeSet);
			}
			typeSet.add(prototype);
			// register with extension map
			if(attribute.getExtension() == null) {
				// non-extension attribute
				noExtensionSet.add(prototype);
			} else {
				// register with extension map
				Set<XAttribute> extensionSet = extensionMap.get(attribute.getExtension());
				if(extensionSet == null) {
					extensionSet = new UnifiedSet<XAttribute>();
					extensionMap.put(attribute.getExtension(), extensionSet);
				}
				extensionSet.add(prototype);
			}
		} else {
			// adjust frequency
			frequencies.put(attribute.getKey(), frequencies.get(attribute.getKey()) + 1);
		}
		// adjust total frequency
		totalFrequency++;
	}

}
