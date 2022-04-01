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

package org.apromore.similaritysearch.tools;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.similaritysearch.algorithms.FindModelSimilarity;
import org.apromore.similaritysearch.common.ModelParser;
import org.apromore.similaritysearch.common.IdGeneratorHelper;
import org.apromore.similaritysearch.graph.Graph;

public class SearchForSimilarProcesses {

    /**
     * Finds the Processes Similarity.
     * @param search    the BPMNDiagram
     * @param d         The BPMNDiagram
     * @param algorithm the search Algorithm
     * @param param     the search parameters
     * @return the similarity between processes
     */
    public static double findProcessesSimilarity(BPMNDiagram search, BPMNDiagram dbDiagram, String algorithm, double... param) {
        if (search.getNodes().size() == 0 || dbDiagram.getNodes().size() == 0) {
            return 0;
        }

        Graph searchGraph = ModelParser.readModel(search);
        searchGraph.setIdGenerator(new IdGeneratorHelper());
        searchGraph.removeEmptyNodes();

        Graph dbGraph = ModelParser.readModel(dbDiagram);
        dbGraph.setIdGenerator(new IdGeneratorHelper());
        dbGraph.removeEmptyNodes();

        double netSimilarity = FindModelSimilarity.findProcessSimilarity(searchGraph, dbGraph, algorithm, param);
        return netSimilarity;
    }

}
