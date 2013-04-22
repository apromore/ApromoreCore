package org.apromore.dao;

import org.apromore.dao.model.EdgeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeMappingRepository extends JpaRepository<EdgeMapping, Integer> {

}
