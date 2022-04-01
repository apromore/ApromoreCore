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

import org.apromore.logman.ALog;
import org.apromore.logman.ATrace;
import org.apromore.logman.Constants;
import org.deckfour.xes.model.*;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.list.primitive.IntInterval;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class is used to manage all attributes in a log. For example, how many LiteralAttribute,
 * ContinuousAttribute, or how many attributes at the log and trace level.
 * Each attribute carries the range of its domain values
 * 
 * Attribute in the AttributeStore can be obtained via three ways:
 * - Attribute index and value index: these are integers
 * - Attribute key and level: e.g. "concept:name" and event level, or "concept:name" and trace level
 * - XAttribute and XElement: XAttribute contains the key and XElement represents the level
 * 
 * Programs using the AttributeStore should use the attribute index and value index for efficient
 * storage and processing. Other ways provide convenience to get access to attribute.
 * 
 * Due to the heterogeneous nature of logs, it is usually unknown beforehand that an attribute can
 * be of any type (e.g. string, double, long). Programs using the AttributeStore should not assume
 * that an attribute is of a certain type, e.g. string, but should check the attribute type
 * (Attribute.getType) for proper processing, e.g. for display format or sorting order.
 * 
 * When getting the value of an attribute, the returning value must be checked against null for
 * unsupported attribute type (e.g. unknown data type). When getting value index from an attribute,
 * the rerutning index must be checked agaist -1 for unsupported attribute type. This is usually
 * chosen because data processing must keep going for any data types rather than throwing out
 * exceptions and then stopping (a restrictive view of data).
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeStore {
	protected MutableList<AbstractAttribute> attributes = Lists.mutable.empty();
	protected MutableObjectIntMap<AbstractAttribute> indexMap = ObjectIntMaps.mutable.empty(); //fasten attribute index retrieval
	protected MutableMap<String, AbstractAttribute> keyLevelMap = Maps.mutable.empty(); //fasten attribute retrieval based on level+key
	protected final String KEY_SEPARATOR = "@";

	public AttributeStore(ALog log) {
        registerXAttributes(log.getAttributes(), AttributeLevel.LOG);
        for (ATrace trace: log.getTraces()) {
            registerXAttributes(trace.getAttributes(), AttributeLevel.TRACE);
            for (XEvent event : trace.getEvents()) {
                registerXAttributes(event.getAttributes(), AttributeLevel.EVENT);
            }
        }
	}

	protected String getLevelKey(String key, AttributeLevel level) {
		return level.name() + KEY_SEPARATOR + key;
	}
	
	protected ImmutableSet<AbstractAttribute> registerXAttributes(XAttributeMap attMap, AttributeLevel level) {
	    MutableSet<AbstractAttribute> registeredAtts = Sets.mutable.empty();
		for (String key : attMap.keySet()) {
			XAttribute xatt = attMap.get(key);
			String levelKey = getLevelKey(xatt.getKey(), level);
			AbstractAttribute att = keyLevelMap.get(levelKey);
			if (att == null) {
				if (xatt instanceof XAttributeLiteral) {
					att = AttributeFactory.createLiteralAttribute(key, level);
				}
				else if (xatt instanceof XAttributeContinuous) {
					att = AttributeFactory.createContinuousAttribute(key, level);
				}
				else if (xatt instanceof XAttributeDiscrete) {
					att = AttributeFactory.createDiscreteAttribute(key, level);
				}
				else if (xatt instanceof XAttributeBoolean) {
					att = AttributeFactory.createBooleanAttribute(key, level);
				}
				else if (xatt instanceof XAttributeTimestamp) {
					att = AttributeFactory.createTimestampAttribute(key, level);
				}
				else {
					continue; //ignore this attribute value
				}
				
				keyLevelMap.put(levelKey, att);
				attributes.add(att);
				indexMap.put(att, attributes.size()-1);
			}
			att.registerXAttribute(xatt);
			registeredAtts.add(att);
		}
		return registeredAtts.toImmutable();
		
	}
	
	public void updateAttributeValueCount(XEvent event, boolean increase) {
	    for (AbstractAttribute att : attributes) {
	        if (att instanceof IndexableAttribute) {
	            ((IndexableAttribute) att).updateValueCount(event, increase);
	        }
	    }
	}
	
    public void updateAttributeValueCount(ATrace trace, boolean increase) {
        for (AbstractAttribute att : attributes) {
            if (att instanceof IndexableAttribute) {
                ((IndexableAttribute) att).updateValueCount(trace, increase);
            }
        }
    }
	
	
	/////////////////////// Basic methods //////////////////////////////////////////
	
	
	public AbstractAttribute getAttribute(int attIndex) {
		try {
			return attributes.get(attIndex);
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	public int[] getAttributeIndexes() {
		return attributes.isEmpty() ? (new int[] {}) : IntInterval.fromTo(0, attributes.size()-1).toArray();
	}
	
	// return null if not found.
	public AbstractAttribute getAttribute(String key, AttributeLevel level) {
		return keyLevelMap.get(getLevelKey(key, level));
	}
	
	// return null if not found
	public AbstractAttribute getAttribute(XAttribute xatt, XElement element) {
		return this.getAttribute(xatt.getKey(), getLevel(element));
	}
	
	// return -1 if not found
	public int getAttributeIndex(AbstractAttribute attribute) {
		return indexMap.getIfAbsent(attribute, -1);
	}
	
	//return -1 if not found
	public int getAttributeIndex(String key, AttributeLevel level) {
		return indexMap.getIfAbsent(getAttribute(key, level),-1);
	}
	
	// return -1 if not found
	public int getAttributeIndex(XAttribute xatt, XElement element) {
		return getAttributeIndex(xatt.getKey(), getLevel(element));
	}
	
	/////////////////////// Search attributes //////////////////////////////////////////
	
	// return empty list if not found
	public ImmutableList<AbstractAttribute> getLogAttributes() {
		return attributes.select(a -> a.getLevel() == AttributeLevel.LOG).toImmutable();
	}
	
	// return empty list if not found
	public ImmutableList<AbstractAttribute> getTraceAttributes() {
		return attributes.select(a -> a.getLevel() == AttributeLevel.TRACE).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getEventAttributes() {
		return attributes.select(a -> a.getLevel() == AttributeLevel.EVENT).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getIndexableEventAttribute() {
		return attributes.select(a -> a instanceof IndexableAttribute && a.getLevel() == AttributeLevel.EVENT).toImmutable();
	}
	
    public ImmutableList<AbstractAttribute> getIndexableEventAttributeNoBoolean() {
        return attributes.select(a -> a instanceof IndexableAttribute &&
                a.getLevel() == AttributeLevel.EVENT &&
                a.getType() != AttributeType.BOOLEAN).toImmutable();
    }
    
    public ImmutableList<AbstractAttribute> getIndexableEventAttributeWithLimits(int valueSize, Collection<String> keys) {
        return attributes.select(a -> a instanceof IndexableAttribute &&
					a.getLevel() == AttributeLevel.EVENT &&
					((IndexableAttribute)a).getValueSize() <= valueSize &&
					keys.contains(a.getKey()))
				.toImmutable();
    }
    
    public ImmutableList<AbstractAttribute> getPerspectiveEventAttributes(int valueSize, Collection<String> keys) {
        return getIndexableEventAttributeWithLimits(valueSize, keys);
    }
	
	public ImmutableList<AbstractAttribute> getLiteralAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.LITERAL).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getContinuousAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.CONTINUOUS).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getDiscreteAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.DISCRETE).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getBooleanAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.BOOLEAN).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getTimestampAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.TIMESTAMP).toImmutable();
	}
	
	public ImmutableList<AbstractAttribute> getLiteralAttributesWithValues(String...values) {
		return this.getLiteralAttributes().select(a -> {
											ImmutableList<Object> test = ((IndexableAttribute)a).getValues();
											return test.containsAll(Arrays.asList(values));
										});
	}
	
	public ImmutableList<AbstractAttribute> getDiscreteAttributesWithValues(Long...values) {
		return this.getDiscreteAttributes().select(a -> {
											ImmutableList<Object> test = ((IndexableAttribute)a).getValues();
											return test.containsAll(Arrays.asList(values));
										});
	}
	
	
	public ImmutableList<AbstractAttribute> getBooleanAttributesWithValues(Boolean...values) {
		return this.getBooleanAttributes().select(a -> {
											ImmutableList<Object> test = ((IndexableAttribute)a).getValues();
											return test.containsAll(Arrays.asList(values));
										});
	}
	
	public ImmutableList<AbstractAttribute> getContinuousAttributesWithValue(double value) {
		return this.getContinuousAttributes().select(a -> {
											ContinuousAttribute att = (ContinuousAttribute)a;
											return (double)att.getMin() <= value &&
													(double)att.getMax() >= value;
										});
	}
	
	public ImmutableList<AbstractAttribute> getTimestampAttributesWithValue(long timestamp) {
//		long timestamp = value.getMillis();
		return this.getTimestampAttributes().select(a -> {
											TimestampAttribute att = (TimestampAttribute)a;
											return (long)att.getMin() <= timestamp &&
													(long)att.getMax() >= timestamp;
										});
	}
	
	
	// Convenience to get attribute level from an XLog element
	public AttributeLevel getLevel(XElement element) {
		if (element instanceof XLog) {
			return AttributeLevel.LOG;
		}
		else if (element instanceof XTrace) {
			return AttributeLevel.TRACE;
		}
		else if (element instanceof XEvent) {
			return AttributeLevel.EVENT;
		}
		else {
			return AttributeLevel.UNKNOWN;
		}
	}
	
	// Convenience to get attribute type from an XLog element
	public AttributeType getType(XAttribute attr) {
		if (attr instanceof XAttributeLiteral) {
			return AttributeType.LITERAL;
		}
		else if (attr instanceof XAttributeContinuous) {
			return AttributeType.CONTINUOUS;
		}
		else if (attr instanceof XAttributeDiscrete) {
			return AttributeType.DISCRETE;
		}
		else if (attr instanceof XAttributeBoolean) {
			return AttributeType.BOOLEAN;
		}
		else if (attr instanceof XAttributeTimestamp) {
			return AttributeType.TIMESTAMP;
		}
		else {
			return AttributeType.UNKNOWN;
		}
	}
	
	
	
	////////////////////Access a number of standard attributes ////////////////
	
	public AbstractAttribute getStandardLogConceptName() {
		return this.getAttribute(Constants.ATT_KEY_CONCEPT_NAME, AttributeLevel.LOG);
	}

	public AbstractAttribute getStandardTraceConceptName() {
		return this.getAttribute(Constants.ATT_KEY_CONCEPT_NAME, AttributeLevel.TRACE);
	}
	
	public IndexableAttribute getStandardEventConceptName() {
		return (IndexableAttribute)this.getAttribute(Constants.ATT_KEY_CONCEPT_NAME, AttributeLevel.EVENT);
	}
	
	public IndexableAttribute getStandardEventResource() {
		return (IndexableAttribute)this.getAttribute(Constants.ATT_KEY_RESOURCE, AttributeLevel.EVENT);
	}
	
	public IndexableAttribute getStandardEventGroup() {
		return (IndexableAttribute)this.getAttribute(Constants.ATT_KEY_GROUP, AttributeLevel.EVENT);
	}
	
	public IndexableAttribute getStandardEventRole() {
		return (IndexableAttribute)this.getAttribute(Constants.ATT_KEY_ROLE, AttributeLevel.EVENT);
	}
	
	public IndexableAttribute getStandardEventLifecycleTransition() {
		return (IndexableAttribute)this.getAttribute(Constants.ATT_KEY_LIFECYCLE_TRANSITION, AttributeLevel.EVENT);
	}
	
	public Attribute getStandardEventTimestamp() {
		return this.getAttribute(Constants.ATT_KEY_TIMESTAMP, AttributeLevel.EVENT);
	}
	
	//////////////////// LOG CHARACTERISTICS ////////////////
	
	
}
