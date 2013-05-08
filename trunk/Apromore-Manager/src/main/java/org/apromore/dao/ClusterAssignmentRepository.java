/**
 *
 */
package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.ClusterAssignment;
import org.apromore.dao.model.FragmentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Clustering.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 * @version 2.0
 * @see org.apromore.dao.model.ClusterAssignment
 */
@Repository
public interface ClusterAssignmentRepository extends JpaRepository<ClusterAssignment, Integer> {

    /**
     * find a fragments of a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    @Query("SELECT fv FROM ClusterAssignment ca JOIN ca.fragment fv JOIN ca.cluster c WHERE c.id = ?1")
    List<FragmentVersion> findFragmentVersionByClusterId(Integer clusterId);

}
