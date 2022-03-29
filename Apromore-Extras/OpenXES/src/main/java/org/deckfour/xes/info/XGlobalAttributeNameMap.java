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
package org.deckfour.xes.info;

import java.util.Collection;
import java.util.Collections;

import org.deckfour.xes.info.impl.XAttributeNameMapImpl;
import org.deckfour.xes.model.XAttribute;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

/**
 * This singleton class implements a global attribute name
 * mapping facility and can manage a number of attribute name
 * mappings.
 * 
 * Further, this class also acts as a proxy to the standard
 * mapping, i.e. it can be used directly as a attribute name
 * mapping instance.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XGlobalAttributeNameMap implements XAttributeNameMap {
	
	/**
	 * The standard attribute name mapping, to the English language (EN).
	 */
	public static final String MAPPING_STANDARD = "EN";
	
	/**
	 * The attribute name mapping to the English language.
	 */
	public static final String MAPPING_ENGLISH = "EN";
	/**
	 * The attribute name mapping to the German language.
	 */
	public static final String MAPPING_GERMAN = "DE";
	/**
	 * The attribute name mapping to the Dutch language.
	 */
	public static final String MAPPING_DUTCH = "NL";
	/**
	 * The attribute name mapping to the French language.
	 */
	public static final String MAPPING_FRENCH = "FR";
	/**
	 * The attribute name mapping to the Italian language.
	 */
	public static final String MAPPING_ITALIAN = "IT";
	/**
	 * The attribute name mapping to the Spanish language.
	 */
	public static final String MAPPING_SPANISH = "ES";
	/**
	 * The attribute name mapping to the Portuguese language.
	 */
	public static final String MAPPING_PORTUGUESE = "PT";
	
	/**
	 * Singleton instance.
	 */
	private static final XGlobalAttributeNameMap singleton = new XGlobalAttributeNameMap();
	
	/**
	 * Accesses the singleton instance.
	 * 
	 * @return The global attribute name mapping instance.
	 */
	public static XGlobalAttributeNameMap instance() {
		return singleton;
	}
	
	/**
	 * Stores attribute name mappings by their name.
	 */
	private UnifiedMap<String, XAttributeNameMapImpl> mappings;
	/**
	 * Standard mapping (EN).
	 */
	private XAttributeNameMapImpl standardMapping;
	
	/**
	 * Creates a new instance (private constructor)
	 */
	private XGlobalAttributeNameMap() {
		mappings = new UnifiedMap<String, XAttributeNameMapImpl>();
		standardMapping = new XAttributeNameMapImpl(MAPPING_STANDARD);
		mappings.put(MAPPING_STANDARD, standardMapping);
	}
	
	/**
	 * Returns the names of all available mappings. Note that
	 * referenced mappings may be empty.
	 * 
	 * @return A collection of names of all available mappings.
	 */
	public Collection<String> getAvailableMappingNames() {
		return Collections.unmodifiableCollection(mappings.keySet());
	}
	
	/**
	 * Returns all available mappings. Note that
	 * returned mappings may be empty.
	 * 
	 * @return A collection of all available mappings.
	 */
	public Collection<XAttributeNameMap> getAvailableMappings() {
		UnifiedSet<XAttributeNameMap> result = new UnifiedSet<XAttributeNameMap>();
		result.addAll(mappings.values());
		return Collections.unmodifiableCollection(result);
	}
	
	/**
	 * Provides access to a specific attribute name
	 * mapping by its name. If the requested mapping does
	 * not exist yet, a new mapping will be created, added
	 * to the set of managed mappings, and returned. This
	 * means, this method will always return a mapping,
	 * but this could be empty.
	 * 
	 * @param name Name of the requested mapping.
	 * @return The requested mapping, as stored in this
	 * 	facility (or newly created).
	 */
	public XAttributeNameMap getMapping(String name) {
		XAttributeNameMapImpl mapping = mappings.get(name);
		if(mapping == null) {
			mapping = new XAttributeNameMapImpl(name);
			mappings.put(name, mapping);
		}
		return mapping;
	}
	
	/**
	 * Retrieves the standard attribute name mapping,
	 * i.e. the EN english language mapping.
	 * 
	 * @return The standard mapping.
	 */
	public XAttributeNameMap getStandardMapping() {
		return standardMapping;
	}
	
	/**
	 * Maps an attribute safely, using the given attribute mapping.
	 * Safe mapping attempts to map the attribute using the given
	 * mapping first. If this does not succeed, the standard mapping
	 * (EN) will be used for mapping. If no mapping is available in
	 * the standard mapping, the original attribute key is returned
	 * unchanged. This way, it is always ensured that this method
	 * returns a valid string for naming attributes.
	 * 
	 * @param attribute Attribute to map.
	 * @param mapping Mapping to be used preferably.
	 * @return The safe mapping for the given attribute.
	 */
	public String mapSafely(XAttribute attribute, XAttributeNameMap mapping) {
		return mapSafely(attribute.getKey(), mapping);
	}
	
	/**
	 * Maps an attribute safely, using the given attribute mapping.
	 * Safe mapping attempts to map the attribute using the given
	 * mapping first. If this does not succeed, the standard mapping
	 * (EN) will be used for mapping. If no mapping is available in
	 * the standard mapping, the original attribute key is returned
	 * unchanged. This way, it is always ensured that this method
	 * returns a valid string for naming attributes.
	 * 
	 * @param attributeKey Key of the attribute to map.
	 * @param mapping Mapping to be used preferably.
	 * @return The safe mapping for the given attribute key.
	 */
	public String mapSafely(String attributeKey, XAttributeNameMap mapping) {
		String alias = null;
		if(mapping != null) {
			// check valid requested mapping
			alias = mapping.map(attributeKey);
		}
		if(alias == null) {
			// no match in requested mapping, try standard mapping
			alias = standardMapping.map(attributeKey);
		}
		if(alias == null) {
			// no match in standard mapping, fall back to key
			alias = attributeKey;
		}
		return alias;
	}
	
	/**
	 * Maps an attribute safely, using the given attribute mapping.
	 * Safe mapping attempts to map the attribute using the given
	 * mapping first. If this does not succeed, the standard mapping
	 * (EN) will be used for mapping. If no mapping is available in
	 * the standard mapping, the original attribute key is returned
	 * unchanged. This way, it is always ensured that this method
	 * returns a valid string for naming attributes.
	 * 
	 * @param attribute Attribute to map.
	 * @param mappingName Name of the mapping to be used preferably.
	 * @return The safe mapping for the given attribute.
	 */
	public String mapSafely(XAttribute attribute, String mappingName) {
		return mapSafely(attribute, mappings.get(mappingName));
	}
	
	/**
	 * Maps an attribute safely, using the given attribute mapping.
	 * Safe mapping attempts to map the attribute using the given
	 * mapping first. If this does not succeed, the standard mapping
	 * (EN) will be used for mapping. If no mapping is available in
	 * the standard mapping, the original attribute key is returned
	 * unchanged. This way, it is always ensured that this method
	 * returns a valid string for naming attributes.
	 * 
	 * @param attributeKey Key of the attribute to map.
	 * @param mappingName Name of the mapping to be used preferably.
	 * @return The safe mapping for the given attribute.
	 */
	public String mapSafely(String attributeKey, String mappingName) {
		return mapSafely(attributeKey, mappings.get(mappingName));
	}
	
	/**
	 * Registers a known attribute for mapping in a given attribute name
	 * map. <b>IMPORTANT:</b> This method should only be called when one
	 * intends to create, or add to, the global attribute name mapping.
	 * 
	 * @param mappingName Name of the mapping to register with.
	 * @param attributeKey Attribute key to be mapped.
	 * @param alias Alias to map the given attribute to.
	 */
	public void registerMapping(String mappingName, String attributeKey, String alias) {
		XAttributeNameMapImpl mapping = (XAttributeNameMapImpl)getMapping(mappingName);
		mapping.registerMapping(attributeKey, alias);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XAttributeNameMap#getMappingName()
	 */
	public String getMappingName() {
		return MAPPING_STANDARD;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XAttributeNameMap#map(org.deckfour.xes.model.XAttribute)
	 */
	public String map(XAttribute attribute) {
		return standardMapping.map(attribute);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XAttributeNameMap#map(java.lang.String)
	 */
	public String map(String attributeKey) {
		return standardMapping.map(attributeKey);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Global attribute name map.\n\nContained maps:\n\n");
		for(XAttributeNameMapImpl map : mappings.values()) {
			sb.append(map.toString());
			sb.append("\n\n");
		}
		return sb.toString();
	}

}
