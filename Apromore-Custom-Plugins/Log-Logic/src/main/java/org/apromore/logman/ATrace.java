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

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;

/**
 * An ATrace is created from a raw trace by aggregating start and complete events
 * It is the same as the original trace, but events are paired to form activities (event mapping)
 * An event mapping is kept to store information of paired events.
 * From the event mapping, each pair of events forms an Activity.
 * An ATrace can also be seen as a sequence of Activity.
 * 
 * When events are filtered, the corresponding Activity objects are updated instead of creating 
 * new Activity objects. The order of Activity objects in the trace remains. For example,
 * when a start event is filtered out but its complete event remains, the corresponding Activity is
 * updated with zero duration (considered as a single-event activity). For another filtering, if the 
 * start event is re-added, the same Activity is updated with the same event pair as before.
 * 
 * Another noticeable feature of ATrace is that the start time and end time of each activity are always
 * the same regardless of any chosen attribute to look at the trace. Therefore, the transition time between
 * Activity within a trace is always the same when switching from one attribute to another attribute, e.g. 
 * when a trace is seen as a sequence of activities or a sequence of human resource. This is because the start
 * and complete timestamps of events are only associated with the concept:name attribute (activity). That's why
 * ATrace keeps a start trace and end time trace.
 * 
 * However, for value trace, i.e. the sequence of values of a chosen even attribute, it varies depending on 
 * the attribute. Therefore, based on an attribute, ATrace will become AttributeTrace, with the start and end
 * time trace copied to AttributeTrace.
 * 
 * At present, ATrace must be an XTrace because the filter expects XLog. This can be removed in the future.
 * 
 * @author Bruce Nguyen
 *
 */
public class ATrace extends XTraceImpl {
    private ALog log;
    private List<XEvent> originalEvents = Lists.mutable.empty(); 
	private BitSet originalEventStatus;
	private MutableMap<XEvent, AActivity> originalEventActMap = Maps.mutable.empty();
	
	private BitSet originalActivityStatus;
	private MutableList<AActivity> originalActivities = Lists.mutable.empty(); //Directly-follow start-to-start (DFSS) order  
	private MutableLongList originalStartTimeTrace = LongLists.mutable.empty(); // same indexes as the list of activites
	private MutableLongList originalEndTimeTrace = LongLists.mutable.empty(); // same indexes as the list of activites
	
    // These data are only to boost the retrieval of active elements, they are actually
    // can be known from the original data and their status.
	private MutableList<XEvent> activeEvents = Lists.mutable.empty();
	private MutableList<AActivity> activeActivities = Lists.mutable.empty();
	private MutableLongList activeStartTimeTrace = LongLists.mutable.empty(); // same indexes as the list of activites
    private MutableLongList activeEndTimeTrace = LongLists.mutable.empty(); // same indexes as the list of activites
	
	public ATrace(XTrace trace, ALog log) {
	    super(trace.getAttributes());
	    this.log = log;
		this.originalEventStatus = new BitSet(trace.size());
		for (int i=0; i<trace.size(); i++) {
		    this.add(trace.get(i));
		    originalEventStatus.set(i);
		}
		originalEvents = this; 
		
		this.createActivities();
		this.setOriginalEventStatus(originalEventStatus);
	}
	
	public String getTraceId() {
	    return LogUtils.getConceptName(this);
	}
	
