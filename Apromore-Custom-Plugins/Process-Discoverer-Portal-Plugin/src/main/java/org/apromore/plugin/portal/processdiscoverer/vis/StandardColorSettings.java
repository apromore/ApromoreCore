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

package org.apromore.plugin.portal.processdiscoverer.vis;

import java.awt.Color;

import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.processdiscoverer.utils.BPMNHelper;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;

/**
 * Contains all color settings for visualization
 * 
 * @author Bruce Nguyen
 *
 */
public class StandardColorSettings {
    private final String start_event_bg_color = "#C1C9B0";
    private final String end_event_bg_color = "#C0A3A1";     
    private final String start_event_edge_color = "#646464";
    private final String end_event_edge_color = "#646464";
    private final String gateway_bg_color = "white"; //"#C0A1BE";
    private final String edge_starting_ending_color = "#646464";
	private final String edge_cost_color = "#2E9A56";

	private final Color NODE_LIGHT_BLUE = new Color(226,242,248);
	private final Color NODE_DARK_BLUE = new Color(91,136,155);
	private final Color NODE_LIGHT_RED = new Color(238,206,211);
	private final Color NODE_DARK_RED = new Color(142,43,60);
	private final Color NODE_LIGHT_GREEN = new Color(226, 246, 233);
	private final Color NODE_DARK_GREEN = new Color(31, 105, 58);

	// Sync closer to dash palette
	// Two bases for the frequency and duration:
	// Blue, #84c7e3, 132, 199, 227
	// Red, #bb3a50, 187, 58, 80
	// private final Color NODE_LIGHT_BLUE = new Color(196,228,242);
	// private final Color NODE_DARK_BLUE = new Color(34,115,149);
	// private final Color NODE_LIGHT_RED = new Color(239,202,209);
	// private final Color NODE_DARK_RED = new Color(157,49,67);

	private final Color EDGE_LIGHT_BLUE = new Color(103,156,178);
	private final Color EDGE_DARK_BLUE = new Color(70,105,119);
	private final Color EDGE_LIGHT_RED = new Color(180, 100, 100);
	private final Color EDGE_DARK_RED = new Color(142,43,60);
	private final Color EDGE_LIGHT_GREEN = new Color(52, 173, 97);
	private final Color EDGE_DARK_GREEN = new Color(31, 105, 58);

	private double text_color_limit = 0.7;
	
    private final LinearColorBlender activity_frequency_blender = new LinearColorBlender(NODE_LIGHT_BLUE, NODE_DARK_BLUE);
    private final LinearColorBlender activity_duration_blender = new LinearColorBlender(NODE_LIGHT_RED, NODE_DARK_RED);
	private final LinearColorBlender activity_cost_blender = new LinearColorBlender(NODE_LIGHT_GREEN, NODE_DARK_GREEN);
    private final LinearColorBlender edge_frequency_blender = new LinearColorBlender(EDGE_LIGHT_BLUE, EDGE_DARK_BLUE);
    private final LinearColorBlender edge_duration_blender = new LinearColorBlender(EDGE_LIGHT_RED, EDGE_DARK_RED);
	private final LinearColorBlender edge_cost_blender = new LinearColorBlender(EDGE_LIGHT_GREEN, EDGE_DARK_GREEN);

    public String getStartEventBackgroundColor() {
    	return start_event_bg_color;
    }
    
    public String getEndEventBackgroundColor() {
    	return end_event_bg_color;
    }
    
    public String getGatewayBackgroundColor() {
    	return gateway_bg_color;
    }
    
	public String getActivityBackgroundColor(ContainableDirectedGraphElement element, VisualContext visContext,
			VisualSettings visSettings) throws UnsupportedElementException {
		if (!(element instanceof Activity)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMN Activity object.");
		}
		BPMNNode node = (BPMNNode)element;
		double node_relative_weight = visContext.getProcessAbstraction().getNodeRelativePrimaryWeight(node);
		if (node_relative_weight < 0) node_relative_weight = 0;
		
		AbstractionParams params = visContext.getProcessAbstraction().getAbstractionParams();
		LinearColorBlender colorBlender = (params.getPrimaryType() == MeasureType.FREQUENCY ?
			activity_frequency_blender : (params.getPrimaryType() == MeasureType.DURATION ?
			activity_duration_blender : activity_cost_blender));
        int background_color = colorBlender.blend(node_relative_weight).getRGB();
        return "#" + Integer.toHexString(background_color).substring(2);
	}
	
	public String getActivityTextColor(ContainableDirectedGraphElement element, VisualContext visContext,
			VisualSettings visSettings) throws UnsupportedElementException {
		if (!(element instanceof Activity)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMN Activity object.");
		}
		BPMNNode node = (BPMNNode)element;
		double node_relative_weight = visContext.getProcessAbstraction().getNodeRelativePrimaryWeight(node);
		if (node_relative_weight < 0) node_relative_weight = 0;
        return (node_relative_weight > text_color_limit) ? "white" : "black";
	}
	
    public String getStartEventEdgeColor() {
    	return start_event_edge_color;
    }    
    
    public String getEndEventEdgeColor() {
    	return end_event_edge_color;
    }   
    
    public String getStartingEndingEdgeColor() {
    	return edge_starting_ending_color;
    }     
    
	public String getEdgeColor(ContainableDirectedGraphElement element, VisualContext visContext,
			VisualSettings visSettings) throws UnsupportedElementException {
		if (!(element instanceof BPMNEdge<?, ?>)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMNEdge object.");
		}
		
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge = (BPMNEdge<?,?>)element;
		Abstraction abs = visContext.getProcessAbstraction();
		AbstractionParams params = visContext.getProcessAbstraction().getAbstractionParams();

        double edge_relative_weight = visContext.getProcessAbstraction().getEdgeRelativePrimaryWeight(edge);
        if (BPMNHelper.isStartingOrEndingEdge(edge, abs.getDiagram())) {
            return edge_starting_ending_color;
        } 
        else if (params.getPrimaryType() == MeasureType.FREQUENCY) {
        	return "#" + Integer.toHexString(edge_frequency_blender.blend(edge_relative_weight).getRGB()).substring(2);
        }
		else if (params.getPrimaryType() == MeasureType.DURATION) {
			return "#" + Integer.toHexString(edge_duration_blender.blend(edge_relative_weight).getRGB()).substring(2);
		}
        else {
            return edge_cost_color;
        }
	}
}
