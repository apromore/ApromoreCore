package org.apromore.canoniser.bpmn;

// Local classes
import org.apromore.canoniser.exception.CanoniserException;

/**
 * Deferred command pattern.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface Initialization {

    /**
     * Perform initialization command.
     *
     * By the time this is called, all the elements of the document under construction ought to exist and have
     * assigned identifiers.
     *
     * @throws CanoniserException if the command fails
     */
    void initialize() throws CanoniserException;
}
