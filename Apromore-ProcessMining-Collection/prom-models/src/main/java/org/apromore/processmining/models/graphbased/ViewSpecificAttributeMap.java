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
package org.apromore.processmining.models.graphbased;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViewSpecificAttributeMap {

	public static final ViewSpecificAttributeMap EMPTYMAP = new ViewSpecificAttributeMap();

	private final Map<AttributeMapOwner, AttributeMap> maps = new HashMap<AttributeMapOwner, AttributeMap>();

	public Object get(AttributeMapOwner owner, String key) {
		AttributeMap map = maps.get(owner);
		if ((map == null) || !map.containsKey(key)) {
			map = owner.getAttributeMap();
		}
		return map.get(key);

	}

	@SuppressWarnings("unchecked")
	public <T> T get(AttributeMapOwner owner, String key, T defaultValue) {
		AttributeMap map = maps.get(owner);
		if (map != null) {
			if (map.containsKey(key)) {
				Object o = map.get(key);
				if (o == null) {
					return null;
				}
				return (T) map.get(key);
			}
		}
		map = owner.getAttributeMap();
		if (map.containsKey(key)) {
			Object o = map.get(key);
			if (o == null) {
				return null;
			}
			return (T) map.get(key);
		} else {
			return defaultValue;
		}
	}

	public void clearViewSpecific(AttributeMapOwner owner) {
		AttributeMap map = maps.get(owner);
		if (map != null) {
			for (String key : map.keySet()) {
				putViewSpecific(owner, key, null);
			}
		}
		maps.remove(owner);
	}

	public Set<String> keySet(AttributeMapOwner owner) {
		Set<String> result = new HashSet<String>(owner.getAttributeMap().keySet());
		AttributeMap map = maps.get(owner);
		if (map != null) {
			result.addAll(map.keySet());
		}
		return result;
	}

	/**
	 * This method updates the map and signals the owner. The origin is passed
	 * in this update, to make sure that no unnecessary updates are performed
	 * 
	 * @param key
	 * @param value
	 * @param origin
	 * @return
	 */
	public boolean putViewSpecific(AttributeMapOwner owner, String key, Object value) {
		AttributeMap map = getMapFor(owner);
		Object old = map.get(key);
		map.put(key, value);
		if (value == old) {
			return false;
		}
		if ((value == null) || (old == null) || !value.equals(old)) {
			return true;
		}
		return false;
	}

	public void removeViewSpecific(AttributeMapOwner owner, String key) {
		AttributeMap map = maps.get(owner);
		if (map == null) {
			return;
		}
		map.remove(key);
	}

	public Set<AttributeMapOwner> keySet() {
		return maps.keySet();
	}

	public AttributeMap getMapFor(AttributeMapOwner node) {
		AttributeMap m = maps.get(node);
		if (m == null) {
			m = new AttributeMap();
			maps.put(node, m);
		}
		return m;
	}

	public ViewSpecificAttributeMap createClone() {
		ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();
		for (AttributeMapOwner owner : maps.keySet()) {
			for (String key : maps.get(owner).keySet()) {
				map.putViewSpecific(owner, key, maps.get(owner).get(key));
			}
		}
		return map;
	}
}
