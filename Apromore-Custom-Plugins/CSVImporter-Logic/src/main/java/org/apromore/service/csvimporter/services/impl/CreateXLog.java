/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.service.csvimporter.services.impl;

import org.apromore.service.csvimporter.model.LogEventModel;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

import java.sql.Timestamp;
import java.util.*;

public class CreateXLog {

    public XLog generateXLog(List<LogEventModel> events) {
        if (events == null) return null;

        XFactory xFactory = new XFactoryNaiveImpl();

        XConceptExtension concept = XConceptExtension.instance();
        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        XOrganizationalExtension resource = XOrganizationalExtension.instance();

        XLog xLog = xFactory.createLog();
        xLog.getExtensions().add(concept);
        xLog.getExtensions().add(lifecycle);
        xLog.getExtensions().add(timestamp);
        xLog.getExtensions().add(resource);

        lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);

        XTrace xTrace = null;
        XEvent xEvent;
        List<XEvent> allEvents = new ArrayList<XEvent>();
        String newTraceID = null;    // to keep track of events, when a new trace is created we assign its value and add the respective events for the trace.
        Map<String, String> caseAttributes = new HashMap<>();

        for (LogEventModel myEvent : events) {
            String caseID = myEvent.getCaseID();

            if (newTraceID == null || !newTraceID.equals(caseID)) {    // This could be new Trace

                assignEventsToTrace(allEvents, xTrace);
                assignMyCaseAttributes(caseAttributes, xTrace);
                allEvents = new ArrayList<>();
                caseAttributes = new HashMap<>();

                xTrace = xFactory.createTrace();
                concept.assignName(xTrace, caseID);
                xLog.add(xTrace);
                newTraceID = caseID;
            }


            setMyCaseAttributes(caseAttributes, myEvent.getCaseAttributes());

            if (myEvent.getStartTimestamp() != null) {
                xEvent = createEvent(myEvent, false);
                allEvents.add(xEvent);
            }

            xEvent = createEvent(myEvent, true);
            allEvents.add(xEvent);
        }

        // for last trace
        assignEventsToTrace(allEvents, xTrace);
        assignMyCaseAttributes(caseAttributes, xTrace);
        return xLog;
    }

    private void assignEventsToTrace(List<XEvent> allEvents, XTrace xTrace){
        if (allEvents != null && !allEvents.isEmpty()) {
            Comparator<XEvent> compareTimestamp = (XEvent o1, XEvent o2) -> {
                Date o1Date;
                Date o2Date;
                if (o1.getAttributes().get("time:timestamp") != null) {
                    XAttribute o1da = o1.getAttributes().get("time:timestamp");
                    if (((XAttributeTimestamp) o1da).getValue() != null) {
                        o1Date = ((XAttributeTimestamp) o1da).getValue();
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }

                if (o2.getAttributes().get("time:timestamp") != null) {
                    XAttribute o2da = o2.getAttributes().get("time:timestamp");
                    if (((XAttributeTimestamp) o2da).getValue() != null) {
                        o2Date = ((XAttributeTimestamp) o2da).getValue();
                    } else {
                        return 1;
                    }
                } else {
                    return 1;
                }

                if (o1Date == null || o1Date.toString().isEmpty()) {
                    return 1;
                } else if (o2Date == null || o2Date.toString().isEmpty()) {
                    return -1;
                } else {
                    return o1Date.compareTo(o2Date);
                }
            };

            allEvents.sort(compareTimestamp);
            xTrace.addAll(allEvents);
        }
    }

    private void assignMyCaseAttributes(Map<String, String> caseAttributes, XTrace xTrace){
        if(caseAttributes!= null && !caseAttributes.isEmpty()){
            XAttribute attribute;
            for (Map.Entry<String, String> entry : caseAttributes.entrySet()) {
                if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                    attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                    xTrace.getAttributes().put(entry.getKey(), attribute);
                }
            }
        }
    }

    private void setMyCaseAttributes(Map<String, String> caseAttributes, Map<String, String> eventCaseAttributes){
        for (Map.Entry<String, String> entry : eventCaseAttributes.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() != 0 && !caseAttributes.containsKey(entry.getKey())) {
                caseAttributes.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private XEvent createEvent(LogEventModel myEvent, Boolean isEndTimestamp) {

        XFactory xFactory = new XFactoryNaiveImpl();
        XEvent xEvent = xFactory.createEvent();

        XConceptExtension concept = XConceptExtension.instance();
        concept.assignName(xEvent, myEvent.getActivity());

        if (myEvent.getResource() != null) {
            XOrganizationalExtension resource = XOrganizationalExtension.instance();
            resource.assignResource(xEvent, myEvent.getResource());
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
}
