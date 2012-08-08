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

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.dao.ClusteringDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusterAssignment;
import org.apromore.dao.model.ClusterAssignmentId;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.FragmentDistance;
import org.apromore.dao.model.FragmentDistanceId;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ClusteringDaoJpa implements ClusteringDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;


    /**
     * @see org.apromore.dao.ClusteringDao#getAllClusters()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Cluster> getAllClusters() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_CLUSTERS);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getCluster(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Cluster getCluster(final String clusterId) {
        return em.find(Cluster.class, clusterId);
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getClusteringSummary()
     * {@inheritDoc}
     */
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

    /**
     * @see org.apromore.dao.ClusteringDao#getClusterSummary(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
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

    /**
     * @see org.apromore.dao.ClusteringDao#getFilteredClusters(org.apromore.service.model.ClusterFilter)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
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


    /**
     * @see org.apromore.dao.ClusteringDao#getDistance(String, String)
     * {@inheritDoc}
     */
    @Override
    public double getDistance(final String fragmentId1, final String fragmentId2) {
        double distance = getOrderedDistance(fragmentId1, fragmentId2);
        if (distance < 0) {
            distance = getOrderedDistance(fragmentId2, fragmentId1);
        }
        return distance;
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getDistances(double)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<FragmentPair, Double> getDistances(final double threshold) {
        Query query = em.createNamedQuery(NamedQueries.GET_DISTANCES_BELOW_THRESHOLD);
        query.setParameter("threshold", threshold);
        List<FragmentDistance> distances = query.getResultList();

        Map<FragmentPair, Double> fragmentDistances = new HashMap<>();
        for (FragmentDistance d : distances) {
            FragmentPair pair = new FragmentPair(d.getId().getFragmentId1(), d.getId().getFragmentId2());
            fragmentDistances.put(pair, d.getDistance());
        }
        return fragmentDistances;
    }

    /**
     * @see org.apromore.dao.ClusteringDao#insertDistances(org.apache.commons.collections.map.MultiKeyMap)
     * {@inheritDoc}
     */
    @Override
    public void insertDistances(MultiKeyMap dissimmap) {
        MapIterator mi = dissimmap.mapIterator();
        while (mi.hasNext()) {
            Object k = mi.next();
            Object v = mi.getValue();

            MultiKey fids = (MultiKey) k;
            String fid1 = (String) fids.getKey(0);
            String fid2 = (String) fids.getKey(1);
            Double gedValue = (Double) v;

            FragmentDistance ged = new FragmentDistance();
            FragmentDistanceId gid = new FragmentDistanceId();
            gid.setFragmentId1(fid1);
            gid.setFragmentId2(fid2);
            ged.setId(gid);
            ged.setDistance(gedValue);

            em.persist(ged);
        }
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getFragmentIds(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getFragmentIds(final String clusterId) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENTIDS_OF_CLUSTER);
        query.setParameter("clusterId", clusterId);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getFragments(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getFragments(final String clusterId) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENTS_OF_CLUSTER);
        query.setParameter("clusterId", clusterId);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getUnprocessedFragments()
     * {@inheritDoc}
     */
    @Override
    public List<FragmentDataObject> getUnprocessedFragments() {
        List<FragmentDataObject> fragments = new ArrayList<>();
        List<FragmentVersion> fvs = getAllFragments();
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragmentId(fv.getFragmentVersionId());
            fragment.setSize(fv.getFragmentSize());
            fragments.add(fragment);
        }
        return fragments;
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getUnprocessedFragmentsOfProcesses(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(final List<Integer> processIds) {
        List<FragmentDataObject> fragments = new ArrayList<>();
        List<FragmentVersion> fvs = getFragmentsOfProcesses(processIds);
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragmentId(fv.getFragmentVersionId());
            fragment.setSize(fv.getFragmentSize());
            fragments.add(fragment);
        }
        return fragments;
    }



    /**
     * @see org.apromore.dao.ClusteringDao#persistClusters(java.util.Collection)
     * {@inheritDoc}
     */
    @Override
    public void persistClusters(final Collection<Cluster> cs) {
        for (Cluster c : cs) {
            em.persist(c);
        }
    }

    /**
     * @see org.apromore.dao.ClusteringDao#persistCluster(Cluster)
     * {@inheritDoc}
     */
    @Override
    public void persistCluster(final Cluster c) {
        em.persist(c);
    }

    /**
     * @see org.apromore.dao.ClusteringDao#persistClusterAssignment(ClusterAssignment)
     * {@inheritDoc}
     */
    @Override
    public void persistClusterAssignment(final ClusterAssignment assignment) {
        em.persist(assignment);
    }


    /**
     * @see org.apromore.dao.ClusteringDao#persistClusterAssignments(java.util.Collection)
     * {@inheritDoc}
     */
    @Override
    public void persistClusterAssignments(final Collection<InMemoryCluster> clusters) {
        Cluster cluster;
        FragmentVersion fv;
        List<ClusterAssignment> cas = new ArrayList<>();

        for (InMemoryCluster c : clusters) {
            Collection<FragmentDataObject> fs = c.getFragments();
            for (FragmentDataObject f : fs) {
                cluster = getCluster(c.getClusterId());
                fv = fvDao.findFragmentVersion(f.getFragmentId());

                ClusterAssignment ca = new ClusterAssignment();
                ca.setId(new ClusterAssignmentId(f.getFragmentId(), cluster.getClusterId()));
                ca.setCluster(cluster);
                ca.setFragment(fv);
                cas.add(ca);
            }
        }
        persistClusterFragmentMappings(cas);
    }



    /**
     * @see org.apromore.dao.ClusteringDao#clearClusters()
     * {@inheritDoc}
     */
    @Override
    public void clearClusters() {
        Query query = em.createNamedQuery(NamedQueries.DELETE_ALL_CLUSTERS);
        query.executeUpdate();
        query = em.createNamedQuery(NamedQueries.DELETE_ALL_CLUSTER_ASSIGNMENTS);
        query.executeUpdate();
    }






    private void persistClusterFragmentMappings(final List<ClusterAssignment> cas) {
        for (ClusterAssignment ca : cas) {
            em.persist(ca);
        }
    }

    @SuppressWarnings("unchecked")
    private List<FragmentVersion> getFragmentsOfProcesses(final List<Integer> processIds) {
        Query query = em.createNamedQuery(NamedQueries.GET_UNPROCESSED_FRAGMENTS_OF_PROCESSES);
        query.setParameter("processIds", processIds);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<FragmentVersion> getAllFragments() {
        Query query = em.createNamedQuery(NamedQueries.GET_UNPROCESSED_FRAGMENTS);
        return query.getResultList();
    }

    private double getOrderedDistance(final String fragmentId1, final String fragmentId2) {
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



    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }
}
