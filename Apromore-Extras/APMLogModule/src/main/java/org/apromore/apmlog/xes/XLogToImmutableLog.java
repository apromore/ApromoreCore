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
package org.apromore.apmlog.xes;

import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.logobjects.ImmutableEvent;
import org.apromore.apmlog.logobjects.ImmutableLog;
import org.apromore.apmlog.logobjects.ImmutableTrace;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

public class XLogToImmutableLog {

    public static ImmutableLog convertXLog(String logName, XLog xLog) throws EmptyInputException {

        validateXLog(xLog);

        if (xLog.isEmpty()) throw new EmptyInputException(xLog);

        HashBiMap<String, Integer> activityNameIndicatorMap = getActivityNameIndicatorMap(xLog);

        List<ATrace> traces = new ArrayList<>(xLog.size());

        int traceIndex = 0;
        for (XTrace xTrace : xLog) {
            UnifiedMap<String, String> caseAttributes = getAttributes(xTrace);

            List<Integer>  markedEventIndexes = new ArrayList<>();

            int eventIndex = 0;
            int actIndex = 0;


            List<ActivityInstance> activityInstances = new ArrayList<>();

            for (XEvent xEvent : xTrace) {

                if (!markedEventIndexes.contains(eventIndex)) {
                    try {
                        int actNameIndicator = activityNameIndicatorMap.get(getConceptName(xEvent));
                        ActivityInstance instance = getActivityInstance(actIndex, actNameIndicator,
                                eventIndex, traceIndex, getConceptName(xTrace), xTrace, markedEventIndexes);

                        activityInstances.add(instance);
                        actIndex += 1;
                    } catch (Exception e) {
                        System.out.println("");
                    }
                }

                eventIndex += 1;
            }

            List<ImmutableEvent> immutableEvents = new ArrayList<>();
            for (int i = 0; i < xTrace.size(); i++) {
                immutableEvents.add(new ImmutableEvent(i));
            }

            ImmutableTrace iTrace = new ImmutableTrace(traceIndex, getConceptName(xTrace), immutableEvents,
                    activityInstances, caseAttributes);

            traces.add(iTrace);
            traceIndex += 1;
        }

        return new ImmutableLog(logName, traces, activityNameIndicatorMap);
    }

    private static String getConceptName(XAttributable xAttributable) {
        return xAttributable.getAttributes().containsKey("concept:name") ?
                xAttributable.getAttributes().get("concept:name").toString() : "";
    }

    private static HashBiMap<String, Integer> getActivityNameIndicatorMap(XLog xLog) {
        List<XEvent> allXEvents = xLog.stream()
                .flatMap(xTrace -> xTrace.stream())
                .collect(Collectors.toList());

        Set<String> allConceptNames = allXEvents.stream()
                .filter(xEvent -> xEvent.getAttributes().containsKey("concept:name"))
                .map(xEvent -> xEvent.getAttributes().get("concept:name").toString())
                .collect(Collectors.toSet());

        if (allConceptNames == null || allConceptNames.isEmpty()) allConceptNames = new HashSet<>(Arrays.asList(""));

        List<String> conceptNameList = new ArrayList<>(allConceptNames);
        Collections.sort(conceptNameList);

        HashBiMap<String, Integer> indicatorMap = new HashBiMap<>(conceptNameList.size());
        int indicator = 0;
        for (String conceptName : conceptNameList) {
            indicatorMap.put(conceptName, indicator);
            indicator += 1;
        }

        return indicatorMap;
    }

    private static void validateXLog(XLog xLog) {
        List<XTrace> tobeRemovedTraces  = new ArrayList<>();

        for(XTrace trace : xLog) {

            List<XEvent> tobeRemovedEvents = new ArrayList<>();

            for(XEvent event : trace) {

                if(!event.getAttributes().containsKey("lifecycle:transition")) {
                    event.getAttributes().put("lifecycle:transition",
                            new XAttributeLiteralImpl("lifecycle:transition", "complete", null));
                }

                if (isInvalidEvent(event)) {
                    tobeRemovedEvents.add(event);
                }
            }

            if (!tobeRemovedEvents.isEmpty()) {
                trace.removeAll(tobeRemovedEvents);
            }

            if (trace.isEmpty()) tobeRemovedTraces.add(trace);
        }

        if (!tobeRemovedTraces.isEmpty()) xLog.removeAll(tobeRemovedTraces);
    }

