/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.common.converters.bpstruct;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;

/**
 * Converts the Editor Basic Diagram into a JBPT Process Model.
 *
 * @author Cameron James
 */
public class BasicDiagramToProcessModel {

    private static final String AND = "AND";
    private static final String AND_CONNECTOR = "AndConnector";
    private static final String XOR = "XOR";
    private static final String XOR_CONNECTOR = "XorConnector";
    private static final String OR = "OR";
    private static final String OR_CONNECTOR = "OrConnector";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String GATEWAY_TYPE = "gatewaytype";


    /**
     * Does the conversion from Basic Diagram (sIgnavio) into a Process Model (JBPT) for B
     * @param diagram the Signavio Basic Diagram
     * @return the Process Model
     */
    public Process convert(BasicDiagram diagram) {
        final Map<String, BasicNode> nodeMap = new HashMap<>();
        final Map<String, BasicNode> gatewayMap = new HashMap<>();

        Process process = new Process();
        process.setName(diagram.getProperty(TITLE));
        if (process.getName() == null || process.getName().equals("")) {
            process.setName(diagram.getProperty(NAME));
        }

        prepareNodesForProcessing(diagram, nodeMap, gatewayMap);

        buildNodeList(process, nodeMap, gatewayMap);
        buildGatewayList(process, gatewayMap);
        buildEdgeList(process, diagram, nodeMap);

        return process;
    }

    private void buildNodeList(Process process, Map<String, BasicNode> nodeMap, Map<String, BasicNode> gatewayMap) {
        Task task;
        for (Map.Entry<String, BasicNode> node : nodeMap.entrySet()) {
            if (!gatewayMap.containsKey(node.getKey())) {
                task = buildTask(node.getValue());

                process.addTask(task);
            }
        }
    }

    /* Build the list of Gateways in the process object. */
    private void buildGatewayList(Process process, Map<String, BasicNode> gatewayMap) {
        Gateway gateway;
        for (Map.Entry<String, BasicNode> node : gatewayMap.entrySet()) {
            if (AND.equals(node.getValue().getProperty(GATEWAY_TYPE)) || node.getValue().getStencilId().equals(AND_CONNECTOR)) {
                gateway = new Gateway(GatewayType.AND, node.getValue().getProperty(NAME));
            } else if (XOR.equals(node.getValue().getProperty(GATEWAY_TYPE)) || node.getValue().getStencilId().equals(XOR_CONNECTOR)) {
                gateway = new Gateway(GatewayType.XOR, node.getValue().getProperty(NAME));
            } else if (OR.equals(node.getValue().getProperty(GATEWAY_TYPE)) || node.getValue().getStencilId().equals(OR_CONNECTOR)) {
                gateway = new Gateway(GatewayType.OR, node.getValue().getProperty(NAME));
            } else {
                gateway = new Gateway(GatewayType.UNDEFINED, node.getValue().getProperty(NAME));
            }

            gateway.setId(node.getKey());
            gateway.setName(findName(node.getValue()));

            process.addGateway(gateway);
        }
    }

    /* Build the list of Edges in the process object. */
    private void buildEdgeList(Process process, BasicDiagram diagram, Map<String, BasicNode> nodeMap) {
        BasicEdge edge;
        BasicShape source, target;
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            if (shape.isEdge()) {
                edge = (BasicEdge) shape;
                source = nodeMap.get(edge.getSource().getResourceId());
                target = nodeMap.get(edge.getTarget().getResourceId());

                process.addControlFlow(findNode(source, process.getNodes()), findNode(target, process.getNodes()));
            }
        }
    }


    private Task buildTask(BasicShape source) {
        Task node = new Task();
        node.setId(source.getResourceId());
        node.setName(findName(source));
        return node;
    }

    private Node findNode(BasicShape source, Collection<Node> nodes) {
        for (Node node : nodes) {
            if (node.getId().equals(source.getResourceId())) {
                return node;
            }
        }
        return null;
    }

    private String findName(BasicShape node) {
        String name = node.getProperty(TITLE);
        if (name == null || name.equals("")) {
            name = node.getProperty(NAME);
        }
        return name;
    }



    /* First pass, during which the EPC topology is examined (i.e. sourceMap and targetMap are populated) */
    private void prepareNodesForProcessing(BasicDiagram diagram, Map<String, BasicNode> nodeMap, Map<String, BasicNode> gatewayMap) {
        BasicNode node;
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            if (shape.isNode()) {
                node = (BasicNode) shape;
                nodeMap.put(node.getResourceId(), node);

                if (node.getIncomingsReadOnly().size() > 1 || node.getOutgoingsReadOnly().size() > 1) {
                    gatewayMap.put(node.getResourceId(), node);
                }
            }
        }
    }
}
