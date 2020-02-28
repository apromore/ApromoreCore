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

package org.apromore.toolbox.clustering.algorithm.dbscan;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.dao.ClusterRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusterAssignment;
import org.apromore.exception.RepositoryException;
import org.apromore.service.FragmentService;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.analyzers.ClusterAnalyzer;
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
public class InMemoryClusterer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryClusterer.class);

    private InMemoryGEDMatrix inMemoryGEDMatrix;
    private InMemoryExcludedObjectClusterer inMemoryExcludedObjectClusterer;
    private InMemoryHierarchyBasedFilter inMemoryHierarchyBasedFilter;
    private ClusterAnalyzer clusterAnalyzer;
    private ClusterRepository clusterRepository;
    private FragmentService fragmentService;

    private int minPoints = 4;
    private ClusterSettings settings;
    private ClusteringContext cc = null;
    private List<FragmentDataObject> unprocessedFragments;
    private List<Integer> allowedFragmentIds = null;
    private List<FragmentDataObject> ignoredFragments = null;
    private List<FragmentDataObject> noise = null;
    private List<FragmentDataObject> excluded = null;
    private Map<Integer, InMemoryCluster> clusters = null;



    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public InMemoryClusterer() { }

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public InMemoryClusterer(final InMemoryGEDMatrix gedMatrix, final InMemoryExcludedObjectClusterer excludeObjects,
            final InMemoryHierarchyBasedFilter hierarchyFilter, final ClusterAnalyzer clusterAn, final ClusterRepository cRepo,
            final FragmentService fragService) {
        inMemoryGEDMatrix = gedMatrix;
        inMemoryExcludedObjectClusterer = excludeObjects;
        inMemoryHierarchyBasedFilter = hierarchyFilter;
        clusterAnalyzer = clusterAn;
        clusterRepository = cRepo;
        fragmentService = fragService;
    }


    @Transactional(readOnly = false)
    public void clusterRepository(ClusterSettings settings) throws RepositoryException {
        LOGGER.debug("Initializing the in memory clusterer...");
        initializeForClustering(settings);

        LOGGER.debug("Starting the clustering process...");
        long t1 = System.currentTimeMillis();
        while (!unprocessedFragments.isEmpty()) {
            if(unprocessedFragments.size() % 1000 == 0) LOGGER.info("Still to process: " + unprocessedFragments.size());
            FragmentDataObject unclassifiedFragment = unprocessedFragments.remove(0);
            if (unclassifiedFragment != null) {
                if (2 < unclassifiedFragment.getSize() && unclassifiedFragment.getSize() < settings.getMaxClusteringFragmentSize()) {
                    expandFromCoreObject(unclassifiedFragment);
                } else {
                    unclassifiedFragment.setClusterStatus(FragmentDataObject.IGNORED_STATUS);
                    ignoredFragments.add(unclassifiedFragment);
                }
            }
        }

        if (settings.isEnableClusterOverlapping()) {
            Map<Integer, InMemoryCluster> excludedClusters = inMemoryExcludedObjectClusterer.clusterRepository(excluded);
            LOGGER.debug("Excluded object clusters: " + excludedClusters.size());
            clusters.putAll(excludedClusters);
        }

        long t2 = System.currentTimeMillis();
        long duration = t2 - t1;
        LOGGER.info("Time for clustering: " + duration);
        LOGGER.info("Clusters: " + clusters.size() + ", Excluded core objects: " + excluded.size());

        long pt1 = System.currentTimeMillis();
        LOGGER.info("Analyzing and persisting " + clusters.size() + " clusters in the database...");

        clusterAnalyzer.loadFragmentSizes();
        List<Cluster> cds = new ArrayList<>();
        for (InMemoryCluster cluster : clusters.values()) {
            Cluster cd = clusterAnalyzer.analyzeCluster(cluster, settings);
            cds.add(cd);
        }

        if (settings.isIgnoreClustersWithExactClones()) {
            Set<Cluster> toBeRemovedCDs = new HashSet<>();
            for (Cluster cd : cds) {
                if (cd.getStandardizingEffort() == 0) {
                    toBeRemovedCDs.add(cd);
                    clusters.remove(cd.getId());
                    LOGGER.info("Removed cluster: " + cd.getId() + " from results as it only contains identical fragments (i.e. exact clones)");
                }
            }
            cds.removeAll(toBeRemovedCDs);
        }

        buildClusters(cds, clusters.values());
        long pt2 = System.currentTimeMillis();
        long pduration = pt2 - pt1;
        LOGGER.info("Time for persisting clusters: " + pduration);
        LOGGER.debug("Cluster persistence completed.");
    }


    @Transactional(readOnly = false)
    private void buildClusters(final List<Cluster> cds, final Collection<InMemoryCluster> values) {
        ClusterAssignment newClusterAssignment;

        for (Cluster cluster : cds) {
            for (InMemoryCluster imc : values) {
                if (cluster.getId().equals(imc.getClusterId())) {
                    for (FragmentDataObject f : imc.getFragments()) {
                        newClusterAssignment = new ClusterAssignment();
                        newClusterAssignment.setCluster(cluster);
                        newClusterAssignment.setFragment(fragmentService.getFragmentVersion(f.getFragmentId()));
                        newClusterAssignment.setCoreObjectNb(f.getCoreObjectNB());

                        cluster.addClusterAssignment(newClusterAssignment);
                    }

                    clusterRepository.save(cluster);
                }
            }
        }
    }

    private void initializeForClustering(ClusterSettings settings) throws RepositoryException {
        cc = new ClusteringContext();
        ignoredFragments = cc.getIgnoredFragments();
        noise = cc.getNoise();
        excluded = cc.getExcluded();
        clusters = cc.getClusters();

        this.settings = settings;
        minPoints = settings.getMinPoints();
        if (settings.getConstrainedProcessIds() == null || settings.getConstrainedProcessIds().isEmpty()) {
            allowedFragmentIds = null;
            unprocessedFragments = fragmentService.getUnprocessedFragments();
        } else {
            unprocessedFragments = fragmentService.getUnprocessedFragmentsOfProcesses(settings.getConstrainedProcessIds());
            allowedFragmentIds = new ArrayList<>();
            for (FragmentDataObject f : unprocessedFragments) {
                allowedFragmentIds.add(f.getFragmentId());
            }
        }
        cc.setUnprocessedFragments(unprocessedFragments);
        cc.setAllowedFragmentIds(allowedFragmentIds);
        inMemoryHierarchyBasedFilter.initialize(settings, cc);
        inMemoryExcludedObjectClusterer.initialize(settings, cc);

        inMemoryGEDMatrix.initialize(settings, clusters, noise, unprocessedFragments);
    }

    private void expandFromCoreObject(FragmentDataObject fo) throws RepositoryException {
        List<FragmentDataObject> n = settings.isEnableClusterOverlapping() ?
                inMemoryGEDMatrix.getCoreObjectNeighborhood(fo, allowedFragmentIds) :
                inMemoryGEDMatrix.getUnsharedCoreObjectNeighborhood(fo, FragmentDataObject.NOISE, allowedFragmentIds);

        Set<Integer> usedHierarchies = new HashSet<>();
        if (settings.isEnableNearestRelativeFiltering() && n != null && n.size() >= minPoints) {
            usedHierarchies = inMemoryHierarchyBasedFilter.retainNearestRelatives(fo, n, inMemoryGEDMatrix);
        }

        if (n != null && n.size() >= minPoints) {
            Integer clusterId = new Random().nextInt();
            InMemoryCluster cluster = new InMemoryCluster(clusterId, Constants.PHASE1);
            clusters.put(clusterId, cluster);
            expandClusterer(fo, n, cluster, usedHierarchies);
        } else {
            fo.setClusterStatus(FragmentDataObject.NOISE_STATUS);
            noise.add(fo);
        }
    }

    private void expandClusterer(FragmentDataObject firstCore, List<FragmentDataObject> n, InMemoryCluster cluster,
            Set<Integer> usedHierarchies) throws RepositoryException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Expanding a cluster from the core fragment: " + firstCore.getFragmentId());
        }

        List<FragmentDataObject> excludedCoreObjects = new ArrayList<>();
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

            List<FragmentDataObject> n2 = settings.isEnableClusterOverlapping() ?
                    inMemoryGEDMatrix.getCoreObjectNeighborhood(o, allowedFragmentIds) :
                    inMemoryGEDMatrix.getUnsharedCoreObjectNeighborhood(o, cluster.getClusterId(), allowedFragmentIds);

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
                        if (excludedCoreObjects.contains(o)) excludedCoreObjects.remove(o);
                        allClusterFragments.addAll(newNeighbours);

                    } else {
                        excludedCoreObjects.add(o);
                    }
                } else {
                    // if there are no new neighbours, the core object neighbourhood is already included
                    // in the current cluster.
                    excluded.remove(o);
                    if (excludedCoreObjects.contains(o)) excludedCoreObjects.remove(o);
                }
            }
            o = unexpandedMembers.poll();
        }


        LOGGER.debug("Core objects to exclude: " + excludedCoreObjects.size());

        // it is required to run the below two statements in this order. otherwise excluded status will be cleared.
        for (FragmentDataObject fo : allClusterFragments) {
            fo.setClusterStatus(FragmentDataObject.CLUSTERED_STATUS);
            cluster.addFragment(fo);
            cc.mapFragmentToCluster(fo.getFragmentId(), cluster.getClusterId());
        }
        unprocessedFragments.removeAll(allClusterFragments);

        for (FragmentDataObject fo : excludedCoreObjects) {
            fo.setClusterStatus(FragmentDataObject.EXCLUDED_STATUS);
        }
        excluded.addAll(excludedCoreObjects);
    }

    /**
     * @param n FragmentDataObjects
     * @param usedHierarchies the set of used Hierarchies
     */
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
     * @param newNeighbours list of FragmentDataObjects
     * @param allClusterFragments list of FragmentDataObjects
     * @return true if the data satisfies the common medoid
     * @throws org.apromore.exception.RepositoryException
     */
    private boolean isSatisfyCommonMedoid(List<FragmentDataObject> newNeighbours, List<FragmentDataObject> allClusterFragments)
            throws RepositoryException {
        if (!settings.isEnableMergingRestriction()) {
            return true;
        }

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
