/**
 *
 */
package org.apromore.toolbox.clustering.algorithms.dbscan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chathura Ekanayake
 */
public class ClusteringContext {

    private Map<String, Set<String>> fragmentClusterMap = new HashMap<String, Set<String>>();
    private List<FragmentDataObject> unprocessedFragments;
    private List<String> allowedFragmentIds;
    private List<FragmentDataObject> ignoredFragments = new ArrayList<FragmentDataObject>();
    private List<FragmentDataObject> noise = new ArrayList<FragmentDataObject>();
    private List<FragmentDataObject> excluded = new ArrayList<FragmentDataObject>();
    private Map<String, InMemoryCluster> clusters = new HashMap<String, InMemoryCluster>();

    public void mapFragmentToCluster(String fid, String cid) {
        Set<String> cids = fragmentClusterMap.get(fid);
        if (cids == null) {
            cids = new HashSet<String>();
            fragmentClusterMap.put(fid, cids);
        }
        cids.add(cid);
    }

    public List<String> getAllowedFragmentIds() {
        return allowedFragmentIds;
    }

    public void setAllowedFragmentIds(List<String> allowedFragmentIds) {
        this.allowedFragmentIds = allowedFragmentIds;
    }

    public Map<String, Set<String>> getFragmentClusterMap() {
        return fragmentClusterMap;
    }

    public void setFragmentClusterMap(Map<String, Set<String>> fragmentClusterMap) {
        this.fragmentClusterMap = fragmentClusterMap;
    }

    public List<FragmentDataObject> getUnprocessedFragments() {
        return unprocessedFragments;
    }

    public void setUnprocessedFragments(List<FragmentDataObject> unprocessedFragments) {
        this.unprocessedFragments = unprocessedFragments;
    }

    public List<FragmentDataObject> getIgnoredFragments() {
        return ignoredFragments;
    }

    public void setIgnoredFragments(List<FragmentDataObject> ignoredFragments) {
        this.ignoredFragments = ignoredFragments;
    }

    public List<FragmentDataObject> getNoise() {
        return noise;
    }

    public void setNoise(List<FragmentDataObject> noise) {
        this.noise = noise;
    }

    public List<FragmentDataObject> getExcluded() {
        return excluded;
    }

    public void setExcluded(List<FragmentDataObject> excluded) {
        this.excluded = excluded;
    }

    public Map<String, InMemoryCluster> getClusters() {
        return clusters;
    }

    public void setClusters(Map<String, InMemoryCluster> clusters) {
        this.clusters = clusters;
    }
}
