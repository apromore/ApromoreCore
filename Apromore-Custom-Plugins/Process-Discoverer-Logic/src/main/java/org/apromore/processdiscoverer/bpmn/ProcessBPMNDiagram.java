/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.processdiscoverer.bpmn;

import org.apromore.logman.Constants;
import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

/**
 * ProcessBPMNDiagram is a {@link SimpleBPMNDiagram} that is used to visualize an <b>AttributeGraph</b>.
 * It contains the following elements:
 *  - One start event
 *  - One end event
 *  - Multiple tasks
 *  - Sequence flows between events and tasks
 * 
 * @author Bruce Nguyen
 *
 */
public class ProcessBPMNDiagram extends SimpleBPMNDiagram {
    public final String LABEL_START_EVENT = Constants.START_NAME;
    public final String LABEL_END_EVENT = Constants.END_NAME;
    private BPMNNode startNode;
    private BPMNNode endNode;    
    
    public ProcessBPMNDiagram(FilteredGraph processGraph, AttributeLog log) {
        super(log);
        IntObjectHashMap<BPMNNode> map = new IntObjectHashMap<>();

        for(int node : processGraph.getNodes().toArray()) {
            BPMNNode bpmnNode = this.addNode(node);
            map.put(node, bpmnNode);
            if (node == processGraph.getSourceNode()) startNode = bpmnNode;
            if (node == processGraph.getSinkNode()) endNode = bpmnNode;
        }

        for(int arc : processGraph.getArcs().toArray()) {
            BPMNNode source = map.get(processGraph.getSource(arc));
            BPMNNode target = map.get(processGraph.getTarget(arc));
            this.addFlow(source, target, "");
        }
        
        BPMNDiagramHelper.updateStandardEventLabels(this);
    }
    
    public BPMNNode getStartNode() {
        return startNode;
    }
    
    public BPMNNode getEndNode() {
        return endNode;
    }
    
    public BPMNDiagram createBPMNDiagramWithGateways() {
        BPMNDiagram newDiagram = BPMNDiagramFactory.cloneBPMNDiagram(this);

        for (BPMNNode node : newDiagram.getNodes()) {
            // Add XOR-Split
            if (newDiagram.getOutEdges(node).size() > 1) {
                Gateway split = newDiagram.addGateway("", Gateway.GatewayType.DATABASED);
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : newDiagram.getOutEdges(node)) {
                    newDiagram.addFlow(split, edge.getTarget(), "");
                    newDiagram.removeEdge(edge);
                }
                newDiagram.addFlow(node, split, "");
            }

            // Add XOR-Join
            if (newDiagram.getInEdges(node).size() > 1) {
                Gateway join = newDiagram.addGateway("", Gateway.GatewayType.DATABASED);
                for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : newDiagram.getInEdges(node)) {
                    newDiagram.addFlow(edge.getSource(), join, "");
                    newDiagram.removeEdge(edge);
                }
                newDiagram.addFlow(join, node, ""); 
            }
        }

        return newDiagram;
    }    
}
