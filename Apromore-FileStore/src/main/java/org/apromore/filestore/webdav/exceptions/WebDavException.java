package org.apromore.filestore.webdav.exceptions;

public class WebDavException extends RuntimeException {

    public WebDavException() {
        super();
    }

    public WebDavException(String message) {
        super(message);
    }

    public WebDavException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebDavException(Throwable cause) {
        super(cause);
    }
}
