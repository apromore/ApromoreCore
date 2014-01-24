package org.apromore.portal.dialogController;

/**
 * A process model could not be configured.
 *
 * This class is guaranteed to have a human-legible detail message suitable for an error dialog.
 */
public class ConfigureException extends Exception {

    /**
     * @param message  detail message
     * @param cause  the throwable which caused configuration to fail
     */
    ConfigureException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message  detail message
     */
    ConfigureException(final String message) {
        super(message);
    }
}
