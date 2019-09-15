package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XElement;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.primitive.IntInterval;

/**
 * Attribute represents an attribute in the whole log and stores all values found in the log
 * along with value type and attribute level (log, trace or event). 
 * It is different from XAttribute of OpenXES which represents one attribute value only.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class Attribute implements Indexable {
	private String key;
	private AttributeType type;
	private AttributeLevel level;
	
	public Attribute(String key, AttributeLevel level, AttributeType type) {
		this.key = key;
		this.type = type;
		this.level = level;
	}
	
	public String getKey() {
		return key;
	}
	
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
	
	public AttributeType getType() {
		return this.type;
	}
	
	public AttributeLevel getLevel() {
		return this.level;
	}
	
	// Return null for unknown attribute type or timestamp
	public Object getObjectValue(int valueIndex) {
		if (valueIndex <0 || valueIndex > (this.getValueSize()-1)) {
			return null;
		}
		else {
			switch (this.getType()) {
				case LITERAL:
					return ((LiteralAttribute)this).getValue(valueIndex);
				case CONTINUOUS:
					return ((ContinuousAttribute)this).getValue(valueIndex);
				case DISCRETE:
					return ((DiscreteAttribute)this).getValue(valueIndex);
				case BOOLEAN:
					return ((BooleanAttribute)this).getValue(valueIndex);
				default:
					return null;
			}
		}
	}
	
	// Programs calling this method alreay know the type of the attribute being
	// used, so they should know how to use the returning object values properly.
	// This method is a generic and convenience way of returning all values of an attribute
	// but it's not as efficient as getting values from each attribute (e.g. LiteralAttribute).
	public ImmutableList<Object> getObjectValues() {
		MutableList<Object> values = Lists.mutable.ofInitialCapacity(this.getValueSize());
		for (int i=0; i<this.getValueSize()-1; i++) {
			Object value = this.getObjectValue(i);
			if (value != null) values.add(value);
		}
		return values.toImmutable();
	}
	
	// Return -1 if not found or non-indexable
	public int getValueIndex(XAttribute xatt, XElement element) {
		if (this instanceof LiteralAttribute && xatt instanceof XAttributeLiteral) {
			return this.getValueIndex(((XAttributeLiteral)xatt).getValue());
		}
		else if (this instanceof DiscreteAttribute && xatt instanceof XAttributeDiscrete) {
			return this.getValueIndex(((XAttributeDiscrete)xatt).getValue());
		}
		else if (this instanceof ContinuousAttribute && xatt instanceof XAttributeContinuous) {
			return this.getValueIndex(((XAttributeContinuous)xatt).getValue());
		}
		else if (this instanceof BooleanAttribute && xatt instanceof XAttributeBoolean) {
			return this.getValueIndex(((XAttributeBoolean)xatt).getValue());
		}
		else {
			return -1;
		}
	}
	
	public int getValueIndex(String value) {
		return (this instanceof LiteralAttribute ? ((LiteralAttribute)this).getValueIndex(value) : -1);
	}
	
	public int getValueIndex(double value) {
		return (this instanceof ContinuousAttribute ? ((ContinuousAttribute)this).getValueIndex(value) : -1);
	}
	
	public int getValueIndex(long value) {
		return (this instanceof DiscreteAttribute ? ((DiscreteAttribute)this).getValueIndex(value) : -1);
	}
	
	public int getValueIndex(boolean value) {
		return (this instanceof BooleanAttribute ? ((BooleanAttribute)this).getValueIndex(value) : -1);
	}
	
	// Return -1 if not found or not Indexable
	public int getValueIndex(XAttribute xatt) {
		if (this instanceof LiteralAttribute && xatt instanceof XAttributeLiteral) {
			return this.getValueIndex(((XAttributeLiteral)xatt).getValue());
		}
		else if (this instanceof ContinuousAttribute && xatt instanceof XAttributeContinuous) {
			return this.getValueIndex(((XAttributeLiteral)xatt).getValue());
		}
		else if (this instanceof DiscreteAttribute && xatt instanceof XAttributeDiscrete) {
			return this.getValueIndex(((XAttributeDiscrete)xatt).getValue());
		}
		else if (this instanceof BooleanAttribute && xatt instanceof XAttributeBoolean) {
			return this.getValueIndex(((XAttributeBoolean)xatt).getValue());
		}
		else {
			return -1;
		}
	}
	
	public int[] getValueIndexes() {
		return IntInterval.fromTo(0, this.getValueSize()-1).toArray();
	}
	
	public abstract int getValueSize(); 
	
	public abstract int registerXAttribute(XAttribute attr);
	
}
