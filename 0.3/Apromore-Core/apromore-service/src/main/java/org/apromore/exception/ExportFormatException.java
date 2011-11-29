package org.apromore.exception;

/**
 * Exception for when a Annotation isn't found.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ExportFormatException extends Exception {

    /**
     * Default Constructor.
     */
	public ExportFormatException() {
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     */
	public ExportFormatException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param cause The exception that caused this exception to be thrown.
     */
	public ExportFormatException(Throwable cause) {
		super(cause);
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     * @param cause The exception that caused this exception to be thrown.
     */
	public ExportFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
