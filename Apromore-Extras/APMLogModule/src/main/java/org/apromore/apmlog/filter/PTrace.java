/*-
 * #%L
 * Process Discoverer Logic
 *
 * This file is part of "Apromore".
 * %%
 * Copyright (C) 2019 - 2020 The University of Melbourne. All rights reserved.
 * %%
 * #L%
 */


package org.apromore.apmlog.filter;

import org.apromore.apmlog.*;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
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
 */
public class PTrace implements Comparable<PTrace>, LaTrace {

//    private ATrace aTrace;

    private String caseId = "";
    public long caseIdDigit = 0;
    private int caseVariantId = 0;
    private long startTimeMilli = -1;
    private long endTimeMilli = -1;
    private long duration = 0;
    private boolean hasActivity = false;
    private long totalProcessingTime = 0;
    private long averageProcessingTime = 0;
    private long maxProcessingTime = 0;
    private long totalWaitingTime = 0;
    private long averageWaitingTime = 0;
    private long maxWaitingTime = 0;
    private double caseUtilization = 0.0;
    private int eventSize;

    public String startTimeString, endTimeString, durationString;

    private List<AActivity> activityList;
    private List<AEvent> eventList;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap;
    private UnifiedMap<String, String> attributeMap;
    private List<String> activityNameList;
    private UnifiedSet<String> eventNameSet;

    private BitSet validEventIndexBS;

    private long originalStartTimeMilli;
    private long originalEndTimeMilli;
    private long originalDuration = 0;
    private boolean originalHasActivity = false;
    private long originalTotalProcessingTime = 0;
    private long originalAverageProcessingTime = 0;
    private long originalMaxProcessingTime = 0;
    private long originalTotalWaitingTime = 0;
    private long originalAverageWaitingTime = 0;
    private long originalMaxWaitingTime = 0;
    private double originalCaseUtilization = 0;
    private List<AActivity> originalActivityList;
    private List<AEvent> originalEventList;
    private UnifiedMap<String, UnifiedMap<String, Integer>> originalEventAttributeValueFreqMap;
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
    private long previousDuration = 0;
    private boolean previousHasActivity = false;
    private long previousTotalProcessingTime = 0;
    private long previousAverageProcessingTime = 0;
    private long previousMaxProcessingTime = 0;
    private long previousTotalWaitingTime = 0;
    private long previousAverageWaitingTime = 0;
    private long previousMaxWaitingTime = 0;
    private double previousCaseUtilization = 0;
    private List<AActivity> previousActivityList;
    private List<AEvent> previousEventList;
    private UnifiedMap<String, UnifiedMap<String, Integer>> previousEventAttributeValueFreqMap;
    private UnifiedMap<String, String> previousAttributeMap;
    private List<String> previousActivityNameList;
    private UnifiedSet<String> previousEventNameSet;

    private APMLog apmLog;

    public PTrace(ATrace aTrace, APMLog apmLog) {
//        this.aTrace = aTrace;

        this.apmLog = apmLog;

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



        UnifiedMap<String, UnifiedMap<String, Integer>> eavfMap = aTrace.getEventAttributeValueFreqMap();



        this.eventAttributeValueFreqMap = new UnifiedMap<>(eavfMap);
        this.originalEventAttributeValueFreqMap = new UnifiedMap<>(eavfMap);
        this.previousEventAttributeValueFreqMap = new UnifiedMap<>(eavfMap);

        this.attributeMap = aTrace.getAttributeMap();
        this.previousAttributeMap = aTrace.getAttributeMap();
        this.originalAttributeMap = aTrace.getAttributeMap();


        List<String> aTraceActNameList = aTrace.getActivityNameList();


        this.activityNameList = new ArrayList<>(aTraceActNameList);
        this.originalActivityNameList = new ArrayList<>(aTraceActNameList);
        this.previousActivityNameList = new ArrayList<>(aTraceActNameList);

        this.activityNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
        this.originalActivityNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());
        this.previousActiivtyNameIndexList = new ArrayList<>(aTrace.getActivityNameIndexList());

        UnifiedSet<String> aTraceEventNameSet = aTrace.getEventNameSet();



