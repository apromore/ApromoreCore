package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// Local classes
import org.apromore.canoniser.exception.CanoniserException;

/**
 * Base class for constructor helper classes, providing a deferred command pattern.
 *
 * We use this while constructing documents because references won't necessarily have
 * existing referenced elements until the first traversal of the document is complete.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AbstractInitializer {

    /** Logger. */
    private final Logger logger = Logger.getAnonymousLogger();

    /** Deferred initialization commands. */
    private final List<Initialization> deferredInitializationList = new ArrayList<Initialization>();

    /** @param initialization  a command for later execution */
    public void defer(final Initialization initialization) {
        deferredInitializationList.add(initialization);
    }

    /**
     * Execute all the {@link #defer}red {@link Initialization}s.
     *
     * @throws CanoniserException if any undone tasks still remain for the BPMN document construction
     */
    public void close() throws CanoniserException {

        // Execute deferred initialization
        for (Initialization initialization : deferredInitializationList) {
            initialization.initialize();
        }
    }

    /**
     * @param message  human-legible text message about the canonisation or de-canonisation
     */
    public void warn(final String message) {
        logger.fine(message);
    }
}
