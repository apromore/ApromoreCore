package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableDoubleIntMap;
import org.eclipse.collections.impl.factory.primitive.DoubleIntMaps;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

public class ContinuousAttribute extends Attribute implements Indexable {
	private DoubleArrayList values = new DoubleArrayList();
	private MutableDoubleIntMap indexMap = DoubleIntMaps.mutable.empty(); //to fasten the retrieval of indexes
	
	public ContinuousAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.CONTINUOUS);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeContinuous) {
			double value = ((XAttributeContinuous) att).getValue();
			if (!indexMap.containsKey(value)) {
				values.add(value);
				indexMap.put(value, values.size()-1);
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
		return indexMap.get(value);
	}
	
	public double getValue(int index) {
		return values.get(index);
	}
	
	@Override
	public int getValueRangeSize() {
		return values.size();
	}
	
}
