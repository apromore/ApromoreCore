/**
 *
 */
package org.apromore.dao;

import org.apromore.dao.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Clustering.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 * @version 2.0
 * @see org.apromore.dao.model.Cluster
 * @see org.apromore.dao.model.ClusteringSummary
 * @see org.apromore.dao.model.ClusterAssignment
 * @see org.apromore.dao.model.FragmentDistance
 */
@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Integer>, ClusterRepositoryCustom {

}
