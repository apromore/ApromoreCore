package org.apromore.service;

import org.apromore.graph.canonical.Canonical;

/**
 * Interface for the Graphing Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface GraphService {

    /**
     * Fill the Nodes by Fragment.
     * @param procModelGraph the graph we are building.
     * @param fragmentURI the fragment URI.
     */
    Canonical fillNodesByFragment(Canonical procModelGraph, String fragmentURI);

    /**
     * Fill the Nodes by Fragment.
     * @param procModelGraph the graph we are building.
     * @param fragmentURI the fragment URI.
     */
    Canonical fillEdgesByFragmentURI(Canonical procModelGraph, String fragmentURI);
}
