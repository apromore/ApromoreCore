/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.dao.jpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.StatisticRepositoryCustom;
import org.apromore.dao.model.Statistic;
import org.apromore.util.StatType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class StatisticRepositoryCustomImpl implements StatisticRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /* ************************** JPA Methods here ******************************* */

//    public void storeAllStats(List<Statistic> stats) {
//        try {
//            int ip = 0;
//                for(Statistic stat : stats) {
//                    ip = ip +1;
//                    em.persist(stat);
//                    if((ip % 10000) == 0 ) {
//                        em.flush();
//                        em.clear();
//                    }
//            }
//        } catch (Exception e) {
//            LOGGER.error("Error " + e.getMessage());
//        }
//    }

    @Override
    @Transactional
    public boolean existsByLogidAndStatType(Integer logid, StatType statType) {
//        if (logid != null && statType != null) {
//            Query query = em.createQuery("SELECT s FROM Statistic s WHERE s.logid =:param1 AND s.stat_key=:param2");
//            query.setParameter("param1", logid);
//            query.setParameter("param2", statType.toString());
//            List<Statistic> stats = query.getResultList();
//
//            LOGGER.info(" The number of stats is: " + statType + " - "  + stats.size());
//
//            return stats != null && stats.size() > 0;
//        } else {
//            return false;
//        }

        if (logid != null && statType != null) {
            Query query = em.createQuery("SELECT s FROM Statistic s WHERE s.logid =:param1 AND s.stat_key=:param2");
            query.setParameter("param1", logid);
            query.setParameter("param2", statType.toString());
//            string result = query.getSingleResult().toString();

            return !query.setMaxResults(1).getResultList().isEmpty();
        }

        return false;
    }

    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    @Transactional
    public void storeAllStats(final List<Statistic> stats) {

        // *******  profiling code start here ********
        long startTime = System.nanoTime();
        // *******  profiling code end here ********

        if (null != stats && stats.size() != 0) {
            String sql = "INSERT INTO statistic (id, logid, pid, stat_key, stat_value) VALUES (?,?,?,?,?)";

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Statistic stat = stats.get(i);
                    ps.setBytes(1, stat.getId());
                    ps.setInt(2, stat.getLogid());
                    ps.setBytes(3, stat.getPid());
                    ps.setString(4, stat.getStat_key());
                    ps.setString(5, stat.getStat_value());
                }

                @Override
                public int getBatchSize() {
                    return stats.size();
                }
            });
        }

        // *******  profiling code start here ********
        long elapsedNanos = System.nanoTime() - startTime;
//        LOGGER.info("Elapsed time: " + elapsedNanos / 1000000 + " ms");
//        LOGGER.info("Insert speed: " + 100000 / ( elapsedNanos / 1000000 /1000 ) + " records/sec");
        // *******  profiling code end here ********

            LOGGER.info("Stored [" + stats.size() + "] stats in " + elapsedNanos / 1000000 );

    }
}
