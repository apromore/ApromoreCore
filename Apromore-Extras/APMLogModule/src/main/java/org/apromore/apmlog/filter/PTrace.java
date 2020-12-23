/*-
 * #%L
 * Process Discoverer Logic
 *
 * This file is part of "Apromore".
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


package org.apromore.apmlog.filter;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.immutable.ImmutableTrace;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * This class provides pointers of ATrace (of APMLog) used in filterlogic.
 * It can output a new ATrace (of APMLog) based on the valid event Id index (validEventIndexBS)
 * @author Chii Chang
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020)
 * Modified: Chii Chang (12/03/2020)
 * Modified: Chii Chang (24/05/2020)
 * Modified: Chii Chang (26/05/2020)
 * Modified: Chii Chang (07/10/2020) - include "schedule" event to activity
 * Modified: Chii Chang (11/11/2020)
 * Modified: Chii Chang (23/12/2020)
 */
public class PTrace implements Comparable<PTrace>, ATrace {

    private ATrace aTrace;

    private String caseId = "";
    private int immutableIndex;
    private int mutableIndex;
    public long caseIdDigit = 0;
    private int caseVariantId = 0;
    private long startTimeMilli = -1;
    private long endTimeMilli = -1;
    private double duration = 0;
    private boolean hasActivity = false;
    private double totalProcessingTime = 0;
    private double averageProcessingTime = 0;
    private double maxProcessingTime = 0;
    private double totalWaitingTime = 0;
    private double averageWaitingTime = 0;
    private double maxWaitingTime = 0;
    private double caseUtilization = 0.0;
    private int eventSize;

    public String startTimeString, endTimeString, durationString;

    private List<AActivity> activityList;
    private List<AEvent> eventList;
    private UnifiedMap<String, String> attributeMap;
    private List<String> activityNameList;
    private UnifiedSet<String> eventNameSet;

    private BitSet validEventIndexBS;

    private long originalStartTimeMilli;
    private long originalEndTimeMilli;
    private double originalDuration = 0;
    private boolean originalHasActivity = false;
    private double originalTotalProcessingTime = 0;
    private double originalAverageProcessingTime = 0;
    private double originalMaxProcessingTime = 0;
    private double originalTotalWaitingTime = 0;
    private double originalAverageWaitingTime = 0;
    private double originalMaxWaitingTime = 0;
    private double originalCaseUtilization = 0;
    private List<AActivity> originalActivityList;
    private List<AEvent> originalEventList;
    private UnifiedMap<String, String> originalAttributeMap;
    private List<String> originalActivityNameList;
    private UnifiedSet<String> originalEventNameSet;

    private List<Integer> activityNameIndexList;
    private List<Integer> originalActivityNameIndexList;
    private List<Integer> previousActiivtyNameIndexList;

    private BitSet originalValidEventIndexBS;

    private BitSet previousValidEventIndexBS;

    private long previousStartTimeMilli;
    private long previousEndTimeMilli;
    private double previousDuration = 0;
    private boolean previousHasActivity = false;
    private double previousTotalProcessingTime = 0;
    private double previousAverageProcessingTime = 0;
    private double previousMaxProcessingTime = 0;
    private double previousTotalWaitingTime = 0;
    private double previousAverageWaitingTime = 0;
    private double previousMaxWaitingTime = 0;
    private double previousCaseUtilization = 0;
    private List<AActivity> previousActivityList;
    private List<AEvent> previousEventList;
    private UnifiedMap<String, String> previousAttributeMap;
    private List<String> previousActivityNameList;
    private UnifiedSet<String> previousEventNameSet;

    private APMLog apmLog;

    public PTrace(ATrace aTrace, APMLog apmLog) {
        this.aTrace = aTrace;

        this.apmLog = apmLog;

        initDefault();
    }

