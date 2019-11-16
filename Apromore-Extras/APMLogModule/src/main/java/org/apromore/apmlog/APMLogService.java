package org.apromore.apmlog;

import org.deckfour.xes.model.XLog;

public interface APMLogService {

    public APMLog findAPMLogForXLog(XLog xLog);
}