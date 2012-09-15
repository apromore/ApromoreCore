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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service("DBscanHierarchyFilter")
@Transactional(propagation = Propagation.REQUIRED)
public class InMemoryHierarchyBasedFilter {

    private static final Logger log = LoggerFactory.getLogger(InMemoryHierarchyBasedFilter.class);

    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fragmentVersionDagDao;

    private Map<Integer, List<Integer>> parentChildMap;
    private Map<Integer, List<Integer>> childParentMap;
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

    private void fillAscendants(Integer fid, Collection<Integer> ascendants) {
        List<Integer> parents = childParentMap.get(fid);
        if (parents != null) {
            ascendants.addAll(parents);
            for (Integer parentId : parents) {
                fillAscendants(parentId, ascendants);
            }
        }
    }

    private void fillDecendants(Integer fid, Collection<Integer> decendants) {
        List<Integer> children = parentChildMap.get(fid);
        if (children != null) {
            decendants.addAll(children);
            for (Integer childId : children) {
                fillDecendants(childId, decendants);
            }
        }
    }

    public void removeHierarchyClusterContainments(FragmentDataObject o, List<FragmentDataObject> n) throws RepositoryException {
        Set<Integer> hierarchy = new HashSet<Integer>();
        fillAscendants(o.getFragment().getId(), hierarchy);
        fillDecendants(o.getFragment().getId(), hierarchy);
        // note that we don't want to check cluster containments of o's clusters. so we don't include o in the hierarchy

        Set<Integer> hierarchyClusters = new HashSet<Integer>();
        for (Integer h : hierarchy) {
            Set<Integer> fcids = cc.getFragmentClusterMap().get(h);
            if (fcids != null) {
                hierarchyClusters.addAll(fcids);
            }
        }

        Set<FragmentDataObject> toBeRemoved = new HashSet<FragmentDataObject>();
        for (FragmentDataObject nfo : n) {
            Integer nid = nfo.getFragment().getId();
            Set<Integer> ncids = cc.getFragmentClusterMap().get(nid);
            if (ncids != null) {
                for (Integer ncid : ncids) {
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

    public Set<Integer> retainNearestRelatives(FragmentDataObject o, List<FragmentDataObject> n, InMemoryGEDMatrix gedFinder) throws RepositoryException {
        if (settings.isRemoveHierarchyClusterContainments()) {
            removeHierarchyClusterContainments(o, n);
        }

        log.debug("Retaining nearest relatives of " + o.getFragment().getId() + " from neighbourhood size " + n.size());

        if (o.getFragment().getId().equals("8080")) {
            int test1 = 10;
        }

        Set<Integer> allHierarchies = new HashSet<Integer>();
        Set<Integer> visitedContainedHierarchies = new HashSet<Integer>();
        Set<Integer> filteredNeighbourhood = new HashSet<Integer>();
        for (FragmentDataObject pickedNeighbour : n) {
            if (!visitedContainedHierarchies.contains(pickedNeighbour)) {
                Set<Integer> hierarchy = new HashSet<Integer>();
                fillAscendants(pickedNeighbour.getFragment().getId(), hierarchy);
                fillDecendants(pickedNeighbour.getFragment().getId(), hierarchy);
                hierarchy.add(pickedNeighbour.getFragment().getId());
                allHierarchies.addAll(hierarchy);

                Set<Integer> containedHierarchy = new HashSet<Integer>();
                for (Integer h : hierarchy) {
                    if (contains(n, h)) {
                        containedHierarchy.add(h);
                    }
                }

                double lowestGED = Double.MAX_VALUE;
                Integer nearestRelative = null;
                if (containedHierarchy.size() > 1) {
                    for (Integer ch : containedHierarchy) {
                        double ged = gedFinder.getGED(o.getFragment().getId(), ch);
                        if (ged < lowestGED) {
                            lowestGED = ged;
                            nearestRelative = ch;
                        }
                    }
                } else {
                    nearestRelative = pickedNeighbour.getFragment().getId();
                }

                filteredNeighbourhood.removeAll(containedHierarchy);
                if (!visitedContainedHierarchies.contains(nearestRelative)) {
                    filteredNeighbourhood.add(nearestRelative);
                }
                visitedContainedHierarchies.addAll(containedHierarchy);
            }
        }

        retainAll(n, filteredNeighbourhood);
        log.debug("New neighbourhood of size after filtering nearest relatives: " + n.size());

        return allHierarchies;
    }

    /**
     * @param n
     * @param filteredNeighbourhood
     */
    private void retainAll(List<FragmentDataObject> n, Set<Integer> filteredNeighbourhood) {
        List<FragmentDataObject> toBeRemoved = new ArrayList<FragmentDataObject>();
        for (FragmentDataObject nfo : n) {
            Integer nfid = nfo.getFragment().getId();
            if (!filteredNeighbourhood.contains(nfid)) {
                toBeRemoved.add(nfo);
            }
        }
        n.removeAll(toBeRemoved);
    }

    private boolean contains(Collection<FragmentDataObject> fs, Integer fid) {
        for (FragmentDataObject f : fs) {
            if (f.getFragment().getId().equals(fid)) {
                return true;
            }
        }
        return false;
    }
}