    private void initDefault() {

        this.immutableIndex = aTrace.getImmutableIndex();
        this.mutableIndex = aTrace.getMutableIndex();

        this.caseId = aTrace.getCaseId();
        this.caseVariantId = aTrace.getCaseVariantId();
        this.caseIdDigit = aTrace.getCaseIdDigit();

        this.startTimeString = aTrace.getStartTimeString();
        this.endTimeString = aTrace.getEndTimeString();
        this.durationString = aTrace.getDurationString();

        this.startTimeMilli = aTrace.getStartTimeMilli();
        this.endTimeMilli = aTrace.getEndTimeMilli();
        this.duration = aTrace.getDuration();
        this.hasActivity = aTrace.isHasActivity();
        this.totalProcessingTime = aTrace.getTotalProcessingTime();
        this.averageProcessingTime = aTrace.getAverageProcessingTime();
        this.maxProcessingTime = aTrace.getMaxProcessingTime();
        this.totalWaitingTime = aTrace.getTotalWaitingTime();
        this.averageWaitingTime = aTrace.getAverageWaitingTime();
        this.maxWaitingTime = aTrace.getMaxWaitingTime();
        this.caseUtilization = aTrace.getCaseUtilization();

        this.originalStartTimeMilli = aTrace.getStartTimeMilli();
        this.originalEndTimeMilli = aTrace.getEndTimeMilli();
        this.originalDuration = aTrace.getDuration();
        this.originalHasActivity = aTrace.isHasActivity();
        this.originalTotalProcessingTime = aTrace.getTotalProcessingTime();
        this.originalAverageProcessingTime = aTrace.getAverageProcessingTime();
        this.originalMaxProcessingTime = aTrace.getMaxProcessingTime();
        this.originalTotalWaitingTime = aTrace.getTotalWaitingTime();
        this.originalAverageWaitingTime = aTrace.getAverageWaitingTime();
        this.originalMaxWaitingTime = aTrace.getMaxWaitingTime();
        this.originalCaseUtilization = aTrace.getCaseUtilization();

        this.previousStartTimeMilli = aTrace.getStartTimeMilli();
        this.previousEndTimeMilli = aTrace.getEndTimeMilli();
        this.previousDuration = aTrace.getDuration();
        this.previousHasActivity = aTrace.isHasActivity();
        this.previousTotalProcessingTime = aTrace.getTotalProcessingTime();
        this.previousAverageProcessingTime = aTrace.getAverageProcessingTime();
        this.previousMaxProcessingTime = aTrace.getMaxProcessingTime();
        this.previousTotalWaitingTime = aTrace.getTotalWaitingTime();
        this.previousAverageWaitingTime = aTrace.getAverageWaitingTime();
        this.previousMaxWaitingTime = aTrace.getMaxWaitingTime();
        this.previousCaseUtilization = aTrace.getCaseUtilization();

        List<AEvent> aTraceEventList = aTrace.getEventList();

        this.validEventIndexBS = new BitSet(aTraceEventList.size());
        this.originalValidEventIndexBS = new BitSet(aTraceEventList.size());
        this.previousValidEventIndexBS = new BitSet(aTraceEventList.size());

        this.eventList = new ArrayList<>(aTraceEventList);
        this.originalEventList = new ArrayList<>(aTraceEventList);
        this.previousEventList = new ArrayList<>(aTraceEventList);

        this.validEventIndexBS.set(0, eventList.size(), true);
        this.originalValidEventIndexBS.set(0,  originalEventList.size(), true);
        this.previousValidEventIndexBS.set(0,  previousEventList.size(), true);

        this.activityList = new ArrayList<>(aTrace.getActivityList());
        this.originalActivityList = new ArrayList<>(aTrace.getActivityList());
        this.previousActivityList = new ArrayList<>(aTrace.getActivityList());

        this.eventList = new ArrayList<>(aTrace.getEventList());
        this.originalEventList = new ArrayList<>(aTrace.getEventList());
        this.previousEventList = new ArrayList<>(aTrace.getEventList());

        this.attributeMap = aTrace.getAttributeMap();
        this.previousAttributeMap = aTrace.getAttributeMap();
        this.originalAttributeMap = aTrace.getAttributeMap();


        List<String> aTraceActNameList = aTrace.getActivityNameList();


        this.activityNameList = new ArrayList<>(aTraceActNameList);
        this.originalActivityNameList = new ArrayList<>(aTraceActNameList);
        this.previousActivityNameList = new ArrayList<>(aTraceActNameList);

        if (aTrace.getActivityNameIndexList() != null) {
            this.activityNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
            this.originalActivityNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
            this.previousActiivtyNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
        }

        UnifiedSet<String> aTraceEventNameSet = aTrace.getEventNameSet();



        this.eventNameSet = new UnifiedSet<>(aTraceEventNameSet);
        this.originalEventNameSet = new UnifiedSet<>(aTraceEventNameSet);
        this.previousEventNameSet = new UnifiedSet<>(aTraceEventNameSet);




        for (int i = 0; i < activityList.size(); i++) {
            activityList.get(i).setParentTrace(this);
        }

        this.eventSize = eventList.size();
    }

