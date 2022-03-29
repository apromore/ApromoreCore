/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.similaritysearch.algorithms;

import org.apromore.similaritysearch.common.VertexPair;
import org.apromore.similaritysearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.similaritysearch.common.similarity.AssingmentProblem;
import org.apromore.similaritysearch.graph.Graph;

import java.util.ArrayList;

public class FindModelSimilarity {

    public static double findProcessSimilarity(Graph g1, Graph g2, String algortithm, double... param) {
        if (algortithm.equals("Greedy")) {
            GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
            Object weights[] = { "ledcutoff", param[0], "cedcutoff", param[1], "vweight", param[2], "sweight", param[3], "eweight", param[4] };

            gedepc.setWeight(weights);

            double weight = gedepc.computeGED(g1, g2);
            gedepc.resetDeterminismFlag();
            return (1 - (weight < 0.0000001 ? 0 : (weight > 1 ? 1 : weight)));

        } else if (algortithm.equals("Hungarian")) {
            ArrayList<VertexPair> mapping = AssingmentProblem.getMappingsVetrexUsingNodeMapping(g1, g2, param[0], param[1]);
            double weight = 0.0;
            for (VertexPair vp : mapping) {
                weight += vp.getWeight();
            }
            return (weight / Math.max(g1.getVertices().size(), g2.getVertices().size()));
        }

        return 0;
    }
}
