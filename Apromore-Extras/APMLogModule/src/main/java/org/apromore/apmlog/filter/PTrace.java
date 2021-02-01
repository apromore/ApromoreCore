/*-
 * #%L
 * Process Discoverer Logic
 *
 * This file is part of "Apromore".
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


package org.apromore.apmlog.filter;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.immutable.ImmutableTrace;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;
import java.util.stream.Collectors;

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
 * Modified: Chii Chang (26/01/2021)
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
    private int eventSize;

    public String startTimeString, endTimeString, durationString;

    private List<AActivity> activityList;
    private List<AEvent> eventList;
    private UnifiedMap<String, String> attributeMap;
    private List<String> activityNameList;
    private UnifiedSet<String> eventNameSet;
    private DoubleArrayList processingTimes;
    private DoubleArrayList waitingTimes;
    private double caseUtilization;

    private BitSet validEventIndexBS;

    private List<Integer> activityNameIndexList;
    private List<Integer> previousActiivtyNameIndexList;

    private BitSet originalValidEventIndexBS;

    private BitSet previousValidEventIndexBS;

    private DoubleArrayList previousProcessingTimes;
    private DoubleArrayList previousWaitingTimes;
    private long previousStartTimeMilli;
    private long previousEndTimeMilli;
    private double previousDuration = 0;
    private boolean previousHasActivity = false;
    private double previousCaseUtilization;

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

        this.processingTimes = aTrace.getProcessingTimes();
        this.waitingTimes = aTrace.getWaitingTimes();

        this.startTimeString = aTrace.getStartTimeString();
        this.endTimeString = aTrace.getEndTimeString();
        this.durationString = aTrace.getDurationString();

        this.startTimeMilli = aTrace.getStartTimeMilli();
        this.endTimeMilli = aTrace.getEndTimeMilli();
        this.duration = aTrace.getDuration();
        this.hasActivity = aTrace.isHasActivity();

        this.previousProcessingTimes = new DoubleArrayList(aTrace.getProcessingTimes().toArray());
        this.previousWaitingTimes = new DoubleArrayList(aTrace.getWaitingTimes().toArray());
        this.previousStartTimeMilli = aTrace.getStartTimeMilli();
        this.previousEndTimeMilli = aTrace.getEndTimeMilli();
        this.previousDuration = aTrace.getDuration();
        this.previousHasActivity = aTrace.isHasActivity();

        this.eventList = new ArrayList<>(aTrace.getEventList());
        this.previousEventList = new ArrayList<>(aTrace.getEventList());

        this.activityList = new ArrayList<>(aTrace.getActivityList());
        this.previousActivityList = new ArrayList<>(aTrace.getActivityList());

        this.eventList = new ArrayList<>(aTrace.getEventList());
        this.previousEventList = new ArrayList<>(aTrace.getEventList());

        this.attributeMap = aTrace.getAttributeMap();
        this.previousAttributeMap = aTrace.getAttributeMap();


        List<String> aTraceActNameList = aTrace.getActivityNameList();


        this.activityNameList = new ArrayList<>(aTraceActNameList);
        this.previousActivityNameList = new ArrayList<>(aTraceActNameList);

        if (aTrace.getActivityNameIndexList() != null) {
            this.activityNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
            this.previousActiivtyNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
        }

        UnifiedSet<String> aTraceEventNameSet = aTrace.getEventNameSet();

        this.eventNameSet = new UnifiedSet<>(aTraceEventNameSet);
        this.previousEventNameSet = new UnifiedSet<>(aTraceEventNameSet);

        for (AActivity activity : activityList) {
            activity.setParentTrace(this);
        }

        double aTraceUtilization = aTrace.getCaseUtilization();
        this.caseUtilization = aTraceUtilization;
        this.previousCaseUtilization = aTraceUtilization;

        this.eventSize = eventList.size();

        int originalEventSize = aTrace.getEventSize();

        this.validEventIndexBS = new BitSet(originalEventSize);
        this.validEventIndexBS.set(0, originalEventSize);

        originalValidEventIndexBS = new BitSet(originalEventSize);
        originalValidEventIndexBS.set(0, originalEventSize);

        previousValidEventIndexBS = new BitSet(originalEventSize);
        previousValidEventIndexBS.set(0, originalEventSize);
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

            processingTimes = previousProcessingTimes;
            waitingTimes = previousWaitingTimes;

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

        previousActivityList = activityList;
        previousEventList = eventList;
        previousAttributeMap = attributeMap;
        previousActivityNameList = activityNameList;
        previousEventNameSet = eventNameSet;

        previousWaitingTimes = waitingTimes;
        previousProcessingTimes = processingTimes;

        previousActiivtyNameIndexList = activityNameIndexList;
        previousCaseUtilization = caseUtilization;
    }

    public void update(int mutableIndex) {

        this.mutableIndex = mutableIndex;

        previousValidEventIndexBS = (BitSet) validEventIndexBS.clone();

        previousStartTimeMilli = startTimeMilli;
        previousEndTimeMilli = endTimeMilli;
        previousDuration = duration;
        previousHasActivity = hasActivity;
        previousProcessingTimes = processingTimes;
        previousWaitingTimes = waitingTimes;
        previousActivityList = activityList;
        previousEventList = eventList;
        previousAttributeMap = attributeMap;
        previousActivityNameList = activityNameList;
        previousEventNameSet = eventNameSet;
        previousActiivtyNameIndexList = activityNameIndexList;

        List<AActivity> originalActList = aTrace.getActivityList();

        this.eventList = new ArrayList<>();
        this.activityList = new ArrayList<>();

        processingTimes = new DoubleArrayList();
        waitingTimes = new DoubleArrayList();

        List<AEvent> aEventList = aTrace.getImmutableEvents();

        int actMutIndex = 0;

        for (AActivity activity : originalActList) {
            boolean valid = false;
            IntArrayList eventIndexes = activity.getEventIndexes();
            for (int i = 0; i < eventIndexes.size(); i++) {
                if (validEventIndexBS.get(eventIndexes.get(i))) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                activity.setMutableIndex(actMutIndex);
                this.activityList.add(activity);
                actMutIndex += 1;
            }
        }

        if (this.activityList.size() > 0) {

            for (AActivity activity : activityList) {
                IntArrayList eventIndexes = activity.getEventIndexes();
                for (int j = 0; j < eventIndexes.size(); j++) {
                    this.eventList.add(aEventList.get(eventIndexes.get(j)));
                }
            }

            for (int i = 0; i < activityList.size(); i++) {
                AActivity iAct = activityList.get(i);
                processingTimes.add(iAct.getDuration());

                if (i+1 < activityList.size()) {
                    AActivity nAct = activityList.get(i + 1);
                    long iET = iAct.getEndTimeMilli();
                    long nST = nAct.getStartTimeMilli();
                    long wt = nST > iET ? nST - iET : 0;
                    waitingTimes.add(wt);
                }
            }
        }

        if (waitingTimes.isEmpty() || processingTimes.isEmpty()) caseUtilization = 1.0;
        else {
            double ttlWaitTime = waitingTimes.sum();
            double ttlProcTime = processingTimes.sum();
            double dur = getDuration();

            if (ttlWaitTime > 0 && ttlProcTime > 0) {
                caseUtilization = ttlProcTime / (ttlProcTime + ttlWaitTime);
            } else {
                caseUtilization = ttlProcTime > 0 && ttlProcTime < dur ? ttlProcTime / dur : 1.0;
            }

            if (caseUtilization > 1.0) caseUtilization = 1.0;
        }

    }



    private void updateStats(List<AActivity> activities) {

        processingTimes = new DoubleArrayList();
        waitingTimes = new DoubleArrayList();

        for (int i = 0; i < activities.size(); i++) {
            AActivity iAct = activities.get(i);
            processingTimes.add(iAct.getDuration());

            if (i+1 < activities.size()) {
                AActivity nAct = activities.get(i + 1);
                long iET = iAct.getEndTimeMilli();
                long nST = nAct.getStartTimeMilli();
                long wt = nST > iET ? nST - iET : 0;
                waitingTimes.add(wt);
            }
        }
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
    public DoubleArrayList getWaitingTimes() {
        return waitingTimes;
    }

    @Override
    public DoubleArrayList getProcessingTimes() {
        return processingTimes;
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
        return aTrace.getDuration();
    }

    public List<AEvent> getOriginalEventList() {
        return aTrace.getEventList();
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
        Map<String, String> collect = attributeMap.entrySet().stream()
                .filter(x -> !x.getKey().equals("concept:name") && !x.equals("case:variant") )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new UnifiedMap<>(collect);
    }

    public List<AEvent> getEventList() {

        return eventList;
    }

    public Set<String> getAttributeKeys() {
        return attributeMap.keySet();
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
        return !processingTimes.isEmpty() ? processingTimes.sum() : 0;
    }

    public double getAverageProcessingTime() {
        return !processingTimes.isEmpty() ? processingTimes.average() : 0;
    }

    public double getMaxProcessingTime() {
        return !processingTimes.isEmpty() ? processingTimes.max() : 0;
    }

    public double getTotalWaitingTime() {
        return !waitingTimes.isEmpty() ? waitingTimes.sum() : 0;
    }

    public double getAverageWaitingTime() {
        return !waitingTimes.isEmpty() ? waitingTimes.average() : 0;
    }

    public double getMaxWaitingTime() {
        return !waitingTimes.isEmpty() ? waitingTimes.max() : 0;
    }

    public double getCaseUtilization() {
        return caseUtilization;
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


        ImmutableTrace trace = new ImmutableTrace(immutableIndex, mutableIndex, caseId, attributeMap);


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
        trace.setWaitingTimes(waitingTimes);
        trace.setProcessingTimes(processingTimes);

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
