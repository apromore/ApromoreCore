package org.apromore.filestore.webdav.exceptions;

public class LockFailedException extends WebDavException {

    public LockFailedException() {
        super();
    }

    public LockFailedException(String message) {
        super(message);
    }

    public LockFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockFailedException(Throwable cause) {
        super(cause);
    }
}
