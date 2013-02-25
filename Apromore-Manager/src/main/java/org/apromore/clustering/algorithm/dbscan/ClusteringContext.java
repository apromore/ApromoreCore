/**
 *
 */
package org.apromore.clustering.algorithm.dbscan;

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

    private Map<Integer, Set<Integer>> fragmentClusterMap = new HashMap<Integer, Set<Integer>>();
    private List<FragmentDataObject> unprocessedFragments;
    private List<Integer> allowedFragmentIds;
    private List<FragmentDataObject> ignoredFragments = new ArrayList<FragmentDataObject>();
    private List<FragmentDataObject> noise = new ArrayList<FragmentDataObject>();
    private List<FragmentDataObject> excluded = new ArrayList<FragmentDataObject>();
    private Map<Integer, InMemoryCluster> clusters = new HashMap<Integer, InMemoryCluster>();


    public void mapFragmentToCluster(Integer fid, Integer cid) {
        Set<Integer> cids = fragmentClusterMap.get(fid);
        if (cids == null) {
            cids = new HashSet<Integer>();
            fragmentClusterMap.put(fid, cids);
        }
        cids.add(cid);
    }

    public List<Integer> getAllowedFragmentIds() {
        return allowedFragmentIds;
    }

    public void setAllowedFragmentIds(List<Integer> allowedFragmentIds) {
        this.allowedFragmentIds = allowedFragmentIds;
    }

    public Map<Integer, Set<Integer>> getFragmentClusterMap() {
        return fragmentClusterMap;
    }

    public void setFragmentClusterMap(Map<Integer, Set<Integer>> fragmentClusterMap) {
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

    public Map<Integer, InMemoryCluster> getClusters() {
        return clusters;
    }

    public void setClusters(Map<Integer, InMemoryCluster> clusters) {
        this.clusters = clusters;
    }
}
