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

package org.apromore.plugin.portal.processdiscoverer.json;

import org.apache.commons.lang.StringUtils;
import org.apromore.plugin.portal.processdiscoverer.util.ColorGradient;
import org.apromore.plugin.portal.processdiscoverer.util.StringValues;
import org.apromore.plugin.portal.processdiscoverer.util.TimeConverter;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.abstraction.AbstractAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.Abstraction;
import org.apromore.processdiscoverer.dfg.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramLayouter;
import org.apromore.processdiscoverer.dfg.vis.LayoutElement;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;

import com.google.common.base.CharMatcher;

import java.awt.*;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class JSONBuilder {

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    private final double change_color_limit = 0.7;
    private final String start_name = SimplifiedLog.START_NAME;
    private final String end_name = SimplifiedLog.END_NAME;

    private final String EDGE_START_COLOR_FREQUENCY = "#646464";

    private final String ACTIVITY = "#FFFEEF";
    private final String GATEWAY = "white"; //"#C0A1BE";
    private final String START = "#C1C9B0";
    private final String END = "#C0A3A1";

    private final ColorGradient activity_frequency_gradient = new ColorGradient(new Color(241, 238, 246), new Color(4, 90, 141));
    private final ColorGradient activity_duration_gradient = new ColorGradient(new Color(254,240,217), new Color(179, 0, 0));
    private final ColorGradient arc_frequency_gradient = new ColorGradient(new Color(100, 100, 100), new Color(41, 41, 41));
    private final ColorGradient arc_duration_gradient = new ColorGradient(new Color(100, 100, 100), new Color(139, 0, 0));

    private final int TEXT_TO_PX_RATIO = 4; // from text to pixel is multiplied by this factor
    
    private Abstraction abs;
    private AbstractionParams params;

    public JSONBuilder(Abstraction abs) {
    	this.abs = abs;
    	this.params = abs.getAbstractionParams();
    }

    /**
     * Generate JSON for a log abstraction.
     * TODO: The weights of nodes use NodeInfoCollector or ArcInfoCollector
     * but those of arcs only use the numbers contained in the node and edge labels on the diagram. 
     * This is inconsistency.
     * @param used_bpmn_size
     * @return
     * @throws Exception
     */
    public JSONArray generateJSONFromBPMN(boolean used_bpmn_size) throws Exception {    	
        JSONArray graph = new JSONArray();
        Map<BPMNNode, Integer> mapping = new HashMap<>();
        int i = 1;
        int start_node = -1;
        int end_node = -1;

        String borderwidth = "1";
        String borderwidth_end = "3";

        String event_height = BPMNDiagramLayouter.EVENT_STD_HEIGHT + "px";//"8px"; //"15px";
        String event_width = BPMNDiagramLayouter.EVENT_STD_WIDTH + "px" ;//"8px"; //"15px";
        String gateway_height = BPMNDiagramLayouter.GATEWAY_STD_HEIGHT + "px"; //10px, "50px";
        String gateway_width = BPMNDiagramLayouter.GATEWAY_STD_WIDTH + "px"; //"50px";
        //String activity_height = "24px"; //"50px";
        //String activity_width = "20px";//"80px";

        String activity_font_size = "16"; //"10";
        String xor_gateway_font_size = "25"; //"20"; //(used_bpmn_size) ? "20" : "10";
        String and_gateway_font_size = "40"; //"30"; //(used_bpmn_size) ? "30" : "10";

//        int max_node_label_length = 0;
//        //String textwidth = "90px";
//        BPMNDiagram bpmnDiagram = abs.getDiagram();
//        for (BPMNNode node : getNodes(bpmnDiagram)) {
//        	max_node_label_length = Math.max(max_node_label_length, escapeChars(node.getLabel()).length());
//        }
//        
//        if(max_node_label_length * TEXT_TO_PX_RATIO > MAX_NODE_WIDTH) {
//        	node_with = (max_node_label_length*TEXT_TO_PX_RATIO*ACTUAL_NODE_WIDTH_FACTOR);
//        }
//        textwidth = Math.max(string_length*3 - 10, activity_width_measure) + "px";
//        activity_width = node_with + "px";
//        textwidth = activity_width ;
        String activity_width = BPMNDiagramLayouter.ACTIVITY_STD_WIDTH + "px";
        String activity_height = BPMNDiagramLayouter.ACTIVITY_STD_HEIGHT + "px";
        String textwidth = (BPMNDiagramLayouter.ACTIVITY_STD_WIDTH-5) + "px";
        
        double max_edge_text_length = 0;
        BPMNDiagram bpmnDiagram = abs.getDiagram();
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
        	max_edge_text_length = Math.max((max_edge_text_length+"").length(), (abs.getArcPrimaryWeight(edge)+"").length());
        	if (params.getSecondary()) max_edge_text_length = Math.max((max_edge_text_length+"").length(), (abs.getArcSecondaryWeight(edge)+"").length());
        }
        double minEdgeHorizontalLength = max_edge_text_length*TEXT_TO_PX_RATIO + 10; //10 is the padding
        
        //Adjust layout horizontally to accommodate the new activity width
//        if (abs instanceof BPMNAbstraction) {
//        	((BPMNAbstraction) abs).adjustHorizontalLayout(minEdgeHorizontalLength, node_with);
//        }

        Double[] minMax = this.getMinMax(bpmnDiagram.getNodes());
        //for(BPMNNode node : getNodes(bpmnDiagram)) {
        for(BPMNNode node : bpmnDiagram.getNodes()) {
            if(mapping.containsKey(node)) continue;
            if(node.getLabel().equals("unknown")) continue;

            JSONObject jsonOneNode = new JSONObject();
            mapping.put(node, i);
            jsonOneNode.put("id", i);
            jsonOneNode.put("name", "");
            jsonOneNode.put("textcolor", "black");
            jsonOneNode.put("textwidth", textwidth);
            
            LayoutElement nodeLayout = ((AbstractAbstraction) abs).getLayout().getLayoutElement(node);

            if(node instanceof Event) {
                jsonOneNode.put("shape", "ellipse");
//                jsonOneNode.put("width", (nodeLayout == null) ? event_width : nodeLayout.getWidth() + "px");
//                jsonOneNode.put("height", (nodeLayout == null) ? event_height : nodeLayout.getHeight() + "px");
                jsonOneNode.put("width", event_width);
                jsonOneNode.put("height", event_height);
                jsonOneNode.put("textsize", activity_font_size);
                if(((Event) node).getEventType() == Event.EventType.START || node.getLabel().equals(start_name)) {
                    start_node = i;
                    jsonOneNode.put("oriname", start_name);
                    jsonOneNode.put("color", START);
                    jsonOneNode.put("borderwidth", borderwidth);
                }else if(((Event) node).getEventType() == Event.EventType.END || node.getLabel().equals(end_name)) {
                    end_node = i;
                    jsonOneNode.put("oriname", end_name);
                    jsonOneNode.put("color", END);
                    jsonOneNode.put("borderwidth", borderwidth_end);
                }
            }else if(node instanceof Gateway) {
                jsonOneNode.put("shape", "diamond");
                jsonOneNode.put("color", GATEWAY);
//                jsonOneNode.put("width", (nodeLayout == null) ? gateway_width : nodeLayout.getWidth() + "px");
//                jsonOneNode.put("height", (nodeLayout == null) ? gateway_height : nodeLayout.getHeight() + "px");
                jsonOneNode.put("width", gateway_width);
                jsonOneNode.put("height", gateway_height);
                jsonOneNode.put("borderwidth", borderwidth);
                jsonOneNode.put("gatewayId", node.getId().toString().replace(" ", "_"));

                Gateway gateway = (Gateway) node;
                if(gateway.getGatewayType() == Gateway.GatewayType.DATABASED) {
                    jsonOneNode.put("name", "X");
                    jsonOneNode.put("oriname", "X");
                    jsonOneNode.put("textsize", xor_gateway_font_size);
                }else if(gateway.getGatewayType() == Gateway.GatewayType.PARALLEL) {
                    jsonOneNode.put("name", "+");
                    jsonOneNode.put("oriname", "+");
                    jsonOneNode.put("textsize", and_gateway_font_size);
                }else if(gateway.getGatewayType() == Gateway.GatewayType.INCLUSIVE) {
                    jsonOneNode.put("name", "O");
                    jsonOneNode.put("oriname", "O");
                    jsonOneNode.put("textsize", xor_gateway_font_size);
                }
            }else {
            	String node_oriname = node.getLabel();
            	// This is only for trace abstraction as the values are stored in the node label
            	if (node_oriname.contains("\\n")) { 
            		node_oriname =  node_oriname.substring(0, node_oriname.indexOf("\\n"));
            	}
            	jsonOneNode.put("oriname", escapeChars(node_oriname));
            	
            	//--------------------------------------------
            	// Adjust node display name to prevent it from
            	// overflowing the node shape horizontally and vertically
            	//--------------------------------------------
            	String node_displayname = node_oriname.trim(); 
            	node_displayname = escapeChars(node_displayname);
            	
            	if(params.getPrimaryType() == VisualizationType.DURATION) {
            		// No empty line if dual info
                	if (!params.getSecondary()) {
                		jsonOneNode.put("name", node_displayname + "\\n\\n" + TimeConverter.convertMilliseconds("" + abs.getNodePrimaryWeight(node)));
                	}
                	else {
                		jsonOneNode.put("name", node_displayname + "\\n\\n" + TimeConverter.convertMilliseconds("" + abs.getNodePrimaryWeight(node)) + "\\n" + decimalFormat.format(abs.getNodeSecondaryWeight(node)));
                	}
                }
                else {
                	// No empty line if dual info
                	if (!params.getSecondary()) {
                		jsonOneNode.put("name", node_displayname + "\\n\\n" + decimalFormat.format(abs.getNodePrimaryWeight(node)));
                	}
                	else {
                		jsonOneNode.put("name", node_displayname + "\\n\\n" + decimalFormat.format(abs.getNodePrimaryWeight(node)) + "\\n" + TimeConverter.convertMilliseconds("" + abs.getNodeSecondaryWeight(node)));
                	}
                }

                jsonOneNode.put("shape", "roundrectangle");

                String colors[];
                if(params.getPrimaryType() == null) colors = new String[] {ACTIVITY, "black"};
                else if(params.getPrimaryType() == VisualizationType.DURATION) colors = getDurationColor(node, minMax[0], minMax[1]);
                else colors = getFrequencyColor(node, minMax[0], minMax[1]);
                
                jsonOneNode.put("color", colors[0]);
                jsonOneNode.put("textcolor", colors[1]);
                jsonOneNode.put("width", activity_width);
                jsonOneNode.put("height", activity_height);
                jsonOneNode.put("textsize", activity_font_size);
                jsonOneNode.put("borderwidth", borderwidth);
            }
            JSONObject jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            
            // Add layout for BPMN abstraction

        	if (nodeLayout != null) {
        		double nodeX = 0, nodeY = 0;
                if (node instanceof Event) {
                	nodeX = nodeLayout.getX() + BPMNDiagramLayouter.EVENT_STD_WIDTH/2;
                	nodeY = nodeLayout.getY() + BPMNDiagramLayouter.EVENT_STD_HEIGHT/2;
                }
                else if (node instanceof Gateway) {
                	nodeX = nodeLayout.getX() + BPMNDiagramLayouter.GATEWAY_STD_WIDTH/2;
                	nodeY = nodeLayout.getY() + BPMNDiagramLayouter.GATEWAY_STD_HEIGHT/2;
                }
                else {
                	nodeX = nodeLayout.getX() + BPMNDiagramLayouter.ACTIVITY_STD_WIDTH/2;
                	nodeY = nodeLayout.getY() + BPMNDiagramLayouter.ACTIVITY_STD_HEIGHT/2;
                }
                
        		JSONObject jsonPosition = new JSONObject();
        		jsonPosition.put("x", nodeX);
        		jsonPosition.put("y", nodeY);
        		jsonDataNode.put("position", jsonPosition);
        	}
            
            graph.put(jsonDataNode);
            i++;
        }

        double maxWeight = 0.0;
        double minWeight = 0.0;
//        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : getEdges(bpmnDiagram)) {
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            double number = abs.getArcPrimaryWeight(edge);
            maxWeight = Math.max(maxWeight, number);
            minWeight = Math.min(minWeight, number);
        }

