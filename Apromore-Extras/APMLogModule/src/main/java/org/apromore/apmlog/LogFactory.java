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
package org.apromore.apmlog;


import org.apromore.apmlog.immutable.ImmutableActivity;
import org.apromore.apmlog.immutable.ImmutableEvent;
import org.apromore.apmlog.immutable.ImmutableLog;
import org.apromore.apmlog.immutable.ImmutableTrace;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chii Chang
 */
public class LogFactory {

    private static void validateXLog(XLog xLog) {
        List<XTrace> tobeRemovedTraces  = new ArrayList<>();

        for (XTrace xTrace : xLog) {

            List<XEvent> tobeRemovedEvents = xTrace.stream()
                    .filter(e -> !e.getAttributes().containsKey("time:timestamp") ||
                            !e.getAttributes().containsKey("lifecycle:transition") ||
                            (!e.getAttributes().get("lifecycle:transition").toString()
                                    .equalsIgnoreCase("start") &&
                                    !e.getAttributes().get("lifecycle:transition").toString()
                                            .equalsIgnoreCase("complete")))
                    .collect(Collectors.toList());

            if (!tobeRemovedEvents.isEmpty()) {
                xTrace.removeAll(tobeRemovedEvents);
            }

            if (xTrace.isEmpty()) tobeRemovedTraces.add(xTrace);
        }

        if (!tobeRemovedTraces.isEmpty()) xLog.removeAll(tobeRemovedTraces);
    }

    public static APMLog convertXLog(XLog xLog) {

        validateXLog(xLog);

        ImmutableLog log = new ImmutableLog();

        for (int i = 0; i < xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);

            UnifiedMap<String, String> attributes = getAttributes(xTrace, log);

            ImmutableTrace trace = new ImmutableTrace(i, i,
                    attributes.containsKey("concept:name") ? attributes.get("concept:name") : i +"",
                    attributes);

            IntArrayList markedIndexes = new IntArrayList();

            for (int j = 0; j < xTrace.size(); j++) {

                ImmutableEvent event = new ImmutableEvent(j, trace, xTrace.get(j));
                trace.addEvent(event);

                int actSize = trace.getActivityList().size();

                if (!markedIndexes.contains(j)) {

                    ImmutableActivity activity = getActivity(actSize, trace, xTrace, j, markedIndexes);

                    trace.addActivity(activity);

                    if (!log.getActivityNameBiMap().containsKey(activity.getName())) {
                        int index = log.getActivityNameBiMap().size();
                        log.getActivityNameBiMap().put(activity.getName(), index);
                    }

                    if (activity.getEventIndexes().contains(j)) {
                        event.setParentActivityIndex(actSize);
                    }
                }
            }

            List<AActivity> actList = trace.getActivityList();

            DoubleArrayList processingTimes = new DoubleArrayList();
            DoubleArrayList waitingTimes = new DoubleArrayList();

            for (int k = 0; k < actList.size(); k++) {
                processingTimes.add(actList.get(k).getDuration());

                if (k+1 < actList.size()) {
                    AActivity kAct = actList.get(k);
                    AActivity nAct = actList.get(k+1);
                    double kET = kAct.getEndTimeMilli();
                    double nST = nAct.getStartTimeMilli();
                    double wt = nST > kET ? nST - kET : 0;
                    waitingTimes.add(wt);
                }
            }

            trace.setProcessingTimes(processingTimes);
            trace.setWaitingTimes(waitingTimes);

            log.add(trace);
        }


        log.setEventAttributeOccurMap(new UnifiedMap<>());

        List<ATrace> traceList = log.getTraceList();

        for (ATrace trace : traceList) {
            List<AActivity> activityList = trace.getActivityList();
            for (AActivity activity : activityList) {
                fillAttributeOccurMap(activity, log.getEventAttributeOccurMap());
            }
        }

        log.updateStats();

