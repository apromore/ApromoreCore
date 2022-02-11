/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.logimporter.services.legacy;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.Log;
import org.apromore.service.logimporter.common.EventLogImporter;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.services.LogProcessor;
import org.apromore.service.logimporter.utilities.XEventComparator;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

public abstract class AbstractLogImporter implements LogImporter {
    protected List<LogErrorReport> logErrorReport;
    protected LogProcessor logProcessor;

    @Inject
    EventLogImporter eventLogImporter;
    @Inject
    ConfigBean config;

    protected XEvent createEvent(LogEventModel myEvent, boolean isEndTimestamp) {

        XFactory xFactory = new XFactoryNaiveImpl();
        XEvent xEvent = xFactory.createEvent();

        XConceptExtension concept = XConceptExtension.instance();
        concept.assignName(xEvent, myEvent.getActivity());

        XOrganizationalExtension orgExtension = XOrganizationalExtension.instance();
        if (myEvent.getResource() != null) {
            orgExtension.assignResource(xEvent, myEvent.getResource());
        }

        if (myEvent.getRole() != null) {
            orgExtension.assignRole(xEvent, myEvent.getRole());
        }

        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        if (isEndTimestamp) {
            lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
            timestamp.assignTimestamp(xEvent, myEvent.getEndTimestamp());
        } else {
            lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
            timestamp.assignTimestamp(xEvent, myEvent.getStartTimestamp());
        }

        XAttribute attribute;
        if (myEvent.getOtherTimestamps() != null) {
            Map<String, Timestamp> otherTimestamps = myEvent.getOtherTimestamps();
            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }

        Map<String, String> eventAttributes = myEvent.getEventAttributes();
        for (Map.Entry<String, String> entry : eventAttributes.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        return xEvent;
    }

    protected void assignEventsToTrace(LogEventModel logEventModel, XTrace xTrace) {
        XEvent xEvent;

        if (logEventModel.getStartTimestamp() != null) {
            xEvent = createEvent(logEventModel, false);
            xTrace.add(xEvent);
        }
        xEvent = createEvent(logEventModel, true);
        xTrace.add(xEvent);
    }

    protected void assignMyCaseAttributes(Map<String, String> caseAttributes, XTrace xTrace) {
        XAttributeMap xAttributeMap = xTrace.getAttributes();

        if (caseAttributes != null && !caseAttributes.isEmpty()) {
            XAttribute attribute;
            for (Map.Entry<String, String> entry : caseAttributes.entrySet()) {
                if (entry.getValue() != null && entry.getValue().trim().length() != 0
                    && !xAttributeMap.containsKey(entry.getKey())) {
                    attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                    xTrace.getAttributes().put(entry.getKey(), attribute);
                }
            }
        }
    }

    protected boolean isValidLineCount(int lineCount) {
        return config == null || config.getMaxEventCount() == null || lineCount <= config.getMaxEventCount();
    }

    protected void constructTrace(
        final TreeMap<String, XTrace> tracesHistory, final LogEventModel logEventModel,
        final XFactory xFactory, XConceptExtension concept) {
        if (tracesHistory.isEmpty() || !tracesHistory.containsKey(logEventModel.getCaseID())) {
            XTrace xT = xFactory.createTrace();
            concept.assignName(xT, logEventModel.getCaseID());
            assignEventsToTrace(logEventModel, xT);
            assignMyCaseAttributes(logEventModel.getCaseAttributes(), xT);
            tracesHistory.put(logEventModel.getCaseID(), xT);
        } else {
            XTrace xT = tracesHistory.get(logEventModel.getCaseID());
            assignEventsToTrace(logEventModel, xT);
            assignMyCaseAttributes(logEventModel.getCaseAttributes(), xT);
        }
    }

    protected void sortAndFeedLog(final TreeMap<String, XTrace> tracesHistory, final XLog xLog) {
        tracesHistory.forEach(
            (k, v) -> {
                v.sort(new XEventComparator());
                xLog.add(v);
            });
    }

    protected Log importXesLog(String username, Integer folderId, String logName, boolean skipInvalidRow, XLog xLog)
        throws Exception {
        Log log = null;
        if (username != null
            && !username.isEmpty()
            && folderId != null
            && logName != null
            && !logName.isEmpty()
            // 1. If there is invalid row and skipInvalidRow equals false, then don't import XES and
            // keep log  null.
            // 2. If there is invalid row and skipInvalidRow equals true (when user click 'Skip
            // invalid row/s'), then import this Log with invalid row skipped.
            && (logErrorReport.isEmpty() || skipInvalidRow)) {

            log = eventLogImporter.importXesLog(xLog, username, folderId, logName);
        }
        return log;
    }
}