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

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.attribute.AttributeMatrixGraph;
import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.graph.filtering.NodeBasedGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.IntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.IntDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;

/**
 * AttributeLogGraph is a {@link WeightedAttributeGraph} for an {@link AttributeLog}.
 * Thus, AttributeLogGraph is a subgraph of an {@link AttributeMatrixGraph} (a base graph).
 * AttributeLogGraph is created from an AttributeLog by merging all the graphs of {@link AttributeTrace} in the log.
 * AttributeLogGraph can be filtered on nodes and arcs to create subgraphs.
 * 
 * @author Bruce Nguyen
 * 
 */
public class AttributeLogGraph extends WeightedAttributeGraph {
    private AttributeLog attLog;
    
    private MutableIntDoubleMap nodeTotalFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeCaseFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMinFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMaxFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMedianFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMeanFreqs = IntDoubleMaps.mutable.empty();
    
    private MutableIntDoubleMap nodeTotalDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMinDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMaxDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMedianDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMeanDurs = IntDoubleMaps.mutable.empty();

    private MutableIntDoubleMap nodeTotalCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMinCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMaxCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMedianCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap nodeMeanCosts = IntDoubleMaps.mutable.empty();
    
    private MutableIntDoubleMap arcTotalFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcCaseFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMinFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMaxFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMedianFreqs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMeanFreqs = IntDoubleMaps.mutable.empty();
    
    private MutableIntDoubleMap arcTotalDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMinDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMaxDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMedianDurs = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMeanDurs = IntDoubleMaps.mutable.empty();

    private MutableIntDoubleMap arcTotalCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMinCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMaxCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMedianCosts = IntDoubleMaps.mutable.empty();
    private MutableIntDoubleMap arcMeanCosts = IntDoubleMaps.mutable.empty();

    // Used for calculating median
    private MutableIntObjectMap<MutableDoubleList> nodeFreqs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> arcFreqs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> nodeCosts = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> arcCosts = IntObjectMaps.mutable.empty();

    // Collection of node and arc intervals
    private MutableIntObjectMap<MutableList<LongLongPair>> nodeIntervals = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableList<LongLongPair>> arcIntervals = IntObjectMaps.mutable.empty();
    
    private IntList sortedNodes;
    private IntList sortedArcs;
    
