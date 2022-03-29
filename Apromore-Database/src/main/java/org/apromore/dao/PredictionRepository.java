package org.apromore.dao;

import org.apromore.dao.model.PredictionDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionRepository extends JpaRepository<PredictionDao, Long> {
}
