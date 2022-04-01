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

package org.apromore.similaritysearch.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramSupport;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.apromore.similaritysearch.graph.Edge;
import org.apromore.similaritysearch.graph.Graph;
import org.apromore.similaritysearch.graph.Vertex;
import org.apromore.similaritysearch.graph.Vertex.GWType;
import org.apromore.similaritysearch.graph.Vertex.Type;
import org.apromore.similaritysearch.graph.VertexObjectRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelParser.class.getName());

    public static Graph readModel(BPMNDiagram mainNet) {
        Graph epcGraph = new Graph();
        if (mainNet == null) return epcGraph;
        
        epcGraph.name = mainNet.getLabel();
        epcGraph.ID = mainNet.getLabel();

        addNodes(mainNet, epcGraph);
        addEdges(mainNet, epcGraph);
        epcGraph.linkVertices();

        return epcGraph;
    }

    public static List<Graph> readModels(Collection<BPMNDiagram> diagrams) {
        ArrayList<Graph> l = new ArrayList<Graph>();
        if (diagrams == null) return l;

        for (BPMNDiagram d : diagrams) {
            l.add(readModel(d));
        }
        return l;
    }

    private static void addNodes(BPMNDiagram mainNet, Graph epcGraph) {
        BPMNDiagramSupport bpmnSupport = new BPMNDiagramSupport(mainNet);
        for (BPMNNode n : mainNet.getNodes()) {
            // gateways
            if (bpmnSupport.isGateway(n)) {
                boolean initialGW = true;
                Vertex v = new Vertex(bpmnSupport.isXORGateway(n) ? "xor" : (bpmnSupport.isANDGateway(n) ? "and" : "or"), 
                                        n.getId().toString());
                v.setLabel("");
                // this is initial gateway
                // TODO if we don't have graph that is already merged, then
                // set all gateways to initials
                if (initialGW) {
                    v.setInitialGW();
                }

                epcGraph.addVertex(v);
            } else if (bpmnSupport.isActivity(n)) {
                Vertex v = new Vertex(Type.function, n.getLabel(), n.getId().toString());
                epcGraph.addVertex(v);
            } else if (bpmnSupport.isEvent(n)) {
                Vertex v = new Vertex(Type.event, "", n.getId().toString());
                if (bpmnSupport.isStartEvent(n)) {
                    v.objectRefs.add(new VertexObjectRef(false, v.getID(), Boolean.TRUE, 
                                                        VertexObjectRef.InputOutput.Input, 
                                                        new HashSet<String>()));
                }
                else if (bpmnSupport.isEndEvent(n)) {
                    v.objectRefs.add(new VertexObjectRef(false, v.getID(), Boolean.TRUE, 
                                                        VertexObjectRef.InputOutput.Output, 
                                                        new HashSet<String>()));
                }
                epcGraph.addVertex(v);
            }
        }
    }

    private static void addEdges(BPMNDiagram mainNet, Graph epcGraph) {

        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = mainNet.getEdges();
        HashSet<String> graphLabels = new HashSet<String>();
        HashSet<String> allGraphLabels = new HashSet<String>();

        // add elements
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : edges) {
            Edge toAdd = new Edge(e.getSource().getId().toString(), e.getTarget().getId().toString(), e.getEdgeID().toString());
            toAdd.addLabels(new HashSet<>(Arrays.asList(new String[] {e.getLabel()})));
            allGraphLabels.addAll(graphLabels);
            epcGraph.addEdge(toAdd);
        }

        if (allGraphLabels.size() > 0) {
            epcGraph.setGraphConfigurable();
            epcGraph.addGraphLabels(allGraphLabels);
        }
    }
    
    private static EventType getEventType(Vertex v) {
        EventType eventType = null;
        if (v.getType().equals(Vertex.Type.event)) {
            for (VertexObjectRef o : v.objectRefs) {
                if (o.getInputOutput().equals(VertexObjectRef.InputOutput.Input)) {
                    eventType = EventType.START;
                    break;
                } else if (o.getInputOutput().equals(VertexObjectRef.InputOutput.Output)) {
                    eventType = EventType.END;
                    break;
                }
                else {
                    eventType = EventType.INTERMEDIATE;
                }
            }
        }
        return eventType;
    }

    public static BPMNDiagram writeModel(Graph g, IdGeneratorHelper idGenerator) {
        BPMNDiagram toReturn = BPMNDiagramFactory.newBPMNDiagram(g.getGraphLabel());
        
        Map<Vertex, BPMNNode> nodeMapping = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            BPMNNode node = null;
            if (v.getType().equals(Vertex.Type.event)) {
                node = toReturn.addEvent("", getEventType(v), EventTrigger.NONE, EventUse.THROW, true, null);
            } else if (v.getType().equals(Vertex.Type.function)) {
                node = toReturn.addActivity(v.getLabel(), false, false, false, false, false);
            } else if (v.getType().equals(Vertex.Type.gateway)) {
                if (v.getParents().size() > 1) {
                    if (v.getGWType().equals(GWType.and)) {
                        node = toReturn.addGateway("", GatewayType.DATABASED);
                    } else if (v.getGWType().equals(GWType.or)) {
                        node = toReturn.addGateway("", GatewayType.DATABASED);
                    } else if (v.getGWType().equals(GWType.xor)) {
                        node = toReturn.addGateway("", GatewayType.DATABASED);
                    }
                } else if (v.getChildren().size() > 1) {
                    if (v.getGWType().equals(GWType.and)) {
                        node = toReturn.addGateway("", GatewayType.DATABASED);
                    } else if (v.getGWType().equals(GWType.or)) {
                        node = toReturn.addGateway("", GatewayType.DATABASED);
                    } else if (v.getGWType().equals(GWType.xor)) {
                        node = toReturn.addGateway("", GatewayType.DATABASED);
                    }
                }
            }
            
            if (node != null) {
                nodeMapping.put(v, node);
            }

        }

        for (Vertex v : g.getVertices()) {
            for (Vertex target : v.getChildren()) {
                toReturn.addFlow(nodeMapping.get(v), nodeMapping.get(target), "");
            }
        }

        return toReturn;
    }


}