    // Sub-graphs and related data and parameters
    private MutableList<FilteredGraph> subGraphs = Lists.mutable.empty();
    private IntDoubleMap nodeWeightsForGraphStructure = nodeCaseFreqs;
    private IntDoubleMap arcWeightsForGraphStructure = arcCaseFreqs;
    private boolean nodeInverted = false;
    private boolean arcInverted = false;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeLogGraph.class.getCanonicalName());
    
    public AttributeLogGraph(AttributeLog attLog) {
        super(attLog.getAttribute());
        this.attLog = attLog;
    }
    
    @Override
    public void clear() {
        super.clear();
        
        nodeTotalFreqs.clear();
        nodeCaseFreqs.clear();
        nodeMinFreqs.clear();
        nodeMaxFreqs.clear();
        nodeMedianFreqs.clear();
        nodeMeanFreqs.clear();
                 
        nodeTotalDurs.clear();
        nodeMinDurs.clear();
        nodeMaxDurs.clear();
        nodeMedianDurs.clear();
        nodeMeanDurs.clear();

        nodeTotalCosts.clear();
        nodeMinCosts.clear();
        nodeMaxCosts.clear();
        nodeMedianCosts.clear();
        nodeMeanCosts.clear();        
                 
        arcTotalFreqs.clear();
        arcCaseFreqs.clear();
        arcMinFreqs.clear();
        arcMaxFreqs.clear();
        arcMedianFreqs.clear();
        arcMeanFreqs.clear();
                 
        arcTotalDurs.clear();
        arcMinDurs.clear();
        arcMaxDurs.clear();
        arcMedianDurs.clear();
        arcMeanDurs.clear();

        arcTotalCosts.clear();
        arcMinCosts.clear();
        arcMaxCosts.clear();
        arcMedianCosts.clear();
        arcMeanCosts.clear();
        
        nodeFreqs.clear();
        arcFreqs.clear();
        nodeIntervals.clear();
        arcIntervals.clear();
        nodeCosts.clear();
        arcCosts.clear();
        
        subGraphs.clear();
    }
    
    public void addTraceGraph(AttributeTraceGraph traceGraph) {
        addNodes(traceGraph.getNodes());
        addArcs(traceGraph.getArcs());
        traceGraph.getNodes().forEach(node -> updateNodeWeights(node, traceGraph));
        traceGraph.getArcs().forEach(arc -> updateArcWeights(arc, traceGraph));
    }
    
    private void addZeros(MutableDoubleList list, int num) {
    	for (int i=0; i<num; i++) {
    		list.add(0d);
    	}
    }
    
    private double getMedian(LongList longList, Median medianCalculator) {
        return longList.size() == 0 ? 0 : medianCalculator.evaluate(Arrays.stream(longList.toArray()).asDoubleStream().toArray());
    }

    private double getMedian(DoubleList doubleList, Median medianCalculator) {
        return doubleList.size() == 0 ? 0 : medianCalculator.evaluate(doubleList.toArray());
    }
    
    // Used to update other data after the log and graph has been fully updated.
    // This final update is needed for mean, median and min frequency
    public void finalUpdate() {
        CalendarModel cal = this.attLog.getCalendarModel();
        final int NUM_OF_TRACES = attLog.getTraces().size();
        Median medianCalculator = new Median();
        graphNodes.forEach(node -> {
            nodeMeanFreqs.put(node, nodeTotalFreqs.get(node)/attLog.getTraces().size());
            
            // Add the same number of zeros as the number of traces that a node doesn't occur
            if (nodeFreqs.get(node).size() < NUM_OF_TRACES) addZeros(nodeFreqs.get(node), NUM_OF_TRACES - nodeFreqs.get(node).size());
            nodeMedianFreqs.put(node, medianCalculator.evaluate(nodeFreqs.get(node).toArray()));
            
            // Fix the min node frequency
            if (nodeCaseFreqs.get(node) != NUM_OF_TRACES) nodeMinFreqs.put(node, 0);
            
            // Calculate node duration measures
            long[] starts = nodeIntervals.getIfAbsent(node, Lists.mutable::empty).collectLong(LongLongPair::getOne).toArray();
            long[] ends = nodeIntervals.getIfAbsent(node, Lists.mutable::empty).collectLong(LongLongPair::getTwo).toArray();
            LongList durations = LongLists.mutable.of(cal.getDuration(starts, ends));
            nodeTotalDurs.put(node, durations.sum());
            nodeMinDurs.put(node, durations.minIfEmpty(0) );
            nodeMaxDurs.put(node, durations.maxIfEmpty(0));
            nodeMeanDurs.put(node, nodeTotalDurs.get(node)/nodeTotalFreqs.get(node));
            nodeMedianDurs.put(node, getMedian(durations, medianCalculator));

            DoubleList costs = nodeCosts.get(node);
            nodeTotalCosts.put(node, costs.sum());
            nodeMinCosts.put(node, costs.minIfEmpty(0) );
            nodeMaxCosts.put(node, costs.maxIfEmpty(0));
            nodeMeanCosts.put(node, nodeTotalCosts.get(node)/nodeTotalFreqs.get(node));
            nodeMedianCosts.put(node, getMedian(costs, medianCalculator));
        });
        
        graphArcs.forEach(arc -> {
            arcMeanFreqs.put(arc, arcTotalFreqs.getIfAbsentPut(arc, 0)/attLog.getTraces().size());
            
            // Add the same number of zeros as the number of traces that a node doesn't occur
            if (arcFreqs.get(arc).size() < NUM_OF_TRACES) addZeros(arcFreqs.get(arc), NUM_OF_TRACES - arcFreqs.get(arc).size());
            arcMedianFreqs.put(arc, medianCalculator.evaluate(arcFreqs.get(arc).toArray()));
            
            // Fix the min arc frequency
            if (arcCaseFreqs.get(arc) != NUM_OF_TRACES) arcMinFreqs.put(arc, 0);
            
            // Calculate arc duration measures
            long[] starts = arcIntervals.getIfAbsent(arc, Lists.mutable::empty).collectLong(LongLongPair::getOne).toArray();
            long[] ends = arcIntervals.getIfAbsent(arc, Lists.mutable::empty).collectLong(LongLongPair::getTwo).toArray();
            LongList durations = LongLists.mutable.of(cal.getDuration(starts, ends));
            arcTotalDurs.put(arc, durations.sum());
            arcMinDurs.put(arc, durations.minIfEmpty(0));
            arcMaxDurs.put(arc, durations.maxIfEmpty(0));
            arcMeanDurs.put(arc, arcTotalDurs.get(arc)/arcTotalFreqs.get(arc));
            arcMedianDurs.put(arc, getMedian(durations, medianCalculator));

            DoubleList costs = arcCosts.get(arc);
            arcTotalCosts.put(arc, costs.sum());
            arcMinCosts.put(arc, costs.minIfEmpty(0) );
            arcMaxCosts.put(arc, costs.maxIfEmpty(0));
            arcMeanCosts.put(arc, arcTotalCosts.get(arc)/arcTotalFreqs.get(arc));
            arcMedianCosts.put(arc, getMedian(costs, medianCalculator));
        });
        
        sortedNodes = graphNodes.toList();
        sortedArcs = graphArcs.toList();
        
        // Release data structures storing median values
        nodeFreqs.clear();
        arcFreqs.clear();
        nodeIntervals.clear();
        arcIntervals.clear();
    }
    
    private void updateNodeWeights(int node, AttributeTraceGraph traceGraph) {
        incrementNodeTotalFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        incrementNodeCaseFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE));
        updateNodeMinFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        updateNodeMaxFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        collectNodeFrequency(node, traceGraph.getNodeWeight(node, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        collectNodeIntervals(node, traceGraph.getNodeIntervals(node));
        collectNodeCosts(node, traceGraph.getNodeCosts(node));
    }
    
    private void updateArcWeights(int arc, AttributeTraceGraph traceGraph) {
        incrementArcTotalFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        incrementArcCaseFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE));
        updateArcMinFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        updateArcMaxFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        collectArcFrequency(arc, traceGraph.getArcWeight(arc, MeasureType.FREQUENCY, MeasureAggregation.TOTAL, MeasureRelation.ABSOLUTE));
        collectArcIntervals(arc, traceGraph.getArcIntervals(arc));
        collectArcCosts(arc, traceGraph.getArcCosts(arc));
    }

    ////////////////////////////  Node measure ///////////////////////////////////
    
    private void incrementNodeTotalFrequency(int node, double nodeTotalCount) {
        nodeTotalFreqs.put(node, nodeTotalFreqs.getIfAbsentPut(node, 0) + nodeTotalCount);
    }
    
    private void incrementNodeCaseFrequency(int node, double nodeCaseCount) {
        nodeCaseFreqs.put(node, nodeCaseFreqs.getIfAbsentPut(node, 0) + (nodeCaseCount>0 ? 1 : 0));
    }
    
    private void updateNodeMinFrequency(int node, double nodeCount) {
        nodeMinFreqs.put(node, Math.min(nodeMinFreqs.getIfAbsentPut(node, Double.MAX_VALUE), nodeCount));
    }
    
    private void updateNodeMaxFrequency(int node, double nodeCount) {
        nodeMaxFreqs.put(node, Math.max(nodeMaxFreqs.getIfAbsentPut(node, 0), nodeCount));
    }
    
    private void collectNodeFrequency(int node, double nodeFreq) {
        nodeFreqs.getIfAbsentPut(node, DoubleLists.mutable::empty).add(nodeFreq);
    }

    private void collectNodeIntervals(int node, ListIterable<LongLongPair> nodeIntervals) {
        this.nodeIntervals.getIfAbsentPut(node, Lists.mutable::empty).addAllIterable(nodeIntervals);
    }

    private void collectNodeCosts(int node, DoubleList nodeCosts) {
        this.nodeCosts.getIfAbsentPut(node, DoubleLists.mutable::empty).addAll(nodeCosts);
    }
    
    //////////////////////////// Arc measure ///////////////////////////////////
    
    
    private void incrementArcTotalFrequency(int arc, double arcTotalCount) {
        arcTotalFreqs.put(arc, arcTotalFreqs.getIfAbsentPut(arc, 0) + arcTotalCount);
    }
    
    private void incrementArcCaseFrequency(int arc, double arcCaseCount) {
        arcCaseFreqs.put(arc, arcCaseFreqs.getIfAbsentPut(arc, 0) + (arcCaseCount>0 ? 1 : 0));
    }
    
    private void updateArcMinFrequency(int arc, double arcCount) {
        arcMinFreqs.put(arc, Math.min(arcMinFreqs.getIfAbsentPut(arc, Double.MAX_VALUE), arcCount));
    }
    
    private void updateArcMaxFrequency(int arc, double arcCount) {
        arcMaxFreqs.put(arc, Math.max(arcMaxFreqs.getIfAbsentPut(arc, 0), arcCount));
    }
    
    private void collectArcFrequency(int arc, double arcFreq) {
        arcFreqs.getIfAbsentPut(arc, DoubleLists.mutable::empty).add(arcFreq);
    }
    
    private void collectArcIntervals(int arc, ListIterable<LongLongPair> arcIntervals) {
        this.arcIntervals.getIfAbsentPut(arc, Lists.mutable::empty).addAllIterable(arcIntervals);
    }

    private void collectArcCosts(int node, DoubleList arcCosts) {
        this.arcCosts.getIfAbsentPut(node, DoubleLists.mutable::empty).addAll(arcCosts);
    }

    ///////////////////////////// MEAUSURES ////////////////////////////////////////
    
    public double getNodeWeight(String nodeName, MeasureType type, MeasureAggregation aggregation, MeasureRelation measureRelation) {
        int node = this.getNodeFromName(nodeName);
        return (node >= 0) ? this.getNodeWeight(node, type, aggregation, measureRelation) : 0;
    }
    
    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation, MeasureRelation measureRelation) {
        if (!containNode(node)) {
            return 0d;
        }
        else if (measureRelation == MeasureRelation.ABSOLUTE) {
            return getNodeWeightMap(type, aggregation).get(node);
        }
        else {
            double totalWeight = getTotalWeight(type, aggregation);
            return totalWeight==0 ? 0d : getNodeWeightMap(type, aggregation).get(node)/totalWeight;
        }
    }
    
    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation, MeasureRelation measureRelation) {
        if (!containArc(arc)) {
            return 0d;
        }
        else if (measureRelation == MeasureRelation.ABSOLUTE) {
            return getArcWeightMap(type, aggregation).get(arc);
        }
        else {
            double totalWeight = getTotalWeight(type, aggregation);
            return totalWeight==0 ? 0d : getArcWeightMap(type, aggregation).get(arc)/totalWeight;
        }
    }
    
    public double getArcWeight(String source, String target, MeasureType type, MeasureAggregation aggregation, MeasureRelation measureRelation) {
        int arc = this.getArc(getNodeFromName(source), getNodeFromName(target));
        return (arc >= 0) ? this.getArcWeight(arc, type, aggregation, measureRelation) : 0;
    }
    
    public double getArcStructuralWeight(int arc) {
        return (!containArc(arc) ? 0d : arcWeightsForGraphStructure.getIfAbsent(arc, 0));
    }
    
    public double getNodeStructuralWeight(int node) {
        return (!containNode(node) ? 0d : nodeWeightsForGraphStructure.getIfAbsent(node, 0));
    }
    
    private IntDoubleMap getNodeWeightMap(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
                case TOTAL:
                    return nodeTotalFreqs;
                case CASES:
                    return nodeCaseFreqs;
                case MEAN:
                    return nodeMeanFreqs;
                case MIN:
                    return nodeMinFreqs;
                case MAX:
                    return nodeMaxFreqs;
                case MEDIAN:
                    return nodeMedianFreqs;
                default:
                    return nodeTotalFreqs;
            }
        } else if (type == MeasureType.DURATION) {
            switch (aggregation) {
                case TOTAL:
                    return nodeTotalDurs;
                case MEAN:
                    return nodeMeanDurs;
                case MIN:
                    return nodeMinDurs;
                case MAX:
                    return nodeMaxDurs;
                case MEDIAN:
                    return nodeMedianDurs;
                default:
                    return nodeTotalDurs;
            }
        } else { // COST
            switch (aggregation) {
                case TOTAL:
                    return nodeTotalCosts;
                case MEAN:
                    return nodeMeanCosts;
                case MIN:
                    return nodeMinCosts;
                case MAX:
                    return nodeMaxCosts;
                case MEDIAN:
                    return nodeMedianCosts;
                default:
                    return nodeTotalCosts;
            }
        }
    }
    
    private IntDoubleMap getArcWeightMap(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return arcTotalFreqs;
            case CASES:
                return arcCaseFreqs;
            case MEAN:
                return arcMeanFreqs;
            case MIN:
                return arcMinFreqs;
            case MAX:
                return arcMaxFreqs;
            case MEDIAN:
                return arcMedianFreqs;
            default:
                return arcTotalFreqs;
            }
        } else if (type == MeasureType.DURATION) {
            switch (aggregation) {
                case TOTAL:
                    return arcTotalDurs;
                case MEAN:
                    return arcMeanDurs;
                case MIN:
                    return arcMinDurs;
                case MAX:
                    return arcMaxDurs;
                case MEDIAN:
                    return arcMedianDurs;
                default:
                    return arcTotalDurs;
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return arcTotalCosts;
            case MEAN:
                return arcMeanCosts;
            case MIN:
                return arcMinCosts;
            case MAX:
                return arcMaxCosts;
            case MEDIAN:
                return arcMedianCosts;
            default:
                return arcTotalCosts;
            }
        }
    }
    
    // The total weight is used for calculating relative measures
    // Only used for Case Frequency atm, can extend for other types of measures if needed
    private double getTotalWeight(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return 1;
            case CASES:
                return this.attLog.getTraces().size();
            case MEAN:
                return 1;
            case MIN:
                return 1;
            case MAX:
                return 1;
            case MEDIAN:
                return 1;
            default:
                return 1;
            }
        } else if (type == MeasureType.DURATION) {
            switch (aggregation) {
                case TOTAL:
                    return 1;
                case MEAN:
                    return 1;
                case MIN:
                    return 1;
                case MAX:
                    return 1;
                case MEDIAN:
                    return 1;
                default:
                    return 1;
            }
        } else {
            switch (aggregation) {
            case TOTAL:
                return 1;
            case MEAN:
                return 1;
            case MIN:
                return 1;
            case MAX:
                return 1;
            case MEDIAN:
                return 1;
            default:
                return 1;
            }
        }
    }
    
    ///////////////////////////// FILTER ////////////////////////////////////////
    
    /**
     * Build a list of subgraphs from this graph.
     * The subgraphs range from small to large (the largest one is this graph)
     * This is done by selecting nodes/arcs to remove from a graph to produce smaller ones, starting from this graph.
     * Selecting nodes and arcs is done on a sorted list of nodes and arcs ({@link AttributeLogGraph#sortNodesAndArcs})
     * The selection can be made from the start of the list and forward or from the end of the list and backward.
     * 
     * @param invertedElementSelection if true, nodes and arcs are selected from the end of the list and backward
     * @param maxNumberOfNodes maximum number of nodes allowed in each subgraph
     * @param maxNumberOfArcs maximum number of arcs allowed in each subgraph
     */
    public void buildSubGraphs(boolean invertedElementSelection, int maxNumberOfNodes, int maxNumberOfArcs) {
        LOGGER.debug("Total Number of nodes: " + this.getNodes().size());
        LOGGER.debug("Total Number of arcs: " + this.getArcs().size());
        
        long timer = System.currentTimeMillis();
        
        this.nodeInverted = invertedElementSelection;
        this.arcInverted = invertedElementSelection;
        
        // Select nodes
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
        
        // Build arc-based graphs from the smallest one first
        for (FilteredGraph nodeGraph: subGraphs.toReversed()) {
            ((NodeBasedGraph)nodeGraph).buildSubGraphs(this.arcInverted, maxNumberOfArcs);
        }
        
        LOGGER.debug("Build all graphs: " + (System.currentTimeMillis() - timer) + " ms.");
    }
    
    // This method only builds bins of nodes based on one single connected node
    // (i.e. the node that after removing them the graph remains connected).
    @Deprecated
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
                LOGGER.debug("The node slider graph at level " + i + "/" + subGraphs.size() + " is selected.");
                break;
            }
        }
        
        ListIterable<FilteredGraph> arcSubGraphs = nodeBasedGraph.getSubGraphs();
        long numberOfArcs = Math.round(arcThreshold*nodeBasedGraph.getArcs().size());
        FilteredGraph arcBasedGraph = arcSubGraphs.getLast();
        for (int i=0;i<arcSubGraphs.size();i++) {
            if (arcSubGraphs.get(i).getArcs().size() <= numberOfArcs) {
                arcBasedGraph = arcSubGraphs.get(i);
                LOGGER.debug("The arc slider graph at level " + i + "/" + arcSubGraphs.size() + " is selected.");
                break;
            }
        }
        
        return arcBasedGraph;
    }
    
    /**
     * Sort the graph nodes and arcs based on the increasing order of a chosen weight
     * @param weightType
     * @param weightAggregation
     */
    public void sortNodesAndArcs(MeasureType weightType, MeasureAggregation weightAggregation) {
    	nodeWeightsForGraphStructure = getNodeWeightMap(weightType, weightAggregation);
        arcWeightsForGraphStructure = getArcWeightMap(weightType, weightAggregation);
        if (nodeWeightsForGraphStructure.isEmpty() || arcWeightsForGraphStructure.isEmpty()) {
        	return;
        }
    	
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
