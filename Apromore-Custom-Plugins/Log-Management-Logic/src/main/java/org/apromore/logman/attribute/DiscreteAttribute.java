package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.eclipse.collections.impl.factory.primitive.LongIntMaps;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

public class DiscreteAttribute extends Attribute implements Indexable {
	private LongArrayList values = new LongArrayList();
	private MutableLongIntMap indexMap = LongIntMaps.mutable.empty(); //to fasten the retrieval of indexes
	
	public DiscreteAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.DISCRETE);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeDiscrete) {
			long value = ((XAttributeDiscrete) att).getValue();
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
	
	public ImmutableLongList getValues() {
		return values.toImmutable();
	}
	
	public int getIndex(long value) {
		return indexMap.get(value);
	}
	
	public long getValue(int index) {
		return values.get(index);
	}
	
	@Override
	public int getValueRangeSize() {
		return values.size();
	}
	
}
