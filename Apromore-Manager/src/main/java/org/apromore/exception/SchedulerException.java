package org.apromore.exception;

/**
 * Exception for when a Scheduled task execution fails.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class SchedulerException extends Exception {

    /**
     * Default Constructor.
     */
    public SchedulerException() {
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     */
    public SchedulerException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause The exception that caused this exception to be thrown.
     */
    public SchedulerException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message the message to put with the exception.
     * @param cause   The exception that caused this exception to be thrown.
     */
    public SchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
