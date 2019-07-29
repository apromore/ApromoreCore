package org.apromore.dao.jpa;

import org.apromore.dao.StatisticRepositoryCustom;
import org.apromore.dao.model.Statistic;
import org.apromore.util.StatType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class StatisticRepositoryCustomImpl implements StatisticRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /* ************************** JPA Methods here ******************************* */


    @Override
    @Transactional
    public void storeAllStats(List<Statistic> stats) {
        try {
            int ip = 0;
                for(Statistic stat : stats) {
                    ip = ip +1;
                    em.persist(stat);
                    if((ip % 10000) == 0 ) {
                        em.flush();
                        em.clear();
                    }
            }
        } catch (Exception e) {
            LOGGER.error("Error " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean existsByLogidAndStatType(Integer logid, StatType statType) {
        if (logid != null && statType != null) {
            Query query = em.createQuery("SELECT s FROM Statistic s WHERE s.logid =:param1 AND s.stat_value=:param2");
            query.setParameter("param1", logid);
            query.setParameter("param2", statType.toString());
            List<Statistic> stats = query.getResultList();

            return stats != null && stats.size() > 0;
        } else {
            return false;
        }
    }

    /* ************************** JDBC Template / native SQL Queries ******************************* */
}
