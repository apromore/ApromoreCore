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

package org.apromore.toolbox.clustering.algorithm.dbscan;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class InMemoryHierarchyBasedFilter {

    private static final Logger log = LoggerFactory.getLogger(InMemoryHierarchyBasedFilter.class);

    private FragmentVersionDagRepository fragmentVersionDagRepository;

    private Map<Integer, List<Integer>> parentChildMap;
    private Map<Integer, List<Integer>> childParentMap;
    private ClusteringContext cc;
    private ClusterSettings settings;


    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public InMemoryHierarchyBasedFilter() { }

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public InMemoryHierarchyBasedFilter(final FragmentVersionDagRepository fvdRepo) {
        fragmentVersionDagRepository = fvdRepo;
    }


    public void initialize(ClusterSettings settings, ClusteringContext cc) {
        this.settings = settings;
        this.cc = cc;

        log.debug("Loading parent child hierarchies to memory...");
        parentChildMap = fragmentVersionDagRepository.getAllParentChildMappings();
        childParentMap = fragmentVersionDagRepository.getAllChildParentMappings();
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
        fillAscendants(o.getFragmentId(), hierarchy);
        fillDecendants(o.getFragmentId(), hierarchy);
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
            Integer nid = nfo.getFragmentId();
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

        log.debug("Retaining nearest relatives of " + o.getFragmentId() + " from neighbourhood size " + n.size());

        Set<Integer> allHierarchies = new HashSet<Integer>();
        Set<Integer> visitedContainedHierarchies = new HashSet<Integer>();
        Set<Integer> filteredNeighbourhood = new HashSet<Integer>();
        for (FragmentDataObject pickedNeighbour : n) {
            if (!visitedContainedHierarchies.contains(pickedNeighbour.getClusterId())) {
                Set<Integer> hierarchy = new HashSet<Integer>();
                fillAscendants(pickedNeighbour.getFragmentId(), hierarchy);
                fillDecendants(pickedNeighbour.getFragmentId(), hierarchy);
                hierarchy.add(pickedNeighbour.getFragmentId());
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
            Integer nfid = nfo.getFragmentId();
            if (!filteredNeighbourhood.contains(nfid)) {
                toBeRemoved.add(nfo);
            }
        }
        n.removeAll(toBeRemoved);
    }

    private boolean contains(Collection<FragmentDataObject> fs, Integer fid) {
        for (FragmentDataObject f : fs) {
            if (f.getFragmentId().equals(fid)) {
                return true;
            }
        }
        return false;
    }

}
