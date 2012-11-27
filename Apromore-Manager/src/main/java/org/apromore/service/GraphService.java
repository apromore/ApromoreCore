package org.apromore.service;

import org.apromore.dao.model.Content;
import org.apromore.graph.canonical.Canonical;

import java.util.List;

/**
 * Interface for the Graphing Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface GraphService {

    /**
     * Returns the request Content Object
     *
     * @param fragmentVersionId the fragment version id
     * @return the content corresponding to the fragment id
     */
    Content getContent(Integer fragmentVersionId);

    /**
     * returns all the Distinct Content Id's from the Vertices.
     *
     * @return the list of Id's
     */
    List<String> getContentIds();

    /**
     * Get a processModelGraph
     *
     * @param contentID the content id
     * @return the process model graph
     */
    Canonical getGraph(Integer contentID);

    /**
     * Fills the ProcessModelGraphs vertices
     *
     * @param procModelGraph
     * @param contentID
     */
    void fillNodes(Canonical procModelGraph, Integer contentID);

    /**
     * Fills the ProcessModelGraphs Edges
     *
     * @param procModelGraph
     * @param contentID
     */
    void fillEdges(Canonical procModelGraph, Integer contentID);

    /**
     * Populate Nodes by it's Fragment Id.
     * @param procModelGraph the process model graph
     * @param fragmentID the fragment Id.
     */
    void fillNodesByFragmentId(Canonical procModelGraph, Integer fragmentID);

    /**
     * Populate Nodes by it's Fragment Id.
     * @param procModelGraph process model graph
     * @param fragmentID the fragment id
     */
    void fillEdgesByFragmentId(Canonical procModelGraph, Integer fragmentID);

}
