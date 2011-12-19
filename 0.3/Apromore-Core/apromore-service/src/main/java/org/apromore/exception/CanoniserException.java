package org.apromore.exception;

/**
 * Exception for when a Canonical Format isn't found.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class CanoniserException extends Exception {

    /**
     * Default Constructor.
     */
	public CanoniserException() {
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     */
	public CanoniserException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param cause The exception that caused this exception to be thrown.
     */
	public CanoniserException(Throwable cause) {
		super(cause);
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     * @param cause The exception that caused this exception to be thrown.
     */
	public CanoniserException(String message, Throwable cause) {
		super(message, cause);
	}
}
