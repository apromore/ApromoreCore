package org.apromore.service;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;

/**
 * Interface for the Clustering Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ClusterService {

    /**
     * Assign multiple fragments to a single cluster.
     * @param fragmentIds the list of fragments
     * @param clusterId   the cluster to assign the fragments
     */
    void assignFragments(List<String> fragmentIds, String clusterId);

    /**
     * Assign a single fragment to a single cluster.
     * @param fragVersion the fragment
     * @param clusterId   the cluster to assign the fragments
     */
    void assignFragment(String fragVersion, String clusterId);

    /**
     * Does something to a cluster, comments would be good.
     * @param settings
     * @throws RepositoryException
     */
    void cluster(ClusterSettings settings) throws RepositoryException;

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
    org.apromore.service.model.Cluster getCluster(String clusterId);

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
    List<String> getFragmentIds(String clusterId);

    /**
     * Get distances between pairs of fragments
     * @param fragmentIds the fragment ids to get the distances between
     * @return the distances for the fragment pairs.
     * @throws RepositoryException if the repository had issues
     */
    Map<FragmentPair, Double> getPairDistances(List<String> fragmentIds) throws RepositoryException;

}
