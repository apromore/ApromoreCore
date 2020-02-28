/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.toolbox.clustering;

import javax.inject.Inject;

import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.toolbox.clustering.dissimilarity.measure.CanonicalGEDDeterministicGreedyCalc;
import org.apromore.toolbox.clustering.dissimilarity.measure.SimpleGEDDeterministicGreedyCalc;
import org.apromore.toolbox.clustering.dissimilarity.measure.SizeBasedCanonicalDissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.measure.SizeBasedSimpleDissimilarityCalc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class DMatrix {

    private DissimilarityMatrix generator;
    private ContainmentRelation crel;


    /**
     * Default Constructor for Proxy setup.
     */
    public DMatrix() {
    }


    @Inject
    public DMatrix(final @Qualifier("hierarchyAwareDissimMatrixGenerator") DissimilarityMatrix hadmg, final ContainmentRelation cRelationship) {
        generator = hadmg;
        crel = cRelationship;
    }


    public void compute() throws Exception {
        // "ContainmentRelationImpl"   queries the RPSDAG to retrieve the
        // set of models with a minimum size (in the example of 4)
        // ----> IMPORTANT: As the identifier of models retrieved by this
        // class are usually discontinues, the class also assigns an index
        // and provides a method to translate such index to the identifier
        // as stored in the RPSDAG
        crel.setMinSize(2);
        // Additionally, it computes the containment relation
        // (transitive and symmetric version of the parent-child relation on the RPSDAG
        crel.initialize();

        // "DissimMatrixGeneratorImpl"   computes the dissimilarity values
        // for pair of models. Note that:
        //    1) If two models are in containment relation, the dissimilarity
        //       is not computed and nothing is stored in the matrix.
        //    2) If the dissimilarity value is above a given threshold (0.15 in
        //       this example), the value is not stored in the matrix.
        //    3) There is a chain of classes that compute dissimilarity. The
        //       first one (LJaccardDissimCalc) is cheaper to compute so the
        //       other one is only computed when the first value is bellow
        //       a local threshold (0.45 in this example).
        // Note that some cells in the matrix are empty. The result might be
        // a particularly sparse matrix. That is way, the matrix is stored
        // as list of tuples in the form of a MultiKeyMap.
        generator.setDissThreshold(DissimilarityMatrix.GED_THRESHOLD);
        generator.addDissimCalc(new SizeBasedSimpleDissimilarityCalc(DissimilarityMatrix.GED_THRESHOLD));
        generator.addDissimCalc(new SimpleGEDDeterministicGreedyCalc(DissimilarityMatrix.GED_THRESHOLD, DissimilarityMatrix.LED_CUTOFF));

        generator.addGedCalc(new SizeBasedCanonicalDissimilarityCalc(DissimilarityMatrix.GED_THRESHOLD));
        generator.addGedCalc(new CanonicalGEDDeterministicGreedyCalc(DissimilarityMatrix.GED_THRESHOLD, DissimilarityMatrix.LED_CUTOFF));
        //generator.addGedCalc(new jBPTGEDCalc(DissimilarityMatrix.GED_THRESHOLD));

//        long start = System.currentTimeMillis();
        generator.computeDissimilarity();
//        System.out.println(System.currentTimeMillis() - start);
    }
}
