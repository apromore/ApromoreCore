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
 * Copyright (c) 2012 Christian W. Guenther (christian@deckfour.org)
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
package org.deckfour.xes.extension.std;

import java.util.ArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;

/**
 * This class offers generic support for extracting and assigning values to and
 * from nested attributes.
 * 
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 * 
 */
public abstract class XAbstractNestedAttributeSupport<Type> {

	/**
	 * Abstract method to extract a value from an element.
	 * 
	 * @param element
	 *            The element to extract the value from.
	 * @return The extracted value.
	 */
	public abstract Type extractValue(XAttribute element);

	/**
	 * Abstract method to assign a value to an element.
	 * 
	 * @param element
	 *            The element to assign the value to.
	 * @param value
	 *            The value to be assigned.
	 */
	public abstract void assignValue(XAttribute element, Type value);

	/**
	 * Retrieves a map containing all values for all child attributes of an
	 * element.
	 * 
	 * For example, the XES fragment:
	 * 
	 * <pre>
	 * {@code
	 * <trace>
	 *     <string key="key.1" value="">
	 *         <float key="ext:attr" value="val.1"/>
	 *         <string key="key.1.1" value="">
	 *         	  <float key="ext:attr" value="val.1.1"/>
	 *         </string>
	 *         <string key="key.1.2" value="">
	 *         	  <float key="ext:attr" value="val.1.2"/>
	 *         </string>
	 *     </string>
	 *     <string key="key.2" value="">
	 *        <float key="ext:attr" value="val.2"/>
	 *     </string>
	 *     <string key="key.3" value="">
	 *        <float key="ext:attr" value="val.3"/>
	 *     </string>
	 * </trace>
	 * }
	 * </pre>
	 * 
	 * should result into the following:
	 * 
	 * <pre>
	 * [[key.1 val.1] [key.2 val.2] [key.3 val.3]]
	 * </pre>
	 * 
	 * @param element
	 *            Element to retrieve all values for.
	 * @return Map from all child keys to values.
	 */
	public Map<String, Type> extractValues(XAttributable element) {
		Map<String, Type> values = new UnifiedMap<String, Type>();
		Map<List<String>, Type> nestedValues = extractNestedValues(element);
		/*
		 * Now copy top-level values.
		 */
		for (List<String> keys : nestedValues.keySet()) {
			if (keys.size() == 1) {
				/*
				 * Is top-level value, as key list contains only a single key.
				 * Copy it.
				 */
				values.put(keys.get(0), nestedValues.get(keys));
			}
		}
		return values;
	}

