package org.apromore.service.model;

import org.apromore.service.model.FDNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chathura Ekanayake
 */
public class FragmentDAG {

	private Map<String, FDNode> fragments;
	
	public FragmentDAG() {
		fragments = new HashMap<String, FDNode>();
	}
	
	public boolean contains(String fragmentId) {
		return fragments.containsKey(fragmentId);
	}
	
	public void addFragment(FDNode fdNode) {
		fragments.put(fdNode.getFragmentId(), fdNode);
	}
	
	public FDNode getFragment(String fragmentId) {
		return fragments.get(fragmentId);
	}
	
	public Set<String> getFragmentIds() {
		return fragments.keySet();
	}

    public void setFragments(Map<String, FDNode> newFragments) {
        fragments = newFragments;
    }

    public Map<String, FDNode> getFragments() {
        return fragments;
    }

	public boolean isIncluded(String fragmentId, String includedFragmentId) {
		if (fragmentId.equals(includedFragmentId)) {
			return true;
		}
		
		boolean included = false;
		List<String> childIds = getFragment(fragmentId).getChildIds();
		if (childIds.contains(includedFragmentId)) {
			included = true;
		} else {
			for (String childId : childIds) {
				included = isIncluded(childId, includedFragmentId);
				if (included) {
					break;
				}
			}
		}
		return included;
	}

	public boolean isIncluded(String fragmentId, List<String> includedFragments) {
		boolean included = true;
		for (String includedFragmentId : includedFragments) {
			if (!isIncluded(fragmentId, includedFragmentId)) {
				included = false;
				break;
			}
		}
		return included;
	}
}
