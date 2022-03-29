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

import org.apromore.logman.attribute.log.AttributeTrace;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.IntLongMaps;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

/**
 * AttributeTraceGraph is an {@link WeightedAttributeGraph} implementation for AttributeTrace.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeTraceGraph extends WeightedAttributeGraph {
    private AttributeTrace attTrace;
    
    // Total frequency of nodes and arcs
    private MutableIntLongMap nodeTotalFreqs = IntLongMaps.mutable.empty();
    private MutableIntLongMap arcTotalFreqs = IntLongMaps.mutable.empty();
    
    private MutableIntObjectMap<MutableDoubleList> nodeDurs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> arcDurs = IntObjectMaps.mutable.empty();

    private MutableIntObjectMap<MutableDoubleList> nodeCosts = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> arcCosts = IntObjectMaps.mutable.empty();

    // Collection of node and arc intervals
    private MutableIntObjectMap<MutableList<LongLongPair>> nodeIntervals = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableList<LongLongPair>> arcIntervals = IntObjectMaps.mutable.empty();
    
    public AttributeTraceGraph(AttributeTrace attTrace) {
        super(attTrace.getAttribute());
        this.attTrace = attTrace;
    }
    
    @Override
    public void clear() {
        super.clear();
        nodeTotalFreqs.clear();
        arcTotalFreqs.clear();
        nodeDurs.clear();
        arcDurs.clear();
        nodeCosts.clear();
        arcCosts.clear();
        nodeIntervals.clear();
        arcIntervals.clear();
    }
    
    public void incrementNodeTotalFrequency(int node, long nodeTotalCount) {
        nodeTotalFreqs.put(node, nodeTotalFreqs.getIfAbsentPut(node, 0) + nodeTotalCount);
    }
    
    public void collectNodeDuration(int node, long nodeDuration) {
        if (!nodeDurs.containsKey(node)) nodeDurs.put(node, DoubleLists.mutable.empty());
        nodeDurs.get(node).add(nodeDuration);
    }

    public void collectNodeCost(int node, double nodeCost) {
        if (!nodeCosts.containsKey(node)) nodeCosts.put(node, DoubleLists.mutable.empty());
        nodeCosts.get(node).add(nodeCost);
    }

    public void collectNodeInterval(int node, long startTimestamp, long endTimestamp) {
        nodeIntervals.getIfAbsentPut(node, Lists.mutable::empty).add(PrimitiveTuples.pair(startTimestamp, endTimestamp));
    }
    
    public void incrementArcTotalFrequency(int arc, long arcTotalCount) {
        arcTotalFreqs.put(arc, arcTotalFreqs.getIfAbsentPut(arc, 0) + arcTotalCount);
    }
    
    public void collectArcDuration(int arc, long arcDuration) {
        if (!arcDurs.containsKey(arc)) arcDurs.put(arc, DoubleLists.mutable.empty());
        arcDurs.get(arc).add(arcDuration);
    }

    public void collectArcCost(int arc, double arcCost) {
        if (!arcCosts.containsKey(arc)) arcCosts.put(arc, DoubleLists.mutable.empty());
        arcCosts.get(arc).add(arcCost);
    }

    public void collectArcInterval(int node, long startTimestamp, long endTimestamp) {
        arcIntervals.getIfAbsentPut(node, Lists.mutable::empty).add(PrimitiveTuples.pair(startTimestamp, endTimestamp));
    }

    public DoubleList getNodeCosts(int node) {
        return nodeCosts.getIfAbsent(node, DoubleLists.mutable::empty).toImmutable();
    }

    public DoubleList getArcCosts(int arc) {
        return arcCosts.getIfAbsent(arc, DoubleLists.mutable::empty).toImmutable();
    }
    
    public DoubleList getNodeDurations(int node) {
        return nodeDurs.getIfAbsent(node, DoubleLists.mutable::empty).toImmutable();
    }
    
    public DoubleList getArcDurations(int arc) {
        return arcDurs.getIfAbsent(arc, DoubleLists.mutable::empty).toImmutable();
    }
    
    public ListIterable<LongLongPair> getNodeIntervals(int node) {
        return nodeIntervals.getIfAbsent(node, Lists.mutable::empty).toImmutable();
    }
    
    public ListIterable<LongLongPair> getArcIntervals(int arc) {
        return arcIntervals.getIfAbsent(arc, Lists.mutable::empty).toImmutable();
    }
    
    private double getTotalWeight(MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return 1;
            case CASES:
                return this.attTrace.getValueTrace().size();
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
        } else if (type == MeasureType.DURATION){
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


    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation, MeasureRelation relation) {
    	double totalWeight = (relation == MeasureRelation.ABSOLUTE) ? 1 : getTotalWeight(type, aggregation);
    	if (totalWeight == 0d) totalWeight = 1;
    	
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return nodeTotalFreqs.get(node)/totalWeight;
            case CASES:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0)/totalWeight;
            case MEAN:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0)/totalWeight;
            case MIN:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0)/totalWeight;
            case MAX:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0)/totalWeight;
            case MEDIAN:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0)/totalWeight;
            default:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0)/totalWeight;
            }
        } else if (type == MeasureType.DURATION){
            switch (aggregation) {
                case TOTAL:
                    return nodeDurs.get(node).isEmpty() ? 0 : nodeDurs.get(node).sum()/totalWeight;
                case MEAN:
                    return nodeDurs.get(node).isEmpty() ? 0 : nodeDurs.get(node).average()/totalWeight;
                case MIN:
                    return nodeDurs.get(node).isEmpty() ? 0 : nodeDurs.get(node).min()/totalWeight;
                case MAX:
                    return nodeDurs.get(node).isEmpty() ? 0 : nodeDurs.get(node).max()/totalWeight;
                case MEDIAN:
                    return nodeDurs.get(node).isEmpty() ? 0 : nodeDurs.get(node).median()/totalWeight;
                default:
                    return nodeDurs.get(node).isEmpty() ? 0 : nodeDurs.get(node).average()/totalWeight;
            }
        } else {
            switch (aggregation) {
            case TOTAL:
                return nodeCosts.get(node).isEmpty() ? 0 : nodeCosts.get(node).sum()/totalWeight;
            case MEAN:
                return nodeCosts.get(node).isEmpty() ? 0 : nodeCosts.get(node).average()/totalWeight;
            case MIN:
                return nodeCosts.get(node).isEmpty() ? 0 : nodeCosts.get(node).min()/totalWeight;
            case MAX:
                return nodeCosts.get(node).isEmpty() ? 0 : nodeCosts.get(node).max()/totalWeight;
            case MEDIAN:
                return nodeCosts.get(node).isEmpty() ? 0 : nodeCosts.get(node).median()/totalWeight;
            default:
                return nodeCosts.get(node).isEmpty() ? 0 : nodeCosts.get(node).average()/totalWeight;
            }
        }
    }

    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation, MeasureRelation relation) {
    	double totalWeight = (relation == MeasureRelation.ABSOLUTE) ? 1 : getTotalWeight(type, aggregation);
    	if (totalWeight == 0d) totalWeight = 1;
    	
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return arcTotalFreqs.get(arc)/totalWeight;
            case CASES:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0)/totalWeight;
            case MEAN:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0)/totalWeight;
            case MIN:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0)/totalWeight;
            case MAX:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0)/totalWeight;
            case MEDIAN:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0)/totalWeight;
            default:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0)/totalWeight;
            }
        } else if (type == MeasureType.DURATION){
            switch (aggregation) {
                case TOTAL:
                    return arcDurs.get(arc).isEmpty() ? 0 : arcDurs.get(arc).sum()/totalWeight;
                case MEAN:
                    return arcDurs.get(arc).isEmpty() ? 0 : arcDurs.get(arc).average()/totalWeight;
                case MIN:
                    return arcDurs.get(arc).isEmpty() ? 0 : arcDurs.get(arc).min()/totalWeight;
                case MAX:
                    return arcDurs.get(arc).isEmpty() ? 0 : arcDurs.get(arc).max()/totalWeight;
                case MEDIAN:
                    return arcDurs.get(arc).isEmpty() ? 0 : arcDurs.get(arc).median()/totalWeight;
                default:
                    return arcDurs.get(arc).isEmpty() ? 0 : arcDurs.get(arc).average()/totalWeight;
            }
        } else {
            switch (aggregation) {
            case TOTAL:
                return arcCosts.get(arc).isEmpty() ? 0 : arcCosts.get(arc).sum()/totalWeight;
            case MEAN:
                return arcCosts.get(arc).isEmpty() ? 0 : arcCosts.get(arc).average()/totalWeight;
            case MIN:
                return arcCosts.get(arc).isEmpty() ? 0 : arcCosts.get(arc).min()/totalWeight;
            case MAX:
                return arcCosts.get(arc).isEmpty() ? 0 : arcCosts.get(arc).max()/totalWeight;
            case MEDIAN:
                return arcCosts.get(arc).isEmpty() ? 0 : arcCosts.get(arc).median()/totalWeight;
            default:
                return arcCosts.get(arc).isEmpty() ? 0 : arcCosts.get(arc).average()/totalWeight;
            }
        }
    }
    
}
