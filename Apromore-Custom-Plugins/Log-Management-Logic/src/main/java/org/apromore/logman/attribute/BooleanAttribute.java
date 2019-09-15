package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;

public class BooleanAttribute extends Attribute {
	private BooleanArrayList values = new BooleanArrayList();
	
	public BooleanAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.BOOLEAN);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeBoolean) {
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
	
	public ImmutableBooleanList getValues() {
		return values.toImmutable();
	}
	
	public int getValueIndex(boolean value) {
		return values.indexOf(value);
	}
	
	public boolean getValue(int index) {
		return values.get(index);
	}

	@Override
	public int getValueSize() {
		return values.size();
	}

}
