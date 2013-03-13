package org.apromore.toolbox.clustering.dissimilarity;

import nl.tue.tm.is.graph.SimpleGraph;

public interface DissimilarityCalc {
    double compute(SimpleGraph g1, SimpleGraph g2);

    boolean isAboveThreshold(double disim);
}
