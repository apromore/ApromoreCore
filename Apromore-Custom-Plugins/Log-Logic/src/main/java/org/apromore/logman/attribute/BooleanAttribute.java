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
import org.deckfour.xes.model.XAttributeBoolean;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;

public class BooleanAttribute extends AbstractIndexableAttribute {
	private BooleanArrayList values = new BooleanArrayList();
	
	public BooleanAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.BOOLEAN);
	} 
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeBoolean && this.getKey().equals(att.getKey())) {
			boolean value = ((XAttributeBoolean)att).getValue();
			if (!values.contains(value)) {
				values.add(value);
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
		if (value instanceof Boolean) {
			return values.indexOf((boolean)value);
		}
		else if (value instanceof String) {
			boolean boolValue = Boolean.valueOf((String)value);
			return values.indexOf(boolValue);
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
}
