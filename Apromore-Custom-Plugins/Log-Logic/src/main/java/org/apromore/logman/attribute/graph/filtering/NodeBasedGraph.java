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

package org.apromore.logman.attribute.graph.filtering;

import java.util.BitSet;
import java.util.LinkedList;

import org.apromore.logman.attribute.graph.AttributeLogGraph;
import org.apromore.logman.attribute.graph.WeightedAttributeGraph;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.stack.primitive.MutableIntStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.IntStacks;

/**
 * NodeBasedGraph is a {@link WeightedAttributeGraph} created by filtering out nodes
 * on an original {@link AttributeLogGraph}.
 * 
 * @author Bruce Nguyen
 *
 */
public class NodeBasedGraph extends AbstractFilteredGraph {
    private MutableList<FilteredGraph> subGraphs = Lists.mutable.empty();
    private GraphTraversedArcs forwardArcs;
    private GraphTraversedArcs backwardArcs;
    private boolean isStartEndConnected = false;
    private MutableIntList removableArcs = IntLists.mutable.empty();

    public NodeBasedGraph(AttributeLogGraph originalGraph, BitSet nodeBitMask, BitSet arcBitMask) {
        super(originalGraph, nodeBitMask, arcBitMask);
    }
    
    public void buildSubGraphs(boolean arcInverted, int maxNumberOfArcs) {
        subGraphs.clear();
        long totalRemainingArcs = getArcs().size();
        
        ArcBasedGraph arcBasedGraph = new ArcBasedGraph(this.originalGraph, this.cloneNodeBitMask(), this.cloneArcBitMask());
        if (totalRemainingArcs <= maxNumberOfArcs) subGraphs.add(arcBasedGraph);
        
        if (this.isPerfectSequence()) {
            return;
        }
        
        buildRemovableArcsIndependently(arcInverted);
        
        if (!removableArcs.isEmpty()) {
            // Now remove arcs from the graph and create arc-based graphs
            // If there are many removable arcs, remove them in batches to keep the number of arc-based graphs under control
            int removedBatchSize = removableArcs.size()/100;
            if (removedBatchSize == 0) removedBatchSize = 1;
            
            int batchCount=0;
            while (batchCount<removableArcs.size()) {
                arcBasedGraph = new ArcBasedGraph(this.originalGraph, this.cloneNodeBitMask(), arcBasedGraph.cloneArcBitMask());
                for (int batchArcCount=0; batchArcCount<removedBatchSize; batchArcCount++) {
                    if ((batchCount + batchArcCount) < removableArcs.size()) {
                        arcBasedGraph.markRemoveArc(removableArcs.get(batchCount + batchArcCount));
                        totalRemainingArcs--;
                    }
                }
                if (totalRemainingArcs <= maxNumberOfArcs) subGraphs.add(arcBasedGraph);
                batchCount += removedBatchSize;
            }
        }
        
        // The default is the whole node-based graph itself, never let subgraphs empty. 
        if (subGraphs.isEmpty()) {
            subGraphs.add(arcBasedGraph);
        }
    }
    
    // Compute removable arcs in continuation with the previous node-based graph
    @Deprecated
    public void buildRemovableArcsDependently(NodeBasedGraph preGraph, boolean arcInverted) {
        IntList preRemovableArcs = (preGraph == null ? IntLists.immutable.empty() : preGraph.getRemovableArcs());
        IntList preBackboneArcs = (preGraph == null ? IntLists.immutable.empty() : preGraph.getBackboneArcs());
        
        BitSet backupArcBitMask = (BitSet)arcBitMask.clone();
        MutableIntList sortedArcs = (!arcInverted ? IntLists.mutable.ofAll(getSortedArcs()) : 
                                    IntLists.mutable.ofAll(getSortedArcs().toReversed()));

        removableArcs.clear();
        removableArcs.addAll(!arcInverted ? preRemovableArcs : preRemovableArcs.toReversed());
        preRemovableArcs.forEach(arc -> markRemoveArc(arc));
        
        sortedArcs.removeAll(preRemovableArcs);
        
        // The remaining arcs only include the new arcs on this graph (arcs adjacent to the new nodes)
        // and the backbone arcs on the previous graph. They are checked to form a new backbone
        // arcs for this graph (i.e. non-removable arcs).
        MutableIntSet oldBackboneArcsRemoved = IntSets.mutable.empty();
        MutableIntSet newBackboneArcsAdded = IntSets.mutable.empty();
        sortedArcs.forEach(arc -> {
            int src = getSource(arc);
            int tgt = getTarget(arc);
            markRemoveArc(arc);
            
            if (getSource(arc) == getTarget(arc)) { //self-loops are always removable
                removableArcs.add(arc);
            }
            else if (!isOutgoingConnected(src) || !isIncomingConnected(tgt) || // local or global disconnect
                    !isSinkReachableFromNode(src) || !isNodeReachableFromSource(tgt)) {
                markAddArc(arc);
                if (!preBackboneArcs.contains(arc)) {
                    newBackboneArcsAdded.add(arc);
                }
            }
            else {
                removableArcs.add(arc);
                if (preBackboneArcs.contains(arc)) {
                    oldBackboneArcsRemoved.add(arc);
                }
            }
        });
        
        // Due to the occurrance of the new nodes in this graph compare to the previous one,
        // the previous backbone arcs can turn to be removable in this graph. This is because
        // the new backbone arcs are prioritized to integrate the new nodes into the graph.
        // However, new backbone arcs might not be a good selection
        
        
        // Review to retain the previous backbone arcs that have been removed
        for (int oldArc : oldBackboneArcsRemoved.toArray()) {
            if (isRetainable(oldArc, newBackboneArcsAdded, arcInverted)) {
                removableArcs.remove(oldArc);
            }
        }
        
        arcBitMask = backupArcBitMask;
    }
    