    public void reset() {


        initDefault();

    }

    public ATrace getOriginalATrace() {
        return aTrace;
    }

    public void resetPrevious() {

        if(previousValidEventIndexBS != null) {
            for (int i = 0; i < validEventIndexBS.size(); i++) {
                validEventIndexBS.set(i, previousValidEventIndexBS.get(i));
            }

            startTimeMilli = previousStartTimeMilli;
            endTimeMilli = previousEndTimeMilli;
            duration = previousDuration;
            hasActivity = previousHasActivity;
            totalProcessingTime = previousTotalProcessingTime;
            averageProcessingTime = previousAverageProcessingTime;
            maxProcessingTime = previousMaxProcessingTime;
            totalWaitingTime = previousTotalWaitingTime;
            averageWaitingTime = previousAverageWaitingTime;
            maxWaitingTime = previousMaxWaitingTime;
            caseUtilization = previousCaseUtilization;

            this.activityList = previousActivityList;
            this.eventList = previousEventList;
            this.attributeMap = previousAttributeMap;
            this.activityNameList = previousActivityNameList;
            this.eventNameSet = previousEventNameSet;

            this.activityNameIndexList = previousActiivtyNameIndexList;
        } else {
            reset();
        }
    }

    /**
     * Replace the values of the previous stage as current stage
     */
    public void updatePrevious() {

        for (int i = 0; i < previousValidEventIndexBS.size(); i++) {
            previousValidEventIndexBS.set(i, validEventIndexBS.get(i));
        }

        previousStartTimeMilli = startTimeMilli;
        previousEndTimeMilli = endTimeMilli;
        previousDuration = duration;
        previousHasActivity = hasActivity;
        previousTotalProcessingTime = totalProcessingTime;
        previousAverageProcessingTime = averageProcessingTime;
        previousMaxProcessingTime = maxProcessingTime;
        previousTotalWaitingTime = totalWaitingTime;
        previousAverageWaitingTime = averageWaitingTime;
        previousMaxWaitingTime = maxWaitingTime;
        previousCaseUtilization = caseUtilization;

        previousActivityList = activityList;
        previousEventList = eventList;
        previousAttributeMap = attributeMap;
        previousActivityNameList = activityNameList;
        previousEventNameSet = eventNameSet;

        previousActiivtyNameIndexList = activityNameIndexList;
    }

    public void update(int mutableIndex) {

        this.mutableIndex = mutableIndex;

        previousValidEventIndexBS = (BitSet) validEventIndexBS.clone();

        previousStartTimeMilli = startTimeMilli;
        previousEndTimeMilli = endTimeMilli;
        previousDuration = duration;
        previousHasActivity = hasActivity;
        previousTotalProcessingTime = totalProcessingTime;
        previousAverageProcessingTime = averageProcessingTime;
        previousMaxProcessingTime = maxProcessingTime;
        previousTotalWaitingTime = totalWaitingTime;
        previousAverageWaitingTime = averageWaitingTime;
        previousMaxWaitingTime = maxWaitingTime;
        previousCaseUtilization = caseUtilization;
        previousActivityList = activityList;
        previousEventList = eventList;
        previousAttributeMap = attributeMap;
        previousActivityNameList = activityNameList;
        previousEventNameSet = eventNameSet;
        previousActiivtyNameIndexList = activityNameIndexList;

        this.eventList = new ArrayList<>();


        List<AEvent> aEventList = aTrace.getEventList();

        for (int i = 0; i < aEventList.size(); i++) {
            AEvent event = aEventList.get(i);
            if (validEventIndexBS.get(i)) {
                eventList.add(event);
            }
        }

        this.activityList = new ArrayList<>();

        UnifiedSet<Integer> actIndexes = new UnifiedSet<>();
        for (int j = 0; j < eventList.size(); j++) {
            AEvent aEvent = eventList.get(j);
            int actIndex = aEvent.getParentActivityIndex();
            actIndexes.put(actIndex);
        }

        List<Integer> actIndexList = new ArrayList<>(actIndexes);
        Collections.sort(actIndexList);

        for (int j = 0; j < actIndexList.size(); j++) {
            int actIndex = actIndexList.get(j);
            this.activityList.add(aTrace.getActivityList().get(actIndex));
        }

        updateStats(this.activityList);

    }



