/**
 *
 */
package org.apromore.service;

import org.apromore.exception.ExceptionDao;
import org.apromore.graph.JBPT.CPF;

/**
 * Composes from the DB representation into a RPST Directed Graph.
 * @author Chathura Ekanayake
 */
public interface ComposerService {

    /**
     * Compose from the Apromore DB version to CPF RPST Directed Graph.
     * @param fragmentVersionUri the fragment version id
     * @return the Directed Graph
     * @throws ExceptionDao if there is a DB Exception
     */
    public CPF compose(String fragmentVersionUri) throws ExceptionDao;
}
