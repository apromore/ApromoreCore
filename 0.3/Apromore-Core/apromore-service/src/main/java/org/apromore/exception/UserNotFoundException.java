package org.apromore.exception;

/**
 * Exception for when a user isn't found.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserNotFoundException extends Exception {

    /**
     * Default Constructor.
     */
	public UserNotFoundException() {
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     */
	public UserNotFoundException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param cause The exception that caused this exception to be thrown.
     */
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     * @param cause The exception that caused this exception to be thrown.
     */
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
