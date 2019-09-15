package org.apromore.logman.attribute;

/**
 * The attribute can be indexed, i.e. assigned an integer
 * 
 * @author Bruce Nguyen
 *
 */
public interface Indexable {
	int[] getValueIndexes();
}
