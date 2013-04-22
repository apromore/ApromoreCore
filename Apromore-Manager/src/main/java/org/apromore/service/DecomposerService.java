/**
 *
 */
package org.apromore.service;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.helper.OperationContext;

/**
 * Decomposes from the graph RPST to apromore CPF.
 * @author Chathura Ekanayake
 */
public interface DecomposerService {

    /**
     * Decompose a fragment and make sure the links are defined in the Process Model Version.
     * @param graph the RPST graph
     * @param modelVersion the process Model version for this
     * @return the root fragment of the process model.
     * @throws RepositoryException if saving the conversion fails.
     */
    public OperationContext decompose(Canonical graph, ProcessModelVersion modelVersion) throws RepositoryException;
}
