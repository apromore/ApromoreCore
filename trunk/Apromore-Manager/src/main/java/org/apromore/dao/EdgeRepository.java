package org.apromore.dao;

import org.apromore.dao.model.Edge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Edge.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Edge
 */
@Repository
public interface EdgeRepository extends JpaRepository<Edge, Integer> {

    /**
     * Returns the Edge records for the ContentId.
     * @param contentId the content id
     * @return the list of Edges or null.
     */
    @Query("SELECT e FROM Edge e WHERE e.content.id = ?1")
    List<Edge> getEdgesByContent(Integer contentId);

    /**
     * Returns the Edge records for the fragmentId.
     * @param fragmentId the fragmentId
     * @return the list of edges or null.
     */
    @Query("SELECT e FROM Edge e, FragmentVersion fv WHERE e.content.id = fv.content.id AND fv.id = ?1")
    List<Edge> getEdgesByFragment(Integer fragmentId);

}
