/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 *
 */
package org.apromore.toolbox.clustering.algorithm.dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class FragmentDataObject {

    public static final String NOISE_STATUS = "NOISE";
    public static final Integer NOISE = 1;

    public static final String UNCLASSIFIED_STATUS = "UNCLASSIFIED";
    public static final Integer UNCLASSIFIED = 2;

    public static final String UNPROCESSED_STATUS = "UNPROCESSED";
    public static final Integer UNPROCESSED = 3;

    public static final String IGNORED_STATUS = "IGNORED";
    public static final Integer IGNORED = 4;

    public static final String CLUSTERED_STATUS = "CLUSTERED";
    public static final Integer CLUSTERED = 5;

    public static final String EXCLUDED_STATUS = "EXCLUDED";
    public static final Integer EXCLUDED = 6;

    private boolean clusterSync = false;

    private Integer fragmentId;
    private List<Integer> clusterIds;
    private String clusterStatus;
    private String keywords;
    private int size = 0;
    private int coreObjectNB = -1;


    public FragmentDataObject() { }

    public FragmentDataObject(Integer newFragmentId) {
        this.fragmentId = newFragmentId;
    }


    public boolean isClusterSync() {
        return clusterSync;
    }

    public void setClusterSync(final boolean clusterSync) {
        this.clusterSync = clusterSync;
    }

    public Integer getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(final Integer newFragmentId) {
        this.fragmentId = newFragmentId;
    }

    public String getClusterStatus() {
        return clusterStatus;
    }

    public void setClusterStatus(final String clusterStatus) {
        this.clusterStatus = clusterStatus;
    }

    public List<Integer> getClusterIds() {
        return clusterIds;
    }

    public void setClusterIds(final List<Integer> clusterIds) {
        this.clusterIds = clusterIds;
    }

    public Integer getClusterId() {
        if (clusterIds != null && !clusterIds.isEmpty()) {
            return clusterIds.get(0);
        } else {
            return null;
        }
    }

    public void removeClusterId(final Integer clusterId) {
        if (clusterIds != null && !clusterIds.isEmpty()) {
            clusterIds.remove(clusterId);
        }
    }

    public void addClusterId(Integer clusterId) {
        if (clusterIds == null) {
            clusterIds = new ArrayList<Integer>();
        }

        if (!clusterIds.contains(clusterId)) {
            clusterIds.add(clusterId);
        }
    }

    public void setClusterId(final Integer clusterId) {
        if (clusterIds == null) {
            clusterIds = new ArrayList<Integer>();
        }

        if (!clusterIds.contains(clusterId)) {
            clusterIds.add(clusterId);
        }
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public int getCoreObjectNB() {
        return coreObjectNB;
    }

    public void setCoreObjectNB(final int coreObjectNB) {
        this.coreObjectNB = coreObjectNB;
    }

    @Override
    public int hashCode() {
        if (fragmentId == null) {
            return -1;
        } else {
            return fragmentId.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (fragmentId == null) {
            return false;
        } else if (obj instanceof FragmentDataObject) {
            FragmentDataObject f2 = (FragmentDataObject) obj;
            return fragmentId.equals(f2.getFragmentId());
        } else if (obj instanceof String) {
            String f2Id = (String) obj;
            return fragmentId.equals(f2Id);
        } else {
            return false;
        }
    }

    public String toString() {
        return fragmentId.toString();
    }
}
