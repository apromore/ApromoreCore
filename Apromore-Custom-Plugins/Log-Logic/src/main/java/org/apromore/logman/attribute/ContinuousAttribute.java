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
import org.deckfour.xes.model.XAttributeContinuous;
import org.eclipse.collections.api.map.primitive.MutableDoubleIntMap;
import org.eclipse.collections.impl.factory.primitive.DoubleIntMaps;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;

public class ContinuousAttribute extends AbstractAttribute implements RangeAttribute {
	private DoubleArrayList values = new DoubleArrayList();
	private MutableDoubleIntMap indexMap = DoubleIntMaps.mutable.empty(); //to fasten the retrieval of indexes
	
	public ContinuousAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.CONTINUOUS);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeContinuous && this.getKey().equals(att.getKey())) {
			double value = ((XAttributeContinuous) att).getValue();
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
