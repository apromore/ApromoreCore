package org.apromore.service.helper;

import org.apromore.dao.model.FragmentVersionDag;

import java.util.List;

/**
 * @author Chathura Ekanayake
 */
public class FragmentChildMapping {

	private String fragmentId;
	
	//private Map<String, String> childMapping;
    private List<FragmentVersionDag> childMapping;

	public String getFragmentId() {
		return fragmentId;
	}

	public void setFragmentId(String fragmentId) {
		this.fragmentId = fragmentId;
	}

	public List<FragmentVersionDag>  getChildMapping() {
		return childMapping;
	}

	public void setChildMapping(List<FragmentVersionDag> childMapping) {
		this.childMapping = childMapping;
	}
}
