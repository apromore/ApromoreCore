/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.model;

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
