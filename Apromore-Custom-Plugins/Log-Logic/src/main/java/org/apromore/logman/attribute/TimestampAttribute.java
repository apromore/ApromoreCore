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
import org.deckfour.xes.model.XAttributeTimestamp;

/**
 * This attribute is special as it is neither indexable nor countable
 * It would be extreme to index or count the timestamp values
 * It only keeps the start and end timestamp, i.e. an interval.
 * 
 * @author Bruce
 *
 */
public class TimestampAttribute extends AbstractAttribute implements RangeAttribute {
	private long start = Long.MAX_VALUE;
	private long end = Long.MIN_VALUE;
	
	public TimestampAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.TIMESTAMP);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeTimestamp && this.getKey().equals(att.getKey())) {
			long value = ((XAttributeTimestamp) att).getValueMillis();
			if (value < start) start = value;
			if (value > end) end = value;
			return 0;
		}
		else {
			return -1;
		}
	}
	
	@Override
	public Object getMin() {
		return (start != Long.MAX_VALUE) ? start : null;
	}
	
	@Override
	public Object getMax() {
		return (end != Long.MIN_VALUE) ? end : null;
	}
}
