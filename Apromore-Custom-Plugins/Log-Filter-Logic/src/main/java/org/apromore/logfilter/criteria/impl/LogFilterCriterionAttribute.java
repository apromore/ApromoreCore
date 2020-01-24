/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
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

package org.apromore.logfilter.criteria.impl;

import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logfilter.criteria.model.LogFilterTypeSelector;
import org.apromore.logfilter.criteria.model.Type;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Calendar;
import java.util.Set;

public class LogFilterCriterionAttribute extends AbstractLogFilterCriterion {

    public LogFilterCriterionAttribute(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }
    @Override
    public boolean matchesCriterion(XTrace trace) {//2019-10-24

        if(level == Level.TRACE) {

            if (this.label.toLowerCase().equals("case:attribute")) { // case attribute of the case
                String attributeKey = this.attribute;

                XAttributeMap attrMap = trace.getAttributes();
                if(attrMap.containsKey(attributeKey)) {
                    String value = attrMap.get(attributeKey).toString();
                    if(this.value.contains(value)) return true;
                    else return false;
                }
            } else {
                if (LogFilterTypeSelector.getType(attribute) == Type.TIME_TIMESTAMP) {
                    return isMatchingTraceTime(trace);
                }

                UnifiedMap<String, Boolean> matchMap = new UnifiedMap<>(); //2019-10-24

                for (XEvent event : trace) {
                    if (containment == Containment.CONTAIN_ANY) {
                        if (isMatching(event)) return true;
                    } else if (containment == Containment.CONTAIN_ALL) {
                        for (String v : value) {
                            if (isMatchingEventAttribute(event, attribute, v)) {
                                matchMap.put(event.getAttributes().get("concept:name").toString(), true);
                            }
                        }
                    }
                }
                if (matchMap.size() >= value.size()) return true;
                else return false;
            }
        }

        return false;
    }


    @Override
    public boolean matchesCriterion(XEvent event) {
        if(level == Level.EVENT) {
            return isMatching(event);
        }else return false;
    }

    private boolean isMatching(XEvent event) {
        XAttribute xAttribute = event.getAttributes().get(attribute);
        //if(attribute.equals(timestamp_code)) {
        if (LogFilterTypeSelector.getType(attribute) == Type.TIME_TIMESTAMP) {
            long start = 0;
            long end = Long.MAX_VALUE;
            for(String v : value) {
                if(v.startsWith(">")) start = Long.parseLong(v.substring(1));
                if(v.startsWith("<")) end = Long.parseLong(v.substring(1));
            }
            long t = ((XAttributeTimestamp) xAttribute).getValueMillis();
            return start <= t && t <= end;
        }else {
            if (xAttribute != null) {
                String xAttributeValue = xAttribute.toString();
                return value.contains(xAttributeValue);
            }
        }
        return false;
    }

    private boolean isMatchingEventAttribute(XEvent xEvent, String attributeKey, String attributeValue) {//2019-10-24
        if(xEvent.getAttributes().containsKey(attributeKey)) {
            if(xEvent.getAttributes().get(attributeKey).toString().equals(attributeValue)){
                return true;
            }else{
                return false;
            }
        }else return false;
    }

    private boolean isMatchingTraceTime(XTrace xTrace) { //2019-10-24
        long traceST = getTraceStartTime(xTrace);
        long traceET = getTraceEndTime(xTrace);

        long start = 0;
        long end = Long.MAX_VALUE;
        for(String v : value) {
            if(v.startsWith(">")) start = Long.parseLong(v.substring(1));
            if(v.startsWith("<")) end = Long.parseLong(v.substring(1));
        }
        if(containment == Containment.CONTAIN_ALL) {
            return (getTraceStartTime(xTrace) >= start && getTraceEndTime(xTrace) <= end);
        }else{ //intersecting
            if(traceST >= start && traceET <= end) return true;
            if(traceST <= start && traceET >= end) return true;
            if(traceST <= start && traceET >= start) return true;
            if(traceST <= end && traceET >= end) return true;
        }
        return false;
    }

    private long getTraceStartTime(XTrace xTrace) {
        long minTime = 0;
        for(XEvent xEvent : xTrace) {
            String timestampString = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
            Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(timestampString);
            long t = calendar.getTimeInMillis();
            if(minTime == 0 || t < minTime) minTime = t;
        }
        return  minTime;
    }

    private long getTraceEndTime(XTrace xTrace) {
        long maxTime = 0;
        for(XEvent xEvent : xTrace) {
            String timestampString = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
            Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(timestampString);
            long t = calendar.getTimeInMillis();
            if(t > maxTime) maxTime = t;
        }
        return  maxTime;
    }

}
