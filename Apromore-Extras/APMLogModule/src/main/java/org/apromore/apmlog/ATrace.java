/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.apmlog;

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
import java.util.Collections;
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
 */
public class ATrace implements Serializable, LaTrace {

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

    private IntArrayList markedIndex;

//    private APMLog apmLog;

    public ATrace(XTrace xTrace, APMLog apmLog) {

//        this.apmLog = apmLog;

        activityNameIndexList = new ArrayList<>();

        activityList = new ArrayList<>();
        eventList = new ArrayList<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        attributeMap = new UnifiedMap<>();

        XAttributeMap xAttributeMap = xTrace.getAttributes();
        for(String key : xAttributeMap.keySet()) {
            if(key.toLowerCase().equals("concept:name")) {
                this.caseId = xAttributeMap.get(key).toString();
                if(this.caseId.matches("-?\\d+(\\.\\d+)?")) this.caseIdDigit = new Long(caseId);
            } else {
                this.attributeMap.put(key, xAttributeMap.get(key).toString());
            }
        }
        /**
         * DO NOT TAKE THE CASE:VARIANT IN THE ORIGINAL XLOG
         */
//        if(xTrace.getAttributes().containsKey("case:variant")) caseVariantId = new Integer(xTrace.getAttributes().getById("case:variant").toString());
        // ELSE SET THE VARIANT ID from APMLog
        initStats(xTrace, apmLog);
    }

