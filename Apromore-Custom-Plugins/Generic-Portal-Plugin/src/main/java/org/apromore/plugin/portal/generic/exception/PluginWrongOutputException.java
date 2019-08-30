package org.apromore.plugin.portal.generic.exception;

public abstract class PluginWrongOutputException extends Exception {
    public PluginWrongOutputException(String errorMessage) {
        super(errorMessage);
    }
}
