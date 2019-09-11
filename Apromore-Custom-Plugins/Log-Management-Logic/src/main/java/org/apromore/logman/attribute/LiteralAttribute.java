package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

public class LiteralAttribute extends Attribute implements Indexable {
	private FastList<String> values = new FastList<String>();
	
	public LiteralAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.LITERAL);
	}
	
	@Override
	public int registerXAttribute(XAttribute attr) {
		if (attr instanceof XAttributeLiteral) {
			String value = ((XAttributeLiteral) attr).getValue();
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
	
	public ImmutableList<String> getValues() {
		return values.toImmutable();
	}	
	
	public int getIndex(String value) {
		return values.indexOf(value);
	}
	
	public String getValue(int index) {
		return values.get(index);
	}
}
