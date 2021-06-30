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

import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.apromore.apmlog.util.AttributeCodes;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity instance object contains mutable index attribute
 * which allows the other classes to assign the mutable indexes at runtime
 *
 * The nameIndicator is the numeric representation of the name of this activity instance
 * which is needed for identifying the case variant of the trace at runtime
 * with less memory usage
 *
 * @author Chii Chang (created: 06/05/2021)
 */
public class ActivityInstance implements Serializable {

    private int immutableIndex;
    private int immutableTraceIndex;
    private int mutableTraceIndex;
    private String parentCaseId;
    private List<Integer> immutableEventIndexes;
    private int nameIndicator;
    private long startTime;
    private long endTime;
    private UnifiedMap<String, String> attributes;

    public ActivityInstance(int immutableIndex,
                            List<Integer> immutableEventIndexes,
                            int immutableTraceIndex,
                            String parentCaseId,
                            int nameIndicator,
                            long startTime,
                            long endTime,
                            UnifiedMap<String, String> attributes) {
        this.immutableIndex = immutableIndex;
        this.immutableEventIndexes = immutableEventIndexes;
        this.immutableTraceIndex = immutableTraceIndex;
        this.parentCaseId = parentCaseId;
        this.mutableTraceIndex = immutableTraceIndex;
        this.nameIndicator = nameIndicator;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attributes = attributes;
    }

    // ========================================================
    // SET methods
    // ========================================================

    public void setMutableTraceIndex(int mutableTraceIndex) {
        this.mutableTraceIndex = mutableTraceIndex;
    }

    public void setAttributes(UnifiedMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    // ========================================================
    // GET methods
    // ========================================================

    public int getImmutableIndex() {
        return immutableIndex;
    }

    public int getImmutableTraceIndex() {
        return immutableTraceIndex;
    }

    public int getMutableTraceIndex() {
        return mutableTraceIndex;
    }

    public String getParentTraceId() {
        return parentCaseId;
    }

    public List<Integer> getImmutableEventIndexes() {
        return immutableEventIndexes;
    }

    public int getNameIndicator() {
        return nameIndicator;
    }

    public String getName() {
        return attributes.containsKey(AttributeCodes.CONCEPT_NAME) ? attributes.get(AttributeCodes.CONCEPT_NAME) : "";
    }

    public String getResource() {
        return attributes.containsKey(AttributeCodes.ORG_RESOURCE) ? attributes.get(AttributeCodes.ORG_RESOURCE) : "";
    }

    public UnifiedMap<String, String> getAttributes() {
        return attributes;
    }

    public String getAttributeValue(String key) {
        return attributes.containsKey(key) ? attributes.get(key) : "";
    }

    public long getStartTime() {
        return startTime > 0 ? startTime : 0;
    }

    public long getEndTime() {
        return endTime > 0 ? endTime : 0;
    }

    public double getDuration() {
        return TimeStatsProcessor.getActivityInstanceDuration(this);
    }

    public long getEventSize() {
        return immutableEventIndexes.size();
    }

    public int getFirstEventIndex() {
        return immutableEventIndexes.get(0);
    }

    // ========================================================
    // Operation methods
    // ========================================================

    public ActivityInstance clone() {
        return new ActivityInstance(immutableIndex,
                new ArrayList<>(immutableEventIndexes),
                immutableTraceIndex,
                parentCaseId,
                nameIndicator,
                startTime,
                endTime,
                new UnifiedMap<>(attributes));
    }

    public ActivityInstance clone(int immutableIndex) {
        return new ActivityInstance(immutableIndex,
                new ArrayList<>(immutableEventIndexes),
                immutableTraceIndex,
                parentCaseId,
                nameIndicator,
                startTime,
                endTime,
                new UnifiedMap<>(attributes));
    }
}
