/**
 *
 */
package org.apromore.service;

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
     * Compose from the Apromore DB version to CPF RPST Directed Graph.
     * @param rootFragmentId the fragment version root id
     * @return the Directed Graph
     * @throws ExceptionDao if there is a DB Exception
     */
    public Canonical compose(Integer rootFragmentId) throws ExceptionDao;

}
