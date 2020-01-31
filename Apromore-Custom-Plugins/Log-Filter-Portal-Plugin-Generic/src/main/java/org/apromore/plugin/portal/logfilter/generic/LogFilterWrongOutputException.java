package org.apromore.plugin.portal.logfilter.generic;

import org.apromore.plugin.portal.generic.exception.PluginWrongOutputException;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public class LogFilterWrongOutputException extends PluginWrongOutputException {
    public LogFilterWrongOutputException(String errorMessage) {
        super(errorMessage);
    }
}
