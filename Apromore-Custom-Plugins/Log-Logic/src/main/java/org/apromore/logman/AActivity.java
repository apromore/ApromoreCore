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

package org.apromore.logman;

import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.impl.XEventImpl;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represent a process activity. An activity can contain a list of events.
 * @todo modify to allow an activity to be a list of events, e.g. assign, start, execute, complete events
 * This is to support more sophisticated activity analysis.
 * 
 * An activity can be instant activity, i.e. start and complete events are the same. It can change
 * to an instant activity if it has only one active event left
 * 
 * An activity can be inactive when all of its events are inactive (e.g. filtered out)
 * If an Activity is used, isActive() should be checked if it is unsure of its status
 * 
 * @author Bruce Nguyen
 *
 */
public class AActivity extends XEventImpl {// implements Map.Entry<XEvent,XEvent> {// Pair<XEvent, XEvent> {
    private int originalStartIndex; //index of the start event in the trace
    private int originalCompleteIndex; //index of the complete event in the trace
    private ATrace trace;
    
    public AActivity(ATrace trace, int start, int complete) {
    	this(trace, start,complete,true);
    }
    
    public AActivity(ATrace trace, int originalStartIndex, int originalCompleteIndex, boolean useComplete) {
    	super();
    	this.trace = trace;
    	this.originalStartIndex = originalStartIndex;
    	this.originalCompleteIndex = originalCompleteIndex;
    }
    
    ////////////////////////// ORIGINAL METHODS //////////////////////////////////////
    
    private XAttribute getOriginalStartAttribute(String attributeKey) {
        return trace.getOriginalEventFromIndex(originalStartIndex).getAttributes().get(attributeKey);
    }
    
    private XAttribute getOriginalCompleteAttribute(String attributeKey) {
        return trace.getOriginalEventFromIndex(originalCompleteIndex).getAttributes().get(attributeKey);
    }
    
    public long getOriginalStartTimestamp() {
        return LogUtils.getTimestamp(trace.getOriginalEventFromIndex(originalStartIndex));
    }
    
    public long getOriginalEndTimestamp() {
        return LogUtils.getTimestamp(trace.getOriginalEventFromIndex(originalCompleteIndex));
    }
    
    public long getOriginalDuration() {
        return (getOriginalEndTimestamp() - getOriginalStartTimestamp());
    }

    public double getOriginalCost(Map<String, Double> costTable, CalendarModel calendarModel) {
        if (costTable == null || costTable.isEmpty()) return 0;
        String role = getAttributeMap().get(Constants.ATT_KEY_ROLE);
        if (role == null) return 0;
        double multiplier = costTable.getOrDefault(role, 0.0);
        double cost = (double)calendarModel.getDurationMillis(
            getOriginalStartTimestamp(), getOriginalEndTimestamp()
        );
        cost = (cost / 3600000) * multiplier; // convert ms to hour
        return cost;
    }

    public long getOriginalDurationForAttribute(String attributeKey) {
        XAttribute startAtt = this.getOriginalStartAttribute(attributeKey);
        XAttribute endAtt = this.getOriginalCompleteAttribute(attributeKey);
        if (startAtt == null || endAtt == null) {
            return 0;
        }
        else if (!LogUtils.getValueString(startAtt).equalsIgnoreCase(LogUtils.getValueString(endAtt))) {
            return 0;
        }
        else {
            return this.getOriginalDuration();
        }
    }

    public double getOriginalCostForAttribute(String attributeKey, Map<String, Double> costTable, CalendarModel calendarModel) {
        XAttribute startAtt = this.getOriginalStartAttribute(attributeKey);
        XAttribute endAtt = this.getOriginalCompleteAttribute(attributeKey);
        if (startAtt == null || endAtt == null) {
            return 0;
        }
        else if (!LogUtils.getValueString(startAtt).equalsIgnoreCase(LogUtils.getValueString(endAtt))) {
            return 0;
        }
        else {
            return this.getOriginalCost(costTable, calendarModel);
        }
    }

    ////////////////////////// ACTIVE METHODS //////////////////////////////////////
    
    private XAttribute getStartAttribute(String attributeKey) {
        if (trace.getOriginalEventStatus(originalStartIndex)) {
            return trace.getOriginalEventFromIndex(originalStartIndex).getAttributes().get(attributeKey);
        }
        else if (trace.getOriginalEventStatus(originalCompleteIndex)) {
            return trace.getOriginalEventFromIndex(originalCompleteIndex).getAttributes().get(attributeKey);
        }
        else {
            return null;
        }
    }
    
