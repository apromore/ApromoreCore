package org.apromore.service.helper;

import java.util.List;

import org.apromore.dao.model.FragmentVersionDag;

/**
 * @author Chathura Ekanayake
 */
public class FragmentChildMapping {

    private Integer fragmentId;

    //private Map<String, String> childMapping;
    private List<FragmentVersionDag> childMapping;

    public Integer getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(Integer fragmentId) {
        this.fragmentId = fragmentId;
    }

    public List<FragmentVersionDag> getChildMapping() {
        return childMapping;
    }

    public void setChildMapping(List<FragmentVersionDag> childMapping) {
        this.childMapping = childMapping;
    }
}
