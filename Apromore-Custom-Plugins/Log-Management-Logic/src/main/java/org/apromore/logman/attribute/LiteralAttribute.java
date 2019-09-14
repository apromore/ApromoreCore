package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

public class LiteralAttribute extends Attribute implements Indexable {
	private FastList<String> values = new FastList<String>();
	private MutableObjectIntMap<String> indexMap = ObjectIntMaps.mutable.empty(); //to fasten the retrieval of indexes
	
	public LiteralAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.LITERAL);
	}
	
	@Override
	public int registerXAttribute(XAttribute attr) {
		if (attr instanceof XAttributeLiteral) {
			String value = ((XAttributeLiteral) attr).getValue();
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
	
	public ImmutableList<String> getValues() {
		return values.toImmutable();
	}	
	
	public int getIndex(String value) {
		return indexMap.get(value);
	}
	
	public String getValue(int index) {
		return values.get(index);
	}
	
	@Override
	public int getValueRangeSize() {
		return values.size();
	}
}
