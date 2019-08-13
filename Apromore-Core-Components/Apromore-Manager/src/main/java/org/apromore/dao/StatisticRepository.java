package org.apromore.dao;

import org.apromore.dao.model.Statistic;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.SQLOutput;
import java.util.List;

@Repository
//@EnableCaching
public interface StatisticRepository extends JpaRepository<Statistic, Integer>, StatisticRepositoryCustom {

    /**
     * Get statistics of specified LogId from DB
     * @param logid
     * @return
     */
//    @Cacheable("log")
    List<Statistic> findByLogid(Integer logid);
}


