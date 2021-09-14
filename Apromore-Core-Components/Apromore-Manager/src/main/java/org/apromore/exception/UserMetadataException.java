package org.apromore.exception;

/**
 * Exception for when a user isn't found.
 *
 * @author frankm
 */
public class UserMetadataException extends Exception {

    /**
     * Default Constructor.
     */
    public UserMetadataException() {
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     */
    public UserMetadataException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause The exception that caused this exception to be thrown.
     */
    public UserMetadataException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     * @param cause   The exception that caused this exception to be thrown.
     */
    public UserMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
}
