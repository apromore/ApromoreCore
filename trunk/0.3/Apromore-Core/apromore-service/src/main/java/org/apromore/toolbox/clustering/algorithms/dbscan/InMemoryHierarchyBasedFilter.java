package org.apromore.toolbox.clustering.algorithms.dbscan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Chathura Ekanayake
 */
public class InMemoryHierarchyBasedFilter {

    private static final Logger log = LoggerFactory.getLogger(InMemoryHierarchyBasedFilter.class);

    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fragmentVersionDagDao;

    private Map<String, List<String>> parentChildMap;
    private Map<String, List<String>> childParentMap;
    private ClusteringContext cc;
    private ClusterSettings settings;

    public void initialize(ClusterSettings settings, ClusteringContext cc) {
        this.settings = settings;
        this.cc = cc;

        log.debug("Loading parent child hierarchies to memory...");
        parentChildMap = fragmentVersionDagDao.getAllParentChildMappings();
        childParentMap = fragmentVersionDagDao.getAllChildParentMappings();
        log.debug("Parent child hierarchies were loaded to memory.");
    }

    private void fillAscendants(String fid, Collection<String> ascendants) {
        List<String> parents = childParentMap.get(fid);
        if (parents != null) {
            ascendants.addAll(parents);
            for (String parentId : parents) {
                fillAscendants(parentId, ascendants);
            }
        }
    }

    private void fillDecendants(String fid, Collection<String> decendants) {
        List<String> children = parentChildMap.get(fid);
        if (children != null) {
            decendants.addAll(children);
            for (String childId : children) {
                fillDecendants(childId, decendants);
            }
        }
    }

    public void removeHierarchyClusterContainments(FragmentDataObject o, List<FragmentDataObject> n)
            throws RepositoryException {

        Set<String> hierarchy = new HashSet<String>();
        fillAscendants(o.getFragmentId(), hierarchy);
        fillDecendants(o.getFragmentId(), hierarchy);
        // note that we don't want to check cluster containments of o's clusters. so we don't include o in the hierarchy

        Set<String> hierarchyClusters = new HashSet<String>();
        for (String h : hierarchy) {
            Set<String> fcids = cc.getFragmentClusterMap().get(h);
            if (fcids != null) {
                hierarchyClusters.addAll(fcids);
            }
        }

        Set<FragmentDataObject> toBeRemoved = new HashSet<FragmentDataObject>();
        for (FragmentDataObject nfo : n) {
            String nid = nfo.getFragmentId();
            Set<String> ncids = cc.getFragmentClusterMap().get(nid);
            if (ncids != null) {
                for (String ncid : ncids) {
                    if (hierarchyClusters.contains(ncid)) {
                        // we got a hierarchy containment. this neighbour has to be removed.
                        toBeRemoved.add(nfo);
                        break;
                    }
                }
            }
        }
        n.removeAll(toBeRemoved);
    }

    public Set<String> retainNearestRelatives(FragmentDataObject o, List<FragmentDataObject> n, InMemoryGEDMatrix gedFinder) throws RepositoryException {

        if (settings.isRemoveHierarchyClusterContainments()) {
            removeHierarchyClusterContainments(o, n);
        }

        log.debug("Retaining nearest relatives of " + o.getFragmentId() + " from neighbourhood size " + n.size());

        if (o.getFragmentId().equals("8080")) {
            int test1 = 10;
        }

        Set<String> allHierarchies = new HashSet<String>();
        Set<String> visitedContainedHierarchies = new HashSet<String>();
        Set<String> filteredNeighbourhood = new HashSet<String>();
        for (FragmentDataObject pickedNeighbour : n) {
            if (!visitedContainedHierarchies.contains(pickedNeighbour)) {
                Set<String> hierarchy = new HashSet<String>();
                fillAscendants(pickedNeighbour.getFragmentId(), hierarchy);
                fillDecendants(pickedNeighbour.getFragmentId(), hierarchy);
                hierarchy.add(pickedNeighbour.getFragmentId());
                allHierarchies.addAll(hierarchy);

                Set<String> containedHierarchy = new HashSet<String>();
                for (String h : hierarchy) {
                    if (contains(n, h)) {
                        containedHierarchy.add(h);
                    }
                }

                double lowestGED = Double.MAX_VALUE;
                String nearestRelative = null;
                if (containedHierarchy.size() > 1) {
                    for (String ch : containedHierarchy) {
                        double ged = gedFinder.getGED(o.getFragmentId(), ch);
                        if (ged < lowestGED) {
                            lowestGED = ged;
                            nearestRelative = ch;
                        }
                    }
                } else {
                    nearestRelative = pickedNeighbour.getFragmentId();
                }

                filteredNeighbourhood.removeAll(containedHierarchy);
                if (!visitedContainedHierarchies.contains(nearestRelative)) {
                    filteredNeighbourhood.add(nearestRelative);
                }
                visitedContainedHierarchies.addAll(containedHierarchy);
            }
        }

        retainAll(n, filteredNeighbourhood);
//		if (!n.contains(o)) {
//			n.add(o);
//		}
        log.debug("New neighbourhood of size after filtering nearest relatives: " + n.size());

        return allHierarchies;
    }

    /**
     * @param n
     * @param filteredNeighbourhood
     */
    private void retainAll(List<FragmentDataObject> n, Set<String> filteredNeighbourhood) {
        List<FragmentDataObject> toBeRemoved = new ArrayList<FragmentDataObject>();
        for (FragmentDataObject nfo : n) {
            String nfid = nfo.getFragmentId();
            if (!filteredNeighbourhood.contains(nfid)) {
                toBeRemoved.add(nfo);
            }
        }
        n.removeAll(toBeRemoved);
    }

    private boolean contains(Collection<FragmentDataObject> fs, String fid) {
        for (FragmentDataObject f : fs) {
            if (f.getFragmentId().equals(fid)) {
                return true;
            }
        }
        return false;
    }
}
