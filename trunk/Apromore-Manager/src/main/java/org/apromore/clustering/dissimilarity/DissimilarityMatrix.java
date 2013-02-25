package org.apromore.clustering.dissimilarity;

import org.apromore.clustering.containment.ContainmentRelation;

public interface DissimilarityMatrix {

    void initialize(ContainmentRelation containmentRelation, double threshold);

    Double getDissimilarity(Integer index1, Integer index2);

//    double compute(Integer frag1, Integer frag2);
//
//    Double getDissimilarity(Integer index1, Integer index2);
//
//    void computeDissimilarity();
//
//    void addDissimCalc(DissimilarityCalc calc);
//
//    void setDissThreshold(double dissThreshold);

}
