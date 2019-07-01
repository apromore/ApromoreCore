package org.apromore.dao;

import org.apromore.dao.model.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {

    List<Statistic> findByLogid(Integer logid);
}