    private static boolean isInvalidEvent(XEvent event) {
        return !event.getAttributes().containsKey("time:timestamp") ||
                !event.getAttributes().containsKey("concept:name") ||
                (!event.getAttributes().get("lifecycle:transition").toString()
                        .equalsIgnoreCase("start") &&
                        !event.getAttributes().get("lifecycle:transition").toString()
                                .equalsIgnoreCase("complete"));
    }

    private static UnifiedMap<String, String> getAttributes(XTrace xTrace) {
        UnifiedMap<String, String> map = new UnifiedMap<>();
        XAttributeMap xAttributeMap = xTrace.getAttributes();
        for (String key : xAttributeMap.keySet()) {
            if (!key.toLowerCase().equals("concept:name") ) {
                String val = xAttributeMap.get(key).toString();
                map.put(key.intern(), val.intern());
            }
        }
        return map;
    }

    private static UnifiedMap<String, String> getAttributes(XEvent xEvent) {
        UnifiedMap<String, String> map = new UnifiedMap<>();
        XAttributeMap xAttributeMap = xEvent.getAttributes();
        for (String key : xAttributeMap.keySet()) {
            if (!key.toLowerCase().equals("time:timestamp") &&
                    !key.toLowerCase().equals("lifecycle:transition") ) {
                String val = xAttributeMap.get(key).toString();
                map.put(key.intern(), val.intern());
            }
        }
        return map;
    }

    private static ActivityInstance getActivityInstance(int actIndex,
                                                        int actNameIndicator,
                                                        int baseEventIndex,
                                                        int traceIndex,
                                                        String traceId,
                                                        XTrace xTrace,
                                                        List<Integer> markedEventIndexes) {
        List<Integer> actEventIndexList = new ArrayList<>();
        boolean proceed = true;

        XEvent baseEvent = xTrace.get(baseEventIndex);
        actEventIndexList.add(baseEventIndex);

        long baseT = getTimestamp(baseEvent);
        long startTime = baseT;
        long endTime = baseT;

        String baseLife = baseEvent.getAttributes().containsKey("lifecycle:transition") ?
                baseEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase() : "complete";

        if (baseEventIndex == xTrace.size() - 1) proceed = false;
        if (baseLife.equals("complete")) proceed = false;
        boolean foundStart = baseLife.equals("start");

        XEvent startEvent = baseEvent;

        if (proceed) {

            for (int i = baseEventIndex + 1; i < xTrace.size(); i++) {
                if (!markedEventIndexes.contains(i)) {
                    XEvent nEvent = xTrace.get(i);

                    if (haveCommonMainAttributes(nEvent, baseEvent)) {
                        String lifecycle = nEvent.getAttributes().containsKey("lifecycle:transition") ?
                                nEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase() :
                                "complete";

                        if (foundStart && lifecycle.equals("start")) {
                            // SKIP this event; it is the start of another activity with the same concept:name
                        } else {

                            long nT = getTimestamp(nEvent);

                            if (lifecycle.equals("start") && !foundStart) {
                                startTime = nT;
                                startEvent = nEvent;
                            }

                            if (nT > endTime) endTime = nT;

                            if (lifecycle.equals("complete") ||
                                    lifecycle.equals("manualskip") ||
                                    lifecycle.equals("autoskip")) {

                                actEventIndexList.add(i);
                                break;

                            } else {
                                if (lifecycle.equals("start")) {
                                    if (!foundStart) {
                                        actEventIndexList.add(i);
                                        foundStart = true;
                                    }
                                } else {
                                    actEventIndexList.add(i);
                                }
                            }
                        }
                    }
                }
            }
        }

        markedEventIndexes.addAll(actEventIndexList);

        UnifiedMap<String, String> attributes = getAttributes(startEvent);

        ActivityInstance activityInstance = new ActivityInstance(actIndex, actEventIndexList, traceIndex,
                traceId, actNameIndicator, startTime, endTime, attributes);

        return activityInstance;
    }

    private static long getTimestamp(XEvent xEvent) {
        try {
            ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);
            return Util.epochMilliOf(zdt);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean haveCommonMainAttributes(XEvent event1, XEvent event2) {
        String name1 = event1.getAttributes().get("concept:name").toString();
        String name2 = event2.getAttributes().get("concept:name").toString();
        return name1.equals(name2);
    }
}
