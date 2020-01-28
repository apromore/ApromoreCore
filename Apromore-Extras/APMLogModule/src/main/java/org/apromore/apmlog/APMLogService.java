package org.apromore.apmlog;

import org.deckfour.xes.model.XLog;
/**
 * Frank Ma (16/11/2019)
 */
public interface APMLogService {

    public APMLog findAPMLogForXLog(XLog xLog);
}