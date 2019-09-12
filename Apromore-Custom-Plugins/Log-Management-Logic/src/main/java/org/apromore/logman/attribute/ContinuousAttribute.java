package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

public class ContinuousAttribute extends Attribute implements Indexable {
	private DoubleArrayList values = new DoubleArrayList();
	
	public ContinuousAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.CONTINUOUS);
	}
	
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeContinuous) {
			double value = ((XAttributeContinuous) att).getValue();
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
	
	public ImmutableDoubleList getValues() {
		return values.toImmutable();
	}
	
	public int getIndex(double value) {
		return values.indexOf(value);
	}
	
	public double getValue(int index) {
		return values.get(index);
	}
	
}
