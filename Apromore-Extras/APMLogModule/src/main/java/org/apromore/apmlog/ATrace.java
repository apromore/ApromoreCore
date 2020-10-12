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

import org.apromore.apmlog.stats.AAttributeGraph;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (06/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (17/02/2020)
 * Modified: Chii Chang (20/02/2020)
 * Modified: Chii Chang (11/04/2020)
 * Modified: Chii Chang (07/05/2020)
 * Modified: Chii Chang (19/05/2020)
 * Modified: Chii Chang (24/05/2020)
 * Modified: Chii Chang (01/06/2020)
 * Modified: Chii Chang (05/06/2020)
 * Modified: Chii Chang (07/10/2020) - include "schedule" event to activity
 */
public class ATrace extends LaTraceImpl implements Comparable<ATrace>, Serializable, LaTrace {

    private String caseId = "";
    public long caseIdDigit = 0;
    private int caseVariantId = 0;
    private int caseVariantIdForDisplay;
    private long startTimeMilli = 0;
    private long endTimeMilli = 0;
    private long duration = 0;
    private boolean hasActivity = false;
    private long totalProcessingTime = 0;
    private long averageProcessingTime = 0;
    private long maxProcessingTime = 0;
    private long totalWaitingTime = 0;
    private long averageWaitingTime = 0;
    private long maxWaitingTime = 0;
    private double caseUtilization = 0.0;

    public String startTimeString, endTimeString, durationString;

    private List<AActivity> activityList;
    private List<AEvent> eventList;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap;
    private UnifiedMap<String, String> attributeMap;
    private List<String> activityNameList;
    private UnifiedSet<String> eventNameSet;

    private List<Integer> activityNameIndexList;

//    private UnifiedMap<String, UnifiedMap<String, List<TraceActivityIndex>>> attributeOccurMap;

//    private IntArrayList markedIndex;

    private APMLog apmLog;

    private int index = -1;
//    private AAttributeGraph aAttributeGraph = new AAttributeGraph();

    public ATrace(int index, XTrace xTrace, APMLog apmLog) {
        this.index = index;
        setEventList(xTrace);
        setCaseAttributes(xTrace);
        initStats(apmLog);
    }

    public ATrace(XTrace xTrace, APMLog apmLog) {
        setEventList(xTrace);
        setCaseAttributes(xTrace);
        initStats(apmLog);
    }

    public ATrace(String caseIdString, List<AEvent> inputEventList,
                  UnifiedMap<String, String> caseAttributes, APMLog apmLog) {
        if (!caseIdString.equals("")) {
            this.caseId = caseIdString;
            if (caseIdString.matches("-?\\d+(\\.\\d+)?")) this.caseIdDigit = new Long(caseId);
        }

        this.eventList = inputEventList;
        this.attributeMap = caseAttributes;

        initStats(apmLog);
    }

    private void initStats(APMLog apmLog) {
        this.apmLog = apmLog;

        activityNameIndexList = new ArrayList<>();
        activityList = new ArrayList<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();

        activityNameList = new ArrayList<>();
        eventNameSet = new UnifiedSet<>();


        setStartAndEndTime();

        setActivities();


        /*---------------- Fill the other attributes ----------------*/
        long waitCount = 0;
        long processCount = 0;
        for (int i = 0; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);

            this.eventNameSet.put(activity.getName());
            this.activityNameList.add(activity.getName());
            this.activityNameIndexList.add(apmLog.getActivityNameMapper().set(activity.getName()));

            processCount += 1;
            this.totalProcessingTime += activity.getDuration();
            if (activity.getDuration()>maxProcessingTime) maxProcessingTime = activity.getDuration();

            if (i > 0) {
                AActivity pActivity = activityList.get(i-1);
                waitCount += 1;
                long waitTime = activity.getStartTimeMilli() - pActivity.getEndTimeMilli();
                if (waitTime < 0) waitTime = 0;
                this.totalWaitingTime += waitTime;
                if(waitTime > this.maxWaitingTime) {
                    this.maxWaitingTime = waitTime;
                }
            }
        }
        if(this.totalProcessingTime > 0 && processCount > 0) this.averageProcessingTime = this.totalProcessingTime / processCount;
        if(this.totalWaitingTime > 0 && waitCount > 0) this.averageWaitingTime = this.totalWaitingTime / waitCount;

        if(endTimeMilli > startTimeMilli) {
            this.duration = endTimeMilli - startTimeMilli;
            if(this.hasActivity) {
                this.caseUtilization = (double) this.totalProcessingTime / this.duration;
                if (this.caseUtilization > 1.0) this.caseUtilization = 1.0;
            }else{
                this.caseUtilization = 1.0;
            }
        } else {
            this.caseUtilization = 1.0;
        }

        this.startTimeString = timestampStringOf(millisecondToZonedDateTime(startTimeMilli));
        this.endTimeString = timestampStringOf(millisecondToZonedDateTime(endTimeMilli));
        this.durationString = Util.durationShortStringOf(duration);
    }

    private void setEventList(XTrace xTrace) {
        eventList = new ArrayList<>();

//        List<XEvent> xEventList = new ArrayList<>();

        for (int i = 0; i < xTrace.size(); i++) {
            XEvent xEvent = xTrace.get(i);
            eventList.add(new AEvent(i, xEvent));
//            xEventList.add(xEvent);
        }

//        xTrace.removeAll(xEventList); //clean memory
    }



    private void setCaseAttributes(XTrace xTrace) {
        attributeMap = new UnifiedMap<>();

        XAttributeMap xAttributeMap = xTrace.getAttributes();

        for(String key : xAttributeMap.keySet()) {
            if(key.toLowerCase().equals("concept:name")) {
                this.caseId = xAttributeMap.get(key).toString();
                if(this.caseId.matches("-?\\d+(\\.\\d+)?")) this.caseIdDigit = new Long(caseId);
            } else {
                if (!key.equals("case:variant")) this.attributeMap.put(key, xAttributeMap.get(key).toString());
            }
        }
    }

    private void setStartAndEndTime() {
        for (int i = 0; i < eventList.size(); i++) {

            AEvent aEvent = eventList.get(i);
            long eventTime = aEvent.getTimestampMilli();

            if (startTimeMilli == 0 || eventTime < startTimeMilli) {
                startTimeMilli = eventTime;
            }
            if (endTimeMilli == 0 || eventTime > endTimeMilli) {
                endTimeMilli = eventTime;
            }
        }
    }

    private void setActivities() {
        markedIndex = new IntArrayList();

        for(int i=0; i<eventList.size(); i++) {

            AEvent iAEvent = eventList.get(i);

            validateEventTimestamp(i);

            fillEventAttributeValueFreqMap(iAEvent);

            if (!markedIndex.contains(i)) {
                markedIndex.add(i);
                String lifecycle = iAEvent.getLifecycle();
                List<AEvent> actEvents = new ArrayList<>();
                actEvents.add(iAEvent);
                long actStartTime = iAEvent.getTimestampMilli();
                long actEndTime = iAEvent.getTimestampMilli();
                long actDur = 0;

                if ( (lifecycle.equals("start") || lifecycle.equals("schedule") ) && i < eventList.size()-1) {
                    this.hasActivity = true;
                    IntArrayList followup = getFollowUpIndexList(eventList, i, iAEvent);

                    if (followup != null) {
                        for (int j = 0; j < followup.size(); j++) {
                            int index = followup.get(j);
                            markedIndex.add(index);
                            AEvent fAEvent = eventList.get(index);
                            actEvents.add(fAEvent);

                        }
                        actEndTime = actEvents.get(actEvents.size() - 1).getTimestampMilli();
                        actDur = actEndTime - actStartTime;
                    }

                    addNewActivity(iAEvent.getName(), actEvents, actStartTime, actEndTime, actDur);
                } else {
                    if (!lifecycle.equals("schedule") &&
                            !lifecycle.equals("assign") &&
                            !lifecycle.equals("reassign")) {

                        addNewActivity(iAEvent.getName(), actEvents, actStartTime, actEndTime, actDur);
                    }
                }
            }
        }
    }

    private void addNewActivity(String name, List<AEvent> eventsList, long startTime, long endTime, long duration) {
        AActivity aActivity = new AActivity(name, eventsList, startTime, endTime, duration);
//        int aActIndex = 0;
//        int prevActIndex = -1;
//
//        if (activityList.size() > 1) {
//            prevActIndex = activityList.size() - 1;
//            aActIndex = activityList.size();
//        }

        activityList.add(aActivity);

        updateAttributeGraph();
    }

    private void updateAttributeGraph() {
        AAttributeGraph aAttributeGraph = apmLog.getAAttributeGraph();

        int lastIndex = activityList.size()-1;
        int prevIndex = lastIndex - 1;
        AActivity lastAct = activityList.get(lastIndex);
        AActivity prevAct = activityList.size() > 1 ? activityList.get(lastIndex-1) : null;
        UnifiedMap<String, String> lastActAttrMap = lastAct.getAllAttributes();
        UnifiedMap<String, String> prevActAttrMap = prevAct != null ? prevAct.getAllAttributes() : null;

        String baseTAI = this.index + ":" + lastIndex;
        String prevTAI = prevActAttrMap!=null ? this.index + ":" + prevIndex : null;

        for (String key : lastActAttrMap.keySet()) {
            String val = lastActAttrMap.get(key);

            aAttributeGraph.add(key, val, baseTAI, lastAct.getDuration());

            if (prevActAttrMap != null) {

//                TraceActivityIndexPair indexPair = new TraceActivityIndexPair(prevTAI, baseTAI);
                String indexPair = prevTAI + ">" + baseTAI;

                if (prevActAttrMap.containsKey(key)) {
                    String prevVal = prevActAttrMap.get(key);

                    aAttributeGraph.addNext(key, prevVal, val, indexPair);

                    aAttributeGraph.addPrevious(key, val, prevVal, indexPair);
                }
            }
        }
    }

    private void validateEventTimestamp(int eventIndex) {
        AEvent aEvent = eventList.get(eventIndex);
        if (aEvent.getTimestampMilli() == 0 && eventIndex > 0) {
            for (int j = eventIndex-1; j >= 0; j--) {
                AEvent preEvent = eventList.get(j);
                if (preEvent.getTimestampMilli() > 0) {
                    aEvent.setTimestampMilli(eventList.get(eventIndex-1).getTimestampMilli());
                    break;
                }
            }
        }
    }


//    private IntArrayList getFollowUpIndexList(List<AEvent> eventList, int fromIndex, AEvent baseEvent) {
//        IntArrayList followUpIndex = new IntArrayList();
//
//        if ( (fromIndex + 1) < eventList.size()) {
//            for (int i = (fromIndex + 1); i < eventList.size(); i++) {
//                if (!markedIndex.contains(i)) {
//                    AEvent aEvent = eventList.get(i);
//                    String lifecycle = aEvent.getLifecycle();
//
//                    if (haveCommonMainAttributes(aEvent, baseEvent)) {
////                        if (!lifecycle.equals("start")) {
////                            followUpIndex.add(i);
////                            if (lifecycle.equals("complete") ||
////                                    lifecycle.equals("manualskip") ||
////                                    lifecycle.equals("autoskip")) {
////                                break;
////                            }
////                        }
//                        followUpIndex.add(i);
//                        if (lifecycle.equals("complete") ||
//                                lifecycle.equals("manualskip") ||
//                                lifecycle.equals("autoskip")) {
//                            break;
//                        }
//                    }
//                }
//            }
//            return followUpIndex;
//        } else return null;
//    }
//
//    private boolean haveCommonMainAttributes(AEvent event1, AEvent event2) {
//        if (!event1.getName().equals(event2.getName())) return false;
//        if (!event1.getResource().equals(event2.getResource())) return false;
//        UnifiedMap<String, String> attrMap1 = event1.getAttributeMap();
//        UnifiedMap<String, String> attrMap2 = event2.getAttributeMap();
//        for (String key : attrMap1.keySet()) {
//            String val1 = attrMap1.get(key);
//            String val2 = attrMap2.get(key);
//            if (!val1.equals(val2)) return false;
//        }
//        return true;
//    }

    private void fillEventAttributeValueFreqMap(AEvent aEvent) {
        for(String key : aEvent.getAttributeMap().keySet()) {
            String iAValue = aEvent.getAttributeMap().get(key).intern();
            if (this.eventAttributeValueFreqMap.containsKey(key)) {
                UnifiedMap<String, Integer> valueFreqMap = this.eventAttributeValueFreqMap.get(key);
                if(valueFreqMap.containsKey(iAValue)) {
                    int freq = valueFreqMap.get(iAValue) + 1;
                    valueFreqMap.put(iAValue, freq);
                    this.eventAttributeValueFreqMap.put(key, valueFreqMap);
                }else{
                    valueFreqMap.put(iAValue, 1);
                    this.eventAttributeValueFreqMap.put(key, valueFreqMap);
                }
            }else{
                UnifiedMap<String, Integer> valueFreqMap = new UnifiedMap<>();
                valueFreqMap.put(iAValue, 1);
                this.eventAttributeValueFreqMap.put(key, valueFreqMap);
            }
        }
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return eventAttributeValueFreqMap;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseVariantId(int caseVariantId) {
        this.caseVariantId = caseVariantId;
    }

    public int getCaseVariantId() {
        return caseVariantId;
    }

    public int getEventSize() {
        return this.eventList.size();
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public long getEndTimeMilli() {
        return endTimeMilli;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isHasActivity() {
        return hasActivity;
    }

    public List<AActivity> getActivityList() {
        return activityList;
    }

    public List<String> getActivityNameList() {
        List<String> actNameList = new ArrayList<>();
        for(int i=0; i < this.activityList.size(); i++) {
            actNameList.add(this.activityList.get(i).getName());
        }
        return actNameList;
    }

    public UnifiedSet<String> getEventNameSet() {
        return this.eventNameSet;
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public List<AEvent> getEventList() {
        return eventList;
    }

    public int size() {
        return this.eventList.size();
    }

    public AEvent get(int index) {
        try {
            return this.eventList.get(index);
        } catch (Exception e) {
            System.out.println("Index " + index + " does not exist.\n" + e.toString() );
        }
        return null;
    }

    public long getTotalProcessingTime() {
        return totalProcessingTime;
    }

    public long getAverageProcessingTime() {
        return averageProcessingTime;
    }

    public long getMaxProcessingTime() {
        return maxProcessingTime;
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public long getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public double getCaseUtilization() {
        return caseUtilization;
    }


    public String getStartTimeString() {
        return startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public String getDurationString() {
//        if(getCaseId().equals("0050554374")) {
//            System.out.println("PAUSE");
//        }
        return durationString;
    }

    public long getCaseIdDigit() {
        return caseIdDigit;
    }

    public List<Integer> getActivityNameIndexList() {
        return activityNameIndexList;
    }

    public ATrace(String caseId, int caseVariantId,
                  long startTimeMilli, long endTimeMilli,
                  boolean hasActivity, long duration,
                  long totalProcessingTime, long averageProcessingTime, long maxProcessingTime,
                  long totalWaitingTime, long averageWaitingTime, long maxWaitingTime,
                  double caseUtilization,
                  List<AActivity> activityList,
                  List<AEvent> eventList,
                  UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap,
                  UnifiedMap<String, String> attributeMap,
                  List<String> activityNameList,
                  UnifiedSet<String> eventNameSet,
                  List<Integer> activityNameIndexList) {
        this.caseId = caseId.intern();
        if(this.caseId.matches("-?\\d+(\\.\\d+)?")) this.caseIdDigit = new Long(caseId);
        this.caseVariantId = caseVariantId;
        this.caseVariantIdForDisplay = caseVariantId;
        this.startTimeMilli = startTimeMilli;
        this.endTimeMilli = endTimeMilli;
        this.hasActivity = hasActivity;
        this.duration = duration;
        this.totalProcessingTime = totalProcessingTime;
        this.averageProcessingTime = averageProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.totalWaitingTime = totalWaitingTime;
        this.averageWaitingTime = averageWaitingTime;
        this.maxWaitingTime = maxWaitingTime;
        this.caseUtilization = caseUtilization;
        this.activityList = activityList;
        this.eventList = eventList;
        this.eventAttributeValueFreqMap = eventAttributeValueFreqMap;
        this.attributeMap = attributeMap;
        this.activityNameList = activityNameList;
        this.eventNameSet = eventNameSet;


//        if(getCaseId().equals("0050554374")) {
//            System.out.println("PAUSE");
//        }
        this.startTimeString = timestampStringOf(millisecondToZonedDateTime(startTimeMilli));
        this.endTimeString = timestampStringOf(millisecondToZonedDateTime(endTimeMilli));
        this.durationString = Util.durationShortStringOf(duration);

        this.activityNameIndexList = activityNameIndexList;
    }

    public static String timestampStringOf(ZonedDateTime zdt){
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");
        return zdt.format(formatter);
    }

    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }

    public void setCaseVariantIdForDisplay(int caseVariantIdForDisplay) {
        this.caseVariantIdForDisplay = caseVariantIdForDisplay;
    }

    public int getCaseVariantIdForDisplay() {
        return caseVariantIdForDisplay;
    }

//    public void setaAttributeGraph(AAttributeGraph aAttributeGraph) {
//        this.aAttributeGraph = aAttributeGraph;
//    }
//
//    public AAttributeGraph getaAttributeGraph() {
//        return aAttributeGraph;
//    }

    public ATrace clone() {

        List<AEvent> aEventList = new ArrayList<>();

        List<AEvent> originalEventList = this.getEventList();

        for (int i=0; i < originalEventList.size(); i++) {
            AEvent eventClone = originalEventList.get(i).clone();
            aEventList.add(eventClone);
        }

        List<AActivity> aActivityList = new ArrayList<>();

        for (int i=0; i < this.activityList.size(); i++) {
            AActivity aActivity = this.activityList.get(i).clone();
            aActivityList.add(aActivity);
        }

        UnifiedMap<String, UnifiedMap<String, Integer>> eventAttrValFreqMap = new UnifiedMap<>();

        for (String key : this.eventAttributeValueFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMap = new UnifiedMap<>();

            UnifiedMap<String, Integer> eValFreqMap = this.eventAttributeValueFreqMap.get(key);
            for (String val : eValFreqMap.keySet()) {
                valFreqMap.put(val, eValFreqMap.get(val));
            }

            eventAttrValFreqMap.put(key.intern(), valFreqMap);
        }

        UnifiedMap<String, String> attrMap = new UnifiedMap<>();

        for (String key : this.attributeMap.keySet()) {
            attrMap.put(key.intern(), this.attributeMap.get(key));
        }

        List<String> actNameList = new ArrayList<>();

        for (int i=0; i < this.activityNameList.size(); i++) {
            actNameList.add(this.activityNameList.get(i).intern());
        }

        UnifiedSet<String> eNameSet = new UnifiedSet<>();

        for (String s : this.eventNameSet) {
            eNameSet.put(s.intern());
        }


        ATrace aTrace = new ATrace(this.caseId, this.caseVariantId,
                this.startTimeMilli,
                this.endTimeMilli,
                this.hasActivity,
                this.duration,
                this.totalProcessingTime,
                this.averageProcessingTime,
                this.maxProcessingTime,
                this.totalWaitingTime,
                this.averageWaitingTime,
                this.maxWaitingTime,
                this.caseUtilization,
                aActivityList,
                aEventList,
                eventAttrValFreqMap,
                attrMap,
                actNameList,
                eNameSet,
                this.activityNameIndexList);
        aTrace.setIndex(this.index);
//        aTrace.setaAttributeGraph(this.aAttributeGraph.clone());
        return aTrace;
    }

    @Override
    public int compareTo(ATrace o) {
        if (Util.isNumeric(this.caseId) && Util.isNumeric(o.getCaseId())) {
            if (caseIdDigit > o.caseIdDigit) return 1;
            else if (caseIdDigit < o.caseIdDigit) return -1;
            else return 0;
        } else {
            return getCaseId().compareTo(o.getCaseId());
        }
    }
}
