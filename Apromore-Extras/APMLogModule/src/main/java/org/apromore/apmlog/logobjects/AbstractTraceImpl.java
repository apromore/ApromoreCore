/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogAttribute;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.apromore.apmlog.util.Util;
import org.apromore.calendar.model.CalendarModel;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTraceImpl implements ATrace, APMLogAttribute, Serializable {

    protected APMLog sourceLog;
    protected int immutableIndex;
    protected String caseId;
    protected List<ImmutableEvent> immutableEvents;
    protected final List<ActivityInstance> activityInstances = new ArrayList<>();
    protected UnifiedMap<String, String> attributes;
    private int[] activityInstancesIndicator;

    protected HashBiMap<ActivityInstance, Integer> activityInstanceIndexMap;

    // ========================================================
    // Initiation method
    // ========================================================
    public AbstractTraceImpl(int immutableIndex,
                             String caseId,
                             List<ImmutableEvent> immutableEvents,
                             List<ActivityInstance> activityInstances,
                             UnifiedMap<String, String> attributes,
                             APMLog apmLog) {
        this.immutableIndex = immutableIndex;
        this.caseId = caseId;
        this.immutableEvents = immutableEvents;
        setActivityInstances(activityInstances);
        this.attributes = attributes;
        this.sourceLog = apmLog;
    }

    private void initActivityInstanceIndicators() {
        activityInstancesIndicator = activityInstances.stream()
                .mapToInt(ActivityInstance::getNameIndicator)
                .toArray();
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
    public long getStartTime() {
        return getFirst().getStartTime();
    }

    @Override
    public long getEndTime() {
        return getLast().getEndTime();
    }

    @Override
    public double getDuration() {
        return TimeStatsProcessor.getCaseDuration(this);
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
        if (activityInstances.size() == 1)
            return null;

        if (!activityInstanceIndexMap.containsKey(activityInstance))
            return null;

        if (activityInstance == getLast())
            return null;

        int sourceIndex = activityInstanceIndexMap.get(activityInstance);

        return activityInstanceIndexMap.inverse().get(sourceIndex + 1);
    }

    public ActivityInstance getPreviousOf(ActivityInstance activityInstance) {
        if (activityInstances.size() == 1)
            return null;

        if(!activityInstanceIndexMap.containsKey(activityInstance))
            return null;

        if (activityInstance == getFirst())
            return null;

        int sourceIndex = activityInstanceIndexMap.get(activityInstance);

        return activityInstanceIndexMap.inverse().get(sourceIndex - 1);
    }

    @Override
    public APMLog getSourceLog() {
        return sourceLog;
    }

    @Override
    public CalendarModel getCalendarModel() {
        return sourceLog.getCalendarModel();
    }

    @Override
    public String getActivityInstancesIndicator() {
        if (activityInstancesIndicator == null)
            initActivityInstanceIndicators();

        return Arrays.toString(activityInstancesIndicator);
    }

    @Override
    public int[] getActivityInstancesIndicatorArray() {
        if (activityInstancesIndicator == null)
            initActivityInstanceIndicators();

        return activityInstancesIndicator;
    }

    // ========================================================
    // SET methods
    // ========================================================

    public void setActivityInstances(List<ActivityInstance> activityInstances) {
        activityInstanceIndexMap = new HashBiMap<>();

        this.activityInstances.clear();
        if (activityInstances != null) {
            this.activityInstances.addAll(activityInstances);
            initActivityInstanceIndicators();
            int index = 0;
            for (ActivityInstance activityInstance : activityInstances) {
                activityInstanceIndexMap.put(activityInstance, index);
                index += 1;
            }
        }
    }

    public HashBiMap<ActivityInstance, Integer> getActivityInstanceIndexMap() {
        return activityInstanceIndexMap;
    }

}
