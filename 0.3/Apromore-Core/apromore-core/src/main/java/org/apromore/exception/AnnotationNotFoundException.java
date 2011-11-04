package org.apromore.exception;

/**
 * Exception for when a Annotation isn't found.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class AnnotationNotFoundException extends Exception {

    /**
     * Default Constructor.
     */
	public AnnotationNotFoundException() {
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     */
	public AnnotationNotFoundException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param cause The exception that caused this exception to be thrown.
     */
	public AnnotationNotFoundException(Throwable cause) {
		super(cause);
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     * @param cause The exception that caused this exception to be thrown.
     */
	public AnnotationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
