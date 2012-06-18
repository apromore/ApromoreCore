package org.apromore.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Ekanayake
 */
public class FDNode {

	private String fragmentId;
	private List<String> parentIds;
	private List<String> childIds;

    public FDNode() { }

	public FDNode(String fragmentId) {
		this.fragmentId = fragmentId;
		this.parentIds = new ArrayList<String>();
		this.childIds = new ArrayList<String>();
	}

	public String getFragmentId() {
		return fragmentId;
	}
	
	public void setFragmentId(String fragmentId) {
		this.fragmentId = fragmentId;
	}
	
	public List<String> getParentIds() {
		return parentIds;
	}
	
	public void setParentIds(List<String> parentIds) {
		this.parentIds = parentIds;
	}
	
	public List<String> getChildIds() {
		return childIds;
	}
	
	public void setChildIds(List<String> childIds) {
		this.childIds = childIds;
	}
}
