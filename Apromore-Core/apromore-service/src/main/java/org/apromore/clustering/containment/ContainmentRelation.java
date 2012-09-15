package org.apromore.clustering.containment;

import java.util.List;

public interface ContainmentRelation {
    int getNumberOfFragments();

    Integer getFragmentId(int index);

    Integer getFragmentIndex(Integer fragId);

    boolean areInContainmentRelation(int index1, int index2);

    List<Integer> getRoots();

    List<Integer> getHierarchy(Integer rootFragmentId);
}
