package org.apromore.exception;

/**
 * Exception occurs when serializing or deserializing from the Canonical format to a Process Model Graph.
 *
 * @author cameron.james@suncorp.com.au
 */
public class SerializationException extends Exception {

    public SerializationException() { }

    public SerializationException(String arg0) {
        super(arg0);
    }

    public SerializationException(Throwable arg0) {
        super(arg0);
    }

    public SerializationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
