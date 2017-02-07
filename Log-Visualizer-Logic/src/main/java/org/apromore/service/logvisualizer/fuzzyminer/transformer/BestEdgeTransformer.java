package org.apromore.service.logvisualizer.fuzzyminer.transformer;

import org.apromore.service.logvisualizer.fuzzyminer.model.FMNode;
import org.apromore.service.logvisualizer.fuzzyminer.model.MutableFuzzyGraph;

import java.util.BitSet;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/2/17.
 */
public class BestEdgeTransformer extends FuzzyGraphTransformer {

    protected int numberOfInitialNodes;
    protected MutableFuzzyGraph graph;
    protected BitSet preserveMask;

    public BestEdgeTransformer() {
        super("Best edge transformer");
        graph = null;
        preserveMask = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.processmining.mining.fuzzymining.graph.transform.FuzzyGraphTransformer
     * #transform(org.processmining.mining.fuzzymining.graph.FuzzyGraph)
     */
    public void transform(MutableFuzzyGraph graph) {
        this.graph = graph;
        numberOfInitialNodes = graph.getNumberOfInitialNodes();
        preserveMask = new BitSet(numberOfInitialNodes * numberOfInitialNodes);
        buildBitMask();
        filterEdges();
    }

    protected void buildBitMask() {
        for (int i = 0; i < numberOfInitialNodes; i++) {
            setBitMask(graph.getPrimitiveNode(i));
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

    protected void setBitMask(FMNode node) {
        int nodeIndex = node.getIndex();
        // find best predecessor and successor
        int bestX = -1;
        int bestY = -1;
        double bestXSig = 0.0;
        double bestYSig = 0.0;
        double sigX, sigY;
        for (int x = 0; x < numberOfInitialNodes; x++) {
            if (x == nodeIndex) {
                continue;
            } // skip self
            sigX = graph.getBinarySignificance(x, nodeIndex);
            if (sigX > bestXSig) {
                bestXSig = sigX;
                bestX = x;
            }
            sigY = graph.getBinarySignificance(nodeIndex, x);
            if (sigY > bestYSig) {
                bestYSig = sigY;
                bestY = x;
            }
        }
        // flag best predecessor and successor, if any
        if (bestX >= 0) {
            setBitMask(bestX, nodeIndex, true);
        }
        if (bestY >= 0) {
            setBitMask(nodeIndex, bestY, true);
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
