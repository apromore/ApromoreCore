package org.apromore.pql.indexer;

/**
 * Exception thrown by {@link ConfigBean} if the constants of <code>site.properties</code></code>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class PQLIndexerConfigurationException extends Exception {

    public PQLIndexerConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
