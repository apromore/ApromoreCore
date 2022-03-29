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
import org.apromore.similaritysearch.common.VertexPair;
import org.apromore.similaritysearch.graph.Vertex;

import java.util.ArrayList;


public class SemanticSimilarity {

    public static double getSemanticSimilarity(Vertex v1, Vertex v2, double labelTreshold) {

        ArrayList<Vertex> v1NonGWParents = v1.getAllNonGWParents();
        ArrayList<Vertex> v2NonGWParents = v2.getAllNonGWParents();
        ArrayList<Vertex> v1NonGWChildren = v1.getAllNonGWChildren();
        ArrayList<Vertex> v2NonGWChildren = v2.getAllNonGWChildren();

        ArrayList<VertexPair> parentMappings = AssingmentProblem.getMappingsVetrex(v1NonGWParents, v2NonGWParents, labelTreshold, Settings.getEnglishStemmer(), 0);
        ArrayList<VertexPair> childMappings = AssingmentProblem.getMappingsVetrex(v1NonGWChildren, v2NonGWChildren, labelTreshold, Settings.getEnglishStemmer(), 0);

        return (double) (parentMappings.size() + childMappings.size())
                / (double) (Math.max(v1NonGWParents.size(), v2NonGWParents.size()) + Math.max(v1NonGWChildren.size(), v2NonGWChildren.size()));
    }
}
