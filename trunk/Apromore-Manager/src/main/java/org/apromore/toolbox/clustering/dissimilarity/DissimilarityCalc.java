package org.apromore.toolbox.clustering.dissimilarity;

import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;

public interface DissimilarityCalc {

    double compute(SimpleGraph g1, SimpleGraph g2);

    boolean isAboveThreshold(double disim);

    String getName();
}
