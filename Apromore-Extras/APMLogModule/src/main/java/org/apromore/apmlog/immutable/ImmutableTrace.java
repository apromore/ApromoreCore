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
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ImmutableTrace implements ATrace {

    private int immutableIndex;
    private int mutableIndex;
    private List<AActivity> activities;
    private List<AEvent> events;
    private List<AEvent> immutableEvents;
    private UnifiedMap<String, String> attributes;
    private int caseVariantId;
    private boolean hasActivity;
    private double ttlProcessTime, avgProcessTime, maxProcessTime, ttlWaitTime, avgWaitTime, maxWaitTime,
                    caseUtilization;
    private IntArrayList activityNameIndexes;
    private long startTime = 0, endTime = 0;

    public ImmutableTrace(int immutableIndex, int mutableIndex, UnifiedMap<String, String> attributes) {
        this.immutableIndex = immutableIndex;
        this.mutableIndex = mutableIndex;
        activities = new ArrayList<>();
        events = new ArrayList<>();
        immutableEvents = new ArrayList<>();
        this.attributes = attributes;
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
        return attributes.get("concept:name");
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

        return attributes;
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
        return ttlProcessTime;
    }

    @Override
    public double getAverageProcessingTime() {
        return avgProcessTime;
    }

    @Override
    public double getMaxProcessingTime() {
        return maxProcessTime;
    }

    @Override
    public double getTotalWaitingTime() {
        return ttlWaitTime;
    }

    @Override
    public double getAverageWaitingTime() {
        return avgWaitTime;
    }

    @Override
    public double getMaxWaitingTime() {
        return maxWaitTime;
    }

    @Override
    public double getCaseUtilization() {
        return caseUtilization;
    }

    @Override
    public void setTotalProcessingTime(double time) {
        this.ttlProcessTime = time;
    }

    @Override
    public void setAverageProcessingTime(double time) {
        this.avgProcessTime = time;
    }

    @Override
    public void setMaxProcessingTime(double time) {
        this.maxProcessTime = time;
    }

    @Override
    public void setTotalWaitingTime(double time) {
        this.ttlWaitTime = time;
    }

    @Override
    public void setAverageWaitingTime(double time) {
        this.avgWaitTime = time;
    }

    @Override
    public void setMaxWaitingTime(double time) {
        this.maxWaitTime = time;
    }

    @Override
    public void setCaseUtilization(double caseUtilization) {
        this.caseUtilization = caseUtilization;
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

        ImmutableTrace traceClone = new ImmutableTrace(immutableIndex, mutableIndex, attrClone);


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
        traceClone.setTotalProcessingTime(ttlProcessTime);
        traceClone.setAverageProcessingTime(avgProcessTime);
        traceClone.setMaxProcessingTime(maxProcessTime);
        traceClone.setTotalWaitingTime(ttlWaitTime);
        traceClone.setAverageWaitingTime(avgWaitTime);
        traceClone.setMaxWaitingTime(maxWaitTime);
        traceClone.setCaseUtilization(caseUtilization);

        return traceClone;
    }

}
