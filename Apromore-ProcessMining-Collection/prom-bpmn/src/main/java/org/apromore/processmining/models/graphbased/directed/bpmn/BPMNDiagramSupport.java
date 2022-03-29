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
package org.apromore.processmining.models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.HashSet;

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;

public class BPMNDiagramSupport {
    private BPMNDiagram diagram;
    
    public BPMNDiagramSupport(BPMNDiagram d) {
        diagram = d;
    }
    
    public boolean isEvent(BPMNNode node) {
        return (node instanceof Event);
    }
    
    public boolean isStartEvent(BPMNNode node) {
        return isEvent(node) && ((Event)node).getEventType() == EventType.START;
    }
    
    public boolean isEndEvent(BPMNNode node) {
        return isEvent(node) && ((Event)node).getEventType() == EventType.END;
    }
    
    public boolean isGateway(BPMNNode node) {
        return (node instanceof Gateway);
    }
    
    public boolean isXORGateway(BPMNNode node) {
        return isGateway(node) && ((Gateway)node).getGatewayType() == GatewayType.DATABASED;
    }
    
    public boolean isXORSplitGateway(BPMNNode node) {
        return isXORGateway(node) && diagram.getOutEdges(node).size() >= 1 &&
                diagram.getInEdges(node).size() == 1;
    }
    
    public boolean isXORJoinGateway(BPMNNode node) {
        return isXORGateway(node) && diagram.getOutEdges(node).size() == 1 &&
                diagram.getInEdges(node).size() >= 1;
    }
    
    public boolean isANDGateway(BPMNNode node) {
        return isGateway(node) && ((Gateway)node).getGatewayType() == GatewayType.PARALLEL;
    }
    
    public boolean isANDSplitGateway(BPMNNode node) {
        return isANDGateway(node) && diagram.getOutEdges(node).size() >= 1 &&
                diagram.getInEdges(node).size() == 1;
    }
    
    public boolean isANDJoinGateway(BPMNNode node) {
        return isANDGateway(node) && diagram.getOutEdges(node).size() == 1 &&
                diagram.getInEdges(node).size() >= 1;
    }
    
    public boolean isORGateway(BPMNNode node) {
        return isGateway(node) && ((Gateway)node).getGatewayType() == GatewayType.INCLUSIVE;
    }
    
    public boolean isORSplitGateway(BPMNNode node) {
        return isORGateway(node) && diagram.getOutEdges(node).size() >= 1 &&
                diagram.getInEdges(node).size() == 1;
    }
    
    public boolean isORJoinGateway(BPMNNode node) {
        return isORGateway(node) && diagram.getOutEdges(node).size() == 1 &&
                diagram.getInEdges(node).size() >= 1;
    }
    
    public boolean isActivity(BPMNNode node) {
        return (node instanceof Activity || node instanceof CallActivity);
    }
    

    public Event getStartEvent() {
        for (BPMNNode node : diagram.getNodes()) {
            if (isStartEvent(node)) {
                return (Event)node;
            }
        }
        return null;
    }
    
    public Event getEndEvent() {
        for (BPMNNode node : diagram.getNodes()) {
            if (isEndEvent(node)) {
                return (Event)node;
            }
        }
        return null;
    }
    
    public Collection<BPMNNode> getTargets(BPMNNode node) {
        Collection<BPMNNode> result = new HashSet<BPMNNode>();
        diagram.getOutEdges(node).forEach(e -> result.add(e.getTarget()));
        return result;
    }
    
    public Collection<BPMNNode> getSources(BPMNNode node) {
        Collection<BPMNNode> result = new HashSet<BPMNNode>();
        diagram.getOutEdges(node).forEach(e -> result.add(e.getSource()));
        return result;
    }
}
