package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Edge;

/**
 * Interface domain model Data access object Edge.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Edge
 */
public interface EdgeDao {


    /**
     * Returns the Edge records for the ContentId.
     * @param contentID the content id
     * @return the list of Edges or null.
     */
    List<Edge> getEdgesByContent(Integer contentID);

    /**
     * Returns the count of stored edges in the db.
     * @return the count of edges in the system.
     */
    Integer getStoredEdges();

    /**
     *
     * @param fragmentID
     * @return
     */
    List<Edge> getEdgesByFragment(Integer fragmentID);


    /**
     * Save the edge.
     * @param edge the edge to persist
     */
    void save(Edge edge);

    /**
     * Update the edge.
     * @param edge the edge to update
     * @return the updated object.
     */
    Edge update(Edge edge);

    /**
     * Remove the edge.
     * @param edge the edge to remove
     */
    void delete(Edge edge);

}
