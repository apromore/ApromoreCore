package org.apromore.canoniser.bpmn.cpf;

// Local classes
import org.apromore.canoniser.exception.CanoniserException;

/**
 * Deferred command pattern.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
interface Initialization {
    void initialize() throws CanoniserException;
}