    private void updateStats(List<AActivity> activities) {
        this.totalProcessingTime = 0;
        this.averageProcessingTime = 0;
        this.maxProcessingTime = 0;
        this.totalWaitingTime = 0;
        this.averageWaitingTime = 0;
        this.maxWaitingTime = 0;
        this.caseUtilization = 0;
        for (int i = 0; i < activities.size(); i++) {
            AActivity iAct = activities.get(i);
            totalProcessingTime += iAct.getDuration();
            if (iAct.getDuration() > maxProcessingTime) maxProcessingTime = iAct.getDuration();

            if (i+1 < activities.size()) {
                AActivity nAct = activities.get(i + 1);
                long iET = iAct.getEndTimeMilli();
                long nST = nAct.getStartTimeMilli();
                long wt = nST > iET ? nST - iET : 0;
                totalWaitingTime += wt;

                if (wt > maxWaitingTime) maxWaitingTime = wt;
            }
        }
        averageProcessingTime = totalProcessingTime > 0 ? totalProcessingTime / activities.size() : 0;
        averageWaitingTime = totalWaitingTime > 0 ? totalWaitingTime / (activities.size()-1) : 0;
        double dur = getDuration();

        if (totalWaitingTime > 0 && totalProcessingTime > 0) {
            caseUtilization = totalProcessingTime / (totalProcessingTime + totalWaitingTime);
        } else {
            caseUtilization = totalProcessingTime > 0 && totalProcessingTime < dur ? totalProcessingTime / dur : 1.0;
        }
        if (caseUtilization > 1.0) caseUtilization = 1.0;
    }


    private void appendActivity(UnifiedMap<Long, List<AActivity>> startTimeActivitiesMap, AActivity activity) {
        long actStartTime = activity.getStartTimeMilli();
        if (!startTimeActivitiesMap.containsKey(actStartTime)) {
            List<AActivity> actList = new ArrayList<>();
            actList.add(activity);
            startTimeActivitiesMap.put(actStartTime, actList);
        } else {
            startTimeActivitiesMap.get(actStartTime).add(activity);
        }
    }

    private void configActivityList(UnifiedMap<Long, List<AActivity>> completeTimeActivitiesMap) {
        List<Long> keyList = new ArrayList<>(completeTimeActivitiesMap.keySet());
        Collections.sort(keyList);

        for (int i = 0; i < keyList.size(); i++) {
            long endTime = keyList.get(i);
            List<AActivity> actList = completeTimeActivitiesMap.get(endTime);
            for (int j = 0; j < actList.size(); j++) {
                AActivity act = actList.get(j);
                this.activityList.add(act);
            }
        }
    }

    public List<Integer> getActivityNameIndexList() {
        return activityNameIndexList;
    }

    @Override
    public void setCaseVariantIdForDisplay(int caseVariantIdForDisplay) {

    }

    @Override
    public int getCaseVariantIdForDisplay() {
        return 0;
    }

    @Override
    public void addEvent(AEvent event) {

    }

    @Override
    public void setEventList(List<AEvent> eventList) {

    }

    @Override
    public List<AEvent> getImmutableEvents() {
        return aTrace.getImmutableEvents();
    }

    @Override
    public void setImmutableEvents(List<AEvent> events) {

    }

    @Override
    public ATrace clone() {
        return null;
    }


