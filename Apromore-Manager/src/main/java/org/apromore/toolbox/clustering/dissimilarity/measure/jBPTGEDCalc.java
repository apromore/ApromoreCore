package org.apromore.toolbox.clustering.dissimilarity.measure;

import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.dissimilarity.GEDMatrixCalc;
import org.jbpt.algo.graph.GraphEditDistance;

/**
 * First attempt at using the GED Matrix calc in the jBPT Library.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class jBPTGEDCalc implements GEDMatrixCalc {

    private double threshold;

    public jBPTGEDCalc(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return "jBPTGEDCalc";
    }

    @Override
    public double compute(Canonical graph1, Canonical graph2) {
        if (graph1 != null && graph2 != null) {
            return new GraphEditDistance<>(graph1, graph2).getDistance();
        } else {
            return -1d;
        }
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        return disim > threshold;
    }
}
