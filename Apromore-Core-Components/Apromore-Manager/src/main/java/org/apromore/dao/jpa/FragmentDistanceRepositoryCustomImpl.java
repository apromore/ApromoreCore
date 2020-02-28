/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

/**
 *
 */
package org.apromore.dao.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.collections15.map.MultiKeyMap;
import org.apromore.dao.FragmentDistanceRepositoryCustom;
import org.apromore.dao.dataObject.DistanceDO;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class FragmentDistanceRepositoryCustomImpl implements FragmentDistanceRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void saveDistances(final MultiKeyMap distanceMap) {
        MapIterator mi = distanceMap.mapIterator();
        List<DistanceDO> distances = new ArrayList<>();
        while (mi.hasNext()) {
            MultiKey fragmentIds = (MultiKey) mi.next();
            Double ged = (Double) mi.getValue();

            if (getDistance((Integer) fragmentIds.getKey(0), (Integer) fragmentIds.getKey(1)) == null) {
                distances.add(new DistanceDO((Integer) fragmentIds.getKey(0), (Integer) fragmentIds.getKey(1), ged));
            }
        }
        persistDistance(distances);
    }

    @Override
    public Double getDistance(final Integer fragmentId1, final Integer fragmentId2) {
        if (fragmentId1 != null && fragmentId2 != null) {
            String sql = "SELECT ged FROM fragment_distance WHERE (fragmentVersionId1 = ? AND fragmentVersionId2 = ?) OR " +
                    "(fragmentVersionId1 = ? AND fragmentVersionId2 = ?)";
            List<Double> geds = this.jdbcTemplate.query(sql, new Object[] {fragmentId1, fragmentId2, fragmentId2, fragmentId1},
                    new RowMapper<Double>() {
                        public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getDouble("ged");
                        }
                    });
            if (geds.isEmpty()) {
                return null;
            } else {
                return geds.get(0);
            }
        } else {
            return null;
        }
    }


    /**
     * Save a distance calculation into the DB.
     * @param distances the array of details for the distance inserts.
     */
    @Override
    public void persistDistance(final List<DistanceDO> distances) {
        String sql = "INSERT INTO fragment_distance (fragmentVersionId1, fragmentVersionId2, ged) VALUES (?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DistanceDO distanceDO = distances.get(i);
                ps.setInt(1, distanceDO.getFragmentId1());
                ps.setInt(2, distanceDO.getFragmentId2());
                ps.setDouble(3, distanceDO.getGed());
            }

            @Override
            public int getBatchSize() {
                return distances.size();
            }
        });
    }

}
