package org.apromore.plugin.portal.processdiscoverer.impl.factory;

import org.apromore.apmlog.APMLog;
import org.apromore.logman.ALog;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.data.ContextData;
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.impl.apmlog.LogDataWithAPMLog;
import org.apromore.plugin.portal.processdiscoverer.impl.apmlog.LogFilterControllerWithAPMLog;
import org.apromore.plugin.portal.processdiscoverer.impl.apmlog.LogStatsControllerWithAPMLog;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;

public class PDCustomFactory extends PDStandardFactory {
    @Override
    public LogData createLogData(ContextData contextData, EventLogService eventLogService) throws Exception {
        XLog xlog = eventLogService.getXLog(contextData.getLogId());
        APMLog apmLog = eventLogService.getAggregatedLog(contextData.getLogId());
        if (xlog == null) {
            throw new InvalidDataException("XLog data of this log is missing");
        }
        if (apmLog == null) {
            throw new InvalidDataException("APMLog data of this log is missing");
        }
        long timer = System.currentTimeMillis();
        ALog aLog = new ALog(xlog);
        System.out.println("ALog.constructor: " + (System.currentTimeMillis() - timer) + " ms.");
        
        return new LogDataWithAPMLog(contextData.getConfigData(), aLog, apmLog);
    }
    
    @Override
    public LogStatsController createLogStatsController(PDController pdController) throws Exception {
        return new LogStatsControllerWithAPMLog(pdController);
    }
    
    @Override
    public LogFilterController createLogFilterController(PDController pdController) throws Exception {
        return new LogFilterControllerWithAPMLog(pdController);
    }
}
