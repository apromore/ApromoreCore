/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.apmlog;

import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeIDImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 */
public class AEvent implements Serializable {
    private String name = "";
    private long timestampMilli = 0;
    private String lifecycle = "complete";
    private String resource = "";
    private UnifiedMap<String, String> attributeMap;
    private UnifiedSet<String> attributeNameSet;
    private String timeZone = "";

    public AEvent(String name, long timestampMilli, String lifecycle, String resource,
                  UnifiedMap<String, String> attributeMap,
                  UnifiedSet<String> attributeNameSet,
                  String timeZone) {
        this.name = name;
        this.timestampMilli = timestampMilli;
        this.lifecycle = lifecycle;
        this.resource = resource;
        this.attributeMap = attributeMap;
        this.attributeNameSet = attributeNameSet;
        this.timeZone = timeZone;
    }

    public AEvent(XEvent xEvent) {
        XAttributeMap xAttributeMap = xEvent.getAttributes();


        attributeMap = new UnifiedMap<>();

        if (xAttributeMap.keySet().contains("concept:name")) {
            this.name = xAttributeMap.get("concept:name").toString();
        }

        if (xAttributeMap.keySet().contains("lifecycle:transition")) {
            this.lifecycle = xAttributeMap.get("lifecycle:transition").toString();
        }

        if (xAttributeMap.keySet().contains("org:resource")) {
            this.resource = xAttributeMap.get("org:resource").toString();
        }

        for(String key : xAttributeMap.keySet()) {
            if (!key.equals("concept:name") &&
                    !key.equals("lifecycle:transition") &&
                    !key.equals("org:resource") &&
                    !key.equals("time:timestamp")) {
                if (xAttributeMap.get(key) instanceof XAttributeLiteralImpl) {
                    this.attributeMap.put(key, String.valueOf(((XAttributeLiteralImpl) xAttributeMap.get(key)).getValue()));
                } else if (xAttributeMap.get(key) instanceof XAttributeDiscreteImpl) {
                    this.attributeMap.put(key, String.valueOf(((XAttributeDiscreteImpl) xAttributeMap.get(key)).getValue()));
                } else if (xAttributeMap.get(key) instanceof XAttributeIDImpl) {
                    this.attributeMap.put(key, String.valueOf(((XAttributeIDImpl) xAttributeMap.get(key)).getValue()));
                }
            }

        }
        if(xEvent.getAttributes().containsKey("time:timestamp")) {
            ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);
            this.timestampMilli = Util.epochMilliOf(zdt);
            this.timeZone = zdt.getZone().getId();
        }else{
            Date d = new Date(0);
            ZonedDateTime zdt = ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
            this.timestampMilli = Util.epochMilliOf(zdt);
            this.timeZone = zdt.getZone().getId();
        }

        attributeNameSet = new UnifiedSet<>(attributeMap.keySet());
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }

    public String getLifecycle() {
        return lifecycle;
    }

    public long getTimestampMilli() {
        return timestampMilli;
    }

    public String getAttributeValue(String attributeKey) {
        return this.attributeMap.get(attributeKey);
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public UnifiedSet<String> getAttributeNameSet() {
        return attributeNameSet;
    }

    public String getTimeZone() { //2019-10-20
        return timeZone;
    }

    public AEvent clone()  {
        String clnName = this.name;
        long clnTimestampMilli = this.timestampMilli;
        String clnLifecycle = this.lifecycle;
        String clnResource = this.resource;
        UnifiedMap<String, String> clnAttributeMap = new UnifiedMap<>(this.attributeMap);
        UnifiedSet<String> clnAttributeNameSet = new UnifiedSet<>(this.attributeNameSet);
        String clnTimeZone = this.timeZone;

        AEvent clnEvent = new AEvent(clnName, clnTimestampMilli, clnLifecycle, clnResource,
                clnAttributeMap, clnAttributeNameSet, clnTimeZone);
        return clnEvent;
    }
}
