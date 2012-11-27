/**
 *
 */
package org.apromore.dao;

import org.apromore.dao.model.ClusterAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Clustering.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 * @version 2.0
 * @see org.apromore.dao.model.ClusterAssignment
 */
@Repository
public interface ClusterAssignmentRepository extends JpaRepository<ClusterAssignment, Integer> {

}
