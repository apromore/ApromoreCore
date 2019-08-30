package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.exception.PluginWrongInputException;

public class LogFilterWrongInputException extends PluginWrongInputException {
    public LogFilterWrongInputException(String errorMessage) {
        super(errorMessage);
    }
}
