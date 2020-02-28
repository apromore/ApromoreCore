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

package org.apromore.cpf;

// Java 2 Standard Edition
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Third party packages
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Utility for writing unit tests on CPF models.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CPFValidator {

    final Map<String, NodeType> nodeIdMap = new HashMap<>();

    final SetMultimap<NodeType, EdgeType> incomingEdges = HashMultimap.create();
    final SetMultimap<NodeType, EdgeType> outgoingEdges = HashMultimap.create();

    final CanonicalProcessType cpf;

    /** @param cpf  an arbitrary CPF model */
    public CPFValidator(final CanonicalProcessType cpf) {

        this.cpf = cpf;

        // Initialize nodeIdMap
        for (NetType net: cpf.getNet()) {
            for (NodeType node: net.getNode()) {
                nodeIdMap.put(node.getId(), node);
            }
        }

        // Initialize incomingEdges and outgoingEdges
        for (NetType net: cpf.getNet()) {
            for (EdgeType edge: net.getEdge()) {
                NodeType source = nodeIdMap.get(edge.getSourceId());
                NodeType target = nodeIdMap.get(edge.getTargetId());
                outgoingEdges.put(source, edge);
                incomingEdges.put(target, edge);
            }
        }
    }

    /**
     * Validate the topology of the CPF graph.
     *
     * @return a list of topology issues, possibly empty but never <code>null</code>
     */
    public List<String> validate() {

        final List<String> issues = new LinkedList<>();

        // Validate the topology
        for (NetType net: cpf.getNet()) {
            for (NodeType node: net.getNode()) {
                final int incomingEdgeCount = incomingEdges.get(node).size();
                final int outgoingEdgeCount = outgoingEdges.get(node).size();

                if (node instanceof EventType && incomingEdgeCount > 1) {
                    issues.add("Event " + node.getId() + " has " + incomingEdgeCount + " incoming edges; should never be more than one");
                }

                if (node instanceof EventType && outgoingEdges.get(node).size() > 1) {
                    issues.add("Event " + node.getId() + " has " + outgoingEdgeCount + " outgoing edges; should never be more than one");
                }

                if (node instanceof SplitType && incomingEdgeCount > 1) {
                    issues.add("Split " + node.getId() + " has " + incomingEdgeCount + " incoming edges; should never be more than one");
                }

                if (node instanceof JoinType && outgoingEdgeCount > 1) {
                    issues.add("Join " + node.getId() + " has " + outgoingEdgeCount + " outgoing edges; should never be more than one");
                }

                if (node instanceof TaskType && incomingEdgeCount > 1) {
                    issues.add("Task " + node.getId() + " has " + incomingEdgeCount + " incoming edges; should never be more than one");
                }

                if (node instanceof TaskType && outgoingEdgeCount > 1) {
                    issues.add("Task " + node.getId() + " has " + outgoingEdgeCount + " outgoing edges; should never be more than one");
                }
            }
        }

        return issues;
    }
}
