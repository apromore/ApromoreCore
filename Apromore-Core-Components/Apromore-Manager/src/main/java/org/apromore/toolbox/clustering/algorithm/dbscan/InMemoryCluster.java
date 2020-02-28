/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

/**
 *
 */
package org.apromore.toolbox.clustering.algorithm.dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Ekanayake
 */
public class InMemoryCluster {

    private Integer clusterId;
    private String phase;
    private List<FragmentDataObject> fragments = new ArrayList<FragmentDataObject>();

    public InMemoryCluster(Integer clusterId, String phase) {
        this.clusterId = clusterId;
        this.phase = phase;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void addFragment(FragmentDataObject f) {
        f.addClusterId(clusterId);
        fragments.add(f);
    }

    public FragmentDataObject removeFirstFragment() {
        if (fragments != null && !fragments.isEmpty()) {
            FragmentDataObject f = fragments.remove(0);
            f.removeClusterId(clusterId);
            return f;
        } else {
            return null;
        }
    }

    public List<FragmentDataObject> getFragments() {
        return fragments;
    }

    public void setFragments(List<FragmentDataObject> fragments) {
        this.fragments = fragments;
        for (FragmentDataObject f : this.fragments) {
            f.addClusterId(clusterId);
        }
    }

    public boolean isEmpty() {
        return fragments == null || fragments.isEmpty();
    }
}
