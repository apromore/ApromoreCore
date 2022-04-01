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
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.apromore.logman.attribute.AttributeStore;
import org.apromore.logman.attribute.exception.InvalidAttributeLogStatusUpdateException;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

/** 
 * ALog is a collection of ATrace which aggregates events into activities
 * It uses a BitSet to keep track of trace status (active/inactive) instead of deleting/adding traces
 * It maintains an original collection as well as an active collection of ATrace 
 * It provides an AttributeStore that collects all attributes
 * It provides an AttributeLog that is a perspective view of the log based on a chosen event attribute.
 * 
 * Basically, ALog has the same structure as XLog (ATrace vs. XTrace) with AttributeStore to store 
 * information of attributes and AttributeLog to store an attribute-based view of the log.
 * These aspects are not readily available for XLog because it stores information by events. One has to
 * retrieve them again and again from XLog. For example, for activity analysis, one has to retrieve values 
 * of activity (concept:name) attribute from events of XLog, and if changing to resource, one has to retrieve again values
 * of resource attribute from events of XLog.
 * 
 * At present, ALog must be an XLog because the filter expects XLog. This can be removed in the future.
 * 
 * @author Bruce Nguyen
 *
 */
public class ALog extends XLogImpl {
    // Original data
	private final XLog rawLog;
	private final MutableList<ATrace> originalTraces = Lists.mutable.empty();
	
	// Metadata of all attributes
	private final AttributeStore originalAttributeStore;
	
	// Filtered data
	private BitSet originalTraceStatus;
	private final MutableList<ATrace> activeTraces = Lists.mutable.empty();
    
	public ALog(XLog log) {
	    super(log.getAttributes());
	    this.rawLog = log;
	    this.originalTraceStatus = new BitSet(rawLog.size());
	    
	    int index = 0;
		for (XTrace trace: rawLog) {
		    this.add(trace);
		    ATrace newTrace = new ATrace(trace, this);
			this.originalTraces.add(newTrace);
			this.activeTraces.add(newTrace);
			this.originalTraceStatus.set(index);
			index++;
		}
		originalAttributeStore = new AttributeStore(this);

		setOriginalTraceStatus(originalTraceStatus);
	}
	
	public XLog getRawLog() {
		return this.rawLog;
	}
	
	public long getOriginalNumberOfEvents() {
	    return originalTraces.sumOfLong(trace -> trace.getOriginalEvents().size());
	}
	
    public ListIterable<ATrace> getOriginalTraces() {
        return this.originalTraces;
    }
    
    public ATrace getOriginalTraceFromIndex(int index) {
        return originalTraces.get(index);
    }
    
    public long getNumberOfEvents() {
        return activeTraces.sumOfLong(trace -> trace.getEvents().size());
    }   
    
	// This list must be the same as the original list excluding inactive traces 
	public ListIterable<ATrace> getTraces() {
	    return activeTraces;
	}
	
	public ATrace getTraceFromIndex(int index) {
	    return activeTraces.get(index);
	}  
    
    public AttributeStore getAttributeStore() {
        return this.originalAttributeStore;
    }
    
    // Private only method
    // To keep the integrity of this log status including the status of each trace 
    // and status of each event in traces, only updateLogStatus(LogBitMap) method is made public
    private void setOriginalTraceStatus(BitSet newTraceStatus) {
        activeTraces.clear();
        for (int i=0;i<this.getOriginalTraces().size();i++) {
            if (newTraceStatus.get(i)) activeTraces.add(getOriginalTraceFromIndex(i));
            if (originalTraceStatus.get(i) != newTraceStatus.get(i)) {
                boolean increase = !originalTraceStatus.get(i) && newTraceStatus.get(i);
                this.getAttributeStore().updateAttributeValueCount(getOriginalTraceFromIndex(i), increase);
            }
        }
        originalTraceStatus = newTraceStatus;
    }
    
    public BitSet getOriginalTraceStatus() {
        return this.originalTraceStatus;
    }
    
    // Return a LogBitMap object from the current status of this log
    // Event status bitset is based on the activities of a trace (not based on raw events) with 
    // added start and end activities
    public LogBitMap getLogBitMapAtActivityLevel() throws InvalidLogBitMapException {
        LogBitMap logBitMap = new LogBitMap(this.getOriginalTraces().size());
        logBitMap.setTraceBitSet(this.originalTraceStatus, getOriginalTraces().size());
        for (ATrace trace: this.getOriginalTraces()) {
            logBitMap.addEventBitSet(trace.getOriginalActivityStatusWithStartEnd(), 
                                    trace.getOriginalActivities().size()+2);
        }
        return logBitMap;
    }
    
    // This batch update with LogBitMap is safe as it can check the validity of all bitsets
    // by comparing their size with the corresponding log/trace size.
    public void updateLogStatus(LogBitMap logBitMap) throws Exception {
        boolean hasBeenUpdated = false;
        if (logBitMap.getTraceBitSetSize() != this.getOriginalTraces().size()) {
            throw new InvalidALogStatusUpdateException("Invalid update of ALog: different size from the log size.");
        }
        else {
            for (int i=0;i<getOriginalTraces().size();i++) {
                ATrace trace = this.getOriginalTraceFromIndex(i);
                if (logBitMap.getEventBitSetSizeAtIndex(i) == trace.getOriginalEvents().size()) {
                    if (!logBitMap.getEventBitSetAtIndex(i).equals(trace.getOriginalEventStatus())) {
                        trace.setOriginalEventStatus(logBitMap.getEventBitSetAtIndex(i));
                        hasBeenUpdated = true;
                    }
                }
                else {
                    throw new InvalidAttributeLogStatusUpdateException("Invalid update of ATrace at index = " + i + ": different bitset size");
                }
            }
            
            if (!logBitMap.getTraceBitSet().equals(this.getOriginalTraceStatus())) {
                hasBeenUpdated = true;              
            }
            
            if (hasBeenUpdated) {
                this.setOriginalTraceStatus(logBitMap.getTraceBitSet());
            }

        }
    }
    
    public XLog getActualXLog() {
        XFactory factory = new XFactoryNaiveImpl();
        XLog newLog = factory.createLog(this.getAttributes());
        for (ATrace trace : this.getTraces()) {
            XTrace newTrace = factory.createTrace(trace.getAttributes());
            newTrace.addAll(trace.getEvents());
            if (!newTrace.isEmpty()) newLog.add(newTrace);
        }
        return newLog;
    }
    

    @Override
    public XTrace remove(int index) {
        return null;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }   
    
    @Override
    public boolean removeIf(Predicate<? super XTrace> filter) {
        return false;
    }
    
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        //
    }
    
    @Override
    public void clear() {
        //
    }
    
    @Override
    public void replaceAll(UnaryOperator<XTrace> operator) {
        // do nothing
    }
    
    @Override
    public XTrace set(int index, XTrace element) {
        //do nothing
        return null;
    }
    
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }
    
    @Override
    public Spliterator<XTrace> spliterator() {
        return null;
    }

}
