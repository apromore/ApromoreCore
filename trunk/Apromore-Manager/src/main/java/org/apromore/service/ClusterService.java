package org.apromore.service;

import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.HistoryEvent;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;

import java.util.List;
import java.util.Map;

/**
 * Interface for the Clustering Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ClusterService {

    /**
     * Does something to a cluster, comments would be good.
     * @param settings
     * @throws RepositoryException
     */
    void cluster(ClusterSettings settings) throws RepositoryException;

    /**
     * Computes the GED Matrix used by the clusterers.
     */
    void computeGEDMatrix() throws RepositoryException;

    /**
     * Gets the clustering summary.
     * @return the clustering Summary object
     */
    ClusteringSummary getClusteringSummary();

    /**
     * Return the cluster Summaries using a filter.
     * @param filter the filter we are using in the cluster search
     * @return the list of cluster infos found in the search.
     */
    List<Cluster> getClusterSummaries(ClusterFilter filter);

    /**
     * Find a single cluster.
     * @param clusterId this is the id of the cluster we want
     * @return the found cluster using the id
     */
    org.apromore.service.model.Cluster getCluster(Integer clusterId);

    /**
     * Returns all the clusters.
     * @return the list of cluster info
     */
    List<Cluster> getClusters();

    /**
     * Get the Clusters using a filter.
     * @param filter the filter to apply to the search
     * @return the list of found clusters
     */
    List<org.apromore.service.model.Cluster> getClusters(ClusterFilter filter);

    /**
     * Returns the fragment Id's associated with a cluster.
     * @param clusterId the cluster Id we are using to find fragments with.
     * @return the list of fragments Id's
     */
    List<Integer> getFragmentIds(Integer clusterId);

    /**
     * Get distances between pairs of fragments
     * @param fragmentIds the fragment ids to get the distances between
     * @return the distances for the fragment pairs.
     * @throws RepositoryException if the repository had issues
     */
    Map<FragmentPair, Double> getPairDistances(List<Integer> fragmentIds) throws RepositoryException;


    /**
     * Returns the Last Time the GED matrix completed or wht time it was started if the completed doesn't exist.
     * @return the dateTime of the last time the GED Matrix was completed or it's start time if non have completed or null if both
     * haven't happened yet.
     */
    HistoryEvent getGedMatrixLastExecutionTime();

}
