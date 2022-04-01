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

import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.AActivity;
import org.apromore.logman.ATrace;
import org.apromore.logman.Constants;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.AttributeTraceGraph;
import org.apromore.logman.attribute.log.variants.AttributeTraceVariants;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * AttributeTrac represents a sequence of activities created from an ATrace based on a chosen attribute.
 * The values of the attribute form a value trace (they are the value indexes of the attribute).
 *
 * AttributeTrace stores the start time and end time traces(copied from ATrace), and a duration trace.
 * The start and end time traces are used to compute the duration of transitions, and the duration trace which
 * is used to compute the duration of each value (a value is a node) based on the start-complete of the
 * corresponding activity. Different from ATrace, each AttributeTrace has an artificial start and end event.
 * 
 * Note that events in an AttributeTrace is not one-to-one mapping with activities in the ATrace because
 * some activities may have missing values for the chosen attribute. Thus, the value trace, start time,
 * end time and duration traces in the AttributeTrace could be different from the activity sequence of ATrace.
 * Consequently, AttributeTrace also keeps track of what activities in ATrace are mapped to its value trace.
 * 
 * In addition to the sequence view above, an AttributeTrace can be seen as a subgraph of the AttributeMatrixGraph
 * created by the chosen attribute.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeTrace {
    private IndexableAttribute attribute;
    private ATrace originalTrace;

    // Trace original data
    private MutableIntList originalActIndexes = IntLists.mutable.empty(); //applicable activity indexes in the original trace
    private MutableIntList originalValueTrace = IntLists.mutable.empty();
    private MutableLongList originalStartTimeTrace = LongLists.mutable.empty();
    private MutableLongList originalEndTimeTrace = LongLists.mutable.empty();
    private MutableLongList originalDurationTrace = LongLists.mutable.empty();
    private MutableDoubleList originalCostTrace = DoubleLists.mutable.empty();
    
    // Filter bitset
    private BitSet originalEventStatus;
    
    // The current trace data after applying filter bitset
    private MutableIntList activeValueTrace = IntLists.mutable.empty();
    private MutableLongList activeStartTimeTrace = LongLists.mutable.empty();
    private MutableLongList activeEndTimeTrace = LongLists.mutable.empty();
    private MutableLongList activeDurationTrace = LongLists.mutable.empty();
    private MutableDoubleList activeCostTrace = DoubleLists.mutable.empty();
    
    // Different views on traces
    private AttributeTraceGraph activeGraph;
    private AttributeTraceVariants variants;
    
    public AttributeTrace(IndexableAttribute attribute, ATrace originalTrace, Map<String, Double> costTable, CalendarModel calendarModel) {
        this.originalTrace = originalTrace;
        this.attribute = attribute;
        this.activeGraph = new AttributeTraceGraph(this);
        initializeOriginalData(costTable, calendarModel);
        updateOriginalEventStatus(originalTrace.getOriginalActivityStatusWithStartEnd());
    }
    
    public AttributeTraceGraph getActiveGraph() {
        return activeGraph;
    }
    
    public IndexableAttribute getAttribute() {
        return this.attribute;
    }
    
    // Set this trace to the new perspective attribute including the original data
    // If the trace is being filtered, apply the same filter to the new perspective
    public void setAttribute(IndexableAttribute newAttribute, Map<String, Double> costTable, CalendarModel calendarModel) {
        this.attribute = newAttribute;
        this.activeGraph.resetToAttribute(attribute);
        initializeOriginalData(costTable, calendarModel);
        updateOriginalEventStatus(originalTrace.getOriginalActivityStatusWithStartEnd());
    }
    
    private void initializeOriginalData(Map<String, Double> costTable, CalendarModel calendarModel) {
        originalActIndexes.clear();
        originalValueTrace.clear();
        originalStartTimeTrace.clear();
        originalEndTimeTrace.clear();
        originalDurationTrace.clear();
        originalCostTrace.clear();

        ListIterable<AActivity> acts = originalTrace.getOriginalActivities();
        for(int i=0; i<acts.size();i++) {
            AActivity act = acts.get(i);
            int valueIndex = attribute.getValueIndex(act);
            if (valueIndex >= 0) {
                // Value trace
                originalValueTrace.add(valueIndex);
                originalActIndexes.add(i);
                
                // Start/end time trace
                originalStartTimeTrace.add(act.getOriginalStartTimestamp());
                originalEndTimeTrace.add(act.getOriginalEndTimestamp());
                
                // Duration trace
                if (attribute.getKey().equals(Constants.ATT_KEY_CONCEPT_NAME)) {
                    originalDurationTrace.add(act.getOriginalDuration());
                    originalCostTrace.add(act.getOriginalCost(costTable, calendarModel));
                }
                else {
                    originalDurationTrace.add(act.getOriginalDurationForAttribute(attribute.getKey()));
                    originalCostTrace.add(act.getOriginalCostForAttribute(attribute.getKey(), costTable, calendarModel));
                }
            }
        }
        
        // Add values for the artificial start and end
        if (!originalValueTrace.isEmpty()) {
            originalValueTrace.addAtIndex(0, attribute.getArtificialStartIndex());
            originalValueTrace.add(attribute.getArtificialEndIndex());
            
            // The artificial start event has the start time equal to the start time of the first activity
            // And its end time is the same as its start time
            originalStartTimeTrace.addAtIndex(0, originalStartTimeTrace.get(0));
            originalEndTimeTrace.addAtIndex(0, originalStartTimeTrace.get(0));
            
            // The artificial end event has the start time equal to the end time of the last activity
            // And its end time is the same as its start time
            originalStartTimeTrace.add(originalEndTimeTrace.get(originalEndTimeTrace.size()-1));
            originalEndTimeTrace.add(originalStartTimeTrace.get(originalStartTimeTrace.size()-1));
            
            originalDurationTrace.addAtIndex(0, 0);
            originalDurationTrace.add(0);

            originalCostTrace.addAtIndex(0, 0);
            originalCostTrace.add(0);

        }
        
        this.originalEventStatus = originalTrace.getOriginalActivityStatusWithStartEnd();
    }
    
    public BitSet getOriginalEventStatus() {
        return originalEventStatus;
    }
    
    // Update active data
    public void updateOriginalEventStatus(BitSet eventBitSet) {
        originalEventStatus = eventBitSet;
        
        activeValueTrace.clear();
        activeStartTimeTrace.clear();
        activeEndTimeTrace.clear();
        activeDurationTrace.clear();
        activeCostTrace.clear();
        activeGraph.clear();
        
        int preIndex = -1;
        for (int i=1; i<=originalValueTrace.size()-2; i++) { // exclude the two artificial start and end events
            if (originalEventStatus.get(i)) {
                int node = originalValueTrace.get(i);
                
                activeValueTrace.add(node);
                activeStartTimeTrace.add(originalStartTimeTrace.get(i));
                activeEndTimeTrace.add(originalEndTimeTrace.get(i));
                activeDurationTrace.add(originalDurationTrace.get(i));
                activeCostTrace.add(originalCostTrace.get(i));
                
                // Graph data
                activeGraph.addNode(node);
                
                long nodeDur = originalDurationTrace.get(i);
                double nodeCost = originalCostTrace.get(i);
                activeGraph.incrementNodeTotalFrequency(node, 1);
                activeGraph.collectNodeDuration(node, nodeDur);
                activeGraph.collectNodeCost(node, nodeCost);
                activeGraph.collectNodeInterval(node, originalStartTimeTrace.get(i), originalEndTimeTrace.get(i));
                
                if (preIndex >= 0) {
                    int preNode = originalValueTrace.get(preIndex);
                    int arc = attribute.getMatrixGraph().getArc(preNode, node);
                    long arcDur = originalStartTimeTrace.get(i) - originalEndTimeTrace.get(preIndex);
                    if (arcDur < 0) arcDur = 0;
                    activeGraph.addArc(arc);
                    activeGraph.incrementArcTotalFrequency(arc, 1);
                    activeGraph.collectArcDuration(arc, arcDur);
                    // Set arcCost to 0 for now
                    activeGraph.collectArcCost(arc, 0);
                    activeGraph.collectArcInterval(arc, originalEndTimeTrace.get(preIndex), originalStartTimeTrace.get(i));
                }
                preIndex = i;
            }
        }
        
        // Add artificial start and end events
        if (!activeValueTrace.isEmpty()) {
            activeValueTrace.addAtIndex(0, attribute.getArtificialStartIndex());
            activeValueTrace.add(attribute.getArtificialEndIndex());
            
            // The artificial start event has the start time equal to the start time of the first activity
            // And its end time is the same as its start time
            activeStartTimeTrace.addAtIndex(0, activeStartTimeTrace.get(0));
            activeEndTimeTrace.addAtIndex(0, activeStartTimeTrace.get(0));
            
            // The artificial end event has the start time equal to the end time of the last activity
            // And its end time is the same as its start time
            activeStartTimeTrace.add(activeEndTimeTrace.get(activeEndTimeTrace.size()-1));
            activeEndTimeTrace.add(activeStartTimeTrace.get(activeStartTimeTrace.size()-1));
            
            activeDurationTrace.addAtIndex(0, 0);
            activeDurationTrace.add(0);

            activeCostTrace.addAtIndex(0, 0);
            activeCostTrace.add(0);
            
            int sourceNode = attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(0));
            activeGraph.addNode(sourceNode);
            activeGraph.incrementNodeTotalFrequency(sourceNode, 1);
            activeGraph.collectNodeDuration(sourceNode, 0);
            activeGraph.collectNodeCost(sourceNode, 0);
            
            int sinkNode = attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(activeValueTrace.size()-1));
            activeGraph.addNode(sinkNode);
            activeGraph.incrementNodeTotalFrequency(sinkNode, 1);
            activeGraph.collectNodeDuration(sinkNode, 0);
            activeGraph.collectNodeCost(sinkNode, 0);
            
            int sourceArc = attribute.getMatrixGraph().getArc(sourceNode, attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(1)));
            activeGraph.addArc(sourceArc);
            activeGraph.incrementArcTotalFrequency(sourceArc, 1);
            activeGraph.collectArcDuration(sourceArc, 0);
            activeGraph.collectArcCost(sourceArc, 0);
            
            int sinkArc = attribute.getMatrixGraph().getArc(attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(activeValueTrace.size()-2)), sinkNode);
            activeGraph.addArc(sinkArc);
            activeGraph.incrementArcTotalFrequency(sinkArc, 1);
            activeGraph.collectArcDuration(sinkArc, 0);
            activeGraph.collectArcCost(sinkArc, 0);
        }
        
    }
    
    public String getTraceId() {
        return originalTrace.getTraceId();
    }
    
    public void setVariants(AttributeTraceVariants variants) {
        this.variants = variants;
    }
    
    public int getVariantIndex() {
        return this.variants.getIndexOf(activeValueTrace);
    }
    
    public int getVariantRank() {
        return this.variants.getRankOf(activeValueTrace);
    }
    
    public IntList getOriginalValueTrace() {
        return originalValueTrace.toImmutable();
    }
    
    public LongList getOriginalStartTimeTrace() {
        return originalStartTimeTrace;
    }
    
    public LongList getOriginalEndTimeTrace() {
        return originalEndTimeTrace;
    }
    
    public LongList getOriginalDurationTrace() {
        return originalDurationTrace.toImmutable();
    }
    
    public long getOriginalStartTime() {
        if (!this.getOriginalStartTimeTrace().isEmpty()) {
            return this.getOriginalStartTimeTrace().get(0);
        }
        else {
            return 0;
        }
    }
    
    public long getOriginalEndTime() {
        if (!this.getOriginalEndTimeTrace().isEmpty()) {
            return this.getOriginalEndTimeTrace().get(getOriginalEndTimeTrace().size()-1);
        }
        else {
            return 0;
        }
    }
    
    public long getOriginalDuration() {
        return this.getOriginalEndTime() - this.getOriginalStartTime();
    }
    
    public IntList getValueTrace() {
        return activeValueTrace.toImmutable();
    }
    
    public boolean isEmpty() {
        return activeValueTrace.isEmpty();
    }
    
    public LongList getStartTimeTrace() {
        return activeStartTimeTrace.toImmutable();
    }
    
    public LongList getEndTimeTrace() {
        return activeEndTimeTrace.toImmutable();
    }
    
    public LongList getDurationTrace() {
        return activeDurationTrace.toImmutable();
    }
    
    public long getStartTime() {
        if (!this.getStartTimeTrace().isEmpty()) {
            return this.getStartTimeTrace().get(0);
        }
        else {
            return 0;
        }
    }
    
    public long getEndTime() {
        if (!this.getEndTimeTrace().isEmpty()) {
            return this.getEndTimeTrace().get(getEndTimeTrace().size()-1);
        }
        else {
            return 0;
        }
    }
    
    public long getDuration() {
        return this.getEndTime() - this.getStartTime();
    }
    
    public int getAttributeValueAtIndex(int index) {
        return activeValueTrace.get(index);
    }
    
    public long getDurationAtIndex(int index) {
        return activeDurationTrace.get(index);
    }
    
    public long getStartTimeAtIndex(int index) {
        return activeStartTimeTrace.get(index);
    }
    
    public long getEndTimeAtIndex(int index) {
        return activeEndTimeTrace.get(index);
    }
    
    public long getDurationAtPairIndexes(int sourceIndex, int targetIndex) {
        long targetStart = getStartTimeAtIndex(targetIndex);
        long sourceEnd = getEndTimeAtIndex(sourceIndex);
        return (targetStart > sourceEnd) ? targetStart - sourceEnd : 0;
    }
    
    public  Map<String,String> getAttributeMapAtIndex(int index) {
        if (index == 0 || index == this.getValueTrace().size()-1) {
            return new HashMap<>();
        }
        else { // ATrace doesn't contain the artificial start/end events
            return this.originalTrace.getActivityFromIndex(index-1).getAttributeMap();
        }
    }
    
    @Override
    public String toString() {
        return this.getValueTrace().toString();
    }
}
