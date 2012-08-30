package org.apromore.clustering.containment;

import java.util.List;

public interface ContainmentRelation {
	int getNumberOfFragments();
	String getFragmentId(int index);
	Integer getFragmentIndex(String fragId);
	boolean areInContainmentRelation(int index1, int index2);
	List<String> getRoots();
	List<String> getHierarchy(String rootFragmentId);
}
