package org.apromore.apmlog.impl;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogService;
//import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;

/**
 * Frank Ma (16/11/2019)
 */
public class APMLogServiceImpl implements APMLogService {

    //private EventLogService eventLogService;

    //APMLogServiceImpl(final EventLogService newEventLogService) {
    //    this.eventLogService = newEventLogService;
    //}

    public APMLog findAPMLogForXLog(XLog xLog) {
        return new APMLog(xLog);
    }
}