package org.apromore.logman.attribute;

import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntLongHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

/**
 * The attribute value can be countable.
 * Two levels to count the number of value occurrences: log and trace.
 * The log level counts the total occurrences of attribute values in the whole log.
 * The trace level counts the number of occurrences of an attribute value in each trace.
 * 
 * @author Bruce Nguyen
 *
 */
public interface Countable {
	IntLongHashMap getValueLogCounts(); // attribute index => number of occurrences in the log
	IntObjectHashMap<IntArrayList> getValueTraceCounts(); // attribute index => number of occurrences in each trace 
}