    public void setOriginalEventStatus(BitSet newEventStatus) {
        activeEvents.clear();
        for (int i=0;i<this.getOriginalEvents().size();i++) {
            if (newEventStatus.get(i)) activeEvents.add(this.getOriginalEventFromIndex(i));
            if (originalEventStatus.get(i) != newEventStatus.get(i)) {
                boolean increase = !originalEventStatus.get(i) && newEventStatus.get(i);
                log.getAttributeStore().updateAttributeValueCount(getOriginalEventFromIndex(i), increase);
            }
        }
        
        this.originalEventStatus = newEventStatus;
        
        activeActivities.clear();
        activeStartTimeTrace.clear();
        activeEndTimeTrace.clear();
        for (int i=0;i<originalActivities.size();i++) {
            AActivity act = originalActivities.get(i);
            originalActivityStatus.set(i, act.isActive());
            if (act.isActive()) {
                activeActivities.add(act);
                activeStartTimeTrace.add(act.getStartTimestamp());
                activeEndTimeTrace.add(act.getEndTimestamp());
            }
        }
    }	
    
    public List<XEvent> getOriginalEvents() {
        return originalEvents;
    }
    
    public XEvent getOriginalEventFromIndex(int index) {
        return originalEvents.get(index);
    }
    
    public BitSet getOriginalEventStatus() {
        return originalEventStatus;
    }
    
