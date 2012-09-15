package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Node;

/**
 * Interface domain model Data access object Node.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Node
 */
public interface NodeDao {

    /**
     * Returns a single Node based on the primary Key.
     * @param nodeId the node Id
     * @return the found node
     */
    Node findNode(Integer nodeId);

    /**
     * Find the Node by It's Uri.
     * @param uri the uri to search for.
     * @return the found Node.
     */
    Node findNodeByUri(String uri);

    /**
     * Returns all the Content Id's from Node table.
     * @return the list of content id's from the Node records
     */
    List<String> getContentIDs();

    /**
     * Returns the Node records for the ContentId.
     * @param contentID the content id
     * @return the list of Node or null.
     */
    List<Node> getVertexByContent(Integer contentID);

    /**
     * Returns the count for vertices in the DB.
     * @return the count of found vertices.
     */
    Integer getStoredVertices();

    /**
     * Get Node by It's Fragment id.
     * @param fragmentID the fragment Id
     * @return the Node
     */
    List<Node> getVertexByFragment(Integer fragmentID);



    /**
     * Save the node.
     * @param node the node to persist
     */
    void save(Node node);

    /**
     * Update the node.
     * @param node the node to update
     * @return the updated object.
     */
    Node update(Node node);

    /**
     * Remove the node.
     * @param node the node to remove
     */
    void delete(Node node);

}
