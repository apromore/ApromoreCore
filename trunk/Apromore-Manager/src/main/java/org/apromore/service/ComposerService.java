/**
 *
 */
package org.apromore.service;

import java.util.List;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.graph.canonical.Canonical;

/**
 * Composes from the DB representation into a RPST Directed Graph.
 * @author Chathura Ekanayake
 */
public interface ComposerService {

    /**
     * Compose from the Apromore DB version to CPF RPST Directed Graph.
     * @param rootFragment the fragment version root object
     * @return the Directed Graph
     * @throws ExceptionDao if there is a DB Exception
     */
    public Canonical compose(FragmentVersion rootFragment) throws ExceptionDao;


    /**
     * Some Implementations have an internal Cache that needs to be cleared at times.
     * @param ids the list of ID's that needs to be clears from the cache.
     */
    public void clearCache(List<Integer> ids);
}
