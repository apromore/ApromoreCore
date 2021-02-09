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

import java.util.ArrayList;
import java.util.List;

public class ImmutableActivity implements AActivity {
    private int immutableIndex;
    private int mutableIndex;
    private ATrace parentTrace;
    private IntArrayList eventIndexes;
    private long startTime;
    private long endTime;
    private UnifiedMap<String, String> attributes;

    public ImmutableActivity(int immutableIndex,
                             int mutableIndex,
                             ATrace parentTrace,
                             IntArrayList eventIndexes,
                             long startTime,
                             long endTime,
                             UnifiedMap<String, String> attributes) {
        this.immutableIndex = immutableIndex;
        this.mutableIndex = mutableIndex;
        this.parentTrace = parentTrace;
        this.eventIndexes = eventIndexes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attributes = attributes;
    }

    public ImmutableActivity(int immutableIndex,
                             int mutableIndex,
                             IntArrayList eventIndexes,
                             long startTime,
                             long endTime,
                             UnifiedMap<String, String> attributes) {
        this.immutableIndex = immutableIndex;
        this.mutableIndex = mutableIndex;
        this.eventIndexes = eventIndexes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attributes = attributes;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public IntArrayList getEventIndexes() {
        return eventIndexes;
    }

    public void setMutableIndex(int mutableIndex) {
        this.mutableIndex = mutableIndex;
    }

    public int getMutableIndex() {
        return mutableIndex;
    }

    @Override
    public String getAttributeValue(String key) {
        return getAllAttributes().get(key);
    }

    public ATrace getParentTrace() {
        return parentTrace;
    }

    public int getMutableTraceIndex() {
        return parentTrace.getMutableIndex();
    }

    @Override
    public int getImmutableTraceIndex() {
        return parentTrace.getImmutableIndex();
    }

    @Override
    public void setParentTrace(ATrace parentTrace) {
        this.parentTrace = parentTrace;
    }

    public void setImmutableIndex(int mutableIndex) {
        this.mutableIndex = mutableIndex;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public UnifiedMap<String, String> getAttributes() {
        return attributes;
//        return parentTrace.getActivityAttributesList().get(immutableIndex);
    }

    @Override
    public UnifiedMap<String, String> getAllAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(UnifiedMap<String, String> attributes) {
        this.attributes = attributes;
    }


    @Override
    public int getImmutableIndex() {
        return immutableIndex;
    }

    public String getName() {
        try {
            return getAllAttributes().get("concept:name");
        } catch (Exception e) {
            return null;
        }
    }

    public String getResource() {
        return getAllAttributes().containsKey("org:resource") ? getAllAttributes().get("org:resource") : "";
    }

    @Override
    public UnifiedMap<String, String> getAttributeMap() {
        return getAllAttributes();
    }

    @Override
    public List<AEvent> getImmutableEventList() {
        List<AEvent> events = new ArrayList<>();
        for (int i = 0; i < eventIndexes.size(); i++) {
            int eventIdx = eventIndexes.get(i);
            AEvent event = parentTrace.getImmutableEvents().get(eventIdx);
            events.add(event);
        }
        return events;
    }

    @Override
    public long getStartTimeMilli() {
        return startTime;
    }

    @Override
    public long getEndTimeMilli() {
        return endTime;
    }

    @Override
    public double getDuration() {
        return endTime > startTime ? endTime - startTime : 0;
    }

    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(Double.valueOf(startTime).longValue()));
    }

    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(Double.valueOf(endTime).longValue()));
    }

    public long getEventSize() {
        return eventIndexes.size();
    }

    @Override
    public AActivity clone(ATrace parentTrace) {
        UnifiedMap<String, String> attributesClone = new UnifiedMap<>(attributes.size());
        for (String key : attributes.keySet()) {
            attributesClone.put(key.intern(), attributes.get(key).intern());
        }

        IntArrayList eventIndexClone = new IntArrayList(eventIndexes.size());
        for (int i = 0; i < eventIndexes.size(); i++) {
            eventIndexClone.add(eventIndexes.get(i));
        }


        AActivity actClone = new ImmutableActivity(immutableIndex, mutableIndex, parentTrace, eventIndexClone,
                startTime, endTime, attributesClone);

        return actClone;
    }

    @Override
    public AActivity clone() {
        UnifiedMap<String, String> attributesClone = new UnifiedMap<>(attributes.size());
        for (String key : attributes.keySet()) {
            attributesClone.put(key.intern(), attributes.get(key).intern());
        }

        IntArrayList eventIndexClone = new IntArrayList(eventIndexes.size());
        for (int i = 0; i < eventIndexes.size(); i++) {
            eventIndexClone.add(eventIndexes.get(i));
        }


        AActivity actClone = new ImmutableActivity(immutableIndex, mutableIndex, eventIndexClone,
                startTime, endTime, attributesClone);

        return actClone;
    }


}
