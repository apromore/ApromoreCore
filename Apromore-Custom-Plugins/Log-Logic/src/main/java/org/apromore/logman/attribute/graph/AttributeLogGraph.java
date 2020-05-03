/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import java.util.Comparator;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.graph.filtering.NodeBasedGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.IntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.primitive.IntDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;

/**
 * AttributeLogGraph is an {@link WeightedAttributeGraph} implementation for AttributeLog.
 * AttributeLogGraph can be created by adding graphs of all {@link AttributeTrace}. 
 * AttributeLogGraph can be filtered on nodes and arcs to create subgraphs. 
 * This is filtering at the graph level, not at the log level.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeLogGraph extends WeightedAttributeGraph {
    private AttributeLog attLog;
    
    private MutableIntDoubleMap originalNodeTotalFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeCaseFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMinFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMaxFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMedianFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMeanFreqs = IntDoubleMaps.mutable.empty();
    
    private MutableIntDoubleMap originalNodeTotalDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMinDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMaxDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMedianDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMeanDurs = IntDoubleMaps.mutable.empty();   
    
    private MutableIntDoubleMap originalArcTotalFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcCaseFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMinFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMaxFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMedianFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMeanFreqs = IntDoubleMaps.mutable.empty();
    
    private MutableIntDoubleMap originalArcTotalDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMinDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMaxDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMedianDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMeanDurs = IntDoubleMaps.mutable.empty(); 
    
    // Used for calculating median
    private MutableIntObjectMap<MutableDoubleList> originalNodeFreqs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> originalNodeDurs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> originalArcFreqs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> originalArcDurs = IntObjectMaps.mutable.empty();
    
    // Sub-graphs and related data and parameters
    private MutableList<FilteredGraph> subGraphs = Lists.mutable.empty();
    private IntList sortedNodes;
    private IntList sortedArcs;
    private IndexableAttribute subGraphsSortedAttribute;
    private MeasureType weightType;
    private MeasureAggregation weightAggregation;
    private IntDoubleMap nodeWeightsForGraphStructure;
    private IntDoubleMap arcWeightsForGraphStructure;
    private boolean nodeInverted = false; 
    private boolean arcInverted = false;
    
    public AttributeLogGraph(AttributeLog attLog) {
        super(attLog.getAttribute().getMatrixGraph());
        this.attLog = attLog;
    }
    
    @Override
    public void reset() {
        super.reset();
        
        originalNodeTotalFreqs.clear(); 
        originalNodeCaseFreqs.clear();
        originalNodeMinFreqs.clear(); 
        originalNodeMaxFreqs.clear();
        originalNodeMedianFreqs.clear();
        originalNodeMeanFreqs.clear();
                 
        originalNodeTotalDurs.clear();
        originalNodeMinDurs.clear();
        originalNodeMaxDurs.clear();
        originalNodeMedianDurs.clear();
        originalNodeMeanDurs.clear();
                 
        originalArcTotalFreqs.clear();
        originalArcCaseFreqs.clear();
        originalArcMinFreqs.clear();
        originalArcMaxFreqs.clear();
        originalArcMedianFreqs.clear();
        originalArcMeanFreqs.clear();
                 
        originalArcTotalDurs.clear();
        originalArcMinDurs.clear();
        originalArcMaxDurs.clear();
        originalArcMedianDurs.clear();
        originalArcMeanDurs.clear();
        
        originalNodeFreqs.clear();
        originalNodeDurs.clear();
        originalArcFreqs.clear();
        originalArcDurs.clear();
        
        subGraphs.clear();
    }
    
    public void addTraceGraph(AttributeTraceGraph traceGraph) {
        addNodes(traceGraph.getNodes());
        addArcs(traceGraph.getArcs());
        traceGraph.getNodes().forEach(node -> updateNodeWeights(node, traceGraph));
        traceGraph.getArcs().forEach(arc -> updateArcWeights(arc, traceGraph));
    }
    
    // Used to update other data after the log and graph has been fully updated. 
    public void finalUpdate() {
        Median medianCalculator = new Median();
        graphNodes.forEach(node -> {
            originalNodeMeanFreqs.put(node, originalNodeTotalFreqs.get(node)/attLog.getTraces().size());
            originalNodeMeanDurs.put(node, originalNodeTotalDurs.get(node)/originalNodeTotalFreqs.get(node));
            originalNodeMedianFreqs.put(node, medianCalculator.evaluate(originalNodeFreqs.get(node).toArray()));
            originalNodeMedianDurs.put(node, medianCalculator.evaluate(originalNodeDurs.get(node).toArray()));
            if (originalNodeCaseFreqs.get(node) != attLog.getTraces().size()) {// there's a case not containing the node
                originalNodeMinFreqs.put(node, 0);
            }
        });
        
        graphArcs.forEach(arc -> {
            originalArcMeanFreqs.put(arc, originalArcTotalFreqs.getIfAbsentPut(arc, 0)/attLog.getTraces().size());
            originalArcMeanDurs.put(arc, originalArcTotalDurs.get(arc)/originalArcTotalFreqs.get(arc));
            originalArcMedianFreqs.put(arc, medianCalculator.evaluate(originalArcFreqs.get(arc).toArray()));
            originalArcMedianDurs.put(arc, medianCalculator.evaluate(originalArcDurs.get(arc).toArray()));
            if (originalArcCaseFreqs.get(arc) != attLog.getTraces().size()) { // there's a case not containing the arc
                originalArcMinFreqs.put(arc, 0);
            }
        });
    }    

    ////////////////////////////  Node measure ///////////////////////////////////
    
    private void updateNodeWeights(int node, AttributeTraceGraph traceGraph) {
        incrementNodeTotalFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        incrementNodeCaseFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.CASES));
        updateNodeMinFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        updateNodeMaxFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        
        incrementNodeTotalDuration(node, traceGraph.getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.TOTAL));
        updateNodeMinDuration(node, traceGraph.getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.MIN));
        updateNodeMaxDuration(node, traceGraph.getNodeWeight(node, MeasureType.DURATION, MeasureAggregation.MAX));
        
        collectNodeFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        collectNodeDuration(node, traceGraph.getNodeDurations(node));
    }    
    
    private void incrementNodeTotalFrequency(int node, double nodeTotalCount) {
        originalNodeTotalFreqs.put(node, originalNodeTotalFreqs.getIfAbsentPut(node, 0) + nodeTotalCount);
    }
    
    private void incrementNodeCaseFrequency(int node, double nodeCaseCount) {
        originalNodeCaseFreqs.put(node, originalNodeCaseFreqs.getIfAbsentPut(node, 0) + (nodeCaseCount>0 ? 1 : 0));
    }
    
    private void incrementNodeTotalDuration(int node, double nodeTotalDuration) {
        originalNodeTotalDurs.put(node, originalNodeTotalDurs.getIfAbsentPut(node, 0) + nodeTotalDuration);
    }
    
    private void updateNodeMinFrequency(int node, double nodeCount) {
        originalNodeMinFreqs.put(node, Math.min(originalNodeMinFreqs.getIfAbsentPut(node, Double.MAX_VALUE), nodeCount));
    }
    
    private void updateNodeMaxFrequency(int node, double nodeCount) {
        originalNodeMaxFreqs.put(node, Math.max(originalNodeMaxFreqs.getIfAbsentPut(node, 0), nodeCount));
    }
    
    private void updateNodeMinDuration(int node, double nodeDuration) {
        originalNodeMinDurs.put(node, Math.min(originalNodeMinDurs.getIfAbsentPut(node, Double.MAX_VALUE), nodeDuration));
    }
    
    private void updateNodeMaxDuration(int node, double nodeDuration) {
        originalNodeMaxDurs.put(node, Math.max(originalNodeMaxDurs.getIfAbsentPut(node, 0), nodeDuration));
    }
    
    private void collectNodeFrequency(int node, double nodeFreq) {
        if (originalNodeFreqs.contains(node)) originalNodeFreqs.get(node).add(nodeFreq); 
    }
    
    private void collectNodeDuration(int node, DoubleList nodeDurs) {
        if (originalNodeDurs.contains(node)) originalNodeDurs.get(node).addAll(nodeDurs);
    }
    
    //////////////////////////// Arc measure ///////////////////////////////////
    
    private void updateArcWeights(int arc, AttributeTraceGraph traceGraph) {
        incrementArcTotalFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        incrementArcCaseFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.CASES));
        updateArcMinFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        updateArcMaxFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        
        incrementArcTotalDuration(arc, traceGraph.getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.TOTAL));
        updateArcMinDuration(arc, traceGraph.getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.MIN));
        updateArcMaxDuration(arc, traceGraph.getArcWeight(arc, MeasureType.DURATION, MeasureAggregation.MAX));
        
        collectArcFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL));
        collectArcDuration(arc, traceGraph.getArcDurations(arc));
    }    
    
    private void incrementArcTotalFrequency(int arc, double arcTotalCount) {
        originalArcTotalFreqs.put(arc, originalArcTotalFreqs.getIfAbsentPut(arc, 0) + arcTotalCount);
    }
    
    private void incrementArcCaseFrequency(int arc, double arcCaseCount) {
        originalArcCaseFreqs.put(arc, originalArcCaseFreqs.getIfAbsentPut(arc, 0) + (arcCaseCount>0 ? 1 : 0));
    }
    
    private void incrementArcTotalDuration(int arc, double arcTotalDuration) {
        originalArcTotalDurs.put(arc, originalArcTotalDurs.getIfAbsentPut(arc, 0) + arcTotalDuration);
    }
    
    private void updateArcMinFrequency(int arc, double arcCount) {
        originalArcMinFreqs.put(arc, Math.min(originalArcMinFreqs.getIfAbsentPut(arc, Double.MAX_VALUE), arcCount));
    }
    
    private void updateArcMaxFrequency(int arc, double arcCount) {
        originalArcMaxFreqs.put(arc, Math.max(originalArcMaxFreqs.getIfAbsentPut(arc, 0), arcCount));
    }
    
    private void updateArcMinDuration(int arc, double arcDuration) {
        originalArcMinDurs.put(arc, Math.min(originalArcMinDurs.getIfAbsentPut(arc, Double.MAX_VALUE), arcDuration));
    }
    
    private void updateArcMaxDuration(int arc, double arcDuration) {
        originalArcMaxDurs.put(arc, Math.max(originalArcMaxDurs.getIfAbsentPut(arc, 0), arcDuration));
    }
    
    private void collectArcFrequency(int arc, double arcFreq) {
        if (originalArcFreqs.contains(arc)) originalArcFreqs.get(arc).add(arcFreq);
    }
    
    private void collectArcDuration(int arc, DoubleList arcDurs) {
        if (originalArcDurs.contains(arc)) originalArcDurs.get(arc).addAll(arcDurs);
    }
    
    
    ///////////////////////////// MEAUSURES ////////////////////////////////////////
    
    public double getNodeWeight(String nodeName, MeasureType type, MeasureAggregation aggregation) {
        int node = this.getNodeFromName(nodeName);
        return (node >= 0) ? this.getNodeWeight(node, type, aggregation) : 0;
    }
    
    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation) {
        return getNodeWeightMap(type, aggregation).get(node);
    }
    
    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation) {
        return getArcWeightMap(type, aggregation).get(arc);
    }
    
    public double getArcStructuralWeight(int arc) {
        return arcWeightsForGraphStructure.getIfAbsent(arc, 0);
    }   
    
    public double getNodeStructuralWeight(int node) {
        return nodeWeightsForGraphStructure.getIfAbsent(node, 0);
    }
    
    private IntDoubleMap getNodeWeightMap(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return originalNodeTotalFreqs;
            case CASES:
                return originalNodeCaseFreqs;                
            case MEAN:
                return originalNodeMeanFreqs;
            case MIN:
                return originalNodeMinFreqs;
            case MAX:
                return originalNodeMaxFreqs;
            case MEDIAN:
                return originalNodeMedianFreqs;
            default:
                return originalNodeTotalFreqs;
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return originalNodeTotalDurs;
            case MEAN:
                return originalNodeMeanDurs;
            case MIN:
                return originalNodeMinDurs;
            case MAX:
                return originalNodeMaxDurs;
            case MEDIAN:
                return originalNodeMedianDurs;
            default:
                return originalNodeTotalDurs;
            }
        }
    }
    
    private IntDoubleMap getArcWeightMap(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return originalArcTotalFreqs;
            case CASES:
                return originalArcCaseFreqs;                
            case MEAN:
                return originalArcMeanFreqs;
            case MIN:
                return originalArcMinFreqs;
            case MAX:
                return originalArcMaxFreqs;
            case MEDIAN:
                return originalArcMedianFreqs;
            default:
                return originalArcTotalFreqs;
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return originalArcTotalDurs;
            case MEAN:
                return originalArcMeanDurs;
            case MIN:
                return originalArcMinDurs;
            case MAX:
                return originalArcMaxDurs;
            case MEDIAN:
                return originalArcMedianDurs;
            default:
                return originalArcTotalDurs;
            }
        }
    }
    
    ///////////////////////////// FILTER ////////////////////////////////////////
    
    // The sorted nodes are sequenced: A (disconnected) B (connected) C (disconnected) D (connected, weight changes a lot) E (connected)
    // Disconnected means removing the node would make the graph disconnected, the same for connected. 
    // The batches would be: {A,B}, {C,D,E} 
    public void buildSubGraphs(IndexableAttribute sortedAttribute, MeasureType newWeightType, MeasureAggregation newWeightAggregation, 
            boolean newNodeInverted, boolean newArcInverted) {
        System.out.println("Total Number of nodes: " + this.getNodes().size());
        System.out.println("Total Number of arcs: " + this.getArcs().size());
        
        long timer = System.currentTimeMillis();
        
        boolean buildSubgraphs = false;
        boolean sortNodesArcs = false;
        
        if (subGraphs.isEmpty()) {
            sortNodesArcs = true;
        }
        else if (subGraphsSortedAttribute != sortedAttribute) {
            sortNodesArcs = true;
        }
        else if (weightType != newWeightType || weightAggregation != newWeightAggregation) {
            nodeWeightsForGraphStructure = getNodeWeightMap(weightType, weightAggregation);
            arcWeightsForGraphStructure = getArcWeightMap(weightType, weightAggregation);
            sortNodesArcs = true;
        }
        else if (nodeInverted != newNodeInverted || arcInverted != newArcInverted) {
            buildSubgraphs = true;
        }
        
        subGraphsSortedAttribute = sortedAttribute;
        weightType = newWeightType;
        weightAggregation = newWeightAggregation;
        nodeInverted = newNodeInverted;
        arcInverted = newArcInverted;
        
        if (!sortNodesArcs && !buildSubgraphs) {
            return;
        }
        
        // Start building sub-graphs
        if (sortNodesArcs) {
            nodeWeightsForGraphStructure = getNodeWeightMap(weightType, weightAggregation);
            arcWeightsForGraphStructure = getArcWeightMap(weightType, weightAggregation);
            sortNodesAndArcs();
        }
        
        // Select nodes
        //MutableList<MutableIntList> removableBins = buildRemovaleNodes();
        MutableList<MutableIntList> removableBins = buildRemovaleNodesByEvenBinning();
        
        // Create sub-graphs
        subGraphs.clear();
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, getNodeBitMask(), getArcBitMask());
        subGraphs.add(nodeBasedGraph);        
        for (IntList b : removableBins) {
            nodeBasedGraph = new NodeBasedGraph(this, nodeBasedGraph.cloneNodeBitMask(), nodeBasedGraph.cloneArcBitMask());
            for (int node : b.toArray()) {
                nodeBasedGraph.markRemoveNode(node);
            }
            subGraphs.add(nodeBasedGraph);
        }
        
        //System.out.println("Build all node slider graphs: " + (System.currentTimeMillis() - timer) + " ms.");
        
        // Build arc-based graphs from the smallest one first
        //timer = System.currentTimeMillis();
        NodeBasedGraph preGraph = null;
        for (FilteredGraph nodeGraph: subGraphs.toReversed()) {
            ((NodeBasedGraph)nodeGraph).buildSubGraphs(preGraph, arcInverted);
            preGraph = (NodeBasedGraph)nodeGraph;
        }
        
        System.out.println("Build all graphs: " + (System.currentTimeMillis() - timer) + " ms.");
    }  
    
    // This method only builds bins of nodes based on one single connected node
    // (i.e. the node that after removing them the graph remains connected).
    private MutableList<MutableIntList> buildRemovaleNodes() {
        MutableIntList sortedNodes = (!nodeInverted ? getSortedNodes().toList() : getSortedNodes().toReversed().toList()) ;
        sortedNodes.remove(getSourceNode());
        sortedNodes.remove(getSinkNode());
        //sortedNodes.removeAll(getBackboneNodes());
        
        // Create bins of removable nodes: nodes in a bin have close weight measures and the graph is 
        // still connected after removing them altogether.
        MutableList<MutableIntList> removableBins = Lists.mutable.empty();
        MutableIntList currentBin = IntLists.mutable.empty();
        MutableIntList disconnectNodes = IntLists.mutable.empty();
        
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, getNodeBitMask(), getArcBitMask());  
        for (int node : sortedNodes.toArray()) {
            nodeBasedGraph.markRemoveNode(node);
            if (nodeBasedGraph.isConnected()) {
                currentBin.addAll(disconnectNodes);
                currentBin.add(node);
                removableBins.add(currentBin);
                
                disconnectNodes.clear();
                currentBin = IntLists.mutable.empty(); 
                if (nodeBasedGraph.isPerfectSequence()) {
                    break;
                }
            }
            else {
                disconnectNodes.add(node); // collect these nodes, where to add them is determined when encountering a connected node.
            }
        }
        
        return removableBins;
    }
    
    private MutableList<MutableIntList> buildRemovaleNodesByEvenBinning() {
        MutableIntList sortedNodes = (!nodeInverted ? getSortedNodes().toList() : getSortedNodes().toReversed().toList()) ;
        sortedNodes.remove(getSourceNode());
        sortedNodes.remove(getSinkNode());
        
        // Create bins of removable nodes: nodes in a bin have close weight measures and the graph is 
        // still connected after removing them altogether.
        MutableList<MutableIntList> removableBins = Lists.mutable.empty();
        MutableIntList currentBin = IntLists.mutable.empty();
        MutableIntSet disconnectNodes = IntSets.mutable.empty();
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, getNodeBitMask(), getArcBitMask());  
        
        final int BIN_SIZE = getBinSize();
        
        for (int node : sortedNodes.toArray()) {
            nodeBasedGraph.markRemoveNode(node);
            if (nodeBasedGraph.isConnected()) {
                currentBin.addAll(disconnectNodes);
                currentBin.add(node);
                
                boolean isPerfectSequence = nodeBasedGraph.isPerfectSequence();
                
                if (currentBin.size() >= BIN_SIZE || isPerfectSequence) {
                    removableBins.add(currentBin);
                    disconnectNodes.clear();
                    currentBin = IntLists.mutable.empty(); 
                    
                    if (isPerfectSequence) {
                        break;
                    }
                }
                
            }
            else {
                disconnectNodes.add(node); // collect these nodes, where to add them is determined when encountering a connected node.
            }
        }
        
        return removableBins;
    }
    
    private int getBinSize() {
        int numberOfNodes = getNodes().size();
        final int STD_BIN_SIZE = 40;
        int k = numberOfNodes/STD_BIN_SIZE;
        int kUpperBound = k*STD_BIN_SIZE;
        return (numberOfNodes == kUpperBound ? k : k+1);
    }
    
    public ListIterable<FilteredGraph> getSubGraphs() {
        return subGraphs;
    }
    
    // Select an AttributeGraph based on node and arc thresholds set on the UI
    public FilteredGraph filter(double nodeThreshold, double arcThreshold) {
        long numberOfNodes = Math.round(nodeThreshold*this.getNodes().size());
        FilteredGraph nodeBasedGraph = subGraphs.getLast();
        for (int i=0;i<subGraphs.size();i++) {
            if (subGraphs.get(i).getNodes().size() <= numberOfNodes) {
                nodeBasedGraph = subGraphs.get(i);
                System.out.println("The node slider graph at level " + i + "/" + subGraphs.size() + " is selected.");
                break;
            }
        }
        
        ListIterable<FilteredGraph> arcSubGraphs = nodeBasedGraph.getSubGraphs();
        long numberOfArcs = Math.round(arcThreshold*nodeBasedGraph.getArcs().size());
        FilteredGraph arcBasedGraph = arcSubGraphs.getLast();
        for (int i=0;i<arcSubGraphs.size();i++) {
            if (arcSubGraphs.get(i).getArcs().size() <= numberOfArcs) {
                arcBasedGraph = arcSubGraphs.get(i);
                System.out.println("The arc slider graph at level " + i + "/" + arcSubGraphs.size() + " is selected.");
                break;
            }
        }
        
        return arcBasedGraph;
    }
    
    
    private void sortNodesAndArcs() {
        // The ordering must be deterministic
        MutableSortedSet<Integer> tempSortedNodes = SortedSets.mutable.of(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int freqCompare = Integer.compare((int)nodeWeightsForGraphStructure.get(o1), (int)nodeWeightsForGraphStructure.get(o2));
                return (freqCompare == 0) ? Integer.compare(o1, o2) : freqCompare;
            }
        });
        
        // The ordering must be deterministic
        MutableSortedSet<Integer> tempSortedArcs = SortedSets.mutable.of(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int freqCompare = Integer.compare((int)arcWeightsForGraphStructure.get(o1), (int)arcWeightsForGraphStructure.get(o2));
                return (freqCompare == 0) ? Integer.compare(o1, o2) : freqCompare;
            }
        });
        
        graphNodes.forEach(node -> tempSortedNodes.add(node));
        graphArcs.forEach(arc -> tempSortedArcs.add(arc));
        sortedNodes = tempSortedNodes.collectInt(node -> node);
        sortedArcs = tempSortedArcs.collectInt(arc -> arc);
    }
    
    public IntList getSortedNodes() {
        return sortedNodes.toImmutable();
    }

    public IntList getSortedArcs() {
        return sortedArcs.toImmutable();
    }
    

    
}
