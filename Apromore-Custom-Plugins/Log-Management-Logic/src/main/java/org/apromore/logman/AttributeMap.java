package org.apromore.logman;

import org.apromore.logman.attribute.Attribute;
import org.apromore.logman.attribute.AttributeFactory;
import org.apromore.logman.attribute.AttributeLevel;
import org.apromore.logman.attribute.BooleanAttribute;
import org.apromore.logman.attribute.ContinuousAttribute;
import org.apromore.logman.attribute.DiscreteAttribute;
import org.apromore.logman.attribute.Indexable;
import org.apromore.logman.attribute.LiteralAttribute;
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
public class AttributeMap {
	private FastList<Attribute> logAttributes = new FastList<Attribute>();
	private FastList<Attribute> traceAttributes = new FastList<Attribute>();
	private FastList<Attribute> eventAttributes = new FastList<Attribute>();
	
	public AttributeMap(XLog log) {
		registerXAttributes(log.getAttributes(), AttributeLevel.LOG);
		for (XTrace trace: log) {
			registerXAttributes(trace.getAttributes(), AttributeLevel.TRACE);
			for (XEvent event : trace) {
				registerXAttributes(event.getAttributes(), AttributeLevel.EVENT);
			}
		}
	}
	
	public ImmutableList<Attribute> getLogAttributes() {
		return logAttributes.toImmutable();
	}
	
	public ImmutableList<Attribute> getTraceAttributes() {
		return traceAttributes.toImmutable();
	}
	
	public ImmutableList<Attribute> getEventAttributes() {
		return eventAttributes.toImmutable();
	}
	
	public Attribute find(String key, AttributeLevel level) {
		if (level == AttributeLevel.LOG) {
			return logAttributes.detect(a -> a.getKey().equals(key));
		}
		else if (level == AttributeLevel.TRACE) {
			return traceAttributes.detect(a -> a.getKey().equals(key));
		}
		else if (level == AttributeLevel.EVENT) {
			return eventAttributes.detect(a -> a.getKey().equals(key));
		}
		else {
			return null;
		}
	}
	
	public int getIndex(XAttribute xatt, AttributeLevel level) {
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
				Attribute attr = AttributeFactory.createLiteralAttribute(key, level);
				attr.registerXAttribute(xatt);
				if (level == AttributeLevel.LOG) {
					addAttribute(logAttributes, attr);
				}
				else if (level == AttributeLevel.TRACE) {
					addAttribute(traceAttributes, attr);
				}
				else if (level == AttributeLevel.EVENT) {
					addAttribute(eventAttributes, attr);
				}
			}
		}
	}
	
	private void addAttribute(FastList<Attribute> attributes, Attribute attr) {
		Attribute find = attributes.detect(a -> a.getKey().equals(attr.getKey()));
		if (find == null) attributes.add(attr);
	}
	
}