	/**
	 * Retrieves a map containing all values for all descending attributes of an
	 * element.
	 * 
	 * For example, the XES fragment:
	 * 
	 * <pre>
	 * {@code
	 * <trace>
	 *     <string key="key.1" value="">
	 *         <float key="ext:attr" value="val.1"/>
	 *         <string key="key.1.1" value="">
	 *         	  <float key="ext:attr" value="val.1.1"/>
	 *         </string>
	 *         <string key="key.1.2" value="">
	 *         	  <float key="ext:attr" value="val.1.2"/>
	 *         </string>
	 *     </string>
	 *     <string key="key.2" value="">
	 *        <float key="ext:attr" value="val.2"/>
	 *     </string>
	 *     <string key="key.3" value="">
	 *        <float key="ext:attr" value="val.3"/>
	 *     </string>
	 * </trace>
	 * }
	 * </pre>
	 * 
	 * should result into the following:
	 * 
	 * <pre>
	 * [[[key.1] val.1] [[key.1 key.1.1] val.1.1] [[key.1 key.1.2] val.1.2] [[key.2] val.2] [[key.3] val.3]]
	 * </pre>
	 * 
	 * @param element
	 *            Element to retrieve all values for.
	 * @return Map from all descending keys to values.
	 */
	public Map<List<String>, Type> extractNestedValues(XAttributable element) {
		Map<List<String>, Type> nestedValues = new UnifiedMap<List<String>, Type>();
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> keys = new ArrayList<String>();
			keys.add(attr.getKey());
			extractNestedValuesPrivate(attr, nestedValues, keys);
		}
		return nestedValues;
	}

	/*
	 * Retrieves a map containing all values for all descending attributes of an
	 * element.
	 * 
	 * @param element Element to retrieve all values for.
	 * 
	 * @return Map from all descending keys to values.
	 */
	private void extractNestedValuesPrivate(XAttribute element,
			Map<List<String>, Type> nestedValues, List<String> keys) {
		Type value = extractValue(element);
		if (value != null) {
			nestedValues.put(keys, value);
		}
		for (XAttribute attr : element.getAttributes().values()) {
			List<String> newKeys = new ArrayList<String>(keys);
			newKeys.add(element.getKey());
			extractNestedValuesPrivate(attr, nestedValues, newKeys);
		}
	}

	/**
	 * Assigns (to the given element) multiple values given their keys. Note
	 * that as a side effect this method creates attributes when it does not
	 * find an attribute with the proper key.
	 * 
	 * For example, the call:
	 * 
	 * <pre>
	 * assignValues(event, [[key.1 val.1] [key.2 val.2] [key.3 val.3]])
	 * </pre>
	 * 
	 * should result into the following XES fragment:
	 * 
	 * <pre>
	 * {@code
	 * <event>
	 *     <string key="key.1" value="">
	 *         <float key="ext:attr" value="val.1"/>
	 *     </string>
	 *     <string key="key.2" value="">
	 *        <float key="ext:attr" value="val.2"/>
	 *     </string>
	 *     <string key="key.3" value="">
	 *        <float key="ext:attr" value="val.3"/>
	 *     </string>
	 * </event>
	 * }
	 * </pre>
	 * 
	 * @param event
	 *            Event to assign the values to.
	 * @param amounts
	 *            Mapping from keys to values which are to be assigned.
	 */
	public void assignValues(XAttributable element, Map<String, Type> values) {
		Map<List<String>, Type> nestedValues = new UnifiedMap<List<String>, Type>();
		for (String key : values.keySet()) {
			List<String> keys = new ArrayList<String>();
			keys.add(key);
			nestedValues.put(keys, values.get(key));
		}
		assignNestedValues(element, nestedValues);
	}

	/**
	 * Assigns (to the given event) multiple values given their key lists. The
	 * i-th element in the key list should correspond to an i-level attribute
	 * with the prescribed key. Note that as a side effect this method creates
	 * attributes when it does not find an attribute with the proper key.
	 * 
	 * For example, the call:
	 * 
	 * <pre>
	 * assignNestedValues(event, [[[key.1] val.1] [[key.1 key.1.1] val.1.1] [[key.1 key.1.2] val.1.2] [[key.2] val.2] [[key.3] val.3]])
	 * </pre>
	 * 
	 * should result into the following XES fragment:
	 * 
	 * <pre>
	 * {@code
	 * <event>
	 *     <string key="key.1" value="">
	 *         <float key="ext:attr" value="val.1"/>
	 *         <string key="key.1.1" value="">
	 *         	  <float key="ext:attr" value="val.1.1"/>
	 *         </string>
	 *         <string key="key.1.2" value="">
	 *         	  <float key="ext:attr" value="val.1.2"/>
	 *         </string>
	 *     </string>
	 *     <string key="key.2" value="">
	 *        <float key="ext:attr" value="val.2"/>
	 *     </string>
	 *     <string key="key.3" value="">
	 *        <float key="ext:attr" value="val.3"/>
	 *     </string>
	 * </event>
	 * }
	 * </pre>
	 * 
	 * @param element
	 *            Element to assign the values to.
	 * @param amounts
	 *            Mapping from key lists to values which are to be assigned.
	 */
	public void assignNestedValues(XAttributable element,
			Map<List<String>, Type> amounts) {
		/*
		 * Add the proper value for every key list.
		 */
		for (List<String> keys : amounts.keySet()) {
			assignNestedValuesPrivate(element, keys, amounts.get(keys));
		}
	}

	/*
	 * Assigns the given value to the attribute that can be found through the
	 * given key list. The first key corresponds to the highest-level attribute,
	 * whereas the latest key corresponds to the lowest-level attribute.
	 */
	private void assignNestedValuesPrivate(XAttributable element,
			List<String> keys, Type value) {
		if (keys.isEmpty()) {
			/*
			 * Key list is empty. Assign amount here if attribute. Else skip.
			 */
			if (element instanceof XAttribute) {
				assignValue((XAttribute) element, value);
			}
		} else {
			/*
			 * Key list not empty yet. Step down to the next attribute.
			 */
			String key = keys.get(0);
			List<String> keysTail = keys.subList(1, keys.size());
			XAttribute attr;
			if (element.getAttributes().containsKey(key)) {
				/*
				 * Attribute with given key already exists. Use it.
				 */
				attr = element.getAttributes().get(key);
			} else {
				/*
				 * Attribute with given key does not exist yet.
				 */
				attr = XFactoryRegistry.instance().currentDefault()
						.createAttributeLiteral(key, "", null);
				element.getAttributes().put(key, attr);
				/*
				 * Now it does.
				 */
			}
			/*
			 * Step down.
			 */
			assignNestedValuesPrivate(attr, keysTail, value);
		}
	}
}
