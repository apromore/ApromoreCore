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
package org.apromore.apmlog.immutable;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImmutableTrace implements ATrace {

    private int immutableIndex;
    private int mutableIndex;
    private String caseId;
    private List<AActivity> activities;
    private List<AEvent> events;
    private List<AEvent> immutableEvents;
    private UnifiedMap<String, String> attributes;
    private int caseVariantId;
    private boolean hasActivity;

    private IntArrayList activityNameIndexes;
    private long startTime = 0, endTime = 0;
    private DoubleArrayList waitingTimes;
    private DoubleArrayList processingTimes;

    public ImmutableTrace(int immutableIndex, int mutableIndex, String caseId, UnifiedMap<String, String> attributes) {
        this.immutableIndex = immutableIndex;
        this.mutableIndex = mutableIndex;
        activities = new ArrayList<>();
        events = new ArrayList<>();
        immutableEvents = new ArrayList<>();
        this.attributes = attributes;

        this.caseId = caseId;
    }

    public void addEvent(AEvent event) {
        events.add(event);
        immutableEvents.add(event);
        if (event.getLifecycle().toLowerCase().equals("start")) hasActivity = true;
    }

    public void setAttributes(UnifiedMap<String, String> attributes) {
        this.attributes = attributes;
    }


    public void addActivity(AActivity aActivity) {
        aActivity.setParentTrace(this);
        this.activities.add(aActivity);
        if (startTime == 0 || aActivity.getStartTimeMilli() < startTime) startTime = aActivity.getStartTimeMilli();
        if (endTime == 0 || aActivity.getEndTimeMilli() > endTime) endTime = aActivity.getEndTimeMilli();
    }


    @Override
    public void setEventList(List<AEvent> eventList) {
        this.events = eventList;
    }


    @Override
    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return null;
    }


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

    @Override
    public String getCaseId() {
        return caseId;
    }

    @Override
    public void setCaseVariantId(int caseVariantId) {
        this.caseVariantId = caseVariantId;
    }

    @Override
    public int getCaseVariantId() {
        return caseVariantId;
    }

    @Override
    public int getEventSize() {
        return events.size();
    }

    @Override
    public long getStartTimeMilli() {
        if (activities.size() == 0) return 0;
        return startTime;
    }

    @Override
    public long getEndTimeMilli() {
        return endTime;
    }

    @Override
    public double getDuration() {
        long st = getStartTimeMilli();
        long et = getEndTimeMilli();
        return et > st ? et - st : 0;
    }

    @Override
    public boolean isHasActivity() {
        return hasActivity;
    }

    @Override
    public List<AActivity> getActivityList() {
        return activities;
    }

    @Override
    public List<String> getActivityNameList() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            UnifiedMap<String, String> attrMap = activities.get(i).getAttributes();
            if (attrMap != null) {
                if (attrMap.containsKey("concept:name")) names.add(attrMap.get("concept:name"));
            }
        }
        return names;

    }

    @Override
    public UnifiedSet<String> getEventNameSet() {
        UnifiedSet<String> set = new UnifiedSet<>();
        for (AActivity act : activities) {
            set.put(act.getName());
        }
        return set;
    }

    @Override
    public UnifiedMap<String, String> getAttributeMap() {

        Map<String, String> collect = attributes.entrySet().stream()
                .filter(x -> !x.getKey().equals("concept:name") && !x.equals("case:variant") )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new UnifiedMap<>(collect);
    }

    @Override
    public List<AEvent> getEventList() {
        return events;
    }

    public List<AEvent> getImmutableEvents() {
        return immutableEvents;
    }

    @Override
    public void setImmutableEvents(List<AEvent> events) {
        this.immutableEvents = events;
    }

    public void setWaitingTimes(DoubleArrayList waitingTimes) {
        this.waitingTimes = waitingTimes;
    }

    public void setProcessingTimes(DoubleArrayList processingTimes) {
        this.processingTimes = processingTimes;
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
    public void setStartTimeMilli(long startTimeMilli) {
        this.startTime = startTimeMilli;
    }

    @Override
    public void setEndTimeMilli(long endTimeMilli) {
        this.endTime = endTimeMilli;
    }



    @Override
    public int size() {
        return events.size();
    }

    @Override
    public AEvent get(int index) {
        return events.get(index);
    }

    @Override
    public double getTotalProcessingTime() {
        return !processingTimes.isEmpty() ? processingTimes.sum() : 0;
    }

    @Override
    public double getAverageProcessingTime() {
        return !processingTimes.isEmpty() ?  processingTimes.average() : 0;
    }

    @Override
    public double getMaxProcessingTime() {
        return !processingTimes.isEmpty() ?  processingTimes.max() : 0;
    }

    @Override
    public double getTotalWaitingTime() {
        return !waitingTimes.isEmpty() ?  waitingTimes.sum() : 0;
    }

    @Override
    public double getAverageWaitingTime() {
        return !waitingTimes.isEmpty() ? waitingTimes.average() : 0;
    }

    @Override
    public double getMaxWaitingTime() {
        return !waitingTimes.isEmpty() ? waitingTimes.max() : 0;
    }

    @Override
    public double getCaseUtilization() {
        if (waitingTimes.isEmpty() || processingTimes.isEmpty()) return 1.0;

        double ttlWaitTime = waitingTimes.sum();
        double ttlProcTime = processingTimes.sum();
        double dur = getDuration();
        double caseUtil;
        if (ttlWaitTime > 0 && ttlProcTime > 0) {
            caseUtil = ttlProcTime / (ttlProcTime + ttlWaitTime);
        } else {
            caseUtil = ttlProcTime > 0 && ttlProcTime < dur ? ttlProcTime / dur : 1.0;
        }

        if (caseUtil > 1.0) caseUtil = 1.0;
        return caseUtil;
    }

    @Override
    public BitSet getValidEventIndexBitSet() {
        return null;
    }

    @Override
    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(getStartTimeMilli()));
    }

    @Override
    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(getEndTimeMilli()));
    }

    @Override
    public String getDurationString() {
        return Util.durationStringOf(getDuration());
    }

    @Override
    public long getCaseIdDigit() {
        return Util.isNumeric(getCaseId()) ? Long.valueOf(getCaseId()) : -1;
    }

    @Override
    public List<Integer> getActivityNameIndexList() {
        return null;
    }

    @Override
    public void setCaseVariantIdForDisplay(int caseVariantIdForDisplay) {

    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    @Override
    public int getCaseVariantIdForDisplay() {
        return 0;
    }

    @Override
    public void setHasActivity(boolean opt) {
        hasActivity = opt;
    }


    @Override
    public ATrace clone() {
        UnifiedMap<String, String> attrClone = new UnifiedMap<>();
        for (String key : attributes.keySet()) {
            attrClone.put(key.intern(), attributes.get(key).intern());
        }

        ImmutableTrace traceClone = new ImmutableTrace(immutableIndex, mutableIndex, caseId, attrClone);


        for (int i = 0; i < activities.size(); i++) {
            AActivity originAct = activities.get(i);

            traceClone.addActivity(originAct.clone(traceClone));
        }

        for (int i = 0; i < events.size(); i++) {
            AEvent event = events.get(i);
            int parentActIndex = event.getParentActivityIndex();
            traceClone.addEvent(events.get(i).clone(traceClone, traceClone.getActivityList().get(parentActIndex)));
        }

        traceClone.setCaseVariantId(caseVariantId);
        traceClone.setHasActivity(hasActivity);
        traceClone.setWaitingTimes(new DoubleArrayList(waitingTimes.toArray()));
        traceClone.setProcessingTimes(new DoubleArrayList(processingTimes.toArray()));
        traceClone.setCaseId(caseId);

        return traceClone;
    }

}