    // A removed backbone arc can be retained if one of the following conditions is met:
    // - It is adjacent to a newly added backbone arc but not a starting or ending arc and it has more important weight than that arc
    // - It is adjacent to NO newly added adjacent backbone arcs, then it should be always retained
    private boolean isRetainable(int arc, IntSet newBackboneArcs, boolean arcInverted) {
        double arcWeight = originalGraph.getArcStructuralWeight(arc);
        double lowestNewBackboneWeight = getLowestAdjacentBackboneWeight(arc, newBackboneArcs, arcInverted);
        return (!arcInverted && arcWeight > lowestNewBackboneWeight) || 
                (arcInverted && arcWeight < lowestNewBackboneWeight);
    }
    
    // Return the lowest weight of the new backbone arcs that are adjacent to the input arc
    // If no new backbone arcs are adjacent to the input arc, return the lowest weight value
    private double getLowestAdjacentBackboneWeight(int arc, IntSet newBackboneArcs, boolean arcInverted) {
        int source = getSource(arc);
        int target = getTarget(arc);
        
        MutableIntSet newBackboneArcsOfSource = IntSets.mutable.ofAll(this.getIncomingArcs(source));
        newBackboneArcsOfSource.addAll(this.getOutgoingArcs(source));
        newBackboneArcsOfSource.retainAll(newBackboneArcs);
        
        MutableIntSet newBackboneArcsOfTarget = IntSets.mutable.ofAll(this.getIncomingArcs(target));
        newBackboneArcsOfTarget.addAll(this.getOutgoingArcs(target));
        newBackboneArcsOfTarget.retainAll(newBackboneArcs);
        
        if (newBackboneArcsOfSource.isEmpty() && newBackboneArcsOfTarget.isEmpty()) {
            return (!arcInverted ? Double.MIN_VALUE : Double.MAX_VALUE);
        }
        else if (newBackboneArcsOfSource.isEmpty()) {
            if (!arcInverted) {
                return newBackboneArcsOfTarget.collect(a -> originalGraph.getArcStructuralWeight(a)).min();
            }
            else {
                return newBackboneArcsOfTarget.collect(a -> originalGraph.getArcStructuralWeight(a)).max();
            }
        }
        else if (newBackboneArcsOfTarget.isEmpty()) {
            if (!arcInverted) {
                return newBackboneArcsOfSource.collect(a -> originalGraph.getArcStructuralWeight(a)).min();
            }
            else {
                return newBackboneArcsOfSource.collect(a -> originalGraph.getArcStructuralWeight(a)).max();
            }
        }
        else {
            if (!arcInverted) {
                double sourceMin = newBackboneArcsOfSource.collect(a -> originalGraph.getArcStructuralWeight(a)).min();
                double targetMin = newBackboneArcsOfTarget.collect(a -> originalGraph.getArcStructuralWeight(a)).min();
                return Math.min(sourceMin, targetMin);
            }
            else {
                double sourceMax = newBackboneArcsOfSource.collect(a -> originalGraph.getArcStructuralWeight(a)).max();
                double targetMax = newBackboneArcsOfTarget.collect(a -> originalGraph.getArcStructuralWeight(a)).max();
                return Math.max(sourceMax, targetMax);
            }
        }
    }
    
