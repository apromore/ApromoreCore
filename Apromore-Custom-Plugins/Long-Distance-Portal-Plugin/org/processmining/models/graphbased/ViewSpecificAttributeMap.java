package org.processmining.models.graphbased;

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
