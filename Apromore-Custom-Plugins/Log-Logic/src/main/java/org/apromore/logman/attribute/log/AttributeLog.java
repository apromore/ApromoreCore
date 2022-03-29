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

package org.apromore.logman.attribute.log;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.ALog;
import org.apromore.logman.ATrace;
import org.apromore.logman.Constants;
import org.apromore.logman.LogBitMap;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.exception.InvalidAttributeLogStatusUpdateException;
import org.apromore.logman.attribute.graph.AttributeLogGraph;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;

/**
 * An AttributeLog is a view of ALog based on an attribute. This log extracts the traces with the chosen attribute only,
 * each trace is an AttributeTrace. Since it is based on a specific attribute, the trace sequence is specific to the
 * chosen attribute (e.g. some original events may not contain values of the attribute), and thus the timing and duration
 * also vary to the attribute. Note that AttributeTrace is created based on the aggregated activities of ATrace.
 * 
 * An AttributeLog once created from an ALog will store all original traces and can be updated later on independently
 * from the ALog. It keeps track of all original traces of the chosen attribute and their status (active/inactive).
 * 
 * AttributeLog has two main views: Variant View and Graph View.
 * 
 * An application using AttributeLog is responsible for keeping it in sync with ALog if necessary.
 * For example, if ALog has been filtered, the same filtering should be applied to an AttributeLog created from the ALog
 * if it is important to do so.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeLog {
    public static final String START_NAME = Constants.START_NAME;
    public static final String END_NAME = Constants.END_NAME;
    
    // Source data
    private ALog fullLog;
    
    // Main perspective attribute. AttributeLog is able to change the perspective attribute
    private IndexableAttribute attribute;
    
    // Calendar model used for this log
    private CalendarModel calendarModel;
    private Map<String, Double> costTable = new HashMap<>();

    // Original log data
    private MutableList<AttributeTrace> originalTraces = Lists.mutable.empty();
    private MutableMap<String, AttributeTrace> originalTraceIdMap = Maps.mutable.empty();
    
    // Filtered data
    private BitSet originalTraceStatus;
    private MutableList<AttributeTrace> activeTraces = Lists.mutable.empty();
    
    private boolean dataStatusChanged = false; // true if this log has been changed (filtered) but not visualized
    
    // Sequence view of the log
    private AttributeLogVariantView variantView;
    
    // Graph view of the log
    private AttributeLogGraph graphView;

    public AttributeLog(ALog log, IndexableAttribute attribute, CalendarModel calendarModel, Map<String, Double> costTable) {
        this.fullLog = log;
        this.attribute = attribute;
        this.calendarModel = calendarModel;
        this.originalTraceStatus = fullLog.getOriginalTraceStatus();
        if (log.getOriginalTraces().size()==0 || attribute == null) return;

        this.variantView = new AttributeLogVariantView(this);
        this.graphView = new AttributeLogGraph(this);
        this.costTable = costTable;

        for(int i=0; i<fullLog.getOriginalTraces().size(); i++) {
            ATrace trace = fullLog.getOriginalTraces().get(i);
            AttributeTrace attTrace = new AttributeTrace(attribute, trace, costTable, calendarModel);
            originalTraces.add(attTrace);
            originalTraceIdMap.put(trace.getTraceId(), attTrace);
            variantView.addOriginalTrace(attTrace);
            if (originalTraceStatus.get(i) && !attTrace.isEmpty()) {
                activeTraces.add(attTrace);
                variantView.addActiveTrace(attTrace);
                graphView.addTraceGraph(attTrace.getActiveGraph());
            }
        }

        variantView.finalUpdate();
        graphView.finalUpdate();
    }

    public AttributeLog(ALog log, IndexableAttribute attribute, CalendarModel calendarModel) {
        this(log, attribute, calendarModel, null);
	}
	
    public IndexableAttribute getAttribute() {
        return this.attribute;
    }
    
    public CalendarModel getCalendarModel() {
        return this.calendarModel;
    }
	
    // Change the perspective attribute of this log without having to recreate a new AttributeLog
    // When changing the attribute, each AttributeTrace will switch to the new attribute
	public void setAttribute(IndexableAttribute newAttribute) {
	    if (newAttribute != this.attribute && newAttribute != null) {
	        attribute = newAttribute;
	        variantView.reset();
	        graphView.resetToAttribute(attribute);
	        activeTraces.clear();
    	    for (int i=0; i<originalTraces.size(); i++) {
    	        AttributeTrace attTrace = originalTraces.get(i);
    	        attTrace.setAttribute(newAttribute, costTable, calendarModel);
    	        variantView.addOriginalTrace(attTrace);
    	        if (originalTraceStatus.get(i) && !attTrace.isEmpty()) {
    	            activeTraces.add(attTrace);
    	            variantView.addActiveTrace(attTrace);
    	            graphView.addTraceGraph(attTrace.getActiveGraph());
    	        }
    	    }
    	    
    	    variantView.finalUpdate();
    	    graphView.finalUpdate();
    	    dataStatusChanged = true;
	    }
	}
	
	public boolean isDataStatusChanged() {
	    return dataStatusChanged;
	}
	
	public void resetDataStatus() {
	    dataStatusChanged = false;
	}
	
	public BitSet getOriginalTraceStatus() {
	    return originalTraceStatus;
	}
	
	public AttributeLogVariantView getVariantView() {
	    return variantView;
	}
	
	public AttributeLogGraph getGraphView() {
	    return graphView;
	}
	
	
	// Filter this log
	// This batch update is safe as it can check the validity of the bitset
	// Note that this method if calling alone will make this object out of sync with its original ALog
	public void updateLogStatus(LogBitMap logBitMap) throws Exception {
        if (logBitMap.size() != this.getOriginalTraces().size()) {
            throw new InvalidAttributeLogStatusUpdateException("Invalid update of AttributeLog: different bitmap size from the log size.");
        }
        else {
            activeTraces.clear();
            variantView.resetActiveData();
            graphView.clear();
            
            if (!logBitMap.getTraceBitSet().equals(this.getOriginalTraceStatus())) {
                originalTraceStatus = logBitMap.getTraceBitSet();
                dataStatusChanged = true;
            }
            
            for (int i=0;i<getOriginalTraces().size();i++) {
                AttributeTrace trace = getOriginalTraceFromIndex(i);
                if (logBitMap.getEventBitSetSizeAtIndex(i) == trace.getOriginalValueTrace().size()) {
                    if (!logBitMap.getEventBitSetAtIndex(i).equals(trace.getOriginalEventStatus())) {
                        trace.updateOriginalEventStatus(logBitMap.getEventBitSetAtIndex(i));
                        dataStatusChanged = true;
                    }
                    if (originalTraceStatus.get(i) && !trace.isEmpty()) {
                        activeTraces.add(trace);
                        graphView.addTraceGraph(trace.getActiveGraph());
                        variantView.addActiveTrace(trace);
                    }
                }
                else {
                    throw new InvalidAttributeLogStatusUpdateException("Invalid update of AttributeTrace at traceIndex=" + i +
                                                                    ", traceId=" + trace.getTraceId() +
                                                                    ": different bitset size");
                }
            }
            
            variantView.finalUpdate();
            graphView.finalUpdate();
        }
    }
	
	// Reload data from the current ALog and Attribute
	// This method can be applied after the ALog has been changed, e.g. after filtering.
	// This method makes this object in sync again with its ALog status
	public void refresh() throws Exception {
	    this.updateLogStatus(fullLog.getLogBitMapAtActivityLevel());
	}
	
    public ALog getFullLog() {
        return fullLog;
    }
    
    public long getOriginalNumberOfEvents() {
        return originalTraces.sumOfLong(trace -> trace.getOriginalValueTrace().size()-2);
    }
    
    public long getNumberOfEvents() {
        return activeTraces.sumOfLong(trace -> trace.getValueTrace().size()-2);
    }
    
    // Do not get from the original number of values because of activity merging effect
    // Need to get from the value trace after merging activities
    public IntSet getOriginalAttributeValues() {
        MutableIntSet values = IntSets.mutable.empty();
        getOriginalTraces().forEach(trace -> values.addAll(trace.getOriginalValueTrace()));
        return values.toImmutable();
    }
	
	public IntSet getAttributeValues() {
	    MutableIntSet values = IntSets.mutable.empty();
	    getTraces().forEach(trace -> values.addAll(trace.getValueTrace()));
	    return values.toImmutable();
	}
	
    public String getStringFromValue(int value) {
        if (value == attribute.getArtificialStartIndex()) {
            return AttributeLog.START_NAME;
        }
        else if (value == attribute.getArtificialEndIndex()) {
            return AttributeLog.END_NAME;
        }
        else {
            return attribute.getValue(value).toString();
        }
    }
    
    public int getValueFromString(String valueString) {
        if (valueString.equals(AttributeLog.START_NAME)) {
            return attribute.getMatrixGraph().getSource();
        }
        else if (valueString.equals(AttributeLog.END_NAME)) {
            return attribute.getMatrixGraph().getSink();
        }
        else {
            return attribute.getValueIndex(valueString);
        }
    }
    
    public int getStartEvent() {
        return attribute.getArtificialStartIndex();
    }
    
    public int getEndEvent() {
        return attribute.getArtificialEndIndex();
    }
	
	public boolean isArtificialStart(int value) {
	    return (value == attribute.getMatrixGraph().getSource());
	}
	
    public boolean isArtificialEnd(int value) {
        return (value == attribute.getMatrixGraph().getSink());
    }
    
    
    
    /////////////////////////////// TRACE METHODS ///////////////////////////////////
    
    public ListIterable<AttributeTrace> getOriginalTraces() {
        return originalTraces;
    }
    
    public AttributeTrace getOriginalTraceFromIndex(int index) {
        return originalTraces.get(index);
    }
    
    public ListIterable<AttributeTrace> getTraces() {
        return activeTraces;
    }
    
    public AttributeTrace getTraceFromIndex(int index) {
        return activeTraces.get(index);
    }
    
    public AttributeTrace getTraceFromTraceId(String traceId) {
        return originalTraceIdMap.get(traceId);
    }
    
    
    private IntList getVariantFromTraceIndex(int traceIndex) {
        return activeTraces.get(traceIndex).getValueTrace();
    }
    
     
    ////////////////////////// GENERAL DATA //////////////////////////////////////
    
    public AttributeLogSummary getOriginalLogSummary() {
        if (getOriginalTraces().isEmpty()) {
            return new AttributeLogSummary(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        
        long startTime = getOriginalTraces().collect(trace -> trace.getOriginalStartTime()).min();
        long endTime = getOriginalTraces().collect(trace -> trace.getOriginalEndTime()).max();
        MutableDoubleList caseDurations = DoubleLists.mutable.empty();
        for (int i=0;i<this.getOriginalTraces().size();i++) {
            caseDurations.add(this.getOriginalTraceFromIndex(i).getOriginalDuration());
        }
        DescriptiveStatistics traceDurationStats = new DescriptiveStatistics(caseDurations.toArray());
        return new AttributeLogSummary(
                            fullLog.getOriginalNumberOfEvents(),
                            this.getOriginalAttributeValues().size()-2,
                            this.getOriginalTraces().size(),
                            variantView.getOriginalVariants().size(),
                            startTime, endTime,
                            traceDurationStats.getMin(),
                            traceDurationStats.getMax(),
                            traceDurationStats.getMean(),
                            traceDurationStats.getPercentile(50));
    }
    
    public AttributeLogSummary getLogSummary() {
        if (getTraces().isEmpty()) {
            return new AttributeLogSummary(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        
        long startTime = getTraces().collect(trace -> trace.getStartTime()).min();
        long endTime = getTraces().collect(trace -> trace.getEndTime()).max();
        MutableDoubleList caseDurations = DoubleLists.mutable.empty();
        for (int i=0;i<this.getTraces().size();i++) {
            caseDurations.add(this.getTraceFromIndex(i).getDuration());
        }
        DescriptiveStatistics traceDurationStats = new DescriptiveStatistics(caseDurations.toArray());
        return new AttributeLogSummary(
                            fullLog.getNumberOfEvents(),
                            this.getAttributeValues().size()-2,
                            this.getTraces().size(),
                            variantView.getActiveVariants().size(),
                            startTime, endTime,
                            traceDurationStats.getMin(),
                            traceDurationStats.getMax(),
                            traceDurationStats.getMean(),
                            traceDurationStats.getPercentile(50));
    }
    
    // The variant index here is the ordered variant index by variant frequency
    public ListIterable<CaseInfo> getCaseInfoList() {
        MutableList<CaseInfo> cases = Lists.mutable.empty();
        for (int i=0; i<this.getTraces().size(); i++) {
            IntList variant = getVariantFromTraceIndex(i);
            cases.add(new CaseInfo(
                    this.getTraceFromIndex(i).getTraceId(),
                    variant.size()-2,
                    variantView.getActiveVariants().getRankOf(variant),
                    variantView.getActiveVariants().getVariantRelativeFrequency(variant)));
        }
        return cases;
    }
    
    public ListIterable<AttributeInfo> getAttributeInfoList() {
        MutableList<AttributeInfo> atts = Lists.mutable.empty();
        long total = this.getNumberOfEvents();
        for (int index: this.getAttributeValues().toArray()) {
            if (index == this.getStartEvent() || index == this.getEndEvent()) continue;
            long actCount = variantView.getActiveVariants().getVariants().collectLong(variant -> {
                return variant.count(item -> item == index)*variantView.getActiveVariants().getFrequency(variant);
                }).sum();
            double actFreq = 1.0*actCount/total;
            atts.add(new AttributeInfo(attribute.getValue(index).toString(), actCount, actFreq));
        }
        return atts;
    }
    
    @Override
	public String toString() {
    	return this.attribute.toString();
    }
}
