/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

/**
 *
 */
package org.apromore.service.helper;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;
import org.apromore.toolbox.clustering.dissimilarity.model.TwoVertices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Wrapper for to build a SimpleGraph from a Full Canonical Graph.
 *
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class SimpleGraphWrapper extends SimpleGraph {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleGraphWrapper.class);


    /**
     * Constructor for the Simple Graph Wrapper.
     * @param pg the canonical Graph.
     */
    public SimpleGraphWrapper(Canonical pg) {
        super();

        setId(pg.getId());
        Set<Integer> incomingCurrent;
        Set<Integer> outgoingCurrent;
        Map<String, Integer> nodeId2vertex = new HashMap<>();
        Map<Integer, String> vertex2nodeId = new HashMap<>();

        vertices = new HashSet<>();
        connectors = new HashSet<>();
        events = new HashSet<>();
        functions = new HashSet<>();

        outgoingEdges = new HashMap<>();
        incomingEdges = new HashMap<>();
        labels = new HashMap<>();
        functionLabels = new HashSet<>();
        eventLabels = new HashSet<>();
        edges = new HashSet<>();

        int vertexId = 0;
        for (CPFNode n : pg.getNodes()) {
            vertices.add(vertexId);
            if (n.getName() != null) {
                labels.put(vertexId, n.getName().replace('\n', ' ').replace("\\n", " "));
            } else {
                LOGGER.warn("Node name is null,  Node id: " + n.getId() + ", " + n.getNodeType());
            }

            nodeId2vertex.put(n.getId(), vertexId);
            vertex2nodeId.put(vertexId, n.getId());

            if (Constants.FUNCTION.equals(pg.getNodeProperty(n.getId(), Constants.TYPE)) && n.getName() != null) {
                functionLabels.add(n.getName().replace('\n', ' '));
                functions.add(vertexId);
            } else if (Constants.EVENT.equals(pg.getNodeProperty(n.getId(), Constants.TYPE)) && n.getName() != null) {
                eventLabels.add(n.getName().replace('\n', ' '));
                events.add(vertexId);
            } else if (Constants.CONNECTOR.equals(pg.getNodeProperty(n.getId(), Constants.TYPE))) {
                connectors.add(vertexId);
            }

            vertexId++;
        }

        for (Integer v = 0; v < vertexId; v++) {
            CPFNode pgv = pg.getNode(vertex2nodeId.get(v));

            incomingCurrent = new HashSet<>();
            for (CPFNode preV : pg.getDirectPredecessors(pgv)) {
                incomingCurrent.add(nodeId2vertex.get(preV.getId()));
            }
            incomingEdges.put(v, incomingCurrent);

            outgoingCurrent = new HashSet<>();
            for (CPFNode postV : pg.getDirectSuccessors(pgv)) {
                outgoingCurrent.add(nodeId2vertex.get(postV.getId()));
                TwoVertices edge = new TwoVertices(v, nodeId2vertex.get(postV.getId()));
                edges.add(edge);
            }
            outgoingEdges.put(v, outgoingCurrent);
        }
    }
}
