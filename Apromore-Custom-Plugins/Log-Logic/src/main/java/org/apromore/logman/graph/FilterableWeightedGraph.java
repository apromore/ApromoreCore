package org.apromore.logman.graph;

import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureType;

public interface FilterableWeightedGraph {
    double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation);
    double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation);
}
