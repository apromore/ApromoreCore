/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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
