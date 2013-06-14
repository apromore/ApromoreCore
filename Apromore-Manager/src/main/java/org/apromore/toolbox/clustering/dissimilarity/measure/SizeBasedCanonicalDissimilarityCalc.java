package org.apromore.toolbox.clustering.dissimilarity.measure;

import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.dissimilarity.GEDMatrixCalc;

public class SizeBasedCanonicalDissimilarityCalc implements GEDMatrixCalc {

    private double threshold;

    double weightSkippedVertex = 1.0;
    double weightSubstitutedVertex = 1.0;
    double weightSkippedEdge = 1.0;

    public SizeBasedCanonicalDissimilarityCalc(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return "SizeBasedCanonicalDissimilarityCalc";
    }

    @Override
    public double compute(Canonical graph1, Canonical graph2) {
        double totalNrVertices = graph1.getVertices().size() + graph2.getVertices().size();
        double totalNrEdges = graph1.getEdges().size() + graph2.getEdges().size();

        double vskip = Math.abs(graph1.getVertices().size() - graph2.getVertices().size()) / totalNrVertices;
        double eskip = Math.abs(graph1.getEdges().size() - graph2.getEdges().size()) / totalNrEdges;
        double vsubs = 0.0;

        return ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip)) / (weightSkippedVertex + weightSubstitutedVertex + weightSkippedEdge);
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        return disim > threshold;
    }
}
