package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.exception.PluginWrongInputException;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public class LogFilterWrongInputException extends PluginWrongInputException {
    public LogFilterWrongInputException(String errorMessage) {
        super(errorMessage);
    }
}
