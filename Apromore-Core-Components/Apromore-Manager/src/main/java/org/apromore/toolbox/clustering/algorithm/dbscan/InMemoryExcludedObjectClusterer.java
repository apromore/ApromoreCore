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

/**
 *
 */
package org.apromore.toolbox.clustering.algorithm.dbscan;

import org.apromore.common.Constants;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javax.inject.Inject;

/**
 * @author Chathura Ekanayake
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class InMemoryExcludedObjectClusterer {

    private static final Logger log = LoggerFactory.getLogger(InMemoryExcludedObjectClusterer.class);

    private InMemoryGEDMatrix inMemoryGEDMatrix;
    private InMemoryHierarchyBasedFilter inMemoryHierarchyBasedFilter;

    private int minPoints;
    private ClusterSettings settings;
    private ClusteringContext cc;

    private Map<Integer, InMemoryCluster> excludedClusters;
    private List<FragmentDataObject> excluded;
    private List<FragmentDataObject> unprocessedFragments;
    private List<Integer> allowedFragmentIds;
    private List<FragmentDataObject> ignoredFragments;
    private List<FragmentDataObject> noise;


    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public InMemoryExcludedObjectClusterer() { }

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public InMemoryExcludedObjectClusterer(final InMemoryGEDMatrix matrix, final InMemoryHierarchyBasedFilter hierarchyFilter) {
        this.inMemoryGEDMatrix = matrix;
        this.inMemoryHierarchyBasedFilter = hierarchyFilter;
    }



    public void initialize(ClusterSettings settings, ClusteringContext cc) {
        this.settings = settings;
        this.cc = cc;

        minPoints = settings.getMinPoints();
        this.unprocessedFragments = cc.getUnprocessedFragments();
        this.allowedFragmentIds = cc.getAllowedFragmentIds();
        this.ignoredFragments = cc.getIgnoredFragments();
        this.noise = cc.getNoise();
    }

    public void setGedMatrix(InMemoryGEDMatrix gedMatrix) {
        this.inMemoryGEDMatrix = gedMatrix;
    }

    public void setInMemoryHierarchyBasedFilter(InMemoryHierarchyBasedFilter inMemoryHierarchyBasedFilter) {
        this.inMemoryHierarchyBasedFilter = inMemoryHierarchyBasedFilter;
    }

    public Map<Integer, InMemoryCluster> clusterRepository(List<FragmentDataObject> excluded) throws RepositoryException {
        this.excluded = excluded;
        excludedClusters = new HashMap<>();

        while (!excluded.isEmpty()) {
            FragmentDataObject excludedFragment = excluded.remove(0);
            if (excludedFragment != null) {
                expandFromCoreObject(excludedFragment);
            }
        }
        return excludedClusters;
    }

    private void expandFromCoreObject(FragmentDataObject fo) throws RepositoryException {
        List<FragmentDataObject> n = inMemoryGEDMatrix.getCoreObjectNeighborhood(fo, allowedFragmentIds);
        Set<Integer> usedHierarchies = new HashSet<>();
        if (settings.isEnableNearestRelativeFiltering() && n != null && n.size() >= minPoints) {
            usedHierarchies = inMemoryHierarchyBasedFilter.retainNearestRelatives(fo, n, inMemoryGEDMatrix);
        }

        if (n == null || n.size() < minPoints) {
            log.error("The excluded fragment " + fo.getFragmentId() + " does not have sufficient neighbourhood to be " +
                    "a core object. It's excluded state will be cleared and the clustering will progress normally.");
            excluded.remove(fo);

        } else if (coveredByExistingCluster(n, cc.getClusters()) || coveredByExistingCluster(n, excludedClusters)) {
            log.debug("Neighbourhood of the excluded object does not contain any fragments that are not already " +
                    "clustered. The excluded object " + fo.getFragmentId() + " will not be processed further.");
            excluded.remove(fo);

        } else {
            Integer clusterId = new Random().nextInt();
            InMemoryCluster cluster = new InMemoryCluster(clusterId, Constants.PHASE2);
            excludedClusters.put(clusterId, cluster);
            expandClusterer(fo, n, cluster, usedHierarchies);
        }
    }

    /**
     * @param n
     * @param clusters
     */
    private boolean coveredByExistingCluster(List<FragmentDataObject> n, Map<Integer, InMemoryCluster> clusters) {
        boolean covered = false;
        for (InMemoryCluster c : clusters.values()) {
            if (c.getFragments().containsAll(n)) {
                covered = true;
                break;
            }
        }
        return covered;
    }

    private void expandClusterer(FragmentDataObject firstCore, List<FragmentDataObject> n, InMemoryCluster cluster,
            Set<Integer> usedHierarchies) throws RepositoryException {
        if (log.isDebugEnabled()) {
            log.debug("Expanding a cluster from the excluded core fragment: " + firstCore.getFragmentId());
        }

        List<FragmentDataObject> allClusterFragments = new ArrayList<>();

        // we should assign the neighbourhood of the first core object to the cluster before entering the loop.
        // so that the first core object is expanded.
        allClusterFragments.addAll(n);
        firstCore.setCoreObjectNB(n.size());

        Queue<FragmentDataObject> unexpandedMembers = new LinkedList<>(n);
        unexpandedMembers.remove(firstCore);
        FragmentDataObject o = unexpandedMembers.poll();
        while (o != null) {
            // if o is assigned to another cluster, we should not expand its neighbourhood for this cluster
            if (!unprocessedFragments.contains(o) && !ignoredFragments.contains(o) && !excluded.contains(o)) {
                o = unexpandedMembers.poll();
                continue;
            }

            // if o is noise, o is not a core object. so we don't have to consider o's neighbourhood
            if (noise.contains(o)) {
                o = unexpandedMembers.poll();
                continue;
            }

            // o is already in the cluster. we only need to check o's neighbourhood
            List<FragmentDataObject> n2 = inMemoryGEDMatrix.getCoreObjectNeighborhood(o, allowedFragmentIds);

            Set<Integer> n2Hierarchies;
            if (settings.isEnableNearestRelativeFiltering() && n2 != null && n2.size() >= minPoints) {
                removeAll(n2, usedHierarchies);
                if (n2.size() >= minPoints) {
                    n2Hierarchies = inMemoryHierarchyBasedFilter.retainNearestRelatives(o, n2, inMemoryGEDMatrix);
                    usedHierarchies.addAll(n2Hierarchies);
                }
            }

            if (n2 != null && n2.size() >= minPoints) {
                o.setCoreObjectNB(n2.size());
                List<FragmentDataObject> newNeighbours = new ArrayList<>();
                for (FragmentDataObject nObject : n2) {
                    if (!allClusterFragments.contains(nObject)) {
                        // nObject can be added to the cluster if it satisfies distance requirement.
                        // as we get the unshared neighbourhood, we know that it doesn't belong to any other cluster.
                        newNeighbours.add(nObject);
                    }
                }

                if (!newNeighbours.isEmpty()) {
                    if (isSatisfyCommonMedoid(newNeighbours, allClusterFragments)) {
                        unexpandedMembers.addAll(newNeighbours);
                        excluded.remove(o);
                        allClusterFragments.addAll(newNeighbours);
                    }
                } else {
                    // if there are no new neighbours, the core object neighbourhood is already included in the current cluster.
                    excluded.remove(o);
                }
            }
            o = unexpandedMembers.poll();
        }

        // it is required to run the below two statements in this order. otherwise excluded status will be cleared.
        for (FragmentDataObject fo : allClusterFragments) {
            fo.setClusterStatus(FragmentDataObject.CLUSTERED_STATUS);
            cluster.addFragment(fo);
        }
        excluded.removeAll(allClusterFragments);
    }

    private void removeAll(List<FragmentDataObject> n, Set<Integer> usedHierarchies) {
        List<FragmentDataObject> toBeRemoved = new ArrayList<>();
        for (FragmentDataObject nfo : n) {
            Integer nfid = nfo.getFragmentId();
            if (usedHierarchies.contains(nfid)) {
                toBeRemoved.add(nfo);
            }
        }
        n.removeAll(toBeRemoved);
    }

    /**
     * @param newNeighbours
     * @param allClusterFragments
     * @return
     * @throws org.apromore.exception.RepositoryException
     *
     */
    private boolean isSatisfyCommonMedoid(List<FragmentDataObject> newNeighbours, List<FragmentDataObject> allClusterFragments)
            throws RepositoryException {
        double gedThreshold = settings.getMaxNeighborGraphEditDistance();

        List<FragmentDataObject> pendingClusterFragments = new ArrayList<>(allClusterFragments);
        pendingClusterFragments.addAll(newNeighbours);

        Map<FragmentPair, Double> distances = new HashMap<>();
        for (int i = 0; i < pendingClusterFragments.size(); i++) {
            FragmentDataObject f1 = pendingClusterFragments.get(i);
            if (i + 1 < pendingClusterFragments.size()) {
                for (int j = i + 1; j < pendingClusterFragments.size(); j++) {
                    FragmentDataObject f2 = pendingClusterFragments.get(j);
                    double ged = inMemoryGEDMatrix.getGED(f1.getFragmentId(), f2.getFragmentId());
                    distances.put(new FragmentPair(f1.getFragmentId(), f2.getFragmentId()), ged);
                }
            }
        }

        //Integer medoidFragmentId;
        double maxMedoidToFragmentDistance = Double.MAX_VALUE;
        double minimumCost = Double.MAX_VALUE;
        for (FragmentDataObject f : pendingClusterFragments) {
            double[] medoidProps = computeMedoidProperties(f.getFragmentId(), distances);
            if (medoidProps[1] <= gedThreshold) {
                if (medoidProps[1] < maxMedoidToFragmentDistance) {
                    minimumCost = medoidProps[0];
                    maxMedoidToFragmentDistance = medoidProps[1];
                } else if (medoidProps[1] == maxMedoidToFragmentDistance) {
                    if (medoidProps[0] < minimumCost) {
                        minimumCost = medoidProps[0];
                        maxMedoidToFragmentDistance = medoidProps[1];
                    }
                }
            }
        }

        return maxMedoidToFragmentDistance <= gedThreshold;
    }

    private double[] computeMedoidProperties(Integer fid, Map<FragmentPair, Double> distances) {
        double totalCost = 0;
        double maxDistance = 0;
        Set<FragmentPair> pairs = distances.keySet();
        for (FragmentPair pair : pairs) {
            if (pair.hasFragment(fid)) {
                double cost = distances.get(pair);
                totalCost += cost;
                if (cost > maxDistance) {
                    maxDistance = cost;
                }
            }
        }
        return new double[]{totalCost, maxDistance};
    }

}