        this.eventNameSet = new UnifiedSet<>(aTraceEventNameSet);
        this.originalEventNameSet = new UnifiedSet<>(aTraceEventNameSet);
        this.previousEventNameSet = new UnifiedSet<>(aTraceEventNameSet);


        this.eventSize = eventList.size();
    }

    public void reset() {



        validEventIndexBS.set(0, originalEventList.size(), true);

        startTimeMilli = originalStartTimeMilli;
        endTimeMilli = originalEndTimeMilli;
        duration = originalDuration;
        hasActivity = originalHasActivity;
        totalProcessingTime = originalTotalProcessingTime;
        averageProcessingTime = originalAverageProcessingTime;
        maxProcessingTime = originalMaxProcessingTime;
        totalWaitingTime = originalTotalWaitingTime;
        averageWaitingTime = originalAverageWaitingTime;
        maxWaitingTime = originalMaxWaitingTime;
        caseUtilization = originalCaseUtilization;

        this.activityList = originalActivityList;
        this.eventList = originalEventList;
        this.eventAttributeValueFreqMap = originalEventAttributeValueFreqMap;
        this.attributeMap = originalAttributeMap;
        this.activityNameList = originalActivityNameList;
        this.eventNameSet = originalEventNameSet;

        this.activityNameIndexList = originalActivityNameIndexList;
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
            this.eventAttributeValueFreqMap = previousEventAttributeValueFreqMap;
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
        previousEventAttributeValueFreqMap = eventAttributeValueFreqMap;
        previousAttributeMap = attributeMap;
        previousActivityNameList = activityNameList;
        previousEventNameSet = eventNameSet;

        previousActiivtyNameIndexList = activityNameIndexList;
    }

    public void update() {


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
        previousEventAttributeValueFreqMap = eventAttributeValueFreqMap;
        previousAttributeMap = attributeMap;
        previousActivityNameList = activityNameList;
        previousEventNameSet = eventNameSet;
        previousActiivtyNameIndexList = activityNameIndexList;


        this.eventList = new ArrayList<>();
        for(int i=0; i < this.originalEventList.size(); i++) {
            if(validEventIndexBS.get(i) == true) eventList.add(this.originalEventList.get(i));
        }

        if (eventList.size() > 0) {

            long waitCount = 0;
            long processCount = 0;

            this.activityList = new ArrayList<>();
            this.eventAttributeValueFreqMap = new UnifiedMap<>();
            this.activityNameList = new ArrayList<>();
            this.eventNameSet = new UnifiedSet<>();
            this.activityNameIndexList = new ArrayList<>();


            IntArrayList markedIndex = new IntArrayList();

            List<Long> allTimestamps = new ArrayList<>();

            this.activityList = new ArrayList<>();

            for (int i = 0; i < this.eventList.size(); i++) {

                if (!markedIndex.contains(i)) {

                    markedIndex.add(i);

                    AEvent iAEvent = this.eventList.get(i);
                    String conceptName = iAEvent.getName();

                    long eventTime = iAEvent.getTimestampMilli();
                    allTimestamps.add(eventTime);
//                if(startTimeMilli == -1 || startTimeMilli > eventTime) {
//                    startTimeMilli = eventTime;
//                }
//                if(endTimeMilli == -1 || endTimeMilli < eventTime) {
//                    endTimeMilli = eventTime;
//                }
                    this.eventNameSet.put(iAEvent.getName());

                    fillEventAttributeValueFreqMap(iAEvent);

                    List<AEvent> actEventList = new ArrayList<>();


                    if (iAEvent.getLifecycle().toLowerCase().equals("start")) {
                        actEventList.add(iAEvent);
                        IntArrayList follows = getFollowUpIndexes(i + 1, conceptName, this.eventList);
                        if (follows.size() > 0) {
                            for (int j = 0; j < follows.size(); j++) {
                                int eventIndex = follows.get(j);
                                markedIndex.add(follows.get(j));
                                actEventList.add(this.eventList.get(eventIndex));
                            }
                        }
                    } else if (iAEvent.getLifecycle().toLowerCase().equals("complete")) {
                        actEventList.add(iAEvent);
                    }

                    if (actEventList.size() > 0) {
                        AActivity aActivity = new AActivity(actEventList);

                        this.activityList.add(aActivity);
                        this.activityNameList.add(aActivity.getName());
                        this.activityNameIndexList.add(
                                apmLog.getActivityNameMapper().set(aActivity.getName()));
                    }
                }

//            AEvent iAEvent = this.eventList.get(i);
//
//            long eventTime = iAEvent.getTimestampMilli();
//
//            if(startTimeMilli == -1 || startTimeMilli > eventTime) {
//                startTimeMilli = eventTime;
//            }
//            if(endTimeMilli == -1 || endTimeMilli < eventTime) {
//                endTimeMilli = eventTime;
//            }
//
//            this.eventNameSet.put(iAEvent.getName());
//
//            fillEventAttributeValueFreqMap(iAEvent);

//            if(iAEvent.getLifecycle().equals("start")) {
//
//                markedEvent.put(iAEvent);
//
//                String startEventName = iAEvent.getName();
//
//                /**
//                 * Find the waiting time
//                 */
//                long iTime = iAEvent.getTimestampMilli();
//
//                if(i > 0) {
//                    for(int j=(i-1); j >=0; j--) {
//                        AEvent preAEvent = this.eventList.get(j);
//                        if(preAEvent.getLifecycle().equals("complete")) {
//                            long preTime = preAEvent.getTimestampMilli();
//                            if(iTime > preTime) {
//                                long waitingTime = iTime - preTime;
//                                this.totalWaitingTime += waitingTime;
//                                waitCount += 1;
//                                if(waitingTime > this.maxWaitingTime) {
//                                    this.maxWaitingTime = waitingTime;
//                                }
//                            }
//                        }
//                    }
//                }
//
//                boolean hasComplete = false;
//
//                /**
//                 * Find the duration
//                 */
//                if((i+1) <= (this.eventList.size()-1)) {
//                    for(int j=(i+1); j < this.eventList.size(); j++) {
//                        AEvent jAEvent = this.eventList.get(j);
//                        if(jAEvent.getName().equals(startEventName) &&
//                                jAEvent.getLifecycle().equals("complete")) {
//
//                            long endTime = jAEvent.getTimestampMilli();
//                            if(endTime > iTime) {
//                                long processTime = endTime - iTime;
//                                this.totalProcessingTime += processTime;
//                                if(processTime > this.maxProcessingTime) this.maxProcessingTime = processTime;
//                                processCount += 1;
//
//                                List<AEvent> aEventListForAct = new ArrayList<>();
//                                aEventListForAct.add(iAEvent);
//                                aEventListForAct.add(jAEvent);
//                                AActivity aActivity = new AActivity(aEventListForAct);
//                                this.activityList.add(aActivity);
//                                this.activityNameList.add(aActivity.getName());
//
//                                this.activityNameIndexList.add(
//                                        apmLog.getActivityNameMapper().set(aActivity.getName()));
//
//                                markedEvent.put(jAEvent);
//                                hasComplete = true;
//                            }
//
//                            break;
//                        }
//                    }
//                }
//            }
//
//
//            if( !markedEvent.contains(iAEvent) && iAEvent.getLifecycle().toLowerCase().equals("complete")) {
//                List<AEvent> aEventListForAct = new ArrayList<>();
//                aEventListForAct.add(iAEvent);
//                AActivity aActivity = new AActivity(aEventListForAct);
//                this.activityList.add(aActivity);
//                this.activityNameList.add(aActivity.getName());
//
//                this.activityNameIndexList.add(
//                        apmLog.getActivityNameMapper().set(aActivity.getName()));
//            }
            }
            if (this.totalProcessingTime > 0 && processCount > 0)
                this.averageProcessingTime = this.totalProcessingTime / processCount;
            if (this.totalWaitingTime > 0 && waitCount > 0) this.averageWaitingTime = this.totalWaitingTime / waitCount;

            Collections.sort(allTimestamps);
            long firstTS = allTimestamps.get(0);
            long lastTS = allTimestamps.get(allTimestamps.size() - 1);

//        if(getCaseId().equals("0050554374")) {
//            System.out.println("PAUSE");
//        }
            this.startTimeMilli = firstTS;
            this.endTimeMilli = lastTS;

            this.duration = lastTS - firstTS;

//        this.duration = endTimeMilli - startTimeMilli;
        } else {
            this.activityList = new ArrayList<>();
            this.eventAttributeValueFreqMap = new UnifiedMap<>();
            this.activityNameList = new ArrayList<>();
            this.eventNameSet = new UnifiedSet<>();
            this.activityNameIndexList = new ArrayList<>();

            this.startTimeMilli = 0;
            this.endTimeMilli = 0;
            this.duration = 0;

            this.totalProcessingTime = 0;
            this.averageProcessingTime = 0;
            this.maxProcessingTime = 0;
            this.totalWaitingTime = 0;
            this.averageWaitingTime = 0;
            this.maxWaitingTime = 0;
        }
    }

    private IntArrayList getFollowUpIndexes(int from, String conceptName, List<AEvent> eventList) {
        IntArrayList follows = new IntArrayList();
        for (int i = from; i < eventList.size(); i++) {
            AEvent aEvent = eventList.get(i);
            String actName = aEvent.getName();
            if (actName.equals(conceptName)) {
                String lifecycle = aEvent.getLifecycle();
                if (lifecycle.toLowerCase().equals("start")) {
                    break;
                }else if(lifecycle.toLowerCase().equals("complete")) {
                    follows.add(i);
                    break;
                } else {
                    follows.add(i);
                }
            }
        }
        return follows;
    }

    public List<Integer> getActivityNameIndexList() {
        return activityNameIndexList;
    }

    private void fillEventAttributeValueFreqMap(AEvent aEvent) {
        for(String key : aEvent.getAttributeMap().keySet()) {
            String iAValue = aEvent.getAttributeMap().get(key);
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

    public long getDuration() {
        return duration;
    }

    public long getOriginalDuration() {
        return originalDuration;
    }

    public List<AEvent> getOriginalEventList() {
        return originalEventList;
    }

    public boolean isHasActivity() {
        return hasActivity;
    }

    public List<AActivity> getActivityList() {
        return this.activityList;
//        UnifiedSet<String> invalidEventSet = new UnifiedSet<>();
//        for (int i = 0; i < this.originalEventList.size(); i++) {
//            if (!this.validEventIndexBS.get(i)) {
//                invalidEventSet.put(this.originalEventList.get(i).getName());
//            }
//        }
//
//        List<AActivity> aActivityList = new ArrayList<>();
//
//        for (int i = 0; i < this.activityList.size(); i++) {
//            AActivity aActivity = this.activityList.get(i);
//            if (!invalidEventSet.contains(aActivity.getName())) aActivityList.add(aActivity);
//        }

//        return aActivityList;
    }

    public List<String> getActivityNameList() {

        return this.activityNameList;

//        List<String> actNameList = new ArrayList<>();
//        for (int i = 0; i < activityList.size(); i++) {
//            actNameList.add(activityList.get(i).getName());
//        }
//        return actNameList;
    }

    public UnifiedSet<String> getEventNameSet() {
        return this.eventNameSet;
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public List<AEvent> getEventList() {
        List<AEvent> aEventList = new ArrayList<>();
        for (int i = 0; i < this.originalEventList.size(); i++) {
            if (this.validEventIndexBS.get(i)) {
                aEventList.add(this.originalEventList.get(i));
            }
        }
        return aEventList;
//        return eventList;
    }

//    public int size() {
//        return this.eventList.size();
//    }

//    public AEvent getById(int index) {
//        return this.eventList.getById(index);
//    }

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

    public BitSet getValidEventIndexBitSet() {
        return validEventIndexBS;
    }

    public BitSet getOriginalValidEventIndexBS() {
        return originalValidEventIndexBS;
    }

    public BitSet getPreviousValidEventIndexBS() {
        return previousValidEventIndexBS;
    }


    public ATrace toATrace() {
//        if(getCaseId().equals("0050554374")) {
//            System.out.println("PAUSE");
//        }
        ATrace aTrace = new ATrace(caseId, caseVariantId,
                startTimeMilli, endTimeMilli,
                hasActivity, duration,
                totalProcessingTime, averageProcessingTime, maxProcessingTime,
                totalWaitingTime,averageWaitingTime,maxWaitingTime,
                caseUtilization,
                activityList,
                eventList,
                eventAttributeValueFreqMap,
                attributeMap,
                activityNameList,
                eventNameSet,
                activityNameIndexList);

        return aTrace;
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
