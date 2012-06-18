package org.apromore.service;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.Node;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.ICpfNode;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;

import java.util.Collection;

/**
 * Interface for the Content Service. Defines all the methods that will do the majority of the work for
 * adding new content into the repository.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ContentService {


    /**
     * Get all the matching Content record with the same Hash.
     * @param hash the hash to search for.
     * @return any matching content records
     */
    String getMatchingContentId(String hash);


    /**
     * Add new Content?
     * @param c RPSTNode
     * @param hash the contents Hash code
     * @param g the Process Model Graph
     */
    Content addContent(RPSTNode c, String hash, CPF g);

    /**
     * Add Vertices to the repository.
     * @param content the content id
     * @param vertices the list of vertices
     * @param g the Process Model Graph
     */
    void addNodes(Content content, Collection<CpfNode> vertices, CPF g);

    /**
     * Add a Node to the repository.
     * @param content the content Id
     * @param v the vertex
     * @param vtype the vertex type
     */
    Node addNode(Content content, ICpfNode v, String vtype);

    /**
     * Add non pocket vertices to the repository.
     * @param vid the vertex id
     */
    void addNonPacketNode(Integer vid);

    /**
     * Add multiple Edges to the Repository.
     * @param content the contentId
     * @param edges the list of edges to add.
     */
    void addEdges(Content content, Collection<AbstractDirectedEdge> edges);

    /**
     * Add a single edge to the Repository.
     * @param content the content to attach
     * @param e the edge
     * @param source the Node
     * @param target the Node
     */
    void addEdge(Content content, AbstractDirectedEdge e, Node source, Node target);


    /**
     * Delete the Content and all it's extra elements.
     * @param contentId the content to remove.
     */
    void deleteContent(String contentId);

    /**
     * Delete the Edges that are linked to the Content
     * @param contentId the content with the edges to remove.
     */
    void deleteEdgesOfContent(String contentId);

    /**
     * Delete the Vetices that are linked to the Content
     * @param contentId the content with the vertices to remove.
     */
    void deleteVerticesOfContent(String contentId);
}