    // Compute removable arcs without continuation from the previous graph
    public void buildRemovableArcsIndependently(boolean arcInverted) {
        BitSet backupArcBitMask = (BitSet)arcBitMask.clone();
        MutableIntList sortedArcs = (!arcInverted ? IntLists.mutable.ofAll(getSortedArcs()) : 
                                    IntLists.mutable.ofAll(getSortedArcs().toReversed()));

        removableArcs.clear();
        
        sortedArcs.forEach(arc -> {
            int src = getSource(arc);
            int tgt = getTarget(arc);
            markRemoveArc(arc);
            
            if (getSource(arc) == getTarget(arc)) { //self-loops are always removable
                removableArcs.add(arc);
            }
            else if (!isOutgoingConnected(src) || !isIncomingConnected(tgt)) { // local disconnect
                markAddArc(arc);
            }
            else if (!isSinkReachableFromNode(src) || !isNodeReachableFromSource(tgt)) {
                markAddArc(arc);
            }
            else {
                removableArcs.add(arc);
            }
        });

        arcBitMask = backupArcBitMask;
    }
    
    public IntList getRemovableArcs() {
        return removableArcs.toImmutable();
    }
    
    // Note that non-removable arcs represent the backbone of the graph
    public IntList getBackboneArcs() {
        return getSortedArcs().select(arc -> !removableArcs.contains(arc)).toImmutable();
    }
    
    public boolean isConnected() {
        int src, tgt;
        MutableIntStack toVisit = IntStacks.mutable.empty();
        MutableIntSet unvisited = IntSets.mutable.empty();
        
        // Forward graph traversal
        toVisit.push(this.getSourceNode());
        unvisited.addAll(this.getNodes());
        unvisited.remove(this.getSourceNode());

        while( !toVisit.isEmpty() ) {
            src = toVisit.pop();
            for( int outArc : this.getOutgoingArcsWithoutSelfLoops(src).toArray()) {
                tgt = getTarget(outArc);
                if(unvisited.contains(tgt) ) {
                    toVisit.push(tgt);
                    unvisited.remove(tgt);
                }
            }
        }

        if (!unvisited.isEmpty()) return false;

        // Backward graph traversal
        toVisit.push(this.getSinkNode());
        unvisited.clear();
        unvisited.addAll(this.getNodes());
        unvisited.remove(this.getSinkNode());

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.pop();
            for( int inArc : this.getIncomingArcsWithoutSelfLoops(tgt).toArray() ) {
                src = this.getSource(inArc);
                if(unvisited.contains(src) ) {
                    toVisit.push(src);
                    unvisited.remove(src);
                }
            }
        }
        
        return unvisited.isEmpty();
    }
    
    public boolean isNodeReachableFromSource(int node) {
        if (node == getSourceNode()) return true;
        
        int src, tgt;
        MutableIntStack toVisit = IntStacks.mutable.empty();
        MutableIntSet unvisited = IntSets.mutable.empty();
        
        // Forward graph traversal
        toVisit.push(this.getSourceNode());
        unvisited.addAll(this.getNodes());
        unvisited.remove(this.getSourceNode());

        while( !toVisit.isEmpty() ) {
            src = toVisit.pop();
            for( int outArc : this.getOutgoingArcsWithoutSelfLoops(src).toArray()) {
                tgt = getTarget(outArc);
                if (tgt == node) return true;
                if(unvisited.contains(tgt) ) {
                    toVisit.push(tgt);
                    unvisited.remove(tgt);
                }
            }
        }

        return false;
    }
    
    public boolean isSinkReachableFromNode(int node) {
        if (node == getSinkNode()) return true;
        
        int src, tgt;
        LinkedList<Integer> toVisit = new LinkedList<>();
        MutableIntSet unvisited = IntSets.mutable.empty();
        
        // Backward graph traversal
        toVisit.push(this.getSinkNode());
        unvisited.addAll(this.getNodes());
        unvisited.remove(this.getSinkNode());

        while( !toVisit.isEmpty() ) {
            tgt = toVisit.pop();
            for( int inArc : this.getIncomingArcsWithoutSelfLoops(tgt).toArray() ) {
                src = this.getSource(inArc);
                if (src == node) return true;
                if(unvisited.contains(src) ) {
                    toVisit.push(src);
                    unvisited.remove(src);
                }
            }
        }
        
        return false;
    }
    
    private boolean isOutgoingConnected(int node) {
        return !getOutgoingArcsWithoutSelfLoops(node).isEmpty();
    }
    
    private boolean isIncomingConnected(int node) {
        return !getIncomingArcsWithoutSelfLoops(node).isEmpty();
    }
    
    public boolean isStartEndConnected() {
        return isStartEndConnected;
    }
    
    @Override
    public ListIterable<FilteredGraph> getSubGraphs() {
        return subGraphs;
    }

    public IntList getSortedRemovableArcs() {
        return originalGraph.getSortedArcs().select(arc -> arcBitMask.get(arc) && !forwardArcs.containArc(arc) && !backwardArcs.containArc(arc));
    }
    
}
