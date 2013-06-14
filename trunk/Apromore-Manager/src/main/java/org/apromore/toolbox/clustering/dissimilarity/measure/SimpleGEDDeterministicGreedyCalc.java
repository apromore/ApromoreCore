package org.apromore.toolbox.clustering.dissimilarity.measure;

import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.algorithm.SimpleGEDDeterministicGreedy;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;

public class SimpleGEDDeterministicGreedyCalc implements DissimilarityCalc {

    private double threshold;

    static double ledcutoff = 0.5;

    static double usepuredistance = 0.0;
    static double prunewhen = 100.0;
    static double pruneto = 10.0;
    static double useepsilon = 0.0;

    static double vweight = 1.0;
    static double sweight = 1.0;
    static double eweight = 1.0;

    static SimpleGEDDeterministicGreedy gedepc = new SimpleGEDDeterministicGreedy();

    public SimpleGEDDeterministicGreedyCalc(double threshold, double ledc) {
        ledcutoff = ledc;
        Object weights[] = {"vweight", vweight, "sweight", sweight, "eweight", eweight, "ledcutoff", ledcutoff, "usepuredistance", usepuredistance, "prunewhen", prunewhen, "pruneto", pruneto, "useepsilon", useepsilon};
        gedepc.setWeight(weights);

        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return "SimpleGEDDeterministicGreedyCalc";
    }

    @Override
    public double compute(SimpleGraph graph1, SimpleGraph graph2) {
        gedepc.resetDeterminismFlag();
        return gedepc.compute(graph1, graph2);
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        return disim > threshold;
    }

    public boolean isDeterministicGED() {
        return gedepc.isDeterministic();
    }

}
