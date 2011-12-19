package org.apromore.exception;

/**
 * Exception for when a The importing of a process model fails.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ImportException extends Exception {

    /**
     * Default Constructor.
     */
	public ImportException() {
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     */
	public ImportException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param cause The exception that caused this exception to be thrown.
     */
	public ImportException(Throwable cause) {
		super(cause);
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     * @param cause The exception that caused this exception to be thrown.
     */
	public ImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
