package org.apromore.service;

import org.apromore.dao.model.Content;
import org.apromore.graph.JBPT.CPF;

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
     * @param fragmentVersionId the fragment version id
     * @return the content corresponding to the fragment id
     */
    Content getContent(String fragmentVersionId);

    /**
     * returns all the Distinct Content Id's from the Vertices.
     * @return the list of Id's
     */
    List<String> getContentIds();

    /**
     * Get a processModelGraph
     * @param contentID the content id
     * @return the process model graph
     */
    CPF getGraph(String contentID);

    /**
     * Fills the ProcessModelGraphs vertices
     * @param procModelGraph
     * @param contentID
     */
    void fillNodes(CPF procModelGraph, String contentID);

    /**
     * Fills the ProcessModelGraphs Edges
     * @param procModelGraph
     * @param contentID
     */
    void fillEdges(CPF procModelGraph, String contentID);
    
}