    public ATrace(String caseIdString, List<AEvent> inputEventList,
                  UnifiedMap<String, String> caseAttributes, APMLog apmLog) {

        activityNameIndexList = new ArrayList<>();

        activityList = new ArrayList<>();
        this.eventList = new ArrayList<>();

        eventAttributeValueFreqMap = new UnifiedMap<>();
        attributeMap = new UnifiedMap<>();

        if (!caseIdString.equals("")) {
            this.caseId = caseIdString;
            if (caseIdString.matches("-?\\d+(\\.\\d+)?")) this.caseIdDigit = new Long(caseId);
        }

        this.attributeMap = caseAttributes;

        activityNameList = new ArrayList<>();
        eventNameSet = new UnifiedSet<>();

        /* ------------- find start time and end time of trace -------------- */
//        if (inputEventList.size() < 2) {
//            AEvent aEvent = inputEventList.get(0);
//            long eventTime = aEvent.getTimestampMilli();
//            startTimeMilli = eventTime;
//            endTimeMilli = eventTime;
//
//        } else {
        for (int i = 0; i < inputEventList.size(); i++) {

            AEvent aEvent = inputEventList.get(i);
            long eventTime = aEvent.getTimestampMilli();

            if (startTimeMilli == 0 || eventTime < startTimeMilli) {
                startTimeMilli = eventTime;
            }
            if (endTimeMilli == 0 || eventTime > endTimeMilli) {
                endTimeMilli = eventTime;
            }
        }
//        }



        /* ----------------------- set activities ----------------------------- */

        markedIndex = new IntArrayList();

        for(int i=0; i<inputEventList.size(); i++) {


            AEvent iAEvent = inputEventList.get(i);
            this.eventList.add(iAEvent);

            fillEventAttributeValueFreqMap(iAEvent);

            if (!markedIndex.contains(i)) {
                markedIndex.add(i);
                String lifecycle = iAEvent.getLifecycle();
                List<AEvent> actEvents = new ArrayList<>();
                actEvents.add(iAEvent);
                long actStartTime = iAEvent.getTimestampMilli();
                long actEndTime = iAEvent.getTimestampMilli();
                long actDur = 0;

                if (this.startTimeMilli == 0 || actStartTime < this.startTimeMilli) this.startTimeMilli = actStartTime;
                if (this.endTimeMilli == 0 || actEndTime > this.endTimeMilli) this.endTimeMilli = actEndTime;

                if (lifecycle.equals("start")) {
                    this.hasActivity = true;
                    IntArrayList followup = getFollowUpIndexList(inputEventList, i, iAEvent.getName());

                    if (followup == null) {
                        AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, iAEvent.getTimestampMilli(),
                                iAEvent.getTimestampMilli(), 0);
                        this.activityList.add(aActivity);
                    } else {
                        for (int j = 0; j < followup.size(); j++) {
                            int index = followup.get(j);
                            markedIndex.add(index);
                            AEvent fAEvent = inputEventList.get(index);
                            actEvents.add(fAEvent);

                        }
                        actEndTime = actEvents.get(actEvents.size() - 1).getTimestampMilli();
                        actDur = actEndTime - actStartTime;
                    }
                    AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, actStartTime,
                            actEndTime, actDur);
                    this.activityList.add(aActivity);
                } else {
                    if (!lifecycle.equals("schedule") &&
                            !lifecycle.equals("assign") &&
                            !lifecycle.equals("reassign")) {

                        AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, actStartTime,
                                actEndTime, actDur);
                        this.activityList.add(aActivity);
                    }
                }
            }
        }

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




    /* ------------- latest code ---------------------*/
    private void initStats(XTrace xTrace, APMLog apmLog) {



        activityNameList = new ArrayList<>();
        eventNameSet = new UnifiedSet<>();

        /* ------------- find start time and end time of trace -------------- */
        for(int i=0; i<xTrace.size(); i++) {
            XEvent xEvent = xTrace.get(i);
            long eventTime = Util.epochMilliOf(Util.zonedDateTimeOf(xEvent));
            if(startTimeMilli == 0 || eventTime < startTimeMilli) {
                startTimeMilli = eventTime;
            }
            if(endTimeMilli == 0 || eventTime > endTimeMilli) {
                endTimeMilli = eventTime;
            }
        }



        /* ----------------------- set activities ----------------------------- */

        markedIndex = new IntArrayList();

        for(int i=0; i<xTrace.size(); i++) {
            XEvent xEvent = xTrace.get(i);

            AEvent iAEvent = new AEvent(xEvent);
            this.eventList.add(iAEvent);

            fillEventAttributeValueFreqMap(iAEvent);

            if (!markedIndex.contains(i)) {
                markedIndex.add(i);
                String lifecycle = iAEvent.getLifecycle();
                List<AEvent> actEvents = new ArrayList<>();
                actEvents.add(iAEvent);

                long actStartTime = iAEvent.getTimestampMilli();
                long actEndTime = iAEvent.getTimestampMilli();
                long actDur = 0;

                if (lifecycle.equals("start")) {
                    this.hasActivity = true;
                    IntArrayList followup = getFollowUpIndexList(xTrace, i, iAEvent.getName());

                    if (followup == null) {
                        AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, iAEvent.getTimestampMilli(),
                                iAEvent.getTimestampMilli(), 0);
                        this.activityList.add(aActivity);
                    } else {

                        if (followup.size() == 0) {
                            AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, iAEvent.getTimestampMilli(),
                                    iAEvent.getTimestampMilli(), 0);
                            this.activityList.add(aActivity);
                        } else {
                            for (int j = 0; j < followup.size(); j++) {
                                int index = followup.get(j);
                                markedIndex.add(index);
                                XEvent fEvent = xTrace.get(index);
                                AEvent fAEvent = new AEvent(fEvent);
                                actEvents.add(fAEvent);
                            }
                            actEndTime = actEvents.get(actEvents.size() - 1).getTimestampMilli();
                            actDur = actEndTime - actStartTime;
                        }
                        AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, actStartTime,
                                actEndTime, actDur);
                        this.activityList.add(aActivity);
                    }
                } else {
                    if (!lifecycle.equals("schedule") &&
                            !lifecycle.equals("assign") &&
                            !lifecycle.equals("reassign")) {
                        /* When the event occurs without 'start', it is considered as a complete with no followup */

                        AActivity aActivity = new AActivity(iAEvent.getName(), actEvents, actStartTime,
                                actEndTime, actDur);
                        this.activityList.add(aActivity);
                    }
                }
            }
        }

        /*---------------- Fill the other attributes ----------------*/

        if(endTimeMilli > startTimeMilli) {
            this.duration = endTimeMilli - startTimeMilli;
        }

        long waitCount = 0;

        this.totalWaitingTime = 0;

        List<Long> waitTimeList = new ArrayList<>();

        for (int i = 1; i < eventList.size(); i++) {
            AEvent aEvent = eventList.get(i);
            String lifecycle = aEvent.getLifecycle();
            if (lifecycle.equals("start")) {

                long iTime = aEvent.getTimestampMilli();

                AEvent pEvent = eventList.get(i-1);
                long pTime = pEvent.getTimestampMilli();

                long waitTime = iTime - pTime;
                this.totalWaitingTime += waitTime;
                waitTimeList.add(waitTime);
                waitCount += 1;
            }
        }

        if (this.totalWaitingTime > 0) {
            this.averageWaitingTime = totalWaitingTime / waitCount;
            Collections.sort(waitTimeList);
            this.maxWaitingTime = waitTimeList.get(waitTimeList.size()-1);
        }





        long processCount = 0;
        for (int i = 0; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);

            this.eventNameSet.put(activity.getName());
            this.activityNameList.add(activity.getName());
            this.activityNameIndexList.add(apmLog.getActivityNameMapper().set(activity.getName()));

            processCount += 1;
            this.totalProcessingTime += activity.getDuration();
            if (activity.getDuration() > this.maxProcessingTime) this.maxProcessingTime = activity.getDuration();
        }
        if(this.totalProcessingTime > 0 && processCount > 0) this.averageProcessingTime = this.totalProcessingTime / processCount;

        if(endTimeMilli > startTimeMilli) {
            if(containsActivity(xTrace)) {
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

    private IntArrayList getFollowUpIndexList(XTrace xTrace, int fromIndex, String conceptName) {

        XEvent startEvent = xTrace.get(fromIndex);
        XAttributeMap seAttributeMap = startEvent.getAttributes();

        IntArrayList followUpIndex = new IntArrayList();
        if ( (fromIndex + 1) < xTrace.size()) {
            for (int i = (fromIndex + 1); i < xTrace.size(); i++) {
                if (!markedIndex.contains(i)) {
                    XEvent xEvent = xTrace.get(i);
                    XAttributeMap xAttributeMap = xEvent.getAttributes();
                    if (xAttributeMap.containsKey("concept:name") && xAttributeMap.containsKey("lifecycle:transition")) {
                        String actName = xAttributeMap.get("concept:name").toString();
                        String lifecycle = xAttributeMap.get("lifecycle:transition").toString().toLowerCase();

                        if (haveSameAttributeValues(seAttributeMap, xAttributeMap)) {
                            if (!lifecycle.equals("start")) {
                                followUpIndex.add(i);
                                if (lifecycle.equals("complete") ||
                                        lifecycle.equals("manualskip") ||
                                        lifecycle.equals("autoskip")) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return followUpIndex;
        } else return null;
    }

    private boolean haveSameAttributeValues(XAttributeMap xAttributeMap1, XAttributeMap xAttributeMap2) {
        for (String key : xAttributeMap1.keySet()) {
            if (!key.toLowerCase().equals("time:timestamp") && !key.toLowerCase().equals("lifecycle:transition")) {
                if (!xAttributeMap2.containsKey(key)) return false;
                String val1 = xAttributeMap1.get(key).toString();
                String val2 = xAttributeMap2.get(key).toString();
                if (!val1.equals(val2)) return false;
            }
        }
        return true;
    }

    private boolean haveSameAttributeValues(UnifiedMap<String, String> attributeMap1,
                                            UnifiedMap<String, String> attributeMap2) {
        for (String key : attributeMap1.keySet()) {
            if (!key.toLowerCase().equals("time:timestamp") && !key.toLowerCase().equals("lifecycle:transition")) {
                if (!attributeMap2.containsKey(key)) return false;
                String val1 = attributeMap1.get(key);
                String val2 = attributeMap2.get(key);
                if (!val1.equals(val2)) return false;
            }
        }
        return true;
    }

    private IntArrayList getFollowUpIndexList(List<AEvent> eventList, int fromIndex, String conceptName) {
        IntArrayList followUpIndex = new IntArrayList();

        UnifiedMap<String, String> attribute1 = eventList.get(fromIndex).getAttributeMap();

        if ( (fromIndex + 1) < eventList.size()) {
            for (int i = (fromIndex + 1); i < eventList.size(); i++) {
                AEvent aEvent = eventList.get(i);
                String lifecycle = aEvent.getLifecycle();

                UnifiedMap<String, String> attribute2 = aEvent.getAttributeMap();

                if (haveSameAttributeValues(attribute1, attribute2)) {
                    if (!lifecycle.equals("start")) {
                        followUpIndex.add(i);
                        if (lifecycle.equals("complete") ||
                                lifecycle.equals("manualskip") ||
                                lifecycle.equals("autoskip")) {
                            break;
                        }
                    }
                }
            }
            return followUpIndex;
        } else return null;
    }

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

    private boolean containsActivity(XTrace xTrace) {
        for (int i=0; i < xTrace.size(); i++) {
            XEvent xEvent = xTrace.get(i);
            if (xEvent.getAttributes().containsKey("lifecycle:transition")) {
                String lifecycle = xEvent.getAttributes().get("lifecycle:transition").toString();
                if (lifecycle.toLowerCase().equals("start")) {
                    this.hasActivity = true;
                    return true;
                }
            }
        }
        return false;
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


        if(getCaseId().equals("0050554374")) {
            System.out.println("PAUSE");
        }
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


        return new ATrace(this.caseId, this.caseVariantId,
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
    }
}
