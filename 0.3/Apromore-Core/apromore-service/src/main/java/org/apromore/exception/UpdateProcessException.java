package org.apromore.exception;

public class UpdateProcessException extends Exception {

    public UpdateProcessException() { }

    public UpdateProcessException(String message) {
        super(message);

    }

    public UpdateProcessException(Throwable cause) {
        super(cause);

    }

    public UpdateProcessException(String message, Throwable cause) {
        super(message, cause);
    }

}
