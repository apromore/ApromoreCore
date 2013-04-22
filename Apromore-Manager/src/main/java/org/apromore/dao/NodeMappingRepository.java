package org.apromore.dao;

import org.apromore.dao.model.NodeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeMappingRepository extends JpaRepository<NodeMapping, Integer> {

}
