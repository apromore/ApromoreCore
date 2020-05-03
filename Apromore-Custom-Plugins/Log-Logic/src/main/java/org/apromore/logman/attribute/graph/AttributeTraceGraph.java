package org.apromore.logman.attribute.graph;

import org.apromore.logman.attribute.AttributeMatrixGraph;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.IntLongMaps;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

/**
 * AttributeTraceGraph is an {@link WeightedAttributeGraph} implementation for AttributeTrace.
 * 
 * @author hoang
 *
 */
public class AttributeTraceGraph extends WeightedAttributeGraph {

    private MutableIntLongMap originalNodeTotalFreqs = IntLongMaps.mutable.empty();
    private MutableIntLongMap originalArcTotalFreqs = IntLongMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> originalNodeDurs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> originalArcDurs = IntObjectMaps.mutable.empty();
    
    public AttributeTraceGraph(AttributeMatrixGraph matrixGraph) {
        super(matrixGraph);
    }
    
    public void incrementNodeTotalFrequency(int node, long nodeTotalCount) {
        originalNodeTotalFreqs.put(node, originalNodeTotalFreqs.getIfAbsentPut(node, 0) + nodeTotalCount);
    }
    
    public void collectNodeDuration(int node, long nodeDuration) {
        if (originalNodeDurs.contains(node)) originalNodeDurs.get(node).add(nodeDuration);
    }
    
    public void incrementArcTotalFrequency(int arc, long arcTotalCount) {
        originalArcTotalFreqs.put(arc, originalArcTotalFreqs.getIfAbsentPut(arc, 0) + arcTotalCount);
    }
    
    public void collectArcDuration(int arc, long arcDuration) {
        if (originalArcDurs.contains(arc)) originalArcDurs.get(arc).add(arcDuration);
    }
    
    public DoubleList getNodeDurations(int node) {
        return originalNodeDurs.getIfAbsent(node, DoubleLists.mutable::empty).toImmutable();
    }
    
    public DoubleList getArcDurations(int arc) {
        return originalArcDurs.getIfAbsent(arc, DoubleLists.mutable::empty).toImmutable();
    }

    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return originalNodeTotalFreqs.get(node);
            case CASES:
                return (originalNodeTotalFreqs.get(node) > 0 ? 1 : 0);                
            case MEAN:
                return (originalNodeTotalFreqs.get(node) > 0 ? 1 : 0);
            case MIN:
                return (originalNodeTotalFreqs.get(node) > 0 ? 1 : 0);
            case MAX:
                return (originalNodeTotalFreqs.get(node) > 0 ? 1 : 0);
            case MEDIAN:
                return (originalNodeTotalFreqs.get(node) > 0 ? 1 : 0);
            default:
                return (originalNodeTotalFreqs.get(node) > 0 ? 1 : 0);
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return originalNodeDurs.get(node).sum();
            case MEAN:
                return originalNodeDurs.get(node).average();
            case MIN:
                return originalNodeDurs.get(node).min();
            case MAX:
                return originalNodeDurs.get(node).max();
            case MEDIAN:
                return originalNodeDurs.get(node).median();
            default:
                return originalNodeDurs.get(node).average();
            }
        }
    }

    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return originalArcTotalFreqs.get(arc);
            case CASES:
                return (originalArcTotalFreqs.get(arc) > 0 ? 1 : 0);                
            case MEAN:
                return (originalArcTotalFreqs.get(arc) > 0 ? 1 : 0);
            case MIN:
                return (originalArcTotalFreqs.get(arc) > 0 ? 1 : 0);
            case MAX:
                return (originalArcTotalFreqs.get(arc) > 0 ? 1 : 0);
            case MEDIAN:
                return (originalArcTotalFreqs.get(arc) > 0 ? 1 : 0);
            default:
                return (originalArcTotalFreqs.get(arc) > 0 ? 1 : 0);
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return originalArcDurs.get(arc).sum();
            case MEAN:
                return originalArcDurs.get(arc).average();
            case MIN:
                return originalArcDurs.get(arc).min();
            case MAX:
                return originalArcDurs.get(arc).max();
            case MEDIAN:
                return originalArcDurs.get(arc).median();
            default:
                return originalArcDurs.get(arc).average();
            }
        }
    }
    
}
