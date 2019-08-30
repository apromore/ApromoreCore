package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.exception.PluginWrongOutputException;

public class LogFilterWrongOutputException extends PluginWrongOutputException {
    public LogFilterWrongOutputException(String errorMessage) {
        super(errorMessage);
    }
}
