/**
 *
 */
package org.apromore.service;

import java.util.List;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.canonical.Canonical;

/**
 * Decomposes from the graph RPST to apromore CPF.
 * @author Chathura Ekanayake
 */
public interface DecomposerService {

    /**
     * Decomposes a graph into a FragmentVersion.
     * @param graph the graph
     * @param fragmentIds the fragment Ids
     * @return the fragment version of the apromore system.
     * @throws RepositoryException if saving the conversion fails.
     */
    public FragmentVersion decompose(Canonical graph, List<String> fragmentIds) throws RepositoryException;

    /**
     * Decompose a fragment into a String.....not quite sure.
     * @param graph the graph
     * @param fragmentIds the fragment Ids
     * @return the string?
     * @throws RepositoryException if saving the conversion fails.
     */
    public String decomposeFragment(Canonical graph, List<String> fragmentIds) throws RepositoryException;
}
