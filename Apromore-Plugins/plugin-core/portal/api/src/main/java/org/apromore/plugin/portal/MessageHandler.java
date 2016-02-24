package org.apromore.plugin.portal;

/**
 * Communication interface to display informational/warning messages on the Portal
 */
public interface MessageHandler {

    /**
     *  Display the supplied message with INFO level.
     *
     * @param message
     */
    void displayInfo(String message);

    /**
     *
     * Display the supplied message and the exception
     *
     * @param message
     * @param exception
     */
    void displayError(String message, Exception exception);

    /**
     * Display the supplied message
     *
     * @param level
     * @param message
     */
    void displayMessage(Level level, String message);

}
