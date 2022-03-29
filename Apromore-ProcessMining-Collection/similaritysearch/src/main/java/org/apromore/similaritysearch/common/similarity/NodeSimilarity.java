/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Reina Uba.
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

package org.apromore.similaritysearch.common.similarity;


import org.apromore.similaritysearch.common.Settings;
import org.apromore.similaritysearch.graph.Graph;
import org.apromore.similaritysearch.graph.Vertex;
import org.apromore.similaritysearch.graph.Vertex.Type;


public class NodeSimilarity {

    public static double findNodeSimilarity(Vertex n, Vertex m, double labelTreshold) {
        // functions or events -
        // compare the labels of these nodes
        // tokenize, stem and find the similarity score
        if (((n.getType().equals(Type.function) && m.getType().equals(Type.function))
                || (n.getType().equals(Type.event) && m.getType().equals(Type.event))
                || (n.getType().equals(Type.state) && m.getType().equals(Type.state))
                || (n.getType().equals(Type.node) && m.getType().equals(Type.node)))
                && AssingmentProblem.canMap(n, m)) {
            return LabelEditDistance.edTokensWithStemming(m.getLabel(),
                    n.getLabel(), Settings.STRING_DELIMETER,
                    Settings.getEnglishStemmer(), true);

        }
        // gateways
        else if (n.getType().equals(Type.gateway) && m.getType().equals(Type.gateway)) {
            // splits can not be merged with joins
            if (Graph.isSplit(n) && Graph.isJoin(m)
                    || Graph.isSplit(m) && Graph.isJoin(n)) {
                return 0;
            }
            return SemanticSimilarity.getSemanticSimilarity(n, m, labelTreshold);
        }
        return 0;
    }
}
