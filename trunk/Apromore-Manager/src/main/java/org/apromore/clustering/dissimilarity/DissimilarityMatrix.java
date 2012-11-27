package org.apromore.clustering.dissimilarity;

public interface DissimilarityMatrix {

    double compute(Integer frag1, Integer frag2);

    Double getDissimilarity(Integer index1, Integer index2);

    void computeDissimilarity();

    void addDissimCalc(DissimilarityCalc calc);

    void setDissThreshold(double dissThreshold);

}