        return log;
    }


    private static UnifiedMap<String, String> getAttributes(XEvent xEvent) {
        UnifiedMap<String, String> map = new UnifiedMap<>();
        XAttributeMap xAttributeMap = xEvent.getAttributes();
        for (String key : xAttributeMap.keySet()) {
            if (!key.toLowerCase().equals("time:timestamp") &&
                    !key.toLowerCase().equals("lifecycle:transition")) {
                String val = xAttributeMap.get(key).toString();
                map.put(key.intern(), val.intern());
            }
        }
        return map;
    }


    private static UnifiedMap<String, String> getAttributes(XTrace xTrace, ImmutableLog log) {

        UnifiedMap<String, String> map = new UnifiedMap<>();
        XAttributeMap xAttributeMap = xTrace.getAttributes();
        for (String key : xAttributeMap.keySet()) {
            if (!key.equals("case:variant")) {
                String val = xAttributeMap.get(key).toString();
                map.put(key.intern(), val.intern());
            }
        }
        return map;
    }


    private static long getTimestamp(XEvent xEvent) {
        try {
            ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);
            return Util.epochMilliOf(zdt);
        } catch (Exception e) {
            return 0;
        }

    }


    private static ImmutableActivity getActivity(int index, ImmutableTrace trace, XTrace xTrace, int fromIndex,
                                                 IntArrayList markedIndexes) {

        IntArrayList eventIndexList = new IntArrayList();

        boolean proceed = true;

        XEvent baseEvent = xTrace.get(fromIndex);
        eventIndexList.add(fromIndex);

        long baseT = getTimestamp(baseEvent);

        long startTime = baseT;
        long endTime = baseT;

        String baseLife = baseEvent.getAttributes().containsKey("lifecycle:transition") ?
                baseEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase() : "complete";

        if (fromIndex == xTrace.size() - 1) proceed = false;
        if (baseLife.equals("complete")) proceed = false;

        boolean foundStart = baseLife.equals("start");

        XEvent startEvent = baseEvent;

        if (proceed) {

            for (int i = fromIndex + 1; i < xTrace.size(); i++) {
                if (!markedIndexes.contains(i)) {
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

                                eventIndexList.add(i);
                                break;

                            } else {
                                if (lifecycle.equals("start")) {
                                    if (!foundStart) {
                                        eventIndexList.add(i);
                                        foundStart = true;
                                    }
                                } else {
                                    eventIndexList.add(i);
                                }
                            }
                        }
                    }
                }
            }
        }

        markedIndexes.addAll(eventIndexList);

        UnifiedMap<String, String> attributes = getAttributes(startEvent);

        ImmutableActivity activity =
                new ImmutableActivity(index, index, trace, eventIndexList, startTime, endTime, attributes);

        return activity;
    }


    private static boolean haveCommonMainAttributes(XEvent event1, XEvent event2) {
        String name1 = event1.getAttributes().get("concept:name").toString();
        String name2 = event2.getAttributes().get("concept:name").toString();
        return name1.equals(name2);
    }


    private static boolean haveCommonMainAttributes(AEvent event1, AEvent event2) {
        String name1 = event1.getName();
        String name2 = event2.getName();

        return name1.equals(name2);
    }


    public static void fillAttributeOccurMap(AActivity activity,
                                             UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>>
                                                     attributeOccurMap) {
        UnifiedMap<String, String> attributes = activity.getAttributes();
        for (String key : attributes.keySet()) {
            String val = attributes.get(key);
            if (!attributeOccurMap.containsKey(key)) {
                UnifiedSet<AActivity> activitySet = new UnifiedSet<>();
                activitySet.add(activity);
                UnifiedMap<String, UnifiedSet<AActivity>> valMap = new UnifiedMap<>();
                valMap.put(val, activitySet);
                attributeOccurMap.put(key, valMap);
            } else {
                UnifiedMap<String, UnifiedSet<AActivity>> valMap = attributeOccurMap.get(key);
                if (!valMap.containsKey(val)) {
                    UnifiedSet<AActivity> activitySet = new UnifiedSet<>();
                    activitySet.add(activity);
                    valMap.put(val, activitySet);
                } else {
                    UnifiedSet<AActivity> activitySet = valMap.get(val);
                    if (!activitySet.contains(activity)) activitySet.add(activity);
                }
            }
        }
    }
}
