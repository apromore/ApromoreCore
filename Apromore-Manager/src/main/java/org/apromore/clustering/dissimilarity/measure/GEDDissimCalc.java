package org.apromore.clustering.dissimilarity.measure;

import matching.algos.GraphEditDistanceDeterministicGreedy;
import nl.tue.tm.is.graph.SimpleGraph;
import org.apromore.clustering.dissimilarity.DissimilarityCalc;

public class GEDDissimCalc implements DissimilarityCalc {
    private double threshold;

    static double ledcutoff = 0.5;

    static double usepuredistance = 0.0; //0.0 represents 'false', 1.0 represents 'true'
    static double prunewhen = 100.0;
    static double pruneto = 10.0;
    static double useepsilon = 0.0; //0.0 represents 'false', 1.0 represents 'true'
    //static boolean considerevents = true;

    static double vweight = 1.0;
    static double sweight = 1.0;
    static double eweight = 1.0;

    static GraphEditDistanceDeterministicGreedy gedepc = new GraphEditDistanceDeterministicGreedy();

    public GEDDissimCalc(double threshold, double ledc) {
        ledcutoff = ledc;
        Object weights[] = {"vweight", vweight, "sweight", sweight, "eweight", eweight, "ledcutoff", ledcutoff, "usepuredistance", usepuredistance, "prunewhen", prunewhen, "pruneto", pruneto, "useepsilon", useepsilon};
        gedepc.setWeight(weights);

        this.threshold = threshold;
    }

    public double compute(SimpleGraph graph1, SimpleGraph graph2) {
        gedepc.resetDeterminismFlag();
        return gedepc.compute(graph1, graph2);
    }

    public boolean isDeterministicGED() {
        return gedepc.isDeterministic();
    }

    public boolean isAboveThreshold(double disim) {
        return disim > threshold;
    }
}
