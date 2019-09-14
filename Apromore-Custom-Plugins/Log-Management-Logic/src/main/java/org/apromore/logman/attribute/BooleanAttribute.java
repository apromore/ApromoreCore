package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

public class BooleanAttribute extends Attribute implements Indexable {
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
	
	@Override
	public IntList getIndexes() {
		return IntInterval.fromTo(0, values.size()-1).toImmutable();
	}
	
	public ImmutableBooleanList getValues() {
		return values.toImmutable();
	}
	
	public int getIndex(boolean value) {
		return values.indexOf(value);
	}
	
	public boolean getValue(int index) {
		return values.get(index);
	}

	@Override
	public int getValueRangeSize() {
		return values.size();
	}

}
