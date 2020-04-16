/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.logman.attribute.graph;

import java.util.BitSet;

import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.set.primitive.IntSet;

public abstract class AbstractAttributeGraph implements AttributeGraph {
    protected AttributeLogGraph originalGraph;
    protected BitSet nodeBitMask; //true indicate that the node i (also at the index i, node = index) is active
    protected BitSet arcBitMask; //true indicate that the arc i (also at the index i, arc = index) is active
    
    public AbstractAttributeGraph(AttributeLogGraph originalGraph, BitSet nodeBitMask, BitSet arcBitMask) {
        this.originalGraph = originalGraph;
        this.nodeBitMask = nodeBitMask;
        this.arcBitMask = arcBitMask;
    }
    
    @Override
    public AttributeLogGraph getOriginalGraph() {
        return originalGraph;
    }
    
    
    ////////////////////// BITMASK OPERATIONS ////////////////////////////
    
    @Override
    public BitSet cloneNodeBitMask() {
        return (BitSet)nodeBitMask.clone();
    }
    
    @Override
    public BitSet cloneArcBitMask() {
        return (BitSet)arcBitMask.clone();
    }
    
    @Override
    public void markRemoveNode(int node) {
        nodeBitMask.clear(node);
        getOutgoingArcs(node).forEach(arc -> markRemoveArc(arc));
        getIncomingArcs(node).forEach(arc -> markRemoveArc(arc));
    }
    
    @Override
    public void markRemoveArc(int arc) {
        arcBitMask.clear(arc);
    }
    
    @Override
    public void markAddArc(int arc) {
        arcBitMask.set(arc);
    }
    
    
    ///////////////////// NODE OPERATIONS //////////////////////////////////
    
    @Override
    public IntSet getNodes() {
        return originalGraph.getOriginalNodes().select(node -> nodeBitMask.get(node)).toImmutable();
    }
    
    @Override
    public boolean containNode(int node) {
        return nodeBitMask.get(node);
    }
    
    @Override
    public int getSourceNode() {
        return originalGraph.getSourceNode();
    }
    
    @Override
    public int getSinkNode() {
        return originalGraph.getSinkNode();
    }
    
    @Override
    public String getNodeName(int node) {
        return originalGraph.getNodeName(node);
    }
    
    @Override
    public IntSet getIncomingArcs(int node) {
        return originalGraph.getIncomingOriginalArcs(node).select(arc -> arcBitMask.get(arc));
    }
    
    @Override
    public IntSet getOutgoingArcs(int node) {
        return originalGraph.getOutgoingOriginalArcs(node).select(arc -> arcBitMask.get(arc));
    }
    
    @Override
    public IntSet getIncomingArcsWithoutSelfLoops(int node) {
        return originalGraph.getIncomingOriginalArcs(node).select(arc -> arcBitMask.get(arc) && getSource(arc) != node);
    }
    
    @Override
    public IntSet getOutgoingArcsWithoutSelfLoops(int node) {
        return originalGraph.getOutgoingOriginalArcs(node).select(arc -> arcBitMask.get(arc) && getTarget(arc) != node);
    }
    
    @Override
    public IntList getSortedArcs() {
        return originalGraph.getSortedArcs().select(arc -> arcBitMask.get(arc));
    }
    
    @Override
    public IntList getSortedArcsWithoutSelfLoops() {
        return originalGraph.getSortedArcs().select(arc -> arcBitMask.get(arc) && getSource(arc) != getTarget(arc));
    }
    
//    @Override
//    public IntList getIncomingSortedArcsWithoutSelfLoops(int node) {
//        return originalGraph.getIncomingOriginalSortedArcs(node).select(arc -> arcBitMask.get(arc) && getSource(arc) != node);
//    }
    
//    @Override
//    public IntList getOutgoingSortedArcsWithoutSelfLoops(int node) {
//        return originalGraph.getOutgoingOriginalSortedArcs(node).select(arc -> arcBitMask.get(arc) && getTarget(arc) != node);
//    }
    
    
    
    ///////////////////// ARC OPERATIONS //////////////////////////////////
    
    @Override
    public int getArc(int source, int target) throws InvalidArcException {
        return originalGraph.getArc(source, target);
    }
    
    @Override
    public int getSource(int arc) {
        return originalGraph.getSource(arc);
    }
    
    @Override
    public int getTarget(int arc) {
        return originalGraph.getTarget(arc);
    }
    
    @Override
    public IntSet getArcs() {
        return originalGraph.getOriginalArcs().select(arc -> arcBitMask.get(arc)).toImmutable();
    }
    
    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation) {
        return originalGraph.getNodeWeight(node, type, aggregation);
    }
    
    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation) {
        return originalGraph.getArcWeight(arc, type, aggregation);
    }
    
    
    
    ///////////////////// GRAPH OPERATIONS //////////////////////////////////
    
    @Override
    public boolean isPerfectSequence() {
        int node = this.getSourceNode();
        IntSet outgoings = getOutgoingArcs(node);
        while (outgoings.size()==1) {
            node = outgoings.intIterator().next();
            outgoings = getOutgoingArcs(node);
        }
        return (node == this.getSinkNode());
    }
    
    @Override
    public int hashCode() {
        return getNodes().hashCode() ^ getArcs().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof AttributeGraph)) return false;
        AttributeGraph otherGraph = (AttributeGraph)other;
        return (this.getNodes().equals(otherGraph.getNodes()) && this.getArcs().equals(otherGraph.getArcs()));
    }
    
    @Override
    public String toString() {
        return getNodes().toString() + ", " + getArcs().toString();
    }
}