//        for(BPMNEdge<BPMNNode, BPMNNode> edge : getEdges(bpmnDiagram)) {
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            Integer source = mapping.get(edge.getSource());
            Integer target = mapping.get(edge.getTarget());

            JSONObject jsonOneLink = new JSONObject();
            jsonOneLink.put("source", source);
            jsonOneLink.put("target", target);

            if(source == start_node) jsonOneLink.put("style", (used_bpmn_size) ? "solid" : "dashed");
            else if(target == end_node) jsonOneLink.put("style", (used_bpmn_size) ? "solid" : "dashed");
            else jsonOneLink.put("style", "solid");
            
            double mainNumber = abs.getArcPrimaryWeight(edge);
            double secondaryNumber = abs.getArcSecondaryWeight(edge);
            
            LayoutElement edgeLayout = ((AbstractAbstraction) abs).getLayout().getLayoutElement(edge.getEdgeID().toString());

            if(mainNumber != 1.0 || maxWeight != 0) {
                BigDecimal bd;
                if (maxWeight != minWeight) {
                	bd = new BigDecimal(((Double.parseDouble(mainNumber+"") - minWeight) * 100.0 / (maxWeight - minWeight)));
                }
                else {
                	bd = new BigDecimal(0.5d);
                }
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                if (params.getPrimaryType() == VisualizationType.DURATION && (source == start_node || target == end_node)) {
                    jsonOneLink.put("strength", 0);
                    jsonOneLink.put("label", "");
                    jsonOneLink.put("color", EDGE_START_COLOR_FREQUENCY);
                } else {
                    jsonOneLink.put("strength", bd.doubleValue());
                    if (params.getPrimaryType() == VisualizationType.DURATION) {
                    	jsonOneLink.put("label", TimeConverter.convertMilliseconds(mainNumber+"") + ((params.getSecondary()) ? "\\n" + decimalFormat.format(Double.parseDouble(secondaryNumber+"")) : ""));
                        jsonOneLink.put("color", "#" + Integer.toHexString(arc_duration_gradient.generateColor(bd.doubleValue() / 100).getRGB()).substring(2));
                    }else {
                        jsonOneLink.put("label", decimalFormat.format(mainNumber) + ((params.getSecondary()) ? "\\n" + TimeConverter.convertMilliseconds(secondaryNumber+"") : ""));
                        jsonOneLink.put("color", "#" + Integer.toHexString(arc_frequency_gradient.generateColor(bd.doubleValue() / 100).getRGB()).substring(2));
                    }
                }
            }else {
                jsonOneLink.put("strength", 0);
                jsonOneLink.put("label", "");
                jsonOneLink.put("color", EDGE_START_COLOR_FREQUENCY);
            }
            
            //Add (distance, weight) points for the edge
            if (edge.getSource() != edge.getTarget()) {
	            if (!edgeLayout.getDWPoints().isEmpty()) {
	            	DecimalFormat df = new DecimalFormat("0.00");
		            String point_distances = "";
		            String point_weights = "";
		            for (Point2D dw : edgeLayout.getDWPoints()) {
		            	point_distances += (df.format(dw.getX()) + " ");
		            	point_weights += (df.format(dw.getY()) + " ");
		            }
		            jsonOneLink.put("edge-style", "unbundled-bezier");
		            jsonOneLink.put("point-distances", point_distances.trim());
		            jsonOneLink.put("point-weights", point_weights.trim());
	            }
	            else {
	            	jsonOneLink.put("edge-style", "unbundled-bezier");
	            	jsonOneLink.put("point-distances", "0");
		            jsonOneLink.put("point-weights", "0.5");
	            }
            }
            else {
            	jsonOneLink.put("edge-style", "bezier");
            }

            JSONObject jsonDataLink = new JSONObject();
            jsonDataLink.put("data", jsonOneLink);
            graph.put(jsonDataLink);
        }

        return graph;    	
    }
    
   
    /**
     * To make string conform to JSON rules. 
     * See ProcessDiscovererController.display()
     * Note: escape characters are doubled since expression patterns also use the same escape characters 
     * @param value
     */
    private String escapeChars(String value) {
    	return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    private Event[] getBoundaryEvents(BPMNDiagram bpmnDiagram) {
        Set<Event> nodes = new HashSet<>(bpmnDiagram.getEvents());
        Event[] array_events = nodes.toArray(new Event[nodes.size()]);
        for(Event n : array_events) {
            if(n.getBoundingNode() == null) {
                nodes.remove(n);
            }else if(n.getEventType() == Event.EventType.START || n.getEventType() == Event.EventType.END) {
                nodes.remove(n);
            }
        }
        Event[] array_nodes = nodes.toArray(new Event[nodes.size()]);
        Arrays.sort(array_nodes, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return array_nodes;
    }

    private SubProcess[] getSubprocesses(BPMNDiagram bpmnDiagram) {
        Collection<SubProcess> nodes = bpmnDiagram.getSubProcesses();
        SubProcess[] array_nodes = nodes.toArray(new SubProcess[nodes.size()]);
        Arrays.sort(array_nodes, new Comparator<SubProcess>() {
            @Override
            public int compare(SubProcess o1, SubProcess o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return array_nodes;
    }

    // Get nodes in order to ensure the order of nodes
    // created in JSON is consistent in order to maintain the same
    // layout between different runs.
    private BPMNNode[] getNodes(BPMNDiagram bpmnDiagram) {
        Set<BPMNNode> nodes = bpmnDiagram.getNodes();
        BPMNNode[] array_nodes = nodes.toArray(new BPMNNode[nodes.size()]);
        Arrays.sort(array_nodes, new Comparator<BPMNNode>() {
            @Override
            public int compare(BPMNNode o1, BPMNNode o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return array_nodes;
    }

    // Get edges in order to ensure the order of edges
    // created in JSON is consistent in order to maintain the same
    // layout between different runs.
    private BPMNEdge<BPMNNode, BPMNNode>[] getEdges(BPMNDiagram bpmnDiagram) {
        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = bpmnDiagram.getEdges();

        BPMNEdge<BPMNNode, BPMNNode>[] array_edges = edges.toArray(new BPMNEdge[edges.size()]);
        Arrays.sort(array_edges, new Comparator<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>() {
            @Override
            public int compare(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> o1, BPMNEdge<? extends BPMNNode, ? extends BPMNNode> o2) {
                if(o1.getSource().getLabel().equals(o2.getSource().getLabel())) {
                    return o1.getTarget().getLabel().compareTo(o2.getTarget().getLabel());
                }
                return o1.getSource().getLabel().compareTo(o2.getSource().getLabel());
            }
        });
        return array_edges;
    }
    
    private Double[] getMinMax(Set<BPMNNode> nodes) {
        double max = 0;
        double min = Double.MAX_VALUE;
        for (BPMNNode n : nodes) {
            if (!(n instanceof Gateway) && !(n instanceof Event)) {
            	max = Math.max(max, abs.getNodePrimaryWeight(n));
            	min = Math.min(min, abs.getNodePrimaryWeight(n));
            }
        }
        return new Double[] {min,max};
    }

    private String[] getFrequencyColor(BPMNNode node, double min, double max) {
        if(!(node instanceof Gateway) && !(node instanceof Event)) {
            //double node_frequency = abs.getLogDFG().getNodeInfoCollector().getNodeFrequency(true, node.getLabel(), params.getPrimaryAggregation());
        	double node_frequency = abs.getNodePrimaryWeight(node);
            double number = (node_frequency - min) / (max - min);
            if(Double.isNaN(number)) number = 1;
            int background_color = activity_frequency_gradient.generateColor(number).getRGB();
            String font_color = (number > change_color_limit) ? "white" : "black";
            return new String[] {"#" + Integer.toHexString(background_color).substring(2), font_color};
        }else {
        	return null;
        }
    }
    
    //skip: true if this is for a trace
    private String[] getDurationColor(BPMNNode node, double min, double max) {
        if(max == 0 || max==min) {
        	return new String[] {"#FEF0D9", "black"};
        }
        else if(!(node instanceof Gateway) && !(node instanceof Event)) {
            double node_duration = abs.getNodePrimaryWeight(node);
            double number = (node_duration - min) / (max - min);
            int background_color = activity_duration_gradient.generateColor(number).getRGB();
            String font_color = (number > change_color_limit) ? "white" : "black";
            return new String[]{"#" + Integer.toHexString(background_color).substring(2), font_color};
        } else {
        	return null;
        }
    }

}
