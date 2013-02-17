package org.apromore.service.impl;

import org.apromore.clustering.containment.ContainmentRelation;
import org.apromore.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.clustering.dissimilarity.measure.GEDDissimCalc;
import org.apromore.clustering.dissimilarity.measure.SizeBasedDissimCalc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class DistanceMatrix {

    @Inject
    private DissimilarityMatrix generator;
    @Inject
    private ContainmentRelation containment;


    /**
     * Computes the distance between two nodes.
     * - "ContainmentRelationImpl"   queries the RPSDAG to retrieve the set of models with a minimum size (in the example of 4). </p>
     * - As the identifier of models retrieved by this class are usually discontinues, the class also assigns an index and provides a method to
     * translate such index to the identifier as stored in the RPSDAG.</p>
     * - Additionally, it computes the containment relation (transitive and symmetric version of the parent-child relation on the RPSDAG. </p>
     * "DissimMatrixGeneratorImpl"   computes the dissimilarity values for pair of models. Note that:
     *    1) If two models are in containment relation, the dissimilarity
     *       is not computed and nothing is stored in the matrix.
     *    2) If the dissimilarity value is above a given threshold (0.15 in
     *       this example), the value is not stored in the matrix.
     *    3) There is a chain of classes that compute dissimilarity. The
     *       first one (LJaccardDissimCalc) is cheaper to compute so the
     *       other one is only computed when the first value is bellow
     *       a local threshold (0.45 in this example).
     * - Note that some cells in the matrix are empty. The result might be a particularly sparse matrix. That is way, the matrix is stored
     * as list of tuples in the form of a MultiKeyMap.
     * @throws Exception If the computation fails.
     */
    public void compute() throws Exception {
        containment.setMinSize(6);
        containment.initialize();

        generator.setDissThreshold(0.45);
        generator.addDissimCalc(new SizeBasedDissimCalc(0.45));
        generator.addDissimCalc(new GEDDissimCalc(0.45, 0.4));
        generator.computeDissimilarity();
    }
}
