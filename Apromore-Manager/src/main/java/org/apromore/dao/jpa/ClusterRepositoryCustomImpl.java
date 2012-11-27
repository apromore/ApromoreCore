/**
 *
 */
package org.apromore.dao.jpa;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.dao.ClusterRepositoryCustom;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.FragmentDistance;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ClusterRepositoryCustomImpl implements ClusterRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private FragmentVersionRepository fragmentVersionRepository;


    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getClusteringSummary
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Object[]> getClusteringSummary() {
        Query query = em.createQuery("SELECT count(c.id), min(c.size), max(c.size), min(c.avgFragmentSize), max(c.avgFragmentSize), min(c.BCR), max(c.BCR) FROM Cluster c");
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getFragmentIds(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Integer> getFragmentIds(Integer clusterId) {
        Query query = em.createQuery("SELECT ca.fragment.id FROM ClusterAssignment ca WHERE ca.cluster.id = :clusterId");
        query.setParameter("clusterId", clusterId);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getFilteredClusters(org.apromore.service.model.ClusterFilter)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Cluster> getFilteredClusters(final ClusterFilter filter) {
        Query query = em.createQuery("SELECT c FROM Cluster c WHERE (c.size > :minClusterSize OR c.size = :minClusterSize) " +
                "AND (c.size < :maxClusterSize OR c.size = :maxClusterSize) AND (c.avgFragmentSize > :minAvgFragmentSize OR c.avgFragmentSize = :minAvgFragmentSize) " +
                "AND (c.avgFragmentSize < :maxAvgFragmentSize OR c.avgFragmentSize = :maxAvgFragmentSize) " +
                "AND (c.BCR > :minBCR OR c.BCR = :minBCR) AND (c.BCR < :maxBCR OR c.BCR = :maxBCR)");
        query.setParameter("minClusterSize", filter.getMinClusterSize());
        query.setParameter("maxClusterSize", filter.getMaxClusterSize());
        query.setParameter("minAvgFragmentSize", filter.getMinAverageFragmentSize());
        query.setParameter("maxAvgFragmentSize", filter.getMaxAverageFragmentSize());
        query.setParameter("minBCR", filter.getMinBCR());
        query.setParameter("maxBCR", filter.getMaxBCR());
        return query.getResultList();
    }


    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getDistance(Integer, Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public double getDistance(final Integer fragmentId1, final Integer fragmentId2) {
        double distance = getOrderedDistance(fragmentId1, fragmentId2);
        if (distance < 0) {
            distance = getOrderedDistance(fragmentId2, fragmentId1);
        }
        return distance;
    }

    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getDistances(double)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<FragmentPair, Double> getDistances(final double threshold) {
        Query query = em.createQuery("SELECT fd FROM FragmentDistance fd WHERE fd.distance < :threshold");
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
     * @see org.apromore.dao.ClusterRepositoryCustom#insertDistances(org.apache.commons.collections.map.MultiKeyMap)
     * {@inheritDoc}
     */
    @Override
    public void insertDistances(MultiKeyMap dissimmap) {
        MapIterator mi = dissimmap.mapIterator();
        while (mi.hasNext()) {
            java.lang.Object k = mi.next();
            java.lang.Object v = mi.getValue();

            MultiKey fids = (MultiKey) k;
            Integer fid1 = (Integer) fids.getKey(0);
            Integer fid2 = (Integer) fids.getKey(1);
            Double gedValue = (Double) v;

            FragmentDistance ged = new FragmentDistance();
            ged.setFragmentVersionId1(fragmentVersionRepository.findOne(fid1));
            ged.setFragmentVersionId2(fragmentVersionRepository.findOne(fid2));
            ged.setDistance(gedValue);

            em.persist(ged);
        }
    }



    @Transactional(readOnly = true)
    private double getOrderedDistance(final Integer fragmentId1, final Integer fragmentId2) {
        Query query = em.createQuery("SELECT fd.distance FROM FragmentDistance fd WHERE fd.fragmentVersionId1.id = :fragmentId1 " +
                "AND fd.fragmentVersionId1.id = :fragmentId2");
        query.setParameter("fragmentId1", fragmentId1);
        query.setParameter("fragmentId2", fragmentId2);
        List results = query.getResultList();
        if (results == null || results.isEmpty()) {
            return -1d;
        } else {
            return (Double) results.get(0);
        }
    }

}
