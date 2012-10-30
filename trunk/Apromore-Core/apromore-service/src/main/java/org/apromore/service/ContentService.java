package org.apromore.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Node;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.INode;

/**
 * Interface for the Content Service. Defines all the methods that will do the majority of the work for
 * adding new content into the repository.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ContentService {


    /**
     * Get all the matching Content record with the same Hash.
     *
     * @param hash the hash to search for.
     * @return any matching content records
     */
    Integer getMatchingContentId(String hash);

    /**
     * Add new Content?
     * @param c    RPSTNode
     * @param hash the contents Hash code
     * @param g    the Process Model Graph
     * @param pocketIdMappings the pocket mappings
     */
    Content addContent(Canonical c, String hash, Canonical g, Map<String, String> pocketIdMappings);

    /**
     * Add Nodes to the repository.
     * @param content  the content id
     * @param vertices the list of vertices
     * @param g        the Process Model Graph
     * @param pocketIdMappings the pocket mappings
     */
    void addNodes(Content content, Collection<org.apromore.graph.canonical.Node> vertices, Canonical g, Map<String, String> pocketIdMappings);

    /**
     * Add a Node to the repository.
     * @param content the content Id
     * @param v       the vertex
     * @param vtype   the vertex type
     */
    Node addNode(Content content, org.apromore.graph.canonical.Node v, String vtype);

    /**
     * Add non pocket vertices to the repository.
     * @param node the vertex id
     */
    void addNonPocketNode(Node node);

    /**
     * Add multiple Edges to the Repository.
     * @param content the contentId
     * @param edges   the list of edges to add.
     */
    void addEdges(Content content, Set<org.apromore.graph.canonical.Edge> edges);

    /**
     * Add a single edge to the Repository.
     * @param content the content to attach
     * @param e       the edge
     * @param source  the Node
     * @param target  the Node
     */
    void addEdge(Content content, org.apromore.graph.canonical.Edge e, Node source, Node target);


    /**
     * Delete the Content and all it's extra elements.
     * @param contentId the content to remove.
     */
    void deleteContent(Integer contentId);

    /**
     * Delete the Edges that are linked to the Content
     * @param contentId the content with the edges to remove.
     */
    void deleteEdgesOfContent(Integer contentId);

    /**
     * Delete the Vetices that are linked to the Content
     * @param contentId the content with the vertices to remove.
     */
    void deleteVerticesOfContent(Integer contentId);
}
