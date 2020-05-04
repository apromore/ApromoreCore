package org.apromore.logman.attribute.graph;

import org.apromore.logman.attribute.log.AttributeTrace;
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
    private AttributeTrace attTrace;
    private MutableIntLongMap nodeTotalFreqs = IntLongMaps.mutable.empty();
    private MutableIntLongMap arcTotalFreqs = IntLongMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> nodeDurs = IntObjectMaps.mutable.empty();
    private MutableIntObjectMap<MutableDoubleList> arcDurs = IntObjectMaps.mutable.empty();
    
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
    }
    
    public void incrementNodeTotalFrequency(int node, long nodeTotalCount) {
        nodeTotalFreqs.put(node, nodeTotalFreqs.getIfAbsentPut(node, 0) + nodeTotalCount);
    }
    
    public void collectNodeDuration(int node, long nodeDuration) {
        if (nodeDurs.contains(node)) nodeDurs.get(node).add(nodeDuration);
    }
    
    public void incrementArcTotalFrequency(int arc, long arcTotalCount) {
        arcTotalFreqs.put(arc, arcTotalFreqs.getIfAbsentPut(arc, 0) + arcTotalCount);
    }
    
    public void collectArcDuration(int arc, long arcDuration) {
        if (arcDurs.contains(arc)) arcDurs.get(arc).add(arcDuration);
    }
    
    public DoubleList getNodeDurations(int node) {
        return nodeDurs.getIfAbsent(node, DoubleLists.mutable::empty).toImmutable();
    }
    
    public DoubleList getArcDurations(int arc) {
        return arcDurs.getIfAbsent(arc, DoubleLists.mutable::empty).toImmutable();
    }

    @Override
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return nodeTotalFreqs.get(node);
            case CASES:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0);                
            case MEAN:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0);
            case MIN:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0);
            case MAX:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0);
            case MEDIAN:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0);
            default:
                return (nodeTotalFreqs.get(node) > 0 ? 1 : 0);
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return nodeDurs.get(node).sum();
            case MEAN:
                return nodeDurs.get(node).average();
            case MIN:
                return nodeDurs.get(node).min();
            case MAX:
                return nodeDurs.get(node).max();
            case MEDIAN:
                return nodeDurs.get(node).median();
            default:
                return nodeDurs.get(node).average();
            }
        }
    }

    @Override
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation) {
        if (type == MeasureType.FREQUENCY) {
            switch (aggregation) {
            case TOTAL:
                return arcTotalFreqs.get(arc);
            case CASES:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0);                
            case MEAN:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0);
            case MIN:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0);
            case MAX:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0);
            case MEDIAN:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0);
            default:
                return (arcTotalFreqs.get(arc) > 0 ? 1 : 0);
            }
        }
        else {
            switch (aggregation) {
            case TOTAL:
                return arcDurs.get(arc).sum();
            case MEAN:
                return arcDurs.get(arc).average();
            case MIN:
                return arcDurs.get(arc).min();
            case MAX:
                return arcDurs.get(arc).max();
            case MEDIAN:
                return arcDurs.get(arc).median();
            default:
                return arcDurs.get(arc).average();
            }
        }
    }
    
}
