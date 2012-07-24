/**
 *
 */
package org.apromore.dao.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.ClusteringDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusterAssignment;
import org.apromore.dao.model.ClusterAssignmentId;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentDistance;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryCluster;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura C. Ekanayake
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ClusteringDaoJpa implements ClusteringDao {

    @PersistenceContext
    private EntityManager em;



    @Override
    public List<Cluster> getAllClusters() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_CLUSTERS);
        return query.getResultList();
    }

    @Override
    public Cluster getClusterSummary(final String clusterId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CLUSTER_BY_ID);
        query.setParameter("clusterId", clusterId);
        List<Cluster> cs = query.getResultList();

        if (cs.isEmpty()) {
            return null;
        } else {
            return cs.get(0);
        }
    }

    /* (non-Javadoc)
      * @see org.apromore.dao.ClusteringDao#getFilteredClusters(org.apromore.service.model.ClusterFilter)
      */
    @Override
    public List<Cluster> getFilteredClusters(final ClusterFilter filter) {
        Query query = em.createNamedQuery(NamedQueries.GET_FILTERED_CLUSTERS);
        query.setParameter("minClusterSize", filter.getMinClusterSize());
        query.setParameter("maxClusterSize", filter.getMaxClusterSize());
        query.setParameter("minAvgFragmentSize", filter.getMinAverageFragmentSize());
        query.setParameter("maxAvgFragmentSize", filter.getMaxAverageFragmentSize());
        query.setParameter("minBCR", filter.getMinBCR());
        query.setParameter("maxBCR", filter.getMaxBCR());
        return query.getResultList();
    }

    @Override
    public ClusteringSummary getClusteringSummary() {
        Query query = em.createNamedQuery(NamedQueries.GET_CLUSTERING_SUMMARY);
        List results = query.getResultList();
        if (results == null || results.isEmpty()) {
            return null;
        } else {
            return (ClusteringSummary) results.get(0);
        }
    }

    @Override
    public double getDistance(String fragmentId1, String fragmentId2) {
        double distance = getOrderedDistance(fragmentId1, fragmentId2);
        if (distance < 0) {
            distance = getOrderedDistance(fragmentId2, fragmentId1);
        }
        return distance;
    }

    public double getOrderedDistance(final String fragmentId1, final String fragmentId2) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENT_DISTANCE);
        query.setParameter("fragmentId1", fragmentId1);
        query.setParameter("fragmentId2", fragmentId2);
        List results = query.getResultList();
        if (results == null || results.isEmpty()) {
            return -1d;
        } else {
            return (Double) results.get(0);
        }
    }

    @Override
    public Map<FragmentPair, Double> getDistances(final double threshold) {
        Query query = em.createNamedQuery(NamedQueries.GET_DISTANCES_BELOW_THRESHOLD);
        query.setParameter("threshold", threshold);
        List<FragmentDistance> distances = query.getResultList();

        Map<FragmentPair, Double> fragmentDistances = new HashMap<FragmentPair, Double>();
        for (FragmentDistance d : distances) {
            FragmentPair pair = new FragmentPair(d.getId().getFragmentId1(), d.getId().getFragmentId2());
            fragmentDistances.put(pair, d.getDistance());
        }
        return fragmentDistances;
    }

    @Override
    public List<String> getFragmentIds(final String clusterId) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENTIDS_OF_CLUSTER);
        query.setParameter("clusterId", clusterId);
        return query.getResultList();
    }

    @Override
    public List<FragmentVersion> getFragments(final String clusterId) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENTS_OF_CLUSTER);
        query.setParameter("clusterId", clusterId);
        return query.getResultList();
    }

    @Override
    public List<FragmentDataObject> getUnprocessedFragments() {
        List<FragmentDataObject> fragments = new ArrayList<FragmentDataObject>();
        List<FragmentVersion> fvs = getAllFragments();
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragmentId(fv.getFragmentVersionId());
            fragment.setSize(fv.getFragmentSize());
            fragments.add(fragment);
        }
        return fragments;
    }

    private List<FragmentVersion> getAllFragments() {
        Query query = em.createNamedQuery(NamedQueries.GET_UNPROCESSED_FRAGMENTS);
        return query.getResultList();
    }

    @Override
    public List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(List<Integer> processIds) {
        List<FragmentDataObject> fragments = new ArrayList<FragmentDataObject>();
        List<FragmentVersion> fvs = getFragmentsOfProcesses(processIds);
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragmentId(fv.getFragmentVersionId());
            fragment.setSize(fv.getFragmentSize());
            fragments.add(fragment);
        }
        return fragments;
    }

    private List<FragmentVersion> getFragmentsOfProcesses(final List<Integer> processIds) {
        Query query = em.createNamedQuery(NamedQueries.GET_UNPROCESSED_FRAGMENTS_OF_PROCESSES);
        query.setParameter("processIds", processIds);
        return query.getResultList();
    }

    public void createClusters(Collection<InMemoryCluster> clusters) {
        List<Cluster> cs = new ArrayList<Cluster>();
        for (InMemoryCluster cluster : clusters) {
            Cluster c = new Cluster();
            c.setClusterId(cluster.getClusterId());
            cs.add(c);
        }
        persistClusters(cs);
    }

    @Override
    public void persistClusterAssignments(Collection<InMemoryCluster> clusters) {
        List<ClusterAssignment> cas = new ArrayList<ClusterAssignment>();
        for (InMemoryCluster cluster : clusters) {
            Collection<FragmentDataObject> fs = cluster.getFragments();
            for (FragmentDataObject f : fs) {
                ClusterAssignment ca = new ClusterAssignment();
                ClusterAssignmentId caid = new ClusterAssignmentId();
                caid.setClusterId(cluster.getClusterId());
                caid.setFragmentId(f.getFragmentId());
                ca.setId(caid);
                cas.add(ca);
            }
        }
        persistClusterFragmentMappings(cas);
    }

    private void persistClusterFragmentMappings(List<ClusterAssignment> cas) {
        for (ClusterAssignment ca : cas) {
            em.persist(ca);
        }
    }

    @Override
    public void persistClusters(Collection<Cluster> cs) {
        for (Cluster c : cs) {
            em.persist(c);
        }
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     *
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
