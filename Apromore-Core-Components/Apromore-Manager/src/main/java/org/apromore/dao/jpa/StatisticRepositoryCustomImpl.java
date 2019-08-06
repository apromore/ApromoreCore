package org.apromore.dao.jpa;

import org.apromore.dao.StatisticRepositoryCustom;
import org.apromore.dao.model.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class StatisticRepositoryCustomImpl implements StatisticRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

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
}
