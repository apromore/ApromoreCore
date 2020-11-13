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
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.time.ZonedDateTime;
import java.util.List;

public class LogFactory {

    public static APMLog convertXLog(XLog xLog) {

        ImmutableLog log = new ImmutableLog();

        for (int i = 0; i < xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);

            ImmutableTrace trace = new ImmutableTrace(i, i, getAttributes(xTrace, log));

            IntArrayList markedIndexes = new IntArrayList();

            double ttlProcTime = 0;
            double avgProcTime = 0;
            double maxProcTime = 0;
            double ttlWaitTime = 0;
            double avgWaitTime = 0;
            double maxWaitTime = 0;


            for (int j = 0; j < xTrace.size(); j++) {

                ImmutableEvent event = new ImmutableEvent(j, trace, xTrace.get(j));
                trace.addEvent(event);

                int actSize = trace.getActivityList().size();

                if (!markedIndexes.contains(j)) {

                    UnifiedMap<String, String> activityAttributes = getAttributes(xTrace.get(j));

                    ImmutableActivity activity = getActivity(actSize, trace, xTrace, j, markedIndexes);

                    trace.addActivity(activity);

                    ttlProcTime += activity.getDuration();

                    if (activity.getDuration() > maxProcTime) maxProcTime = activity.getDuration();

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
            for (int k = 0; k < actList.size(); k++) {
                if (k+1 < actList.size()) {
                    AActivity kAct = actList.get(k);
                    AActivity nAct = actList.get(k+1);
                    double kET = kAct.getEndTimeMilli();
                    double nST = nAct.getStartTimeMilli();
                    double wt = nST > kET ? nST - kET : 0;
                    ttlWaitTime += wt;

                    if (wt > maxWaitTime) maxWaitTime = wt;
                }
            }

            avgProcTime = ttlProcTime > 0 ? ttlProcTime / trace.getActivityList().size() : 0;
            avgWaitTime = ttlWaitTime > 0 ? ttlWaitTime / (trace.getActivityList().size()-1) : 0;

            trace.setTotalProcessingTime(ttlProcTime);
            trace.setAverageProcessingTime(avgProcTime);
            trace.setMaxProcessingTime(maxProcTime);
            trace.setTotalWaitingTime(ttlWaitTime);
            trace.setAverageWaitingTime(avgWaitTime);
            trace.setMaxWaitingTime(maxWaitTime);

            double dur = trace.getDuration();
            double caseUtil = ttlProcTime > 0 && ttlProcTime < dur ? ttlProcTime / dur : 0;
            trace.setCaseUtilization(caseUtil);

            log.add(trace);
        }


        log.setEventAttributeOccurMap(new UnifiedMap<>());

        List<ATrace> traceList = log.getTraceList();

        for (int i = 0; i < traceList.size(); i++) {
            ATrace trace = traceList.get(i);
            List<AActivity> activityList = trace.getActivityList();
            for (int j = 0; j < activityList.size(); j++) {
                AActivity activity = activityList.get(j);
                fillAttributeOccurMap(activity, log.getEventAttributeOccurMap());
            }
        }

        log.updateStats();

        return log;
    }


    public static UnifiedMap<String, String> getAttributes(XEvent xEvent) {
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
        UnifiedMap<String, UnifiedMap<String, Integer>> caseAttrValFreqMap = log.getCaseAttributeValueFreqMap();

        UnifiedMap<String, String> map = new UnifiedMap<>();
        XAttributeMap xAttributeMap = xTrace.getAttributes();
        for (String key : xAttributeMap.keySet()) {
            String val = xAttributeMap.get(key).toString();
            map.put(key.intern(), val.intern());

            if (!key.equals("concept:name") && !key.equals("case:variant")) {
                if (caseAttrValFreqMap.containsKey(key)) {
                    UnifiedMap<String, Integer> valFreqMap = caseAttrValFreqMap.get(key);
                    if (valFreqMap.containsKey(val)) {
                        int freq = valFreqMap.get(val) + 1;
                        valFreqMap.put(val, freq);
                    } else valFreqMap.put(val, 1);
                } else {
                    UnifiedMap<String, Integer> valFreqMap = new UnifiedMap<>();
                    valFreqMap.put(val, 1);
                    caseAttrValFreqMap.put(key, valFreqMap);
                }
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

        if (proceed) {
            for (int i = fromIndex + 1; i < xTrace.size(); i++) {
                if (!markedIndexes.contains(i)) {
                    XEvent nEvent = xTrace.get(i);

                    if (haveCommonMainAttributes(nEvent, baseEvent)) {
                        String lifecycle = nEvent.getAttributes().containsKey("lifecycle:transition") ?
                                nEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase() :
                                "complete";
                        if (lifecycle.equals("complete") ||
                                lifecycle.equals("manualskip") ||
                                lifecycle.equals("autoskip")) {
                            eventIndexList.add(i);

                            long nT = getTimestamp(nEvent);
                            if (nT > endTime) endTime = nT;

                            break;
                        } else {
                            eventIndexList.add(i);
                        }
                    }
                }
            }
        }

        markedIndexes.addAll(eventIndexList);

        UnifiedMap<String, String> attributes = getAttributes(baseEvent);

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
