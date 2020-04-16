/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 *
 */
package org.apromore.dao.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.dao.ClusterRepositoryCustom;
import org.apromore.dao.model.Cluster;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ClusterRepositoryCustomImpl implements ClusterRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getClusteringSummary
     * {@inheritDoc}
     */
    @Override
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
    @SuppressWarnings("unchecked")
    public List<Cluster> getFilteredClusters(final ClusterFilter filter) {
        Query query = em.createQuery("SELECT c FROM Cluster c WHERE (c.size >= :minClusterSize) " +
                "AND (c.size <= :maxClusterSize) AND (c.avgFragmentSize >= :minAvgFragmentSize) " +
                "AND (c.avgFragmentSize <= :maxAvgFragmentSize) AND (c.BCR >= :minBCR) AND (c.BCR <= :maxBCR)");
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
    public double getDistance(final Integer fragmentId1, final Integer fragmentId2) {
        double distance = getOrderedDistance(fragmentId1, fragmentId2);
        if (distance < 0) {
            distance = getOrderedDistance(fragmentId2, fragmentId1);
        }
        return distance;
    }

//    /**
//     * @see org.apromore.dao.ClusterRepositoryCustom#getDistances(double)
//     * {@inheritDoc}
//     */
//    @Override
//    @SuppressWarnings("unchecked")
//    public Map<FragmentPair, Double> getDistances(final double threshold) {
//        Query query = em.createQuery("SELECT fd FROM FragmentDistance fd WHERE fd.distance < :threshold");
//        query.setParameter("threshold", threshold);
//        List<FragmentDistance> distances = query.getResultList();
//
//        Map<FragmentPair, Double> fragmentDistances = new HashMap<FragmentPair, Double>();
//        for (FragmentDistance d : distances) {
//            FragmentPair pair = new FragmentPair(d.getFragmentVersionId1().getId(), d.getFragmentVersionId2().getId());
//            fragmentDistances.put(pair, d.getDistance());
//        }
//        return fragmentDistances;
//    }


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




    /* ************************** JDBC Template / native SQL Queries ******************************* */


    /**
     * @see org.apromore.dao.ClusterRepositoryCustom#getDistances(double)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<FragmentPair, Double> getDistances(final double threshold) {
        Map<FragmentPair, Double> fragmentDistances = new HashMap<>();

        String sql = "SELECT fragmentVersionId1, fragmentVersionId2, ged FROM fragment_distance WHERE ged < ?";
        List<FragmentPair> distances = this.jdbcTemplate.query(sql, new Object[] {threshold},
            new RowMapper<FragmentPair>() {
                public FragmentPair mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FragmentPair pair = new FragmentPair();
                    pair.setFid1(rs.getInt("fragmentVersionId1"));
                    pair.setFid2(rs.getInt("fragmentVersionId2"));
                    pair.setDistance(rs.getDouble("ged"));
                    return pair;
                }
            }
        );

        for (FragmentPair distance : distances) {
            fragmentDistances.put(distance, distance.getDistance());
        }

        return fragmentDistances;
    }
}
