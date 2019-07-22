package org.apromore.dao;

import org.apromore.dao.model.Statistic;

import java.util.List;

public interface StatisticRepositoryCustom {

    /**
     * Store all statistics into DB. Use custom JPA instead of sava() for better performance.
     * @param stats
     */
    void storeAllStats(List<Statistic> stats);
}
