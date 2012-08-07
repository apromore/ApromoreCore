/**
 *
 */
package org.apromore.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryCluster;

/**
 * Interface domain model Data access object Clustering.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 * @version 2.0
 * @see org.apromore.dao.model.Cluster
 * @see org.apromore.dao.model.ClusteringSummary
 * @see org.apromore.dao.model.ClusterAssignment
 * @see org.apromore.dao.model.FragmentDistance
 */
public interface ClusteringDao {

    /**
     * Find all the clusters.
     * @return the list of clusters
     */
    List<Cluster> getAllClusters();

    /**
     * find clusters by a filter.
     * @param filter the cluster filter
     * @return the list of found clusters.
     */
    List<Cluster> getFilteredClusters(ClusterFilter filter);

    /**
     * the fragments contained in a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    List<String> getFragmentIds(String clusterId);

    /**
     * find a fragments of a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    List<FragmentVersion> getFragments(String clusterId);

    /**
     * find the distance between two fragments.
     * @param fragmentId1 fragment one
     * @param fragmentId2 fragment two
     * @return the distance between the two
     */
    double getDistance(String fragmentId1, String fragmentId2);

    /**
     * find the fragment pairs within a certain distance.
     * @param threshold the fragment distance threshold
     * @return the lsit of fragment pairs and their distance.
     */
    Map<FragmentPair, Double> getDistances(final double threshold);

    /**
     * insert the distances.
     * @param dissimmap  the multi key map
     */
    void insertDistances(MultiKeyMap dissimmap);

    /**
     * Find all unprocessed fragments.
     * @return the list of unprocessed fragments.
     */
    List<FragmentDataObject> getUnprocessedFragments();

    /**
     * Find all unprocessed fragments of a process.
     * @param processIds the process id
     * @return the list of unprocessed fragments.
     */
    List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(List<Integer> processIds);

    /**
     * Get the clustering Summary.
     * @return the clustering summary.
     */
    ClusteringSummary getClusteringSummary();

    /**
     * the cluster summary for a single cluster.
     * @param clusterId the cluster id
     * @return the summary for the cluster.
     */
    Cluster getClusterSummary(String clusterId);


    /**
     * save a list of clusters to the db.
     * @param clusters the list of clusters.
     */
    void persistClusters(Collection<Cluster> clusters);

    /**
     * save a list of cluster assignments to the db.
     * @param values the list of cluster assignments
     */
    void persistClusterAssignments(Collection<InMemoryCluster> values);

    /**
     * Clear all the clusters from the DB.
     */
    void clearClusters();
}
