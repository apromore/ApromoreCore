/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.toolbox.similaritySearch.common.similarity;

import java.util.LinkedList;

import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.VertexPair;
import org.apromore.toolbox.similaritySearch.graph.Vertex;


public class SemanticSimilarity {

    public static double getSemanticSimilarity(Vertex v1, Vertex v2, double labelTreshold) {

        LinkedList<Vertex> v1NonGWParents = v1.getAllNonGWParents();
        LinkedList<Vertex> v2NonGWParents = v2.getAllNonGWParents();
        LinkedList<Vertex> v1NonGWChildren = v1.getAllNonGWChildren();
        LinkedList<Vertex> v2NonGWChildren = v2.getAllNonGWChildren();

        LinkedList<VertexPair> parentMappings = AssingmentProblem.getMappingsVetrex(v1NonGWParents, v2NonGWParents, labelTreshold, Settings.getEnglishStemmer(), 0);
        LinkedList<VertexPair> childMappings = AssingmentProblem.getMappingsVetrex(v1NonGWChildren, v2NonGWChildren, labelTreshold, Settings.getEnglishStemmer(), 0);

        return (double) (parentMappings.size() + childMappings.size())
                / (double) (Math.max(v1NonGWParents.size(), v2NonGWParents.size()) + Math.max(v1NonGWChildren.size(), v2NonGWChildren.size()));
    }
}
