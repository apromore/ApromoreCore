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
import org.deckfour.xes.model.XAttributeLiteral;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;

public class LiteralAttribute extends AbstractIndexableAttribute {
	private MutableList<String> values = Lists.mutable.empty();
	// value => index
	private MutableObjectIntMap<String> indexMap = ObjectIntMaps.mutable.empty(); //to boost the retrieval of indexes
	
	public LiteralAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.LITERAL);
	}
	
	@Override
	public int registerXAttribute(XAttribute attr) {
		if (attr instanceof XAttributeLiteral && this.getKey().equals(attr.getKey())) {
			String value = ((XAttributeLiteral) attr).getValue();
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
        return Lists.immutable.of(values.toArray());
    }   
	
	@Override
	public int getValueIndex(Object value) {
		return (value instanceof String) ? indexMap.getIfAbsent(value, -1) : -1;
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
	public String toString() {
		String toString = "";
		for (int i=0; i<values.size(); i++) {
			toString += "(" + i + ")" + values.get(i) + ", ";
		}
		return toString.substring(0, toString.length()-2);
	}


}
