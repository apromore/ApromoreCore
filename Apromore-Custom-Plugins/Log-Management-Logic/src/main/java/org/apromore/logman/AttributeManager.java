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
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.joda.time.DateTime;

/**
 * This class is used to manage all attributes in a log
 * For example, how many LiteralAttribute, ContinuousAttribute, etc.
 * How many attributes at the log and trace level. 
 * Each attribute carries the range of its domain values
 * It provides a vertical view of attributes in a log.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeManager {
	private FastList<Attribute> attributes = new FastList<Attribute>();
	
	public AttributeManager(XLog log) {
		registerXAttributes(log.getAttributes(), AttributeLevel.LOG);
		for (XTrace trace: log) {
			registerXAttributes(trace.getAttributes(), AttributeLevel.TRACE);
			for (XEvent event : trace) {
				registerXAttributes(event.getAttributes(), AttributeLevel.EVENT);
			}
		}
	}
	
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
	
	public Attribute find(String key, AttributeLevel level) {
		return attributes.detect(a -> a.getKey().equals(key) && a.getLevel() == level);
	}
	
	public int getIndex(String key, AttributeLevel level) {
		return attributes.detectIndex(a -> a.getKey().equals(key) && a.getLevel()==level);
	}
	
	public int getValueIndex(XAttribute xatt, AttributeLevel level) {
		Attribute find = this.find(xatt.getKey(), level);
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
	
	public IntList getIndexes(String key, AttributeLevel level) {
		Attribute find = this.find(key, level);
		if (find != null && find instanceof Indexable) {
			return ((Indexable)find).getIndexes();
		}
		else {
			return null;
		}
	}
	
	private void registerXAttributes(XAttributeMap attMap, AttributeLevel level) {
		for (String key : attMap.keySet()) {
			XAttribute xatt = attMap.get(key);
			if (xatt instanceof XAttributeLiteral) {
				Attribute att = this.find(xatt.getKey(), level);
				if (att == null) {
					att = AttributeFactory.createLiteralAttribute(key, level);
				}
				att.registerXAttribute(xatt);
				attributes.add(att);
			}
		}
	}
	
}
