/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.toolbox.clustering.algorithm.hac;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import org.apromore.dao.ClusterRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusterAssignment;
import org.apromore.exception.RepositoryException;
import org.apromore.service.FragmentService;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryCluster;
import org.apromore.toolbox.clustering.algorithm.dbscan.InMemoryGEDMatrix;
import org.apromore.toolbox.clustering.algorithm.hac.dendogram.InternalNode;
import org.apromore.toolbox.clustering.analyzers.ClusterAnalyzer;
import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class HACClusterer {

    private static final Logger log = LoggerFactory.getLogger(HACClusterer.class);

    private static final String PHASE1 = "Phase_1";

    private ContainmentRelation containmentRelation;
    private DissimilarityMatrix dmatrixReader;
    private ClusterAnalyzer clusterAnalyzer;
    private InMemoryGEDMatrix gedMatrix;
    private ClusterRepository clusterRepository;
    private FragmentService fragmentService;


    /**
     * Public Constructor used for because we don't implement an interface and use Proxys.
     */
    public HACClusterer() { }

    /**
     * Public Constructor used for spring wiring of objects, also used for tests.
     */
    @Inject
    public HACClusterer(final ContainmentRelation crel, final @Qualifier("dissimilarityMatrixReader") DissimilarityMatrix disMatrix,
            final ClusterAnalyzer cAnalyzer, final InMemoryGEDMatrix matrix, final ClusterRepository cRepository,
            final FragmentService fragService) {
        containmentRelation = crel;
        dmatrixReader = disMatrix;
        clusterAnalyzer = cAnalyzer;
        gedMatrix = matrix;
        clusterRepository = cRepository;
        fragmentService = fragService;
    }



    public void clusterRepository(ClusterSettings settings) throws RepositoryException {
        try {
            double maxDistance = settings.getMaxNeighborGraphEditDistance();
            containmentRelation.setMinSize(6);
            containmentRelation.initialize();
            dmatrixReader.setDissThreshold(0.45);

            HierarchicalAgglomerativeClustering clusterer = new CompleteLinkage(containmentRelation, dmatrixReader);
            clusterer.setDiameterThreshold(maxDistance);
            SortedSet<InternalNode> sources2 = clusterer.cluster();

            // now convert clusters into InMemoryCluster objects so that we can analyse them
            List<InMemoryCluster> clusters = new ArrayList<>();
            for (InternalNode inode : sources2) {
                Integer clusterId = new Random().nextInt();
                InMemoryCluster c = new InMemoryCluster(clusterId, PHASE1);

                for (Integer fid : inode.getChildren()) {
                    FragmentDataObject fd = new FragmentDataObject(fid);
                    c.addFragment(fd);
                }
                clusters.add(c);
            }

            // analyse clusters, which gives persistance bean containing cluster analysis for each cluster
            long pt1 = System.currentTimeMillis();
            log.debug("Analyzing and persisting " + clusters.size() + " clusters in the database...");
            gedMatrix.initialize(settings, null, null, null);
            clusterAnalyzer.loadFragmentSizes();
            List<Cluster> cds = new ArrayList<>();
            for (InMemoryCluster cluster : clusters) {
                Cluster cd = clusterAnalyzer.analyzeCluster(cluster, settings);
                cds.add(cd);
            }

            // if there are exact clones, remove them if the configuration says so
            if (settings.isIgnoreClustersWithExactClones()) {
                Set<Cluster> toBeRemovedCDs = new HashSet<>();
                for (Cluster cd : cds) {
                    if (cd.getStandardizingEffort() == 0) {
                        // this is a cluster with exact clones (i.e. inter-fragment distances and std effort are zero)
                        toBeRemovedCDs.add(cd);
                        clusters.remove(cd.getId());
                        log.debug("Removed cluster: " + cd.getId() + " from results as it only contains identical fragments (i.e. exact clones)");
                    }
                }
                cds.removeAll(toBeRemovedCDs);
            }

            // nor persist clusters and cluster-fragment associations
            persistClusters(cds, clusters);
            long pt2 = System.currentTimeMillis();
            long pduration = pt2 - pt1;
            log.debug("Time for persisting clusters: " + pduration);

            log.debug("Cluster persistance completed.");
        } catch (Exception e) {
            String msg = "Failed to create clusters using the HAC algorithm.";
            log.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /* TODO: Fix this class and not use this temp method. */
    private void persistClusters(final List<Cluster> cds, final Collection<InMemoryCluster> values) {
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
}
