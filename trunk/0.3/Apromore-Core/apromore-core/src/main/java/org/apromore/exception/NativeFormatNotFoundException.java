package org.apromore.exception;

/**
 * Exception for when a Native Format isn't found.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class NativeFormatNotFoundException extends Exception {

    /**
     * Default Constructor.
     */
	public NativeFormatNotFoundException() {
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     */
	public NativeFormatNotFoundException(String message) {
		super(message);
	}

    /**
     * Constructor.
     * @param cause The exception that caused this exception to be thrown.
     */
	public NativeFormatNotFoundException(Throwable cause) {
		super(cause);
	}

    /**
     * Constructor.
     * @param message the message to put with the exception.
     * @param cause The exception that caused this exception to be thrown.
     */
	public NativeFormatNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
