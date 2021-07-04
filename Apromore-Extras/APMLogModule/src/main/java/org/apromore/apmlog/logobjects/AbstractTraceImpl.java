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
package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTraceImpl implements ATrace, Serializable {

    protected int immutableIndex;
    protected String caseId;
    protected int caseVariantId;
    protected List<ImmutableEvent> immutableEvents;
    protected final List<ActivityInstance> activityInstances = new ArrayList<>();
    protected UnifiedMap<String, String> attributes;
    protected final DoubleArrayList processingTimes = new DoubleArrayList();
    protected final DoubleArrayList waitingTimes = new DoubleArrayList();
    protected double caseUtilization;
    protected long startTime;
    protected long endTime;

    // ========================================================
    // Initiation method
    // ========================================================
    public AbstractTraceImpl(int immutableIndex,
                             String caseId,
                             List<ImmutableEvent> immutableEvents,
                             List<ActivityInstance> activityInstances,
                             UnifiedMap<String, String> attributes) {
        this.immutableIndex = immutableIndex;
        this.caseId = caseId;
        this.immutableEvents = immutableEvents;
        setActivityInstances(activityInstances);
        this.attributes = attributes;
    }

    // ========================================================
    // GET methods
    // ========================================================

    @Override
    public int getImmutableIndex() {
        return immutableIndex;
    }

    @Override
    public String getCaseId() {
        return caseId;
    }

    @Override
    public Number getCaseIdDigit() {
        return Util.isNumeric(caseId) ? Double.valueOf(caseId) : null;
    }

    @Override
    public int getCaseVariantId() {
        return caseVariantId;
    }

    @Override
    public List<ActivityInstance> getActivityInstances() {
        return activityInstances;
    }

    @Override
    public String getCaseVariantIndicator() {
        return activityInstances.stream().map(x -> x.getNameIndicator() + "").collect(Collectors.joining());
    }

    @Override
    public UnifiedMap<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public DoubleArrayList getProcessingTimes() {
        if (processingTimes.isEmpty()) processingTimes.add(0);
        return processingTimes;
    }

    @Override
    public DoubleArrayList getWaitingTimes() {
        if (waitingTimes.isEmpty()) waitingTimes.add(0);
        return waitingTimes;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public double getDuration() {
        return TimeStatsProcessor.getCaseDuration(this);
    }

    @Override
    public double getCaseUtilization() {
        return caseUtilization;
    }

    @Override
    public List<ImmutableEvent> getImmutableEvents() {
        return immutableEvents;
    }

    public ActivityInstance getFirst() {
        return activityInstances.get(0);
    }

    public ActivityInstance getLast() {
        return activityInstances.get(activityInstances.size() - 1);
    }

    public ActivityInstance getNextOf(ActivityInstance activityInstance) {
        if (activityInstances.size() == 1) return null;
        if (activityInstance == getLast()) return null;

        return activityInstances.get(activityInstances.indexOf(activityInstance) + 1);
    }

    public ActivityInstance getPreviousOf(ActivityInstance activityInstance) {
        if (activityInstances.size() == 1) return null;
        if (activityInstance == getFirst()) return null;

        return activityInstances.get(activityInstances.indexOf(activityInstance) - 1);
    }

    // ========================================================
    // SET methods
    // ========================================================

    @Override
    public void setCaseVariantId(int caseVariantId) {
        this.caseVariantId = caseVariantId;
    }


    public void setActivityInstances(List<ActivityInstance> activityInstances) {
        this.activityInstances.clear();
        if (activityInstances != null) this.activityInstances.addAll(activityInstances);
    }

    // ===============================================================================================================
    // Operation methods
    // ===============================================================================================================
    protected void updateTimeStats() {
        processingTimes.clear();
        waitingTimes.clear();
        startTime = 0;
        endTime = 0;

        if (!activityInstances.isEmpty()) {
            processingTimes.addAll(TimeStatsProcessor.getProcessingTimes(activityInstances));
            waitingTimes.addAll(TimeStatsProcessor.getWaitingTimes(activityInstances));

            startTime = TimeStatsProcessor.getStartTime(activityInstances);
            endTime = TimeStatsProcessor.getEndTime(activityInstances);

            caseUtilization =
                    TimeStatsProcessor.getCaseUtilization(activityInstances, getProcessingTimes(), getWaitingTimes());
        }
    }

}
