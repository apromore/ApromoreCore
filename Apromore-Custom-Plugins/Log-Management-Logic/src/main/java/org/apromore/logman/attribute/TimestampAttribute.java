package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

public class TimestampAttribute extends Attribute {
	private long start = Long.MAX_VALUE;
	private long end = Long.MIN_VALUE;
	
	public TimestampAttribute(String key, AttributeLevel level) {
		super(key, level, AttributeType.TIMESTAMP);
	}
	
	@Override
	public int registerXAttribute(XAttribute att) {
		if (att instanceof XAttributeTimestamp) {
			long value = ((XAttributeTimestamp) att).getValueMillis();
			if (value < start) start = value;
			if (value > end) end = value;
			return 0;
		}
		else {
			return -1;
		}
	}
	
	public long getStart() {
		return start;
	}
	
	public long getEnd() {
		return end;
	}
	
	public LongArrayList getValues() {
		return LongArrayList.newListWith(start, end);
	}	

}
