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

import java.util.Set;

import org.apromore.logman.Constants;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

/**
 * BPMNDiagram utility
 * 
 * @author Bruce Nguyen
 *
 */
public class BPMNDiagramHelper {
    /**
     * Return all activity/end event nodes that a given node connects to
     * The path from the given node to these target nodes can contain gateways
     * If the given node is an activity node, return itself 
     * @param diagram
     * @param node
     * @param visited
     * @return
     */
    public static Set<BPMNNode> getTargets(BPMNDiagram diagram, BPMNNode node, Set<BPMNNode> visited) {
        Set<BPMNNode> nodes = new UnifiedSet<>();
        if(node instanceof Activity || node instanceof Event) {
            nodes.add(node);
            return nodes;
        }
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getOutEdges(node)) {
            BPMNNode target = edge.getTarget();
            if(!visited.contains(target)) {
                if (target instanceof Activity || target instanceof Event) {
                    visited.add(target);
                    nodes.add(target);
                } else {
                    nodes.addAll(getTargets(diagram, target, visited));
                }
            }
        }
        return nodes;
    }

    /**
     * Return all activity/start event nodes that connect to a given node.
     * The path from these source nodes to the given node can contain gateways
     * If the given node is an activity node, return itself 
     * @param diagram
     * @param node: the given node 
     * @param visited
     * @return
     */
    public static Set<BPMNNode> getSources(BPMNDiagram diagram, BPMNNode node, Set<BPMNNode> visited) {
        Set<BPMNNode> nodes = new UnifiedSet<>();
        if(node instanceof Activity || node instanceof Event) {
            nodes.add(node);
            return nodes;
        }
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getInEdges(node)) {
            BPMNNode source = edge.getSource();
            if(!visited.contains(source)) {
                if (source instanceof Activity || source instanceof Event) {
                    visited.add(source);
                    nodes.add(source);
                } else {
                    nodes.addAll(getSources(diagram, source, visited));
                }
            }
        }
        return nodes;
    }
    
    public static void updateStandardEventLabels(BPMNDiagram diagram) {
    	for (BPMNNode node : diagram.getNodes()) {
        	if (node instanceof Event) {
        		if (((Event) node).getEventType() == Event.EventType.START) {
        			node.getAttributeMap().put(AttributeMap.LABEL, Constants.START_NAME);
        		}
        		else if (((Event) node).getEventType() == Event.EventType.END) {
        			node.getAttributeMap().put(AttributeMap.LABEL, Constants.END_NAME);
        		}
        	}
        }
    }
    

    
}
