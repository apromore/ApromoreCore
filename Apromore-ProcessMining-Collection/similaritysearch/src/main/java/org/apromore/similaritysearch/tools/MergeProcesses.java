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

import java.util.Collection;
import java.util.Iterator;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.similaritysearch.algorithms.MergeModels;
import org.apromore.similaritysearch.common.ModelParser;
import org.apromore.similaritysearch.common.IdGeneratorHelper;
import org.apromore.similaritysearch.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static BPMNDiagram mergeProcesses(Collection<BPMNDiagram> models, boolean removeEnt, String algorithm,
            double threshold, double... param) {
        if (models.isEmpty()) {
            return BPMNDiagramFactory.newBPMNDiagram("Empty diagram");
        }
        else if (models.size() == 1) {
            return BPMNDiagramFactory.cloneBPMNDiagram(models.iterator().next());
        }
        
        Iterator<BPMNDiagram> diagramIterator = models.iterator();
        
        IdGeneratorHelper idGenerator = new IdGeneratorHelper();
        BPMNDiagram d1 = diagramIterator.next();
        Graph m1 = ModelParser.readModel(d1);
        if(m1.getGraphLabel() == null) m1.name = d1.getLabel();
        m1.setIdGenerator(idGenerator);
        m1.removeEmptyNodes();
        m1.reorganizeIDs();

        BPMNDiagram d2 = diagramIterator.next();
        Graph m2 = ModelParser.readModel(d2);
        if(m2.getGraphLabel() == null) m2.name = d2.getLabel();
        m2.setIdGenerator(idGenerator);
        m2.removeEmptyNodes();
        m2.reorganizeIDs();

        m1.addLabelsToUnNamedEdges();
        m2.addLabelsToUnNamedEdges();

        Graph merged = MergeModels.mergeModels(m1, m2, idGenerator, removeEnt, algorithm, param);

        while (diagramIterator.hasNext()) {
            BPMNDiagram d3 = diagramIterator.next(); 
            Graph m3 = ModelParser.readModel(d3);
            if(m3.getGraphLabel() == null) m3.name = d3.getLabel();
            m3.setIdGenerator(idGenerator);
            m3.removeEmptyNodes();
            m3.reorganizeIDs();
            m3.addLabelsToUnNamedEdges();
            merged = MergeModels.mergeModels(merged, m3, idGenerator, removeEnt, algorithm, param);
        }

        return ModelParser.writeModel(merged, idGenerator);
    }
}
