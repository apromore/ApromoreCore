/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2016 Reina Uba.
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.similaritysearch.tools;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.similaritysearch.algorithms.MergeModels;
import org.apromore.similaritysearch.common.CPFModelParser;
import org.apromore.similaritysearch.common.IdGeneratorHelper;
import org.apromore.similaritysearch.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MergeProcesses {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeProcesses.class.getName());

    /**
     * Finds the Processes Similarity.
     * @param models    the Canonical Process Type
     * @param removeEnt The Canonical Process Type
     * @param algorithm the search Algorithm
     * @param threshold the search merge threshold
     * @param param     the search parameters
     * @return the similarity between processes
     */
    public static CanonicalProcessType mergeProcesses(ArrayList<CanonicalProcessType> models, boolean removeEnt, String algorithm,
            double threshold, double... param) {

        IdGeneratorHelper idGenerator = new IdGeneratorHelper();
        Graph m1 = CPFModelParser.readModel(models.get(0));
        if(m1.getGraphLabel() == null) m1.name = models.get(0).getName();
        m1.setIdGenerator(idGenerator);
        m1.removeEmptyNodes();
        m1.reorganizeIDs();

        Graph m2 = CPFModelParser.readModel(models.get(1));
        if(m2.getGraphLabel() == null) m2.name = models.get(1).getName();
        m2.setIdGenerator(idGenerator);
        m2.removeEmptyNodes();
        m2.reorganizeIDs();

        m1.addLabelsToUnNamedEdges();
        m2.addLabelsToUnNamedEdges();

        Graph merged = MergeModels.mergeModels(m1, m2, idGenerator, removeEnt, algorithm, param);

        if (models.size() > 2) {
            for (int i = 2; i < models.size(); i++) {
                Graph m3 = CPFModelParser.readModel(models.get(i));
                if(m3.getGraphLabel() == null) m3.name = models.get(i).getName();
                m3.setIdGenerator(idGenerator);
                m3.removeEmptyNodes();
                m3.reorganizeIDs();
                m3.addLabelsToUnNamedEdges();

                merged = MergeModels.mergeModels(merged, m3, idGenerator, removeEnt, algorithm, param);
            }
        }

        return CPFModelParser.writeModel(merged, idGenerator);
    }
}