    private XAttribute getCompleteAttribute(String attributeKey) {
        if (trace.getOriginalEventStatus(originalCompleteIndex)) {
            return trace.getOriginalEventFromIndex(originalCompleteIndex).getAttributes().get(attributeKey);
        }
        else if (trace.getOriginalEventStatus(originalStartIndex)) {
            return trace.getOriginalEventFromIndex(originalStartIndex).getAttributes().get(attributeKey);
        }
        else {
            return null;
        }
    }
    
    public DateTime getStartTime() {
        if (trace.getOriginalEventStatus(originalStartIndex)) {
            return LogUtils.getDateTime(trace.getOriginalEventFromIndex(originalStartIndex));
        }
        else if (trace.getOriginalEventStatus(originalCompleteIndex)) {
            return LogUtils.getDateTime(trace.getOriginalEventFromIndex(originalCompleteIndex));
        }
        else {
            return Constants.MISSING_DATETIME;
        }
    }
    
    public long getStartTimestamp() {
        if (trace.getOriginalEventStatus(originalStartIndex)) {
            return LogUtils.getTimestamp(trace.getOriginalEventFromIndex(originalStartIndex));
        }
        else if (trace.getOriginalEventStatus(originalCompleteIndex)) {
            return LogUtils.getTimestamp(trace.getOriginalEventFromIndex(originalCompleteIndex));
        }
        else {
            return Constants.MISSING_TIMESTAMP;
        }
    }
    
    public DateTime getEndTime() {
        if (trace.getOriginalEventStatus(originalCompleteIndex)) {
            return LogUtils.getDateTime(trace.getOriginalEventFromIndex(originalCompleteIndex));
        }
        else if (trace.getOriginalEventStatus(originalStartIndex)) {
            return LogUtils.getDateTime(trace.getOriginalEventFromIndex(originalStartIndex));
        }
        else {
            return Constants.MISSING_DATETIME;
        }
    }
    
    public long getEndTimestamp() {
        if (trace.getOriginalEventStatus(originalCompleteIndex)) {
            return LogUtils.getTimestamp(trace.getOriginalEventFromIndex(originalCompleteIndex));
        }
        else if (trace.getOriginalEventStatus(originalStartIndex)) {
            return LogUtils.getTimestamp(trace.getOriginalEventFromIndex(originalStartIndex));
        }
        else {
            return Constants.MISSING_TIMESTAMP;
        }
    }
    
    public long getDuration() {
        return (getEndTimestamp() - getStartTimestamp());
    }

    private boolean isStartEventActive() {
        return trace.getOriginalEventStatus(originalStartIndex);
    }

    private boolean isEndEventActive() {
        return trace.getOriginalEventStatus(originalCompleteIndex);
    }
    
    public boolean isInstant() {
        return (originalStartIndex == originalCompleteIndex) ||
                (isStartEventActive() && !isEndEventActive()) ||
                (!isStartEventActive() && isEndEventActive());
    }
    
    public boolean isActive() {
        return isStartEventActive() || isEndEventActive();
    }
    
    @Override
    public String toString() {
        return LogUtils.getConceptName(this);
    }
    
    public Map<String,String> getAttributeMap() {
        Map<String,String> attributes = getAttributes().entrySet().stream()
                .collect(Collectors.toMap(e->e.getKey(), e->e.getValue().toString()));

        //Adjust for timestamp and lifecycle transition
        if (!isInstant()) {
            attributes.remove(Constants.ATT_KEY_LIFECYCLE_TRANSITION);
            attributes.remove(Constants.ATT_KEY_TIMESTAMP);
            XAttributeMap startEventAtts = trace.getOriginalEventFromIndex(originalStartIndex).getAttributes();
            XAttributeMap endEventAtts = trace.getOriginalEventFromIndex(originalCompleteIndex).getAttributes();
            attributes.put(Constants.ATT_KEY_START_TIME, startEventAtts.get(XTimeExtension.KEY_TIMESTAMP).toString());
            attributes.put(Constants.ATT_KEY_END_TIME, endEventAtts.get(XTimeExtension.KEY_TIMESTAMP).toString());
        }
        return attributes;
    }

    @Override
    public XAttributeMap getAttributes() {
        XAttributeMap xMap;
        if (!isInstant()) {
            xMap = trace.getOriginalEventFromIndex(originalStartIndex).getAttributes();
        }
        else if (isStartEventActive()) {
            xMap = trace.getOriginalEventFromIndex(originalStartIndex).getAttributes();
        }
        else if (isEndEventActive()) {
            xMap = trace.getOriginalEventFromIndex(originalCompleteIndex).getAttributes();
        }
        else {
            xMap = trace.getOriginalEventFromIndex(originalStartIndex).getAttributes();
        }
        return xMap;
    }
}
