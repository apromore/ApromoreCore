package org.apromore.dao;

import org.apromore.dao.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {

    /**
     * Get dashboard cache of specified LogId from DB
     * @param logid
     * @return
     */
    List<Dashboard> findByLogid(Integer logid);
}
