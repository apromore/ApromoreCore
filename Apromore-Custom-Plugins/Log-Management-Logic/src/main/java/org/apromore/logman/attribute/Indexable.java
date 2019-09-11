package org.apromore.logman.attribute;

import org.eclipse.collections.api.list.primitive.IntList;

/**
 * The attribute can be indexed, i.e. assigned an integer
 * 
 * @author Bruce Nguyen
 *
 */
public interface Indexable {
	IntList getIndexes();
}
