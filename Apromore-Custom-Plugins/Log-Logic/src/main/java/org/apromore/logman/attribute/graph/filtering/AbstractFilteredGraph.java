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

import org.apromore.logman.attribute.AttributeMatrixGraph;
import org.apromore.logman.attribute.graph.AttributeLogGraph;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.graph.WeightedAttributeGraph;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.set.primitive.IntSet;

/**
 * AbstractFilteredGraph is a {@link WeightedAttributeGraph} which can be filtered by 
 * removing nodes/arcs. Thus, it also takes an {@link AttributeMatrixGraph} as a base graph.
 * It is created from an original {@link AttributeLogGraph}.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class AbstractFilteredGraph extends WeightedAttributeGraph implements FilteredGraph {
    protected AttributeLogGraph originalGraph;
    protected BitSet nodeBitMask; //true indicate that the node i (also at the index i, node = index) is active
    protected BitSet arcBitMask; //true indicate that the arc i (also at the index i, arc = index) is active
    
    public AbstractFilteredGraph(AttributeLogGraph originalGraph, BitSet nodeBitMask, BitSet arcBitMask) {
        super(originalGraph.getBaseGraph().getAttribute());
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
        return originalGraph.getNodes().select(node -> nodeBitMask.get(node)).toImmutable();
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
        return originalGraph.getIncomingArcs(node).select(arc -> arcBitMask.get(arc));
    }
    
    @Override
    public IntSet getOutgoingArcs(int node) {
        return originalGraph.getOutgoingArcs(node).select(arc -> arcBitMask.get(arc));
    }
    
    @Override
    public IntSet getIncomingArcsWithoutSelfLoops(int node) {
        return originalGraph.getIncomingArcs(node).select(arc -> arcBitMask.get(arc) && getSource(arc) != node);
    }
    
    @Override
    public IntSet getOutgoingArcsWithoutSelfLoops(int node) {
        return originalGraph.getOutgoingArcs(node).select(arc -> arcBitMask.get(arc) && getTarget(arc) != node);
    }
    
    @Override
    public IntList getSortedArcs() {
        return originalGraph.getSortedArcs().select(arc -> arcBitMask.get(arc));
    }
    
    @Override
    public IntList getSortedArcsWithoutSelfLoops() {
        return originalGraph.getSortedArcs().select(arc -> arcBitMask.get(arc) && getSource(arc) != getTarget(arc));
    }
    
    ///////////////////// ARC OPERATIONS //////////////////////////////////
    
    @Override
    public int getArc(int source, int target) {
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
        return originalGraph.getArcs().select(arc -> arcBitMask.get(arc)).toImmutable();
    }
    
    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation, MeasureRelation relation) {
        return originalGraph.getNodeWeight(node, type, aggregation, relation);
    }
    
    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation, MeasureRelation relation) {
        return originalGraph.getArcWeight(arc, type, aggregation, relation);
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
        if (!(other instanceof FilteredGraph)) return false;
        FilteredGraph otherGraph = (FilteredGraph)other;
        return (this.getNodes().equals(otherGraph.getNodes()) && this.getArcs().equals(otherGraph.getArcs()));
    }
}
