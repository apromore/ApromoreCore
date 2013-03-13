package org.apromore.toolbox.clustering.algorithm.hac;

import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;

public class CompleteLinkage extends HierarchicalAgglomerativeClustering {
    private double proximity = -1;

    public CompleteLinkage(ContainmentRelation crel, DissimilarityMatrix dmatrix) throws Exception {
        super(crel, dmatrix);
    }

    protected void resetProximityValue() {
        proximity = -1;
    }

    protected void updateProximityValue(double newValue) {
        proximity = Math.max(proximity, newValue);
    }

    protected boolean isItAValidProximityValue() {
        return proximity >= 0;
    }

    protected double getProximityValue() {
        return proximity;
    }

}
