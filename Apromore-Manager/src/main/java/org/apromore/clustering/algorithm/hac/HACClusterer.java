package org.apromore.clustering.algorithm.hac;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.inject.Inject;

import org.apromore.clustering.containment.ContainmentRelation;
import org.apromore.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.clustering.algorithm.hac.dendogram.InternalNode;
import org.apromore.dao.ClusterRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusterAssignment;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.apromore.clustering.algorithm.dbscan.FragmentDataObject;
import org.apromore.clustering.algorithm.dbscan.InMemoryCluster;
import org.apromore.clustering.algorithm.dbscan.InMemoryGEDMatrix;
import org.apromore.clustering.analyzers.ClusterAnalyzer;
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
    private FragmentVersionRepository fragmentVersionRepository;



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
            final FragmentVersionRepository fvRepository) {
        containmentRelation = crel;
        dmatrixReader = disMatrix;
        clusterAnalyzer = cAnalyzer;
        gedMatrix = matrix;
        clusterRepository = cRepository;
        fragmentVersionRepository = fvRepository;
    }



    public void clusterRepository(ClusterSettings settings) throws RepositoryException {
        try {
            double maxDistance = settings.getMaxNeighborGraphEditDistance();
            containmentRelation.setMinSize(6);
            containmentRelation.initialize();
            dmatrixReader.initialize(containmentRelation, 0.45);

            HierarchicalAgglomerativeClustering clusterer = new CompleteLinkage(containmentRelation, dmatrixReader);
            clusterer.setDiameterThreshold(maxDistance);
            SortedSet<InternalNode> sources2 = clusterer.cluster();

            // now convert clusters into InMemoryCluster objects so that we can analyse them
            List<InMemoryCluster> clusters = new ArrayList<InMemoryCluster>();
            for (InternalNode inode : sources2) {
                Integer clusterId = new Random().nextInt();
                InMemoryCluster c = new InMemoryCluster(clusterId, PHASE1);

                for (Integer fid : inode.getChildren()) {
                    FragmentDataObject fd = new FragmentDataObject(fragmentVersionRepository.findOne(fid));
                    c.addFragment(fd);
                }
                clusters.add(c);
            }

            // analyse clusters, which gives persistance bean containing cluster analysis for each cluster
            long pt1 = System.currentTimeMillis();
            log.debug("Analyzing and persisting " + clusters.size() + " clusters in the database...");
            gedMatrix.initialize(settings, null, null, null);
            clusterAnalyzer.loadFragmentSizes();
            List<Cluster> cds = new ArrayList<Cluster>();
            for (InMemoryCluster cluster : clusters) {
                Cluster cd = clusterAnalyzer.analyzeCluster(cluster, settings);
                cds.add(cd);
            }

            // if there are exact clones, remove them if the configuration says so
            if (settings.isIgnoreClustersWithExactClones()) {
                Set<Cluster> toBeRemovedCDs = new HashSet<Cluster>();
                for (Cluster cd : cds) {
                    if (cd.getStandardizingEffort() == 0) {
                        // this is a cluster with exact clones (i.e. inter-fragment distances and std effort are zero)
                        toBeRemovedCDs.add(cd);
                        clusters.remove(cd.getId());
                        log.debug("Removed cluster: " + cd.getId() +
                                " from results as it only contains identical fragments (i.e. exact clones)");
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
                        newClusterAssignment.setFragment(f.getFragment());
                        newClusterAssignment.setCoreObjectNb(f.getCoreObjectNB());

                        cluster.addClusterAssignment(newClusterAssignment);
                    }

                    clusterRepository.save(cluster);
                }
            }
        }
    }
}
