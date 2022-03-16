package org.apromore.dao;

import org.apromore.dao.model.PredictorDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictorRepository extends JpaRepository<PredictorDao, Long> {
}
