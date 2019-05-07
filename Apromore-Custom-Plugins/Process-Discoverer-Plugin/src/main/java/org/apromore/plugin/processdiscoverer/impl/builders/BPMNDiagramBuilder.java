/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
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

package org.apromore.plugin.processdiscoverer.impl.builders;

import org.apromore.plugin.processdiscoverer.impl.Arc;
import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.apromore.plugin.processdiscoverer.impl.VisualizationType;
import org.apromore.plugin.processdiscoverer.impl.collectors.ArcInfoCollector;
import org.apromore.plugin.processdiscoverer.impl.logprocessors.EventNameAnalyser;
import org.apromore.plugin.processdiscoverer.impl.util.StringValues;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class BPMNDiagramBuilder {

    private final static DecimalFormat decimalFormat = new DecimalFormat(StringValues.b[123]);
    private final static String start_name = "|>";
    private final static String end_name = "[]";
    private final ArcInfoCollector arcInfoCollector;

    private final BPMNDiagram bpmnDiagram = new BPMNDiagramImpl("");

    public BPMNDiagramBuilder(ArcInfoCollector arcInfoCollector) {
        this.arcInfoCollector = arcInfoCollector;
    }

    public BPMNDiagram getBpmnDiagram() {
        return bpmnDiagram;
    }

    public BPMNNode addNode(BPMNNode node) {
        if(node.getLabel().equals(start_name) || (node instanceof Event && ((Event) node).getEventType() == Event.EventType.START)) {
            return bpmnDiagram.addEvent(start_name, Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, false, null);
        }else if(node.getLabel().equals(end_name) || (node instanceof Event && ((Event) node).getEventType() == Event.EventType.END)) {
            return bpmnDiagram.addEvent(end_name, Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
        }else if(node instanceof Gateway){
            return bpmnDiagram.addGateway(node.getLabel(), ((Gateway) node).getGatewayType());
        }else {
            return bpmnDiagram.addActivity(node.getLabel(), false, false, false, false, false);
        }
    }

    public BPMNNode addNode(String label) {
        switch (label) {
            case start_name:
                return bpmnDiagram.addEvent(start_name, Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, false, null);
            case end_name:
                return bpmnDiagram.addEvent(end_name, Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
            default:
                return bpmnDiagram.addActivity(label, false, false, false, false, false);
        }
    }

    public Flow addArc(Set<Arc> arcs, BPMNNode source, BPMNNode target, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation) {
        double mainCost = computeCost(arcs,
                source,
                target,
                primaryType,
                primaryAggregation);

        double secondaryCost = computeCost(
                arcs,
                source,
                target,
                secondaryType,
                secondaryAggregation);

        return addFlow(source, target, "[" + decimalFormat.format(mainCost) + "\n" + decimalFormat.format(secondaryCost) + "]");
    }

    private double computeCost(Set<Arc> arcs, BPMNNode source, BPMNNode target, VisualizationType type, VisualizationAggregation aggregation) {
        double cost = 0;
        IntHashSet sources = new IntHashSet();
        IntHashSet targets = new IntHashSet();
        for(Arc arc : arcs) {
            sources.add(arc.getSource());
            targets.add(arc.getTarget());
        }

        if(source instanceof Activity && target instanceof Activity) {
            for(Arc arc : arcs)
                cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
            cost /= arcs.size();
        }else {
            double source_cost = computeCost(arcs, source, type, aggregation);
            double target_cost = computeCost(arcs, target, type, aggregation);
            cost = Math.max(source_cost, target_cost);
        }

        return cost;
    }

    private double computeCost(Set<Arc> arcs, BPMNNode node, VisualizationType type, VisualizationAggregation aggregation) {
        double cost = 0;
        if(node instanceof Gateway) {
            if (((Gateway) node).getGatewayType() == Gateway.GatewayType.PARALLEL) {
                for (Arc arc : arcs) cost = Math.max(cost, arcInfoCollector.getArcInfo(arc, type, aggregation));
            } else {
                for (Arc arc : arcs) cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
            }
        }else {
            for (Arc arc : arcs) cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
        }
        return cost;
    }

    public Flow addArc(Arc arc, BPMNNode source, BPMNNode target, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation) {
        Set<Arc> arcs = new UnifiedSet<>();
        arcs.add(arc);
        double mainCost = computeCost(arcs,
                source,
                target,
                primaryType,
                primaryAggregation);

        double secondaryCost = computeCost(
                arcs,
                source,
                target,
                secondaryType,
                secondaryAggregation);

        return addFlow(source, target, "[" + decimalFormat.format(mainCost) + "\n" + decimalFormat.format(secondaryCost) + "]");
    }

    public Flow addFlow(BPMNNode source, BPMNNode target, String label) {
        return bpmnDiagram.addFlow(source, target, label);
    }

    public static BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram) {
        BPMNDiagram gatewayDiagram = new BPMNDiagramImpl(bpmnDiagram.getLabel());

        Map<BPMNNode, BPMNNode> incoming = new HashMap<>();
        Map<BPMNNode, BPMNNode> outgoing = new HashMap<>();

        for(BPMNNode node : bpmnDiagram.getNodes()) {
            BPMNNode node1;
            if(node instanceof Event && node.getLabel().equals(start_name)) {
                node1 = gatewayDiagram.addEvent(start_name, Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, false, null);
            }else if(node instanceof Event && node.getLabel().equals(end_name)) {
                node1 = gatewayDiagram.addEvent(end_name, Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
            }else {
                node1 = gatewayDiagram.addActivity(node.getLabel(), false, false, false, false, false);
            }

            if(bpmnDiagram.getInEdges(node).size() > 1) {
                Gateway join = gatewayDiagram.addGateway("", Gateway.GatewayType.DATABASED);
                gatewayDiagram.addFlow(join, node1, "");
                incoming.put(node, join);
            }else {
                incoming.put(node, node1);
            }

            if(bpmnDiagram.getOutEdges(node).size() > 1) {
                Gateway split = gatewayDiagram.addGateway("", Gateway.GatewayType.DATABASED);
                gatewayDiagram.addFlow(node1, split, "");
                outgoing.put(node, split);
            }else {
                outgoing.put(node, node1);
            }
        }

        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            gatewayDiagram.addFlow(outgoing.get(edge.getSource()), incoming.get(edge.getTarget()), "");
        }

        return gatewayDiagram;
    }

    public static BPMNDiagram insertBPMNGatewaysWithCost(BPMNDiagram bpmnDiagram, VisualizationType type) {
        BPMNDiagram gatewayDiagram = new BPMNDiagramImpl(bpmnDiagram.getLabel());

        Map<BPMNNode, BPMNNode> incoming = new HashMap<>();
        Map<BPMNNode, BPMNNode> outgoing = new HashMap<>();

        for(BPMNNode node : bpmnDiagram.getNodes()) {
            BPMNNode node1;
            if(node.getLabel().equals(start_name)) {
                node1 = gatewayDiagram.addEvent(start_name, Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, false, null);
            }else if(node.getLabel().equals(end_name)) {
                node1 = gatewayDiagram.addEvent(end_name, Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
            }else {
                node1 = gatewayDiagram.addActivity(node.getLabel(), false, false, false, false, false);
            }

            if(bpmnDiagram.getInEdges(node).size() > 1) {
                double cost = 0;
                if(type == VisualizationType.FREQUENCY) {
                    for (BPMNEdge edge : bpmnDiagram.getInEdges(node)) {
                        cost += Double.parseDouble(edge.getLabel().substring(1, edge.getLabel().length() - 1));
                    }
                }

                Gateway join = gatewayDiagram.addGateway("", Gateway.GatewayType.DATABASED);
                gatewayDiagram.addFlow(join, node1, "[" + decimalFormat.format(cost) + "]");
                incoming.put(node, join);
            }else {
                incoming.put(node, node1);
            }

            if(bpmnDiagram.getOutEdges(node).size() > 1) {
                double cost = 0;
                if(type == VisualizationType.FREQUENCY) {
                    for (BPMNEdge edge : bpmnDiagram.getOutEdges(node)) {
                        cost += Double.parseDouble(edge.getLabel().substring(1, edge.getLabel().length() - 1));
                    }
                }

                Gateway split = gatewayDiagram.addGateway("", Gateway.GatewayType.DATABASED);
                gatewayDiagram.addFlow(node1, split, "[" + decimalFormat.format(cost) + "]");
                outgoing.put(node, split);
            }else {
                outgoing.put(node, node1);
            }
        }

        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            gatewayDiagram.addFlow(outgoing.get(edge.getSource()), incoming.get(edge.getTarget()), edge.getLabel());
        }

        return gatewayDiagram;
    }

}
