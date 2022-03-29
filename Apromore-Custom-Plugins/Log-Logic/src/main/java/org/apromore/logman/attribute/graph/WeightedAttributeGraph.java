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

package org.apromore.logman.attribute.graph;

import java.util.BitSet;

import org.apromore.logman.attribute.AttributeMatrixGraph;
import org.apromore.logman.attribute.IndexableAttribute;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;

/**
 * WeightedAttributeGraph is a subgraph of {@link AttributeMatrixGraph} (a base graph) 
 * with added weights for nodes/arcs.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class WeightedAttributeGraph {
    protected AttributeMatrixGraph attMatrixGraph;
    
    protected MutableIntSet graphNodes = IntSets.mutable.empty();
    protected MutableIntSet graphArcs = IntSets.mutable.empty();

    // These maps are to boost the retrieval of incoming/outgoing nodes/arcs of nodes
    protected MutableIntObjectMap<MutableIntSet> incomingArcs = IntObjectMaps.mutable.empty(); //node => set of arcs
    protected MutableIntObjectMap<MutableIntSet> outgoingArcs = IntObjectMaps.mutable.empty(); //node => set of arcs
    
    public WeightedAttributeGraph(IndexableAttribute attribute) {
        this.attMatrixGraph = attribute.getMatrixGraph();
    }
    
    public AttributeMatrixGraph getBaseGraph() {
        return this.attMatrixGraph;
    }
    
    public void resetToAttribute(IndexableAttribute attribute) {
        this.attMatrixGraph = attribute.getMatrixGraph();
        clear();
    }
    
    public void clear() {
        graphNodes.clear();
        graphArcs.clear();
        incomingArcs.clear();
        outgoingArcs.clear();
    }
    
    
    //////////////////////////// Nodes and arcs creation ///////////////////////////////////
    
    public boolean addNode(int node) {
        if (attMatrixGraph.getNodes().contains(node) && !containNode(node)) {
            graphNodes.add(node);
            if (!incomingArcs.containsKey(node)) incomingArcs.put(node, IntSets.mutable.empty());
            if (!outgoingArcs.containsKey(node)) outgoingArcs.put(node, IntSets.mutable.empty());
            return true;
        }
        return false;
    }
    
    public void addNodes(IntSet nodes) {
        nodes.forEach(node -> addNode(node));
    }
    
    public boolean addArc(int arc) {
        int source = getSource(arc);
        int target = getTarget(arc);
        if (containNode(source) && containNode(target)) {
            graphArcs.add(arc);
            incomingArcs.get(target).add(arc);
            outgoingArcs.get(source).add(arc);
            return true;
        }
        return false;
    }

    public void addArcs(IntSet arcs) {
        arcs.forEach(arc -> addArc(arc));
    }
    
    //////////////////////////////// NODES OPERATIONS //////////////////////////////
    
    public String getNodeName(int node) {
        if (containNode(node)) {
            return attMatrixGraph.getNodeName(node);
        }
        return "";
    }
    
    public int getNodeFromName(String nodeName) {
        int node = attMatrixGraph.getNodeFromName(nodeName);
        if (node != -1) {
            return node;
        }
        else {
            return -1;
        }
    }
    
    public IntSet getNodes() {
        return graphNodes.toImmutable();
    }   
    
    public int getSourceNode() {
        return attMatrixGraph.getSource();
    }
    
    public int getSinkNode() {
        return attMatrixGraph.getSink();
    }
    
    public boolean containNode(int node) {
        return graphNodes.contains(node);
    }
    
    public IntSet getIncomingArcs(int node) {
        return incomingArcs.getIfAbsent(node, IntSets.mutable::empty);
    }   
    
    public IntSet getOutgoingArcs(int node) {
        return outgoingArcs.getIfAbsent(node, IntSets.mutable::empty);
    }  
    
    public IntSet getOutgoingArcsWithoutSelfLoops(int node) {
        return getOutgoingArcs(node).select(arc -> getTarget(arc) != node);
    }
    
    public IntSet getIncomingArcsWithoutSelfLoops(int node) {
        return getIncomingArcs(node).select(arc -> getSource(arc) != node);
    }

    
    /////////////////////////// ARCS OPERATIONS ///////////////////////////////
    
    public boolean containArc(int arc) {
        return graphArcs.contains(arc);
    }
    
    public int getArc(int source, int target) {
        if (containNode(source) && containNode(target)) {
            int arc = attMatrixGraph.getArc(source, target);
            if (containArc(arc)) return arc;
        }
        return -1;
    }
    
    public int getArc(String sourceName, String targetName) {
        int source = getNodeFromName(sourceName);
        int target = getNodeFromName(targetName);
        if (source != -1 && target != -1) return getArc(source, target);
        return -1;
    }
    
    public IntSet getArcs() {
        return graphArcs;
    }
    
    public int getSource(int arc) {
        return attMatrixGraph.getSource(arc);
    }
    
    public int getTarget(int arc) {
        return attMatrixGraph.getTarget(arc);
    }
    
    public BitSet getNodeBitMask() {
        BitSet nodeBitMask = new BitSet(attMatrixGraph.getDimension());
        graphNodes.forEach(node -> nodeBitMask.set(node));
        return nodeBitMask;
    }
    
    public BitSet getArcBitMask() {
        BitSet arcBitMask = new BitSet(attMatrixGraph.getDimension()*attMatrixGraph.getDimension());
        graphArcs.forEach(arc -> arcBitMask.set(arc));
        return arcBitMask;
    }
    
    /////////////////////////// WEIGHTS ///////////////////////////////    
    
    public abstract double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation, MeasureRelation measureRelation);

    public abstract double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation, MeasureRelation measureRelation);    
    
    // Shortcut methods
    public long getNodeTotalFrequency(int node) {
        return (long)getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE);
    }
    
    public long getNodeCaseFrequency(int node) {
        return (long)getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE);
    }
    
    public long getNodeMinFrequency(int node) {
        return (long)getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE);
    }
    
    public long getNodeMaxFrequency(int node) {
        return (long)getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE);
    }
    
    public double getNodeMeanFrequency(int node) {
        return getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE);
    }
    
    public double getNodeMedianFrequency(int node) {
        return getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE);
    }
    
    public long getNodeTotalDuration(int node) {
        return (long)getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE);
    }
    
    public long getNodeMinDuration(int node) {
        return (long)getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE);
    }
    
    public long getNodeMaxDuration(int node) {
        return (long)getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE);
    }
    
    public double getNodeMeanDuration(int node) {
        return getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE);
    }
    
    public double getNodeMedianDuration(int node) {
        return getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcTotalFrequency(int arc) {
        return (long)getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcCaseFrequency(int arc) {
        return (long)getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcMinFrequency(int arc) {
        return (long)getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcMaxFrequency(int arc) {
        return (long)getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE);
    }
    
    public double getArcMeanFrequency(int arc) {
        return getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE);
    }
    
    public double getArcMedianFrequency(int arc) {
        return getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcTotalDuration(int arc) {
        return (long)getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcMinDuration(int arc) {
        return (long)getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.MIN, MeasureRelation.ABSOLUTE);
    }
    
    public long getArcMaxDuration(int arc) {
        return (long)getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.MAX, MeasureRelation.ABSOLUTE);
    }
    
    public double getArcMeanDuration(int arc) {
        return getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE);
    }
    
    public double getArcMedianDuration(int arc) {
        return getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.MEDIAN, MeasureRelation.ABSOLUTE);
    }
    
    @Override
    public String toString() {
        String toString = "Nodes: ";
        for (int n : getNodes().toArray()) {
            toString += this.getNodeName(n) + "(" + n + ")" + "|";
        }
        
        toString += "\nArcs: ";
        for (int a : getArcs().toArray()) {
            toString += (getNodeName(getSource(a)) + "->" + getNodeName(getTarget(a)) + "(" + a + ")" + "|");
        }
        
        return toString;
    }
}
