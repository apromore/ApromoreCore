package org.apromore.service.logvisualizer.fuzzyminer.transformer;

import org.apromore.service.logvisualizer.fuzzyminer.model.MutableFuzzyGraph;

import java.util.BitSet;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class FuzzyEdgeTransformer extends FuzzyGraphTransformer {

    protected int numberOfInitialNodes;
    protected MutableFuzzyGraph graph;
    protected BitSet preserveMask;
    protected double sigCorRatio;
    protected double preservePercentage;
    protected boolean ignoreSelfLoops;
    protected boolean interpretPercentageAbsolute;

    public FuzzyEdgeTransformer() {
        super("Fuzzy edge transformer");
        numberOfInitialNodes = 0;
        graph = null;
        preserveMask = null;
        sigCorRatio = 0.5;
        preservePercentage = 0.1;
        ignoreSelfLoops = true;
        interpretPercentageAbsolute = false;
    }

    public void setIgnoreSelfLoops(boolean ignored) {
        ignoreSelfLoops = ignored;
    }

    public boolean getIgnoreSelfLoops() {
        return ignoreSelfLoops;
    }

    public void setInterpretPercentageAbsolute(boolean isAbsolute) {
        interpretPercentageAbsolute = isAbsolute;
    }

    public boolean getInterpretPercentageAbsolute() {
        return interpretPercentageAbsolute;
    }

    public void setSignificanceCorrelationRatio(double ratio) {
        if ((ratio >= 0.0) && (ratio <= 1.0)) {
            sigCorRatio = ratio;
        } else {
            System.err.println("FuzzyEdgeTransformer: attempting to set "
                    + "invalid significance/correlation ratio of " + ratio);
        }
    }

    public void setPreservePercentage(double percentage) {
        if ((percentage > 0.0) && (percentage <= 1.0)) {
            preservePercentage = percentage;
        } else {
            System.err.println("FuzzyEdgeTransformer: attempting to set " + "invalid perservation percentage of "
                    + percentage);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.processmining.mining.fuzzymining.graph.transform.FuzzyGraphTransformer
     * #transform(org.processmining.mining.fuzzymining.graph.FuzzyGraph)
     */
    public void transform(MutableFuzzyGraph graph) {
        // setup attributes
        this.graph = graph;
        numberOfInitialNodes = graph.getNumberOfInitialNodes();
        preserveMask = new BitSet(numberOfInitialNodes * numberOfInitialNodes);
        // build preserve mask from current relations
        buildPreserveMask();
        // apply preserve mask to filter edges
        filterEdges();
    }

    protected void buildPreserveMask() {
        for (int i = 0; i < numberOfInitialNodes; i++) {
            processEdgesForNode(i);
        }
    }

    protected void processEdgesForNode(int index) {
        double minInVal = Double.MAX_VALUE;
        double maxInVal = Double.MIN_VALUE;
        double minOutVal = Double.MAX_VALUE;
        double maxOutVal = Double.MIN_VALUE;
        double[] inValues = new double[numberOfInitialNodes];
        double[] outValues = new double[numberOfInitialNodes];
        double sig, cor;
        // scan for min / max values by iterating through all relations
        for (int i = 0; i < numberOfInitialNodes; i++) {
            // check for self loops
            if ((ignoreSelfLoops == true) && (i == index)) {
                // self loop, ignore
//				System.err.println("[FuzzyEdgeTransformer] Ignoring self loops.");
                continue;
            }
            // check incoming relation
            sig = graph.getBinarySignificance(i, index);
            if (sig > 0.0) { // valid relation
                cor = graph.getBinaryCorrelation(i, index);
                inValues[i] = (sig * sigCorRatio) + (cor * (1.0 - sigCorRatio));
                if (inValues[i] > maxInVal) {
                    maxInVal = inValues[i];
                }
                if (inValues[i] < minInVal) {
                    minInVal = inValues[i];
                }
            } else {
                inValues[i] = 0.0;
            }
            // check outgoing relation
            sig = graph.getBinarySignificance(index, i);
            if (sig > 0.0) { // valid relation
                cor = graph.getBinaryCorrelation(index, i);
                outValues[i] = (sig * sigCorRatio) + (cor * (1.0 - sigCorRatio));
                if (outValues[i] > maxOutVal) {
                    maxOutVal = outValues[i];
                }
                if (outValues[i] < minOutVal) {
                    minOutVal = outValues[i];
                }
            } else {
                outValues[i] = 0.0;
            }
        }
        // calculate limit
        if (interpretPercentageAbsolute == true) {
            // absolute interpretation
            maxInVal = Math.max(maxInVal, maxOutVal);
            maxOutVal = maxInVal;
            minInVal = Math.min(minInVal, minOutVal);
            minOutVal = minInVal;
        }
        double inLimit = maxInVal - ((maxInVal - minInVal) * preservePercentage);
        double outLimit = maxOutVal - ((maxOutVal - minOutVal) * preservePercentage);
        // process all relations using stored values
        for (int i = 0; i < numberOfInitialNodes; i++) {
            if (inValues[i] >= inLimit) {
                setBitMask(i, index, true);
            }
            if (outValues[i] >= outLimit) {
                setBitMask(index, i, true);
            }
        }
    }

    protected void filterEdges() {
        for (int x = 0; x < numberOfInitialNodes; x++) {
            for (int y = 0; y < numberOfInitialNodes; y++) {
                if (x == y) {
                    // no self-loops handled here..
                    continue;
                } else if (getBitMask(x, y) == false) {
                    graph.setBinarySignificance(x, y, 0.0);
                    graph.setBinaryCorrelation(x, y, 0.0);
                }
            }
        }
    }

    protected void setBitMask(int x, int y, boolean value) {
        preserveMask.set(translateIndex(x, y), value);
    }

    protected boolean getBitMask(int x, int y) {
        return preserveMask.get(translateIndex(x, y));
    }

    protected int translateIndex(int x, int y) {
        return ((x * numberOfInitialNodes) + y);
    }

}
