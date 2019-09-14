package org.apromore.logman;

import org.apromore.logman.attribute.Attribute;
import org.apromore.logman.attribute.AttributeFactory;
import org.apromore.logman.attribute.AttributeLevel;
import org.apromore.logman.attribute.AttributeType;
import org.apromore.logman.attribute.BooleanAttribute;
import org.apromore.logman.attribute.ContinuousAttribute;
import org.apromore.logman.attribute.DiscreteAttribute;
import org.apromore.logman.attribute.Indexable;
import org.apromore.logman.attribute.LiteralAttribute;
import org.apromore.logman.attribute.TimestampAttribute;
import org.apromore.logman.attribute.exception.WrongAttributeTypeException;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XElement;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.joda.time.DateTime;

/**
 * This class is used to manage all attributes in a log
 * For example, how many LiteralAttribute, ContinuousAttribute, etc.
 * How many attributes at the log and trace level. 
 * Each attribute carries the range of its domain values
 * It provides a vertical view of attributes in a log.
 * 
 * A coordinate to access an attribute in the AttributeStore is: attribute index and value index
 * Programs using AttributeStore should use this coordinate as 
 * they are primitive integer types which are lightweight and fast.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeStore {
	private FastList<Attribute> attributes = new FastList<Attribute>();
	private MutableObjectIntMap<Attribute> indexMap = ObjectIntMaps.mutable.empty(); //fasten attribute index retrieval
	
	public AttributeStore(XLog log) {
		registerXAttributes(log.getAttributes(), AttributeLevel.LOG);
		for (XTrace trace: log) {
			registerXAttributes(trace.getAttributes(), AttributeLevel.TRACE);
			for (XEvent event : trace) {
				registerXAttributes(event.getAttributes(), AttributeLevel.EVENT);
			}
		}
	}
	
	private void registerXAttributes(XAttributeMap attMap, AttributeLevel level) {
		for (String key : attMap.keySet()) {
			XAttribute xatt = attMap.get(key);
			Attribute att = this.getAttribute(xatt.getKey(), level);
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
			}
			att.registerXAttribute(xatt);
			attributes.add(att);
			indexMap.put(att, attributes.size()-1);
		}
	}
	
	/////////////////////// Search attributes //////////////////////////////////////////
	
	public ImmutableList<Attribute> getLogAttributes() {
		return attributes.select(a -> a.getLevel() == AttributeLevel.LOG).toImmutable();
	}
	
	public ImmutableList<Attribute> getTraceAttributes() {
		return attributes.select(a -> a.getLevel() == AttributeLevel.TRACE).toImmutable();
	}
	
	public ImmutableList<Attribute> getEventAttributes() {
		return attributes.select(a -> a.getLevel() == AttributeLevel.EVENT).toImmutable();
	}
	
	public ImmutableList<Attribute> getLiteralAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.LITERAL).toImmutable();
	}
	
	public ImmutableList<Attribute> getContinuousAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.CONTINUOUS).toImmutable();
	}
	
	public ImmutableList<Attribute> getDiscreteAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.DISCRETE).toImmutable();
	}
	
	public ImmutableList<Attribute> getBooleanAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.BOOLEAN).toImmutable();
	}
	
	public ImmutableList<Attribute> getTimestampAttributes() {
		return attributes.select(a -> a.getType() == AttributeType.TIMESTAMP).toImmutable();
	}
	
	public ImmutableList<Attribute> getAttributesWithValue(String value) {
		return this.getLiteralAttributes().select(a -> {
											LiteralAttribute att = (LiteralAttribute)a;
											return att.getValues().contains(value);
										});
	}
	
	public ImmutableList<Attribute> getAttributesWithValue(long value) {
		return this.getDiscreteAttributes().select(a -> {
											DiscreteAttribute att = (DiscreteAttribute)a;
											return att.getValues().contains(value);
										});
	}
	
	public ImmutableList<Attribute> getAttributesWithValue(double value) {
		return this.getContinuousAttributes().select(a -> {
											ContinuousAttribute att = (ContinuousAttribute)a;
											return att.getValues().contains(value);
										});
	}
	
	public ImmutableList<Attribute> getAttributesWithValue(boolean value) {
		return this.getBooleanAttributes().select(a -> {
											BooleanAttribute att = (BooleanAttribute)a;
											return att.getValues().contains(value);
										});
	}
	
	public ImmutableList<Attribute> getAttributesWithValue(DateTime value) {
		long timestamp = value.getMillis();
		return this.getTimestampAttributes().select(a -> {
											TimestampAttribute att = (TimestampAttribute)a;
											return att.getStart() <= timestamp && att.getEnd() <= timestamp;
										});
	}
	
	////////////////////////////// Access attributes using (attIndex, valueIndex) /////////////////////
	
	public Attribute getAttribute(int attIndex) {
		try {
			return attributes.get(attIndex);
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	// return null if value type is unrecognized
	public Object getValue(int attIndex, int valueIndex) {
		Attribute att = attributes.get(attIndex);
		switch (att.getType()) {
			case LITERAL: 
				LiteralAttribute att2 = (LiteralAttribute)att;
				return att2.getValue(valueIndex);
			case CONTINUOUS:
				ContinuousAttribute att3 = (ContinuousAttribute)att;
				return att3.getValue(valueIndex);
			case DISCRETE:
				DiscreteAttribute att4 = (DiscreteAttribute)att;
				return att4.getValue(valueIndex);
			case BOOLEAN:
				BooleanAttribute att5 = (BooleanAttribute)att;
				return att5.getValue(valueIndex);
			default:
				return null;
		}
		
	}
	
	public String getLiteralValue(Attribute att, int valueIndex) throws WrongAttributeTypeException {
		if (att.getType() == AttributeType.LITERAL) {
			return ((LiteralAttribute)att).getValue(valueIndex);
		}
		else {
			throw new WrongAttributeTypeException("Cannot get value of wrong attribute type. Attribute key: " + 
													att.getKey() + ", type: " + att.getLevel());
		}
	}
	
	public double getContinousValue(Attribute att, int valueIndex) throws WrongAttributeTypeException {
		if (att.getType() == AttributeType.CONTINUOUS) {
			return ((ContinuousAttribute)att).getValue(valueIndex);
		}
		else {
			throw new WrongAttributeTypeException("Cannot get value of wrong attribute type. Attribute key: " + 
													att.getKey() + ", type: " + att.getLevel());
		}
	}
	
	public double getDiscreteValue(Attribute att, int valueIndex) throws WrongAttributeTypeException {
		if (att.getType() == AttributeType.DISCRETE) {
			return ((DiscreteAttribute)att).getValue(valueIndex);
		}
		else {
			throw new WrongAttributeTypeException("Cannot get value of wrong attribute type. Attribute key: " + 
													att.getKey() + ", type: " + att.getLevel());
		}
	}
	
	public boolean getBooleanValue(Attribute att, int valueIndex) throws WrongAttributeTypeException {
		if (att.getType() == AttributeType.BOOLEAN) {
			return ((BooleanAttribute)att).getValue(valueIndex);
		}
		else {
			throw new WrongAttributeTypeException("Cannot get value of wrong attribute type. Attribute key: " + 
													att.getKey() + ", type: " + att.getLevel());
		}
	}
	
	public long[] getTimestampValue(Attribute att, int valueIndex) throws WrongAttributeTypeException {
		if (att.getType() == AttributeType.TIMESTAMP) {
			TimestampAttribute timeAtt = ((TimestampAttribute)att);
			return new long[] {timeAtt.getStart(), timeAtt.getEnd()};
		}
		else {
			throw new WrongAttributeTypeException("Cannot get value of wrong attribute type. Attribute key: " + 
													att.getKey() + ", type: " + att.getLevel());
		}
	}
	
	////////////////////////////// Access an attribute using (key, level) /////////////////////
	
	// return null if not found.
	public Attribute getAttribute(String key, AttributeLevel level) {
		return attributes.detect(a -> a.getKey().equals(key) && a.getLevel() == level);
	}
	
	//return -1 if not found
	public int getAttributeIndex(String key, AttributeLevel level) {
		return attributes.detectIndex(a -> a.getKey().equals(key) && a.getLevel()==level);
	}
	
	
	// return null if not found or Indexable
	public IntList getIndexes(String key, AttributeLevel level) {
		Attribute find = this.getAttribute(key, level);
		if (find != null && find instanceof Indexable) {
			return ((Indexable)find).getIndexes();
		}
		else {
			return null;
		}
	}
	

	//////////////////// Access attributes using OpenXES (XAttribute, XElement) ////////////////
	
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
	
	// return null if not found
	public Attribute getAttribute(XAttribute xatt, XElement element) {
		return this.getAttribute(xatt.getKey(), getLevel(element));
	}
	
	// return -1 if not found
	public int getAttributeIndex(XAttribute xatt, XElement element) {
		return attributes.detectIndex(a -> a.getKey().equals(xatt.getKey()) && a.getLevel()==getLevel(element));
	}
	
	// Return -1 if not found or not Indexable
	public int getValueIndex(XAttribute xatt, XElement element) {
		Attribute find = this.getAttribute(xatt, element);
		if (find != null && find instanceof Indexable) {
			if (find instanceof LiteralAttribute && xatt instanceof XAttributeLiteral) {
				return ((LiteralAttribute)find).getIndex(((XAttributeLiteral)xatt).getValue());
			}
			else if (find instanceof DiscreteAttribute && xatt instanceof XAttributeDiscrete) {
				return ((DiscreteAttribute)find).getIndex(((XAttributeDiscrete)xatt).getValue());
			}
			else if (find instanceof ContinuousAttribute && xatt instanceof XAttributeContinuous) {
				return ((ContinuousAttribute)find).getIndex(((XAttributeContinuous)xatt).getValue());
			}
			else if (find instanceof BooleanAttribute && xatt instanceof XAttributeBoolean) {
				return ((BooleanAttribute)find).getIndex(((XAttributeBoolean)xatt).getValue());
			}
			else {
				return -1;
			}
		}
		else {
			return -1;
		}
	}	
	
	public int getValueRangeSize(XAttribute xatt, XElement element) {
		return this.getAttribute(xatt, element).getValueRangeSize();
	}
	
	////////////////////Access a number of standard attributes ////////////////
	
	public Attribute getLogConceptName() {
		return this.getAttribute(XConceptExtension.KEY_NAME, AttributeLevel.LOG);
	}

	public Attribute getTraceConceptName() {
		return this.getAttribute(XConceptExtension.KEY_NAME, AttributeLevel.TRACE);
	}
	
	public Attribute getEventConceptName() {
		return this.getAttribute(XConceptExtension.KEY_NAME, AttributeLevel.EVENT);
	}
	
	public Attribute getEventResource() {
		return this.getAttribute(XOrganizationalExtension.KEY_RESOURCE, AttributeLevel.EVENT);
	}
	
	public Attribute getEventGroup() {
		return this.getAttribute(XOrganizationalExtension.KEY_GROUP, AttributeLevel.EVENT);
	}
	
	public Attribute getEventRole() {
		return this.getAttribute(XOrganizationalExtension.KEY_ROLE, AttributeLevel.EVENT);
	}
	
	public Attribute getEventLifecycleTransition() {
		return this.getAttribute(XLifecycleExtension.KEY_TRANSITION, AttributeLevel.EVENT);
	}
}
