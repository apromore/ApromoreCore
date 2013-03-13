package org.apromore.toolbox.clustering.containment;

import java.util.List;

public interface ContainmentRelation {

    int getNumberOfFragments();

    Integer getFragmentId(Integer index);

    Integer getFragmentIndex(Integer fragmentId);

    int getFragmentSize(Integer fragmentId);

    boolean areInContainmentRelation(Integer index1, Integer index2);

    List<Integer> getRoots();

    List<Integer> getHierarchy(Integer rootFragmentId);

    void setMinSize(int minSize);

    void initialize() throws Exception;

}
