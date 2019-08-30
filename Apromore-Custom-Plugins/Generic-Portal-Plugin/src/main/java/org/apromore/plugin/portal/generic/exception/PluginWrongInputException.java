package org.apromore.plugin.portal.generic.exception;

public abstract class PluginWrongInputException extends Exception {
    public PluginWrongInputException(String errorMessage) {
        super(errorMessage);
    }
}