    public boolean isSameEventStatus(BitSet newStatus) {
        for (int i=0;i<originalEvents.size();i++) {
            if (newStatus.get(i) != originalEventStatus.get(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean getOriginalEventStatus(int eventIndex) {
        return originalEventStatus.get(eventIndex);
    }
    
    public ListIterable<AActivity> getOriginalActivities() {
        return originalActivities;
    }
    
    public AActivity getOriginalActivityFromIndex(int index) {
        return originalActivities.get(index);
    }
    
    public BitSet getOriginalActivityStatus() {
        return this.originalActivityStatus;
    }
    
    public BitSet getOriginalActivityStatusWithStartEnd() {
        BitSet newBitSet = new BitSet(originalActivities.size()+2); // include the bits for the artificial start and end events
        newBitSet.set(0);
        newBitSet.set(originalActivities.size()+1);
        for (int i=1;i<=originalActivities.size();i++) {
            if (originalActivityStatus.get(i-1)) {
                newBitSet.set(i);
            }
        }
        return newBitSet;
    }
    
    public LongList getOriginalStartTimeTrace() {
        return originalStartTimeTrace.toImmutable();
    }  
    
    public long getOriginalStartTime() {
        return !originalStartTimeTrace.isEmpty() ? originalStartTimeTrace.get(0) : 0;
    }
    
    public LongList getOriginalEndTimeTrace() {
        return originalEndTimeTrace.toImmutable();
    } 
    
    public long getOriginalEndTime() {
        return !originalEndTimeTrace.isEmpty() ? originalEndTimeTrace.get(0) : 0;
    }
	
	public List<XEvent> getEvents() {
	    return activeEvents;
	}
	
    public XEvent getEventFromIndex(int index) {
        return activeEvents.get(index);
    }
	
	public ListIterable<AActivity> getActivities() {
	    return activeActivities;
	}
	
    public AActivity getActivityFromIndex(int index) {
        return activeActivities.get(index);
    }	
	
    public long getStartTime() {
        return !activeStartTimeTrace.isEmpty() ? activeStartTimeTrace.get(0) : 0;
    }
    
    public long getEndTime() {
        return !activeEndTimeTrace.isEmpty() ? activeEndTimeTrace.get(activeEndTimeTrace.size()-1) : 0;
    }
	
    public LongList getStartTimeTrace() {
        return activeStartTimeTrace.toImmutable();
    }    
    
    public LongList getEndTimeTrace() {
        return activeEndTimeTrace.toImmutable();
    } 	
	// Pair start and complete events in the trace
	private void createActivities() {

	    // Build mapping of start-end events from the trace
	    // Traverse the trace maximum two times.
	    MutableIntIntMap foundMapping = IntIntMaps.mutable.empty();
	    MutableIntList orderedStartEvents = IntLists.mutable.empty(); // store start events in order from the head of the trace
	    MutableIntList matchingStartEvents = IntLists.mutable.empty(); // store remaining start events for matching
		for (int i=0;i<this.getOriginalEvents().size();i++) {
		    String eventName = LogUtils.getConceptName(this.getOriginalEventFromIndex(i));
		    
		    if (eventName.equalsIgnoreCase(Constants.MISSING_STRING_VALUE)) continue; // cannot use empty or missing values
		    
		    if (LogUtils.getLifecycleTransition(this.getOriginalEventFromIndex(i)).equalsIgnoreCase(Constants.LIFECYCLE_START)) {
		        orderedStartEvents.add(i);
		        matchingStartEvents.add(i);
		    }
		    else if (LogUtils.getLifecycleTransition(this.getOriginalEventFromIndex(i)).equalsIgnoreCase(Constants.LIFECYCLE_COMPLETE)) {
		        int matchIndex = -1;
		        for (int j=0;j<matchingStartEvents.size();j++) {
		            int startEventIndex = matchingStartEvents.get(j);
		            if (LogUtils.getConceptName(this.getOriginalEventFromIndex(startEventIndex)).equalsIgnoreCase(eventName)) {
		                matchIndex = startEventIndex;
		                break;
		            }
		        }
		        if (matchIndex >= 0) {
		            foundMapping.put(matchIndex, i);
		            matchingStartEvents.remove(matchIndex);
		        }
		        else { // an orphaned complete event is considered as an instant event (start=complete)
		            foundMapping.put(i, i);
		            orderedStartEvents.add(i);
		        }
		    }
		    else {
		        // ignore all events with other values of lifecycle:transition 
		    }
		}
		
		
		MutableIntIntMap eventMapping = IntIntMaps.mutable.empty(); //startEventIndex => completeEventIndex
		for (int event: orderedStartEvents.toArray()) {
            if (foundMapping.containsKey(event)) {
                eventMapping.put(event, foundMapping.get(event));
            }
            else { // an orphaned start event is considered as an instant event (start=complete)
                eventMapping.put(event, event);
            }
		}
		
		// This trace has no events with either start or complete lifecycle:transition
		// In this case, treat all events as separate and non-related events
		if (eventMapping.isEmpty()) {
		    for (int i=0;i<this.getOriginalEvents().size();i++) {
                eventMapping.put(i, i);
		    }
		}
		
		// Create activities based on the order of events in the trace and event mapping
		for (int i=0; i<this.getOriginalEvents().size(); i++) {
            if (eventMapping.containsKey(i)) {
                AActivity act = new AActivity(this, i, eventMapping.get(i));
                originalEventActMap.put(this.getOriginalEventFromIndex(i), act);
                originalEventActMap.put(this.getOriginalEventFromIndex(eventMapping.get(i)), act);
                
                originalActivities.add(act);
                activeActivities.add(act);
                
                originalStartTimeTrace.add(LogUtils.getTimestamp(this.getOriginalEventFromIndex(i)));
                activeStartTimeTrace.add(LogUtils.getTimestamp(this.getOriginalEventFromIndex(i)));
                
                originalEndTimeTrace.add(LogUtils.getTimestamp(this.getOriginalEventFromIndex(eventMapping.get(i))));
                activeEndTimeTrace.add(LogUtils.getTimestamp(this.getOriginalEventFromIndex(eventMapping.get(i))));
            }
        }
		originalActivityStatus = new BitSet(originalActivities.size());
		originalActivityStatus.set(0, originalActivities.size()-1);
	} 
	
    @Override
    public void replaceAll(UnaryOperator<XEvent> operator) {
        // do nothing
    }
    
    @Override
    public XEvent set(int index, XEvent element) {
        //do nothing
        return null;
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }
    
    @Override
    public Spliterator<XEvent> spliterator() {
        return null;
    }
    
    @Override
    public boolean remove(Object o) {
        return false;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }
    
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        //
    }
    
    @Override
    public boolean removeIf(Predicate<? super XEvent> filter) {
        return false;
    }
    
    @Override
    public void clear() {
        //
    }
}
