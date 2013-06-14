package org.apromore.toolbox.clustering.dissimilarity;

import org.apromore.graph.canonical.Canonical;

public interface GEDMatrixCalc {

    double compute(Canonical g1, Canonical g2);

    boolean isAboveThreshold(double disim);

    String getName();
}
