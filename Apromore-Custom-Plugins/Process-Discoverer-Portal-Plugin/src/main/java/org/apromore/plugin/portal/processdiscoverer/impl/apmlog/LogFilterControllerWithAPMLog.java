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

package org.apromore.plugin.portal.processdiscoverer.impl.apmlog;

import org.apromore.apmlog.filter.APMLogFilterPackage;
import org.apromore.apmlog.filter.PLog;
import org.apromore.plugin.portal.logfilter.generic.LogFilterContext;
import org.apromore.plugin.portal.logfilter.generic.LogFilterInputParams;
import org.apromore.plugin.portal.logfilter.generic.LogFilterOutputResult;
import org.apromore.plugin.portal.logfilter.generic.LogFilterResultListener;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.controllers.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.data.InvalidDataException;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * LogFilterControllerWithAPMLog is {@link LogFilterController} but uses APMLog to do filtering.
 * 
 * @author Bruce Nguyen
 *
 */
public class LogFilterControllerWithAPMLog extends LogFilterController implements LogFilterResultListener {
    private LogDataWithAPMLog logData;
    public LogFilterControllerWithAPMLog(PDController controller) throws Exception {
        super(controller);
        if (!(parent.getLogData() instanceof LogDataWithAPMLog)) {
            throw new InvalidDataException("Expect LogDataWithAPMLog data but receiving different data!");
        }
        else {
            logData = (LogDataWithAPMLog)controller.getLogData();            
        }
    }

    @Override
    // Open LogFilter window
    public void onEvent(Event event) throws Exception {
        // Store in session for LogFilterEE to work with APMLog
        Session session = Sessions.getCurrent();
        session.setAttribute("apmlog_tobe_filtered", logData.getOriginalAPMLog());
        session.setAttribute("filtered_log_name", parent.getTitle()); // required for showing log name on the title of FilterEE
        Sessions.getCurrent().setAttribute("sourceLogId", parent.getSourceLogId());

        parent.getLogFilterPlugin().execute(new LogFilterContext(parent.getContextData().getPortalContext()), 
                new LogFilterInputParams(
                        parent.getLogData().getLog(), 
                        parent.getUserOptions().getMainAttributeKey(), 
                        parent.getLogData().getCurrentFilterCriteria()),
                        this);

        subscribeFilterResult();
    }

    @Override
    public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception {
        // This has been replaced with ZK Event Queue in onEvent().
    }

    @Override
    public void clearFilter() throws Exception {
        logData.clearFilter();
        parent.updateUI(true);
    }

    @Override
    public void subscribeFilterResult() {
        // Process filtering result
        EventQueue<Event> filterEventQueue = EventQueues.lookup("apmlog_filter_package", EventQueues.DESKTOP, true);
        EventListener<Event> filteredLogEventListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                try {
                    APMLogFilterPackage result = (APMLogFilterPackage) event.getData();
                    PLog pLog = result.getPLog();
                    if (!pLog.getPTraceList().isEmpty()) {
                        parent.getLogData().setCurrentFilterCriteria(result.getCriteria());
                        logData.updateLog(pLog, result.getFilteredAPMLog());
                        parent.updateUI(true);
                    }
                }
                finally {
                    filterEventQueue.unsubscribe(this);
                }
            }
        };
        filterEventQueue.subscribe(filteredLogEventListener);
    }
    
}