    @Override
    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return null;
    }

    @Override
    public void addActivity(AActivity aActivity) {
        this.activityList.add(aActivity);
    }

    @Override
    public int getImmutableIndex() {
        return immutableIndex;
    }

    @Override
    public int getMutableIndex() {
        return mutableIndex;
    }

    @Override
    public void setMutableIndex(int mutableIndex) {
        this.mutableIndex = mutableIndex;
    }

    public String getCaseId() {
        return caseId;
    }

    public long getCaseIdDigit() {
        return caseIdDigit;
    }

    public void setCaseVariantId(int caseVariantId) {
        this.caseVariantId = caseVariantId;
    }

    public int getCaseVariantId() {
        return caseVariantId;
    }

    public void setValidEventIndexBS(BitSet validEventIndexBS) {
        this.validEventIndexBS = validEventIndexBS;
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

    public double getDuration() {
        return duration;
    }

    public double getOriginalDuration() {
        return originalDuration;
    }

    public List<AEvent> getOriginalEventList() {
        return originalEventList;
    }

    public boolean isHasActivity() {
        return hasActivity;
    }

    @Override
    public void setHasActivity(boolean opt) {

    }

    public List<AActivity> getOriginalActivityList() {
        return aTrace.getActivityList();
    }

    public List<AActivity> getActivityList() {
        return this.activityList;
    }

    public List<String> getActivityNameList() {

        return this.activityNameList;

    }

    public UnifiedSet<String> getEventNameSet() {
        return this.eventNameSet;
    }

    public UnifiedMap<String, String> getAttributeMap() {
        UnifiedMap<String, String> attr = new UnifiedMap<>();
        for (String key : attributeMap.keySet()) {
            if (!key.toLowerCase().equals("concept:name")) {
                attr.put(key.intern(), attributeMap.get(key).intern());
            }
        }
        return attr;
    }

    public List<AEvent> getEventList() {

        return eventList;
    }

    @Override
    public int size() {
        return eventList.size();
    }

    @Override
    public AEvent get(int index) {
        return eventList.get(index);
    }


    public double getTotalProcessingTime() {
        return totalProcessingTime;
    }

    public double getAverageProcessingTime() {
        return averageProcessingTime;
    }

    public double getMaxProcessingTime() {
        return maxProcessingTime;
    }

    public double getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public double getCaseUtilization() {
        return caseUtilization;
    }

    @Override
    public void setTotalProcessingTime(double time) {

    }

    @Override
    public void setAverageProcessingTime(double time) {

    }

    @Override
    public void setMaxProcessingTime(double time) {

    }

    @Override
    public void setTotalWaitingTime(double time) {

    }

    @Override
    public void setAverageWaitingTime(double time) {

    }

    @Override
    public void setMaxWaitingTime(double time) {

    }

    @Override
    public void setCaseUtilization(double caseUtilization) {

    }

    public BitSet getValidEventIndexBitSet() {
        return validEventIndexBS;
    }

    @Override
    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(startTimeMilli));
    }

    @Override
    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(endTimeMilli));
    }

    @Override
    public String getDurationString() {
        return Util.durationShortStringOf(duration);
    }

    public BitSet getOriginalValidEventIndexBS() {
        return originalValidEventIndexBS;
    }

    public BitSet getPreviousValidEventIndexBS() {
        return previousValidEventIndexBS;
    }

    public void setOriginalValidEventIndexBS(BitSet originalValidEventIndexBS) {
        this.originalValidEventIndexBS = originalValidEventIndexBS;
    }

    public ATrace toATrace() {


        ImmutableTrace trace = new ImmutableTrace(immutableIndex, mutableIndex, attributeMap);


        for (int i = 0; i < activityList.size(); i++) {
            AActivity act = activityList.get(i);

            trace.addActivity(act);

        }

        for (AEvent event : eventList) {
            event.setParentTrace(trace);
        }

        trace.setEventList(eventList);
        trace.setImmutableEvents(aTrace.getImmutableEvents());
        trace.setCaseVariantId(caseVariantId);
        trace.setHasActivity(hasActivity);
        trace.setTotalProcessingTime(totalProcessingTime);
        trace.setAverageProcessingTime(averageProcessingTime);
        trace.setMaxProcessingTime(maxProcessingTime);
        trace.setTotalWaitingTime(totalWaitingTime);
        trace.setAverageWaitingTime(averageWaitingTime);
        trace.setMaxWaitingTime(maxWaitingTime);
        trace.setCaseUtilization(caseUtilization);

        return trace;
    }

    @Override
    public int compareTo(PTrace o) {
        if (Util.isNumeric(this.caseId) && Util.isNumeric(o.getCaseId())) {
            if (caseIdDigit > o.caseIdDigit) return 1;
            else if (caseIdDigit < o.caseIdDigit) return -1;
            else return 0;
        } else {
            return getCaseId().compareTo(o.getCaseId());
        }
    }
}
