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

package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.LongIntMaps;
import org.eclipse.collections.impl.factory.primitive.LongLists;


public class DiscreteAttribute extends AbstractIndexableAttribute implements RangeAttribute {
	private MutableLongList values = LongLists.mutable.empty();
	private MutableLongIntMap indexMap = LongIntMaps.mutable.empty(); //to fasten the retrieval of indexes
	
	public DiscreteAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.DISCRETE);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeDiscrete && this.getKey().equals(att.getKey())) {
			long value = ((XAttributeDiscrete) att).getValue();
			if (!indexMap.containsKey(value)) {
				values.add(value);
				indexMap.put(value, values.size()-1);
				return (values.size()-1);
			}
			else {
				return -1;
			}
		}
		else {
			return -1;
		}
	}

	@Override
	public ImmutableList<Object> getValues() {
		MutableList<Object> objects = Lists.mutable.ofInitialCapacity(values.size());
		values.forEach(a -> objects.add(a));
		return objects.toImmutable();
	}
	
	@Override
	public int getValueIndex(Object value) {
		if (value instanceof Integer || value instanceof Long || value instanceof Byte || value instanceof Short) {
			return indexMap.getIfAbsent(((Number)value).longValue(), -1);
		}
		else if (value instanceof String) {
			try {
				Long longValue = Long.valueOf((String)value);
				return indexMap.getIfAbsent(longValue, -1);
			}
			catch (NumberFormatException ex) {
				return -1;
			}
		}
		else {
			return -1;
		}
		
	}
	
	@Override
	public Object getValue(int index) {
	    if (index < 0 || index >= values.size()) return null;
		return values.get(index);
	}
	
	@Override
	public int getValueSize() {
		return values.size();
	}
	
	@Override
	public Object getMin() {
		return values.isEmpty() ? null : values.min();
	}
	
	@Override
	public Object getMax() {
		return values.isEmpty() ? null : values.max();
	}
	
	@Override
	public String toString() {
		String toString = "";
		for (int i=0; i<values.size(); i++) {
			toString += "(" + i + ")" + values.get(i) + ", ";
		}
		return toString.substring(0, toString.length()-2);
	}
}
