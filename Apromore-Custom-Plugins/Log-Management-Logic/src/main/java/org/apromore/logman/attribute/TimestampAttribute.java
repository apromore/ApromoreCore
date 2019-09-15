package org.apromore.logman.attribute;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

/**
 * This attribute is special as it is neither indexable nor countable
 * It would be extreme to index or count the timestamp values
 * It only keeps the start and end timestamp, i.e. an interval.
 * 
 * @author Bruce
 *
 */
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
	
	public ImmutableLongList getValues() {
		return LongArrayList.newListWith(start, end).toImmutable();
	}	
	
	public Long getValue(int index) {
		if (index == 0) {
			return this.start;
		}
		else if (index == 1) {
			return this.end;
		}
		else {
			return null;
		}
	}
	
	@Override
	public int getValueSize() {
		return -1;
	}

	@Override
	public int[] getValueIndexes() {
		return new int[] {};
	}
}
