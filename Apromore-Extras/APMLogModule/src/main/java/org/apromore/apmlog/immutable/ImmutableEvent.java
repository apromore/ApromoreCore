/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.apmlog.immutable;


import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.LogFactory;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.time.ZonedDateTime;
import java.util.Set;

public class ImmutableEvent implements AEvent {

    private int index;
    private int immutableParentActivityIndex;
    private ATrace parentTrace;
    private String lifecycle;
    private long timestamp;
    private XEvent xEvent;

    public ImmutableEvent(int index, ATrace parentTrace, XEvent xEvent) {
        this.parentTrace = parentTrace;
        this.index = index;
        this.xEvent = xEvent;

        this.lifecycle = xEvent.getAttributes().containsKey("lifecycle:transition") ?
                xEvent.getAttributes().get("lifecycle:transition").toString().toLowerCase().intern() :
                "complete";
        try {
            ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);
            this.timestamp = Util.epochMilliOf(zdt);
        } catch (Exception e) {
            this.timestamp = 0;
        }
    }

    public ImmutableEvent(int index, String lifecycle, long timestamp, ATrace parentTrace,
                          int immutableParentActivityIndex) {
        this.index = index;
        this.lifecycle = lifecycle;
        this.timestamp = timestamp;
        this.parentTrace = parentTrace;
        this.immutableParentActivityIndex = immutableParentActivityIndex;
    }

    @Override
    public void setParentActivityIndex(int immutableParentActivityIndex) {
        this.immutableParentActivityIndex = immutableParentActivityIndex;
    }

    private String getXAttrVal(String key) {
        return xEvent.getAttributes().get(key).toString();
    }

    @Override
    public UnifiedMap<String, String> getAllAttributes() {
        return parentTrace.getActivityAttributesList().get(immutableParentActivityIndex);
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setResource(String resource) {

    }

    @Override
    public void setLifecycle(String lifecycle) {

    }

    @Override
    public void setTimestampMilli(long timestampMilli) {

    }

    @Override
    public String getName() {
        String conceptName = getAllAttributes().get("concept:name");
        return conceptName != null ? conceptName : "";
    }

    @Override
    public String getResource() {
        String orgResource = getAllAttributes().get("org:resource");
        return orgResource != null ? orgResource : "";
    }

    @Override
    public String getLifecycle() {
        return lifecycle;
    }

    @Override
    public long getTimestampMilli() {
        return timestamp;
    }

    @Override
    public String getAttributeValue(String attributeKey) {
        return getAllAttributes().get(attributeKey);
    }

    @Override
    public UnifiedMap<String, String> getAttributeMap() {
        return getAllAttributes();
    }

    @Override
    public Set<String> getAttributeNameSet() {
        return getAllAttributes().keySet();
    }

    @Override
    public String getTimeZone() {
        return null;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getParentActivityIndex() {
        return immutableParentActivityIndex;
    }

    @Override
    public AEvent clone(ATrace parentTrace, AActivity parentActivity) {
        return new ImmutableEvent(index, lifecycle, timestamp, parentTrace, immutableParentActivityIndex);
    }

    @Override
    public AEvent clone() {
        return null;
    }
}
