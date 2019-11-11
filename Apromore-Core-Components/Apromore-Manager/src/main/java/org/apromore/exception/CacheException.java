package org.apromore.exception;

/**
 * Root exception for all Apromore runtime exceptions.  This class is used as the root instead
 * of {@link java.lang.SecurityException} to remove the potential for conflicts;  many other
 * frameworks and products (such as J2EE containers) perform special operations when
 * encountering {@link java.lang.SecurityException}.
 *
 * @since 7.10
 */
public class CacheException extends RuntimeException {

    /**
     * Creates a new <code>CacheException</code>.
     */
    public CacheException() {
        super();
    }

    /**
     * Creates a new <code>CacheException</code>.
     *
     * @param message the reason for the exception.
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>CacheException</code>.
     *
     * @param cause the underlying cause of the exception.
     */
    public CacheException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new <code>CacheException</code>.
     *
     * @param message the reason for the exception.
     * @param cause   the underlying cause of the exception.
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
