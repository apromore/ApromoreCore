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

/**
 * Attribute represents an attribute in the whole log and stores all values found in the log
 * along with value type and attribute level (log, trace or event). 
 * It is different from XAttribute of OpenXES which represents one attribute value only.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class AbstractAttribute implements Attribute {
	private String key;
	private AttributeType type;
	private AttributeLevel level;
	
	public AbstractAttribute(String key, AttributeLevel level, AttributeType type) {
		this.key = key;
		this.type = type;
		this.level = level;
	}
	
	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public String getKeyWithLevel() {
		if (level == AttributeLevel.LOG) {
			return "(log)" + key;
		}
		else if (level == AttributeLevel.TRACE) {
			return "(case)" + key;
		}
		else {
			return key;
		}
	}
	
	@Override
	public AttributeType getType() {
		return this.type;
	}
	
	@Override
	public AttributeLevel getLevel() {
		return this.level;
	}                    
	
	// return -1 if invalid input attribute
	public abstract int registerXAttribute(XAttribute attr);
	
	@Override
	public boolean isNumeric() {
		return (this instanceof ContinuousAttribute || this instanceof DiscreteAttribute);
	}
	
	@Override
	public boolean isText() {
		return (this instanceof LiteralAttribute);
	}
	
	@Override
	public boolean isDate() {
		return (this instanceof TimestampAttribute);
	}
	
	@Override
	public boolean isListable() {
		return (this instanceof LiteralAttribute || this instanceof DiscreteAttribute ||
				this instanceof BooleanAttribute);
	} 
	
	@Override
	public boolean isIndexable() {
		return (this instanceof IndexableAttribute);
	}
	
	@Override
	public boolean isRange() {
		return (this instanceof RangeAttribute);
	} 
	
	@Override
	public int hashCode() {
		final int prime = 31;
	    int result = 1;
	    result = prime * result + ((key == null) ? 0 : key.hashCode());
	    result = prime * result + ((level == null) ? 0 : level.hashCode());
	    return result;
	}
	
	@Override 
	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			Attribute att2 = (Attribute)obj;
			return (this.key.equals(att2.getKey()) && this.level == att2.getLevel());
		}
		else {
			return false;
		}
	}
}
