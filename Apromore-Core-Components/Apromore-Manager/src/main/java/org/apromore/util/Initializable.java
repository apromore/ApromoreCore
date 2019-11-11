package org.apromore.util;

/**
 * Apromore container-agnostic interface that indicates that this object requires initialization.
 *
 * @since 7.10
 */
public interface Initializable {

    /**
     * Initializes this object.
     *
     * @throws Exception
     *          if an exception occurs during initialization.
     */
    void init() throws Exception;

}
