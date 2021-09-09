package org.apromore.dao;

import org.apromore.dao.model.DbConnectorDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DbConnectorRepository extends JpaRepository<DbConnectorDao, Long> {
}
