/**
 *
 */
package org.apromore.dao.jpa;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.dao.ClusteringDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.*;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentDataObject;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.apromore.toolbox.clustering.algorithms.dbscan.InMemoryCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

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
     * @see org.apromore.dao.ClusteringDao#getCluster(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Cluster getCluster(final Integer clusterId) {
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
     * @see org.apromore.dao.ClusteringDao#getClusterSummary(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Cluster getClusterSummary(final Integer clusterId) {
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
     * @see org.apromore.dao.ClusteringDao#getDistance(Integer, Integer)
     * {@inheritDoc}
     */
    @Override
    public double getDistance(final Integer fragmentId1, final Integer fragmentId2) {
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

        Map<FragmentPair, Double> fragmentDistances = new HashMap<FragmentPair, Double>();
        for (FragmentDistance d : distances) {
            FragmentPair pair = new FragmentPair(d.getFragmentVersionId1(), d.getFragmentVersionId2());
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
            Integer fid1 = (Integer) fids.getKey(0);
            Integer fid2 = (Integer) fids.getKey(1);
            Double gedValue = (Double) v;

            FragmentDistance ged = new FragmentDistance();
            ged.setFragmentVersionId1(fvDao.findFragmentVersion(fid1));
            ged.setFragmentVersionId2(fvDao.findFragmentVersion(fid2));
            ged.setDistance(gedValue);

            em.persist(ged);
        }
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getFragmentIds(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getFragmentIds(final Integer clusterId) {
        Query query = em.createNamedQuery(NamedQueries.GET_FRAGMENTIDS_OF_CLUSTER);
        query.setParameter("clusterId", clusterId);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ClusteringDao#getFragments(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FragmentVersion> getFragments(final Integer clusterId) {
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
        List<FragmentDataObject> fragments = new ArrayList<FragmentDataObject>();
        List<FragmentVersion> fvs = getAllFragments();
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragment(fv);
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
        List<FragmentDataObject> fragments = new ArrayList<FragmentDataObject>();
        List<FragmentVersion> fvs = getFragmentsOfProcesses(processIds);
        for (FragmentVersion fv : fvs) {
            FragmentDataObject fragment = new FragmentDataObject();
            fragment.setFragment(fv);
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
        List<ClusterAssignment> cas = new ArrayList<ClusterAssignment>();

        for (InMemoryCluster c : clusters) {
            Collection<FragmentDataObject> fs = c.getFragments();
            for (FragmentDataObject f : fs) {
                cluster = getCluster(c.getClusterId());
                fv = fvDao.findFragmentVersion(f.getFragment().getId());

                ClusterAssignment ca = new ClusterAssignment();
                ca.setFragment(fv);
                ca.setCluster(cluster);
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

    private double getOrderedDistance(final Integer fragmentId1, final Integer fragmentId2) {
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
