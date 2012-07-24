/**
 *
 */
package org.apromore.toolbox.clustering.algorithms.dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura C. Ekanayake
 */
public class FragmentDataObject {

    public static final String NOISE = "NOISE";
    public static final String UNCLASSIFIED = "UNCLASSIFIED";
    public static final String UNPROCESSED = "UNPROCESSED";
    public static final String IGNORED = "IGNORED";
    public static final String CLUSTERED = "CLUSTERED";
    public static final String EXCLUDED = "EXCLUDED";

    private boolean clusterSync = false;

    private String fragmentId;
    private List<String> clusterIds;
    private String clusterStatus;
    private String keywords;
    private int size = 0;
    private int coreObjectNB = -1;

    public FragmentDataObject(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public FragmentDataObject() {
    }

    public boolean isClusterSync() {
        return clusterSync;
    }

    public void setClusterSync(boolean clusterSync) {
        this.clusterSync = clusterSync;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getClusterStatus() {
        return clusterStatus;
    }

    public void setClusterStatus(String clusterStatus) {
        this.clusterStatus = clusterStatus;
    }

    public List<String> getClusterIds() {
        return clusterIds;
    }

    public void setClusterIds(List<String> clusterIds) {
        this.clusterIds = clusterIds;
    }

    public String getClusterId() {
        if (clusterIds != null && !clusterIds.isEmpty()) {
            return clusterIds.get(0);
        } else {
            return null;
        }
    }

    public void removeClusterId(String clusterId) {
        if (clusterIds != null && !clusterIds.isEmpty()) {
            clusterIds.remove(clusterId);
        }
    }

    public void addClusterId(String clusterId) {
        if (clusterIds == null) {
            clusterIds = new ArrayList<String>();
        }

        if (!clusterIds.contains(clusterId)) {
            clusterIds.add(clusterId);
        }
    }

    public void setClusterId(String clusterId) {
        if (clusterIds == null) {
            clusterIds = new ArrayList<String>();
        }

        if (!clusterIds.contains(clusterId)) {
            clusterIds.add(clusterId);
        }
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCoreObjectNB() {
        return coreObjectNB;
    }

    public void setCoreObjectNB(int coreObjectNB) {
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
        return fragmentId;
    }
}
