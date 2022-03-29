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

package org.apromore.plugin.portal.processdiscoverer.impl.json;

import org.apromore.plugin.portal.processdiscoverer.vis.UnsupportedElementException;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualContext;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualSettings;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;

/**
 * A toolkit of visualizers, provide a visualizer tool to use when needed
 * 
 * @author Bruce Nguyen
 *
 */
public class VisualToolkit {
	private StartEventVisualizer visStartEvent;
	private EndEventVisualizer visEndEvent;
	private ActivityVisualizer visActivity;
	private GatewayVisualizer visGateway;
	private EdgeVisualizer visEdge;
	
    public ElementVisualizer getVisualizer(ContainableDirectedGraphElement element,  
                                            VisualContext visContext, 
                                            VisualSettings visSettings) throws UnsupportedElementException {
        AbstractElementVisualizer visualizer = null;
        if (element instanceof Event && ((Event)element).getEventType() == EventType.START) {
            visualizer = (visStartEvent != null ? visStartEvent : (visStartEvent = new StartEventVisualizer(visContext, visSettings)));
        }
        else if (element instanceof Event && ((Event)element).getEventType() == EventType.END) {
            visualizer = (visEndEvent != null ? visEndEvent : (visEndEvent = new EndEventVisualizer(visContext, visSettings)));
        }
        else if (element instanceof Gateway) {
            visualizer = (visGateway != null ? visGateway : (visGateway = new GatewayVisualizer(visContext, visSettings)));
        }
        else if (element instanceof Activity) {
            visualizer = (visActivity != null ? visActivity : (visActivity = new ActivityVisualizer(visContext, visSettings)));
        }
        else if (element instanceof BPMNEdge<?,?>) {
            visualizer = (visEdge != null ? visEdge : (visEdge = new EdgeVisualizer(visContext, visSettings)));
        }
        else {
        	throw new UnsupportedElementException("Unsupported BPMN elements to convert to JSON format for visualizing.");
        }
        
        // Must set context and visual settings again as they could have been changed.
        visualizer.setVisualContext(visContext);
        visualizer.setVisualSettings(visSettings);
        return visualizer;
    }

}   
