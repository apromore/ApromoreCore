package org.apromore.canoniser.bpmn;

// Local classes
import org.apromore.canoniser.exception.CanoniserException;

/**
 * Deferred command pattern.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface Initialization {
    public void initialize() throws CanoniserException;
}
