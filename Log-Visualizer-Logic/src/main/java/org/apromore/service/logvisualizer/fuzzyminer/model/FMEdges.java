package org.apromore.service.logvisualizer.fuzzyminer.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class FMEdges {

    protected HashSet<FMEdgeImpl> edges;
    protected FuzzyGraph graph;
    protected double attenuationThreshold;

    public FMEdges(FuzzyGraph graph) {
        this.graph = graph;
        edges = new HashSet<FMEdgeImpl>();
        attenuationThreshold = 1.0;
    }

    public void setAttenuationThreshold(double attThreshold) {
        attenuationThreshold = attThreshold;
    }

    public void addEdge(FMNode source, FMNode target, double significance, double correlation) {
        FMEdgeImpl edge = new FMEdgeImpl(source, target, significance, correlation);
        if (edges.contains(edge)) {
            for (FMEdgeImpl oE : edges) {
                if (oE.equals(edge)) {
                    // merge to max value of the two merged edges
                    if (edge.significance > oE.significance) {
                        oE.significance = edge.significance;
                    }
                    if (edge.correlation > oE.correlation) {
                        oE.correlation = edge.correlation;
                    }
                }
                break;
            }
        } else {
            // insert new edge
            edges.add(edge);
        }
    }

    public FMEdgeImpl getEdge(FMNode source, FMNode target) {
        for (FMEdgeImpl edge : edges) {
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                return edge;
            }
        }
        return null;
    }

    public Set<FMEdgeImpl> getEdges() {
        return edges;
    }

}