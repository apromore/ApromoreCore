package org.apromore.service;

import java.util.List;

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
     * @param clusterId the cluster to assign the fragments
     */
    void assignFragments(List<String> fragmentIds, String clusterId);

    /**
     * Assign a single fragment to a single cluster.
     * @param fragVersion the fragment
     * @param clusterId the cluster to assign the fragments
     */
    void assignFragment(String fragVersion, String clusterId);

    /**
     * Clears all the cluster Id from the database.
     */
    void clearClusters();

}
