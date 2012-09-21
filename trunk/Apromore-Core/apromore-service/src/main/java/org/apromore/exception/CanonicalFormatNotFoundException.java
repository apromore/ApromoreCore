package org.apromore.exception;

/**
 * Exception for when a Canonical Format isn't found.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Deprecated // PluginNotFoundException should be used instead
public class CanonicalFormatNotFoundException extends Exception {

    /**
     * Default Constructor.
     */
    public CanonicalFormatNotFoundException() {
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     */
    public CanonicalFormatNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause The exception that caused this exception to be thrown.
     */
    public CanonicalFormatNotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     * @param cause   The exception that caused this exception to be thrown.
     */
    public CanonicalFormatNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
