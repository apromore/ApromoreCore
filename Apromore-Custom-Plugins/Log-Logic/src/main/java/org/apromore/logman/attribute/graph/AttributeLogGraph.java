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
import java.util.Comparator;

import org.apromore.logman.attribute.AttributeMatrixGraph;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.log.AttributeLog;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.IntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.primitive.IntDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;

/**
 * AttributeLogGraph represents a graph created from an AttributeLog. It is a subgraph of the AttributeMatrixGraph defined
 * by the attribute. This makes it a compact graph since there are no new Node or Arc objects in memory.
 * 
 * Note that each AttributeTrace is also a small subgraph of AttributeMatrixGraph and the AttributeLogGraph is 
 * actually the merge of all AttributeTrace graph.

 * An AttributeLogGraph is a graph image of an active parts of an AttributeLog (not the original parts). AttributeLogGraph 
 * does not have an original part and then an active part filtered from the original part like AttributeLog/ALog.
 * This is because the active and original graphs of an AttributeLog can be totally different in terms of nodes, edges and 
 * the weights of nodes/edges.
 * 
 * However, an AttributeLogGraph can be filtered on nodes and arcs to create filtered graph. This is filtering at the graph level,
 * not at the log level.
 * The node and arc bitset uses nodes and arcs on the AttributeMatrixGraph defined by the attribute.
 * Node bitset and arc bitset together determine a filtered AttributeLogGraph.
 * 
 * There are different types of weights for nodes and arcs: total and case-based (min, max, mean and median on the population
 * collected from each case). 
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeLogGraph {
    private AttributeLog attLog;
    private AttributeMatrixGraph attMatrixGraph;
    
    private MutableIntSet originalNodes = IntSets.mutable.empty();
    private MutableIntSet originalArcs = IntSets.mutable.empty();
    
    private BitSet nodeBitMask; //true indicate that the node at the index i is active
    private BitSet arcBitMask; //true indicate that the arc at the index i is active
    
    // These maps are to boost the retrieval of incoming/outgoing nodes/arcs of nodes
    private MutableIntObjectMap<MutableIntSet> incomingOriginalArcs = IntObjectMaps.mutable.empty(); //node => set of arcs
    private MutableIntObjectMap<MutableIntSet> outgoingOriginalArcs = IntObjectMaps.mutable.empty(); //node => set of arcs
    
    // Node frequency weights
    private MutableIntDoubleMap originalNodeTotalFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeCaseFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMinFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMaxFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMedianFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMeanFreqs = IntDoubleMaps.mutable.empty();
    
    // Node duration weights
    private MutableIntDoubleMap originalNodeTotalDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMinDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMaxDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMedianDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalNodeMeanDurs = IntDoubleMaps.mutable.empty();   
    
    private MutableIntDoubleMap originalArcTotalFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcCaseFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMinFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMaxFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalMedianFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalMeanFreqs = IntDoubleMaps.mutable.empty();
    
    private MutableIntDoubleMap originalArcTotalDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMinDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMaxDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMedianDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap originalArcMeanDurs = IntDoubleMaps.mutable.empty();  
    
    // Sub-graphs and related data and parameters
    private MutableList<AttributeGraph> subGraphs = Lists.mutable.empty();
    private IntList sortedNodes;
    private IntList sortedArcs;
    private IndexableAttribute subGraphsAttribute;
    private MeasureType weightType;
    private MeasureAggregation weightAggregation;
    private IntDoubleMap nodeWeightsForGraphStructure;
    private IntDoubleMap arcWeightsForGraphStructure;
    private boolean nodeInverted = false; 
    private boolean arcInverted = false;
    
    public AttributeLogGraph(AttributeLog attLog) {
        this.attLog = attLog;
        reset();
    }
    
    public void reset() {
        this.attMatrixGraph = attLog.getAttribute().getMatrixGraph();
        int nodeMaxSize = attLog.getAttribute().getMatrixGraph().getDimension();
        nodeBitMask = new BitSet(nodeMaxSize);
        arcBitMask = new BitSet(nodeMaxSize*nodeMaxSize);
        
        originalNodes.clear();
        originalArcs.clear();
        incomingOriginalArcs.clear();
        outgoingOriginalArcs.clear();
        
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
        originalMedianFreqs.clear();
        originalMeanFreqs.clear();
                 
        originalArcTotalDurs.clear();
        originalArcMinDurs.clear();
        originalArcMaxDurs.clear();
        originalArcMedianDurs.clear();
        originalArcMeanDurs.clear();
        
        subGraphs.clear();
    }
    
    
    //////////////////////////// Nodes and arcs creation ///////////////////////////////////
    
    // The caller must make sure that source and target nodes of this arc have been added 
    // This method must be followed by other methods to update weights and the subgraphs
    public void addNewNode(int node) {
        originalNodes.add(node);
        nodeBitMask.set(node);
        if (!incomingOriginalArcs.containsKey(node)) incomingOriginalArcs.put(node, IntSets.mutable.empty());
        if (!outgoingOriginalArcs.containsKey(node)) outgoingOriginalArcs.put(node, IntSets.mutable.empty());
    }
    
    // The caller must make sure that source and target nodes of this arc have been added 
    // This method must be followed by other methods to update weights and the subgraphs
    public void addNewNodes(IntSet nodes) {
        originalNodes.addAll(nodes);
        nodes.forEach(node -> {
            nodeBitMask.set(node);
            if (!incomingOriginalArcs.containsKey(node)) incomingOriginalArcs.put(node, IntSets.mutable.empty());
            if (!outgoingOriginalArcs.containsKey(node)) outgoingOriginalArcs.put(node, IntSets.mutable.empty());            
        });
    }
    
    // The caller must make sure that source and target nodes of this arc have been added 
    // This method must be followed by other methods to update weights and the subgraphs
    public void addNewArc(int arc) {
        originalArcs.add(arc);
        arcBitMask.set(arc);
        incomingOriginalArcs.get(getTarget(arc)).add(arc);
        outgoingOriginalArcs.get(getSource(arc)).add(arc);
    }

    // The caller must make sure that source and target nodes of this arc have been added 
    // This method must be followed by other methods to update weights and the subgraphs
    public void addNewArcs(IntSet arcs) {
        originalArcs.addAll(arcs);
        arcs.forEach(arc -> {
            arcBitMask.set(arc);
            incomingOriginalArcs.get(getTarget(arc)).add(arc);
            outgoingOriginalArcs.get(getSource(arc)).add(arc);            
        });
    }
    
    ////////////////////////////  Node measure ///////////////////////////////////
    
    public void incrementNodeTotalFrequency(int node, long nodeCount) {
        originalNodeTotalFreqs.put(node, originalNodeTotalFreqs.getIfAbsentPut(node, 0) + nodeCount);
    }
    
    public void incrementNodeCaseFrequency(int node, long nodeCount) {
        originalNodeCaseFreqs.put(node, originalNodeCaseFreqs.getIfAbsentPut(node, 0) + (nodeCount>0 ? 1 : 0));
    }
    
    public void updateNodeMinFrequency(int node, long nodeCount) {
        originalNodeMinFreqs.put(node, Math.min(originalNodeMinFreqs.getIfAbsentPut(node, Double.MAX_VALUE), nodeCount));
    }
    
    public void updateNodeMaxFrequency(int node, long nodeCount) {
        originalNodeMaxFreqs.put(node, Math.max(originalNodeMaxFreqs.getIfAbsentPut(node, 0), nodeCount));
    }
    
    public void incrementNodeTotalDuration(int node, long nodeDuration) {
        originalNodeTotalDurs.put(node, originalNodeTotalDurs.getIfAbsentPut(node, 0) + nodeDuration);
    }
    
    public void updateNodeMinDuration(int node, long nodeDuration) {
        originalNodeMinDurs.put(node, Math.min(originalNodeMinDurs.getIfAbsentPut(node, Double.MAX_VALUE), nodeDuration));
    }
    
    public void updateNodeMaxDuration(int node, long nodeDuration) {
        originalNodeMaxDurs.put(node, Math.max(originalNodeMaxDurs.getIfAbsentPut(node, 0), nodeDuration));
    }
    
    
    //////////////////////////// Arc measure ///////////////////////////////////
    
    public void incrementArcTotalFrequency(int arc, long arcCount) {
        originalArcTotalFreqs.put(arc, originalArcTotalFreqs.getIfAbsentPut(arc, 0) + arcCount);
    }
    
    public void incrementArcCaseFrequency(int arc, long arcCount) {
        originalArcCaseFreqs.put(arc, originalArcCaseFreqs.getIfAbsentPut(arc, 0) + (arcCount>0 ? 1 : 0));
    }
    
    public void updateArcMinFrequency(int arc, long arcCount) {
        originalArcMinFreqs.put(arc, Math.min(originalArcMinFreqs.getIfAbsentPut(arc, Double.MAX_VALUE), arcCount));
    }
    
    public void updateArcMaxFrequency(int arc, long arcCount) {
        originalArcMaxFreqs.put(arc, Math.max(originalArcMaxFreqs.getIfAbsentPut(arc, 0), arcCount));
    }
    
    public void incrementArcTotalDuration(int arc, long arcDuration) {
        originalArcTotalDurs.put(arc, originalArcTotalDurs.getIfAbsentPut(arc, 0) + arcDuration);
    }
    
    public void updateArcMinDuration(int arc, long arcDuration) {
        originalArcMinDurs.put(arc, Math.min(originalArcMinDurs.getIfAbsentPut(arc, Double.MAX_VALUE), arcDuration));
    }
    
    public void updateArcMaxDuration(int arc, long arcDuration) {
        originalArcMaxDurs.put(arc, Math.max(originalArcMaxDurs.getIfAbsentPut(arc, 0), arcDuration));
    }
    
    // Used to update other data after the log and graph has been fully updated. 
    public void updateFinalWeights() {
        originalNodes.forEach(node -> {
            originalNodeMeanFreqs.put(node, originalNodeTotalFreqs.get(node)/attLog.getTraces().size());
            originalNodeMedianFreqs.put(node, originalNodeMeanDurs.get(node));
            originalNodeMeanDurs.put(node, originalNodeTotalDurs.get(node)/originalNodeTotalFreqs.get(node));
            originalNodeMedianDurs.put(node, originalNodeMeanDurs.get(node));
            if (originalNodeCaseFreqs.get(node) != attLog.getTraces().size()) {// there's a case not containing the node
                originalNodeMinFreqs.put(node, 0);
            }
        });
        
        originalArcs.forEach(arc -> {
            originalMeanFreqs.put(arc, originalArcTotalFreqs.getIfAbsentPut(arc, 0)/attLog.getTraces().size());
            originalMedianFreqs.put(arc, originalMeanFreqs.get(arc));  
            originalArcMeanDurs.put(arc, originalArcTotalDurs.get(arc)/originalArcTotalFreqs.get(arc));
            originalArcMedianDurs.put(arc, originalArcMeanDurs.get(arc));  
            if (originalArcCaseFreqs.get(arc) != attLog.getTraces().size()) { // there's a case not containing the arc
                originalArcMinFreqs.put(arc, 0);
            }
        });
    }
    
    // The sorted nodes are sequenced: A (disconnected) B (connected) C (disconnected) D (connected, weight changes a lot) E (connected)
    // Disconnected means removing the node would make the graph disconnected, the same for connected. 
    // The batches would be: {A,B}, {C,D,E} 
    public void buildSubGraphs(IndexableAttribute attribute, MeasureType newWeightType, MeasureAggregation newWeightAggregation, 
            boolean newNodeInverted, boolean newArcInverted) {
        System.out.println("Total Number of nodes: " + this.getNodes().size());
        System.out.println("Total Number of arcs: " + this.getArcs().size());
        
        long timer = System.currentTimeMillis();
        
        boolean buildSubgraphs = false;
        boolean sortNodesArcs = false;
        
        if (subGraphs.isEmpty()) {
            sortNodesArcs = true;
        }
        else if (subGraphsAttribute != attribute) {
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
        
        subGraphsAttribute = attribute;
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
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, (BitSet)nodeBitMask.clone(), (BitSet)arcBitMask.clone());
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
        for (AttributeGraph nodeGraph: subGraphs.toReversed()) {
            ((NodeBasedGraph)nodeGraph).buildSubGraphs(preGraph, arcInverted);
            preGraph = (NodeBasedGraph)nodeGraph;
        }
        
        System.out.println("Build all graphs: " + (System.currentTimeMillis() - timer) + " ms.");
        
//        System.out.println("Build all graphs: " + (System.currentTimeMillis() - timer) + " ms.");
//        System.out.println("Number of node slider graphs: " + subGraphs.size());
//        for (int i=0;i<subGraphs.size();i++) {
//            System.out.println("Number of arc slider graphs at node slider level " + i + ": " + subGraphs.get(i).getSubGraphs().size());
//        }
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
        
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, (BitSet)nodeBitMask.clone(), (BitSet)arcBitMask.clone());  
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
    
    private IntSet getBackboneNodes() {
        MutableIntList reversedSortedNodes = (nodeInverted ? getSortedNodes().toList() : getSortedNodes().toReversed().toList()) ;
        reversedSortedNodes.remove(getSourceNode());
        reversedSortedNodes.remove(getSinkNode());
        IntList reversedSortedArcs = (arcInverted ? getSortedArcs().toList() : getSortedArcs().toReversed().toList()) ;
        
        MutableIntList topNodes = IntLists.mutable.empty();
        int top20PercentNode = getNumberOfImportantNodes(reversedSortedNodes.size());
        reversedSortedNodes.forEachWithIndex((node,index) -> {if (index <= top20PercentNode) topNodes.add(node);});
        
        MutableIntList topArcs = IntLists.mutable.empty();
        int top20PercentArc = reversedSortedArcs.size()/5;
        reversedSortedArcs.forEachWithIndex((arc,index) -> {if (index <= top20PercentArc) topArcs.add(arc);});
        
        MutableIntSet backBoneNodes = IntSets.mutable.empty();
        topArcs.forEach(arc -> {
            int source = getSource(arc);
            int target = getTarget(arc);
            if (topNodes.contains(source)) backBoneNodes.add(source);
            if (topNodes.contains(target)) backBoneNodes.add(target);
        });
        
        return backBoneNodes;
    }
    
    private int getNumberOfImportantNodes(int totalNumberOfNodes) {
        if (totalNumberOfNodes <= 100) {
            return totalNumberOfNodes/5;
        }
        else if (totalNumberOfNodes <= 200) {
            return totalNumberOfNodes/10;
        }
        else if (totalNumberOfNodes <= 300) {
            return totalNumberOfNodes/15;
        }
        else if (totalNumberOfNodes <= 400) {
            return totalNumberOfNodes/20;
        }
        else if (totalNumberOfNodes <= 500) {
            return totalNumberOfNodes/25;
        }
        else {
            return totalNumberOfNodes/30;
        }
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
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, (BitSet)nodeBitMask.clone(), (BitSet)arcBitMask.clone());  
        
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
    
    // This method builds bins of nodes by grouping nodes of the close weight measures
    // in order to reduce the number of node-based graphs in a sensible way.
    private MutableList<MutableIntList> buildRemovaleNodesByGroupings() {
        MutableIntList sortedNodes = (!nodeInverted ? getSortedNodes().toList() : getSortedNodes().toReversed().toList()) ;
        sortedNodes.remove(getSourceNode());
        sortedNodes.remove(getSinkNode());
        sortedNodes.removeAll(getBackboneNodes());
        
        // Create bins of removable nodes: nodes in a bin have close weight measures and the graph is 
        // still connected after removing them altogether.
        MutableList<MutableIntList> removableBins = Lists.mutable.empty();
        MutableIntList currentBin = IntLists.mutable.empty();
        MutableIntSet disconnectNodes = IntSets.mutable.empty();
        NodeBasedGraph nodeBasedGraph = new NodeBasedGraph(this, (BitSet)nodeBitMask.clone(), (BitSet)arcBitMask.clone());  
        double currentBinMinWeight = 0, currentConnectNodeWeight = 0;
        
        for (int node : sortedNodes.toArray()) {
            nodeBasedGraph.markRemoveNode(node);
            if (nodeBasedGraph.isConnected()) {
                currentConnectNodeWeight = nodeWeightsForGraphStructure.get(node);
                
                // Start new bin if the node weight changes significantly
                if (currentBinMinWeight != 0 && (currentConnectNodeWeight - currentBinMinWeight)/currentBinMinWeight > 0.2) {
                    removableBins.add(currentBin);
                    currentBin = IntLists.mutable.empty(); 
                }
                
                currentBin.add(node);
                currentBin.addAll(disconnectNodes);
                disconnectNodes.clear();
                currentBinMinWeight = currentConnectNodeWeight;
                
                if (nodeBasedGraph.isPerfectSequence()) {
                    break;
                }
            }
            else {
                disconnectNodes.add(node); // collect these nodes, where to add them is determined when encountering a connected node.
            }
        }
        if (!currentBin.isEmpty()) { // add the last bin  
            removableBins.add(currentBin);
        }
        
        return removableBins;
    }
    
    public ListIterable<AttributeGraph> getSubGraphs() {
        return subGraphs;
    }
    
    // Select an AttributeGraph based on node and arc thresholds set on the UI
    public AttributeGraph filter(double nodeThreshold, double arcThreshold) {
        long numberOfNodes = Math.round(nodeThreshold*this.getNodes().size());
        AttributeGraph nodeBasedGraph = subGraphs.getLast();
        for (int i=0;i<subGraphs.size();i++) {
            if (subGraphs.get(i).getNodes().size() <= numberOfNodes) {
                nodeBasedGraph = subGraphs.get(i);
                System.out.println("The node slider graph at level " + i + "/" + subGraphs.size() + " is selected.");
                break;
            }
        }
        
        ListIterable<AttributeGraph> arcSubGraphs = nodeBasedGraph.getSubGraphs();
        long numberOfArcs = Math.round(arcThreshold*nodeBasedGraph.getArcs().size());
        AttributeGraph arcBasedGraph = arcSubGraphs.getLast();
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
        
        originalNodes.forEach(node -> tempSortedNodes.add(node));
        originalArcs.forEach(arc -> tempSortedArcs.add(arc));
        sortedNodes = tempSortedNodes.collectInt(node -> node);
        sortedArcs = tempSortedArcs.collectInt(arc -> arc);
        
        //Sort arcs in incoming and outgoing arcs
//        outgoingOriginalArcs.forEachKeyValue((node,nodeArcs) -> {
//            tempSortedArcs.clear();
//            outgoingOriginalArcs.get(node).forEach(arc -> tempSortedArcs.add(arc));
//            outgoingOriginalSortedArcs.put(node, tempSortedArcs.collectInt(arc -> arc));
//        });
//        
//        incomingOriginalArcs.forEachKeyValue((node,nodeArcs) -> {
//            tempSortedArcs.clear();
//            incomingOriginalArcs.get(node).forEach(arc -> tempSortedArcs.add(arc));
//            incomingOriginalSortedArcs.put(node, tempSortedArcs.collectInt(arc -> arc));
//        });
    }
    
   

    //////////////////////////////// NODES OPERATIONS //////////////////////////////
    
    public IntSet getNodes() {
        return IntSets.immutable.ofAll(nodeBitMask.stream());
    }
    
    public BitSet cloneNodeBitMask() {
        return (BitSet)nodeBitMask.clone();
    }
    
    public IntSet getOriginalNodes() {
        return originalNodes.toImmutable();
    }   
    
    public int getSourceNode() {
        return attLog.getAttribute().getMatrixGraph().getSource();
    }
    
    public int getSinkNode() {
        return attLog.getAttribute().getMatrixGraph().getSink();
    }
    
    // Return -1 if not found
    public int getNodeFromName(String nodeName) {
        return attLog.getValueFromString(nodeName);
    }
    
    public String getNodeName(int node) {
        return attLog.getStringFromValue(node);
    }
    
    private boolean validNode(int node) {
        return originalNodes.contains(node);
    }
    
    public boolean containOriginalNode(int node) {
        return originalNodes.contains(node);
    }
    
    public boolean containNode(int node) {
        return nodeBitMask.get(node);
    }
    
    public boolean removeNode(int node) throws InvalidNodeException, InvalidArcException {
        if (containNode(node)) {
            nodeBitMask.clear(node);
            getOutgoingArcs(node).forEach(arc -> removeArc(arc));
            getIncomingArcs(node).forEach(arc -> removeArc(arc));
            return true;
        }
        else {
            throw new InvalidNodeException("Remove an invalid node: node = " + node);
        }
    }
    
    // Add a node in the original graph back to the current graph
    public boolean addNode(int node) throws InvalidNodeException {
        if (containOriginalNode(node) && !containNode(node)) {
            nodeBitMask.set(node);
            
            //Re-add the original arcs if the adjacent nodes are contained
            for (int arc : outgoingOriginalArcs.get(node).toArray()) {
                if (containNode(getTarget(arc))) {
                    //outgoingArcs.get(node).add(arc);
                    //arcStatus.set(arc);
                    setArcStatus(arc, getSource(arc), getTarget(arc));
                }
            }
            
            for (int arc : incomingOriginalArcs.get(node).toArray()) {
                if (containNode(getSource(arc))) {
                    //incomingArcs.get(node).add(arc);
                    //arcStatus.set(arc);
                    setArcStatus(arc, getSource(arc), getTarget(arc));
                }
            }
            
            return true;
        }
        else {
            throw new InvalidNodeException("Add an invalid node: node = " + node);
        }
    }
    
    public IntSet getIncomingOriginalArcs(int node) {
        return incomingOriginalArcs.getIfAbsent(node, IntSets.mutable::empty);
    }   
    
    public IntSet getOutgoingOriginalArcs(int node) {
        return outgoingOriginalArcs.getIfAbsent(node, IntSets.mutable::empty);
    }  
    
//    public IntList getIncomingOriginalSortedArcs(int node) {
//        return (containOriginalNode(node) ? incomingOriginalSortedArcs.get(node) : IntLists.immutable.empty());
//    }   
//    
//    public IntList getOutgoingOriginalSortedArcs(int node) {
//        return (containOriginalNode(node) ? outgoingOriginalSortedArcs.get(node) : IntLists.immutable.empty());
//    } 
    
    public IntSet getIncomingArcs(int node) {
        return IntSets.immutable.ofAll(arcBitMask.stream()).select(arc -> getTarget(arc) == node);
    }
    
    public IntSet getOutgoingArcs(int node) {
        return IntSets.immutable.ofAll(arcBitMask.stream()).select(arc -> getSource(arc) == node);
    } 
    
    public IntSet getOutgoingOriginalArcsWithoutSelfLoops(int node) {
        return getOutgoingOriginalArcs(node).select(arc -> getTarget(arc) != node);
    }
    
    public IntSet getIncomingOriginalArcsWithoutSelfLoops(int node) {
        return getIncomingOriginalArcs(node).select(arc -> getSource(arc) != node);
    }
    
    public IntList getSortedNodes() {
        return sortedNodes.toImmutable();
    }
    
    /////////////////////////// ARCS OPERATIONS ///////////////////////////////
    
    public boolean containArc(int arc) {
        return arcBitMask.get(arc);
    }
    
    public boolean containOriginalArc(int arc) {
        return originalArcs.contains(arc);
    }
    
    public int getOriginalArc(int source, int target) throws InvalidArcException {
        if (validNode(source) && validNode(target)) {
            int arc = attMatrixGraph.getArc(source, target);
            if (containOriginalArc(arc)) {
                return arc;
            }
            else {
                throw new InvalidArcException("The graph contains no arcs with source = " + source + ", target = " + target);
            }
        }
        else {
            throw new InvalidArcException("Invalid arc with source = " + source + ", target = " + target);
        }
    }
    
    public int getOriginalArc(String sourceName, String targetName) throws InvalidArcException {
        return getOriginalArc(getNodeFromName(sourceName), getNodeFromName(targetName));
    }
    
    public int getArc(int source, int target) throws InvalidArcException {
        if (validNode(source) && validNode(target)) {
            int arc = attMatrixGraph.getArc(source, target);
            if (containArc(arc)) {
                return arc;
            }
            else {
                throw new InvalidArcException("The graph contains no arcs with source = " + source + ", target = " + target);
            }
        }
        else {
            throw new InvalidArcException("Invalid arc with source = " + source + ", target = " + target);
        }
    }
    
    public int getArc(String sourceName, String targetName) throws InvalidArcException {
        return getArc(getNodeFromName(sourceName), getNodeFromName(targetName));
    }
    
    public String getArcName(int arc) {
        int source = getSource(arc);
        int target = getTarget(arc);
        if (validNode(source) && validNode(target)) {
            return getNodeName(source) + "->" + getNodeName(target);
        }
        else {
            return "";
        }
    }
    
    public IntSet getArcs() {
        return originalArcs.select(arc -> arcBitMask.get(arc));
    }
    
    public BitSet cloneArcBitMask() {
        return (BitSet)arcBitMask.clone();
    }
    
    private void setArcStatus(int arc, int source, int target) {
        arcBitMask.set(arc);
//        arcStatusTargetList.get(target).set(source);
    }
    
    private void clearArcStatus(int arc, int source, int target) {
        arcBitMask.clear(arc);
    }
    
    public IntSet getOriginalArcs() {
        return originalArcs;
    }
    
    public int getSource(int arc) {
        return attMatrixGraph.getSource(arc);
    }
    
    public int getTarget(int arc) {
        return attMatrixGraph.getTarget(arc);
    }
    
    // Only set the arc status
    public void addArc(int arc) throws InvalidArcException {
        if (containOriginalArc(arc) && !containArc(arc)) {
            int source = getSource(arc);
            int target = getTarget(arc);
            setArcStatus(arc, source, target);
        }
        else {
            throw new InvalidArcException("Add invalid arc to add: arc = " + arc);
        }
    }
    
    // Only set the arc status
    public void removeArc(int arc) {
        if (containArc(arc)) {
            int source = getSource(arc);
            int target = getTarget(arc);
            clearArcStatus(arc, source, target);
        }
    }
    
    public IntList getSortedArcs() {
        return sortedArcs.toImmutable();
    }
    
    ///////////////////////////// MEAUSURES ////////////////////////////////////////
    
    public double getNodeWeight(String nodeName, MeasureType type, MeasureAggregation aggregation) {
        int node = this.getNodeFromName(nodeName);
        return (node >= 0) ? this.getNodeWeight(node, type, aggregation) : 0;
    }
    
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation) {
        return getNodeWeightMap(type, aggregation).get(node);
    }
    
    public double getNodeStructuralWeight(int node) {
        return nodeWeightsForGraphStructure.getIfAbsent(node, 0);
    }
    
    public IntDoubleMap getNodeWeightMap(MeasureType type, MeasureAggregation aggregation) {
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
                return originalNodeMeanFreqs;
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
                return originalNodeMeanDurs;
            default:
                return originalNodeTotalDurs;
            }
        }
    }
    
    public IntDoubleMap getArcWeightMap(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return originalArcTotalFreqs;
            case CASES:
                return originalArcCaseFreqs;                
            case MEAN:
                return originalMeanFreqs;
            case MIN:
                return originalArcMinFreqs;
            case MAX:
                return originalArcMaxFreqs;
            case MEDIAN:
                return originalMeanFreqs;
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
                return originalArcMeanDurs;
            default:
                return originalArcTotalDurs;
            }
        }
    }
    
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation) {
        return getArcWeightMap(type, aggregation).get(arc);
    }
    
    public double getArcStructuralWeight(int arc) {
        return arcWeightsForGraphStructure.getIfAbsent(arc, 0);
    }
    
}
