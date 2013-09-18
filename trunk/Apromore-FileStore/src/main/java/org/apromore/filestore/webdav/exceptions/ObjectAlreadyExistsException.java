package org.apromore.filestore.webdav.exceptions;

public class ObjectAlreadyExistsException extends WebDavException {

    public ObjectAlreadyExistsException() {
        super();
    }

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }

    public ObjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
