/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.logman.attribute.log;

import java.util.BitSet;

import org.apromore.logman.AActivity;
import org.apromore.logman.ATrace;
import org.apromore.logman.Constants;
import org.apromore.logman.attribute.IndexableAttribute;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntLongMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongLists;

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
    private ATrace originalTrace;
    private IndexableAttribute attribute;
    private AttributeTraceVariants variants;

    private MutableIntList originalActIndexes = IntLists.mutable.empty(); //applicable activity indexes in the original trace 
    private MutableIntList originalValueTrace = IntLists.mutable.empty();
    private MutableLongList originalStartTimeTrace = LongLists.mutable.empty();
    private MutableLongList originalEndTimeTrace = LongLists.mutable.empty();
    private MutableLongList originalDurationTrace = LongLists.mutable.empty();
    private BitSet originalEventStatus;
    
    // These data are only to boost the retrieval of active elements, they are actually
    // can be known from the original data and their status.
    private MutableIntList activeValueTrace = IntLists.mutable.empty();
    private MutableLongList activeStartTimeTrace = LongLists.mutable.empty();
    private MutableLongList activeEndTimeTrace = LongLists.mutable.empty();
    private MutableLongList activeDurationTrace = LongLists.mutable.empty();
    
    // Graph-based data
    private MutableIntSet activeNodes = IntSets.mutable.empty();
    private MutableIntSet activeArcs = IntSets.mutable.empty();
    
    private MutableIntLongMap activeNodeTotalCounts = IntLongMaps.mutable.empty();
    
    private MutableIntLongMap activeNodeTotalDurs = IntLongMaps.mutable.empty();
    private MutableIntLongMap activeNodeMinDurs = IntLongMaps.mutable.empty();
    private MutableIntLongMap activeNodeMaxDurs = IntLongMaps.mutable.empty();    
    
    private MutableIntLongMap activeArcTotalCounts = IntLongMaps.mutable.empty();
    
    private MutableIntLongMap activeArcTotalDurs = IntLongMaps.mutable.empty();
    private MutableIntLongMap activeArcMinDurs = IntLongMaps.mutable.empty();
    private MutableIntLongMap activeArcMaxDurs = IntLongMaps.mutable.empty(); 
    
    public AttributeTrace(ATrace originalTrace, IndexableAttribute attribute) {
        this.originalTrace = originalTrace;
        setAttribute(attribute);
        //this.attribute = attribute;
        //this.initializeOriginalData();
        //setOriginalEventStatus(originalTrace.getOriginalActivityStatusWithStartEnd());
    }
    
    // Set this trace to the new perspective attribute including the original data
    // If the trace is being filtered, apply the same filter to the new perspective
    public void setAttribute(IndexableAttribute attribute) {
        if (attribute != this.attribute && attribute != null) {
            this.attribute = attribute;
            initializeOriginalData();
            setOriginalEventStatus(originalTrace.getOriginalActivityStatusWithStartEnd());
        }
    }
    
    private void initializeOriginalData() {
        originalActIndexes.clear();
        originalValueTrace.clear();
        originalStartTimeTrace.clear();
        originalEndTimeTrace.clear();
        originalDurationTrace.clear();

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
                }
                else {
                    originalDurationTrace.add(act.getOriginalDurationForAttribute(attribute.getKey()));
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
        }
        
        this.originalEventStatus = originalTrace.getOriginalActivityStatusWithStartEnd();
    }
    
    public BitSet getOriginalEventStatus() {
        return originalEventStatus;
    }
    
    // Update active data
    public void setOriginalEventStatus(BitSet eventBitSet) {
        originalEventStatus = eventBitSet;
        
        activeValueTrace.clear();
        activeStartTimeTrace.clear();
        activeEndTimeTrace.clear();
        activeDurationTrace.clear();
        
        activeNodes.clear();
        activeArcs.clear();
        
        activeNodeTotalCounts.clear();
        activeNodeTotalDurs.clear();
        activeNodeMinDurs.clear();
        activeNodeMinDurs.clear();
        
        activeArcTotalCounts.clear();
        activeArcTotalDurs.clear();
        activeArcMinDurs.clear();
        activeArcMaxDurs.clear();
        
        int preIndex = -1;
        for (int i=1; i<=originalValueTrace.size()-2; i++) { // exclude the two artificial start and end events
            if (originalEventStatus.get(i)) {
                int node = originalValueTrace.get(i);
                
                activeValueTrace.add(node);
                activeStartTimeTrace.add(originalStartTimeTrace.get(i));
                activeEndTimeTrace.add(originalEndTimeTrace.get(i));
                activeDurationTrace.add(originalDurationTrace.get(i));
                
                // Graph data
                activeNodes.add(node);
                long nodeDur = originalDurationTrace.get(i);
                activeNodeTotalCounts.put(node, activeNodeTotalCounts.getIfAbsentPut(node, 0) + 1);
                activeNodeTotalDurs.put(node, activeNodeTotalDurs.getIfAbsentPut(node, 0) + nodeDur);
                activeNodeMinDurs.put(node, Math.min(activeNodeMinDurs.getIfAbsentPut(node, Long.MAX_VALUE), nodeDur));
                activeNodeMaxDurs.put(node, Math.max(activeNodeMaxDurs.getIfAbsentPut(node, 0), nodeDur));
                
                if (preIndex >= 0) {
                    int preNode = originalValueTrace.get(preIndex);
                    int arc = attribute.getMatrixGraph().getArc(preNode, node);
                    activeArcs.add(arc);
                    long arcDur = originalStartTimeTrace.get(i) - originalEndTimeTrace.get(preIndex);
                    if (arcDur < 0) arcDur = 0;
                    activeArcTotalCounts.put(arc, activeArcTotalCounts.getIfAbsentPut(arc, 0) + 1);
                    activeArcTotalDurs.put(arc, activeArcTotalDurs.getIfAbsentPut(arc, 0) + arcDur);
                    activeArcMinDurs.put(arc, Math.min(activeArcMinDurs.getIfAbsentPut(arc, Long.MAX_VALUE), arcDur));
                    activeArcMaxDurs.put(arc, Math.max(activeArcMaxDurs.getIfAbsentPut(arc, 0), arcDur));
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
            
            int sourceNode = attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(0));
            activeNodes.add(sourceNode);
            activeNodeTotalCounts.put(sourceNode, 1);
            activeNodeTotalDurs.put(sourceNode, 0);
            activeNodeMinDurs.put(sourceNode, 0);
            activeNodeMaxDurs.put(sourceNode, 0);
            
            int sinkNode = attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(activeValueTrace.size()-1));
            activeNodes.add(sinkNode);
            activeNodeTotalCounts.put(sinkNode, 1);
            activeNodeTotalDurs.put(sinkNode, 0);
            activeNodeMinDurs.put(sinkNode, 0);
            activeNodeMaxDurs.put(sinkNode, 0);
            
            int sourceArc = attribute.getMatrixGraph().getArc(sourceNode, attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(1)));
            activeArcs.add(sourceArc);
            activeArcTotalCounts.put(sourceArc, 1);
            activeArcTotalDurs.put(sourceArc, 0);
            activeArcMinDurs.put(sourceArc, 0);
            activeArcMaxDurs.put(sourceArc, 0);
            
            int sinkArc = attribute.getMatrixGraph().getArc(attribute.getMatrixGraph().getNodeFromValueIndex(activeValueTrace.get(activeValueTrace.size()-2)), sinkNode);
            activeArcs.add(sinkArc);
            activeArcTotalCounts.put(sinkArc, 1);
            activeArcTotalDurs.put(sinkArc, 0);
            activeArcMinDurs.put(sinkArc, 0);
            activeArcMaxDurs.put(sinkArc, 0);
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
    
    public IntSet getActiveNodes() {
        return this.activeNodes;
    }
    
    public IntSet getActiveArcs() {
        return this.activeArcs;
    }
    
    public long getNodeTotalCount(int node) {
        return activeNodeTotalCounts.getIfAbsent(node, 0);
    }
    
    public long getNodeTotalDuration(int node) {
        return activeNodeTotalDurs.getIfAbsent(node, 0);
    }
    
    public long getNodeMinDuration(int node) {
        return activeNodeMinDurs.getIfAbsent(node, 0);
    }
    
    public long getNodeMaxDuration(int node) {
        return activeNodeMaxDurs.getIfAbsent(node, 0);
    }
    
    public long getArcTotalCount(int arc) {
        return activeArcTotalCounts.getIfAbsent(arc, 0);
    }
    
    public long getArcTotalDuration(int arc) {
        return activeArcTotalDurs.getIfAbsent(arc, 0);
    }
    
    public long getArcMinDuration(int arc) {
        return activeArcMinDurs.getIfAbsent(arc, 0);
    }
    
    public long getArcMaxDuration(int arc) {
        return activeArcMaxDurs.getIfAbsent(arc, 0);
    }
    
    @Override
    public String toString() {
        return this.getValueTrace().toString();
    }
}
