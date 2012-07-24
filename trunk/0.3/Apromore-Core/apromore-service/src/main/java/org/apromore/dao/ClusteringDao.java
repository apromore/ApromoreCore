/**
 *
 */
package org.apromore.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryCluster;

/**
 * @author Chathura C. Ekanayake
 */
public interface ClusteringDao {

    List<Cluster> getAllClusters();

    List<Cluster> getFilteredClusters(ClusterFilter filter);

    List<String> getFragmentIds(String clusterId);

    List<FragmentVersion> getFragments(String clusterId);

    double getDistance(String fragmentId1, String fragmentId2);

    Map<FragmentPair, Double> getDistances(final double threshold);

    void persistClusters(Collection<Cluster> clusters);

    void persistClusterAssignments(Collection<InMemoryCluster> values);

    List<FragmentDataObject> getUnprocessedFragments();

    List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(List<Integer> processIds);

    ClusteringSummary getClusteringSummary();

    Cluster getClusterSummary(String clusterId);
}
