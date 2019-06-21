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

package org.apromore.processdiscoverer.dfg.vis;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.abstraction.Abstraction;
import org.apromore.processdiscoverer.util.ColorGradient;
import org.apromore.processdiscoverer.util.StringValues;
import org.apromore.processdiscoverer.util.TimeConverter;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class JSONBuilder {

    private final DecimalFormat decimalFormat = new DecimalFormat(StringValues.b[123]);

    private final double change_color_limit = 0.7;
    private final String start_name = "|>";
    private final String end_name = "[]";

    private final String EDGE_START_COLOR_FREQUENCY = "#646464";

    private final String ACTIVITY = "#FFFEEF";
    private final String GATEWAY = "white"; //"#C0A1BE";
    private final String START = "#C1C9B0";
    private final String END = "#C0A3A1";

    private final ColorGradient activity_frequency_gradient = new ColorGradient(new Color(241, 238, 246), new Color(4, 90, 141));
    private final ColorGradient activity_duration_gradient = new ColorGradient(new Color(254,240,217), new Color(179, 0, 0));
    private final ColorGradient arc_frequency_gradient = new ColorGradient(new Color(100, 100, 100), new Color(41, 41, 41));
    private final ColorGradient arc_duration_gradient = new ColorGradient(new Color(100, 100, 100), new Color(139, 0, 0));
    
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

        String event_height = (used_bpmn_size) ? "37px" : "15px";
        String event_width = (used_bpmn_size) ? "37px" : "15px";
        String gateway_height = (used_bpmn_size) ? "50px" : "50px";
        String gateway_width = (used_bpmn_size) ? "50px" :  "50px";
        String activity_height = (used_bpmn_size) ? "100px" : "50px";
        String activity_width = (used_bpmn_size) ? "125px" : "80px";

        String activity_font_size = (used_bpmn_size) ? "15" : "10";
        String xor_gateway_font_size = (used_bpmn_size) ? "20" : "20"; //(used_bpmn_size) ? "20" : "10";
        String and_gateway_font_size = (used_bpmn_size) ? "30" : "30"; //(used_bpmn_size) ? "30" : "10";

        int string_length = 0;
        String textwidth = "90px";
        BPMNDiagram bpmnDiagram = abs.getDiagram();
        for (BPMNNode node : getNodes(bpmnDiagram)) {
            string_length = Math.max(string_length, escapeChars(node.getLabel()).length());
        }
        if(!used_bpmn_size) {
            if(string_length * 6 > 80) activity_width = (string_length * 6) + "px";
            textwidth = Math.max(string_length*6 - 10, 20) + "px";
        }

        ObjectIntHashMap<Event> boundary = new ObjectIntHashMap<>();
        Map<BPMNNode, Event> bounded = new HashMap<>();
        for(Event node : getBoundaryEvents(bpmnDiagram)) {
            boundary.put(node, i);
            bounded.put(node.getBoundingNode(), node);
            i++;
        }

        ObjectIntHashMap<SubProcess> subprocesses = new ObjectIntHashMap<>();
        for(SubProcess node : getSubprocesses(bpmnDiagram)) {
            subprocesses.put(node, i);
            i++;
        }

        for(Event node : getBoundaryEvents(bpmnDiagram)) {
            int invisible_subprocess = boundary.get(node);
            JSONObject jsonOneNode = new JSONObject();
            jsonOneNode.put("id", invisible_subprocess);
            jsonOneNode.put("name", "");//node.getLabel().replaceAll("'", "")); //need to use escapeChars
            jsonOneNode.put("shape", "roundrectangle");
            jsonOneNode.put("color", "black");
            jsonOneNode.put("width", activity_width);
            jsonOneNode.put("height", activity_height);
            jsonOneNode.put("textsize", activity_font_size);
            jsonOneNode.put("textcolor", "black");
            jsonOneNode.put("textwidth", textwidth);
            jsonOneNode.put("borderwidth", "0");
            if(node.getBoundingNode().getParentSubProcess() != null) {
                jsonOneNode.put("parent", subprocesses.get(node.getBoundingNode().getParentSubProcess()));
            }
            JSONObject jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            graph.put(jsonDataNode);

            mapping.put(node, i);
            jsonOneNode = new JSONObject();
            jsonOneNode.put("id", i);
            jsonOneNode.put("name", "");//node.getLabel().replaceAll("'", "")); //need to use escapeChars
            jsonOneNode.put("shape", "ellipse");
            jsonOneNode.put("color", "white");
            jsonOneNode.put("width", event_width);
            jsonOneNode.put("height", event_height);
            jsonOneNode.put("textsize", activity_font_size);
            jsonOneNode.put("textcolor", "black");
            jsonOneNode.put("textwidth", textwidth);
            jsonOneNode.put("borderwidth", "1");
            jsonOneNode.put("parent", invisible_subprocess);
            jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            graph.put(jsonDataNode);
            i++;
        }

        for(SubProcess node : getSubprocesses(bpmnDiagram)) {
            int j = subprocesses.get(node);
            mapping.put(node, j);
            JSONObject jsonOneNode = new JSONObject();
            jsonOneNode.put("id", j);
            jsonOneNode.put("name", "");//node.getLabel().replaceAll("'", "")); //need to use escapeChars
            jsonOneNode.put("shape", "roundrectangle");
            jsonOneNode.put("color", "white");
            jsonOneNode.put("width", event_width);
            jsonOneNode.put("height", event_height);
            jsonOneNode.put("textsize", activity_font_size);
            jsonOneNode.put("textcolor", "black");
            jsonOneNode.put("textwidth", textwidth);
            jsonOneNode.put("borderwidth", "2");
            if(bounded.containsKey(node)) {
                jsonOneNode.put("parent", boundary.get(bounded.get(node)));
            }else if(node.getParentSubProcess() != null) {
                jsonOneNode.put("parent", subprocesses.get(node.getParentSubProcess()));
            }

            JSONObject jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            graph.put(jsonDataNode);
        }

        Double[] minMax = this.getMinMax(bpmnDiagram.getNodes());
        for(BPMNNode node : getNodes(bpmnDiagram)) {
            if(mapping.containsKey(node)) continue;
            if(node.getLabel().equals("unknown")) continue;

            JSONObject jsonOneNode = new JSONObject();
            mapping.put(node, i);
            jsonOneNode.put("id", i);
            jsonOneNode.put("name", "");
            jsonOneNode.put("textcolor", "black");
            jsonOneNode.put("textwidth", textwidth);
            if(node.getParentSubProcess() != null) {
                jsonOneNode.put("parent", subprocesses.get(node.getParentSubProcess()));
            }

            if(node instanceof Event) {
                jsonOneNode.put("shape", "ellipse");
                jsonOneNode.put("width", event_width);
                jsonOneNode.put("height", event_height);
                jsonOneNode.put("textsize", activity_font_size);
                if(((Event) node).getEventType() == Event.EventType.START || node.getLabel().equals(start_name)) {
                    start_node = i;
                    jsonOneNode.put("color", START);
                    jsonOneNode.put("borderwidth", borderwidth);
                }else if(((Event) node).getEventType() == Event.EventType.END || node.getLabel().equals(end_name)) {
                    end_node = i;
                    jsonOneNode.put("color", END);
                    jsonOneNode.put("borderwidth", borderwidth_end);
                }
            }else if(node instanceof Gateway) {
                jsonOneNode.put("shape", "diamond");
                jsonOneNode.put("color", GATEWAY);
                jsonOneNode.put("width", gateway_width);
                jsonOneNode.put("height", gateway_height);
                jsonOneNode.put("borderwidth", borderwidth);
                jsonOneNode.put("gatewayId", node.getId().toString().replace(" ", "_"));

                Gateway gateway = (Gateway) node;
                if(gateway.getGatewayType() == Gateway.GatewayType.DATABASED) {
                    jsonOneNode.put("name", "X");
                    jsonOneNode.put("textsize", xor_gateway_font_size);
                }else if(gateway.getGatewayType() == Gateway.GatewayType.PARALLEL) {
                    jsonOneNode.put("name", "+");
                    jsonOneNode.put("textsize", and_gateway_font_size);
                }else if(gateway.getGatewayType() == Gateway.GatewayType.INCLUSIVE) {
                    jsonOneNode.put("name", "O");
                    jsonOneNode.put("textsize", xor_gateway_font_size);
                }
            }else {
            	String node_name = node.getLabel();
            	if (node_name.contains("\\n")) { // This is for trace abstraction as the values are stored in node labels.
                    node_name =  escapeChars(node_name.substring(0, node_name.indexOf("\\n")));
            	}
            	else {
            		node_name = escapeChars(node_name);
            	}
                if(params.getPrimaryType() == null) {
                	jsonOneNode.put("name", escapeChars(node.getLabel()));
                } if(params.getPrimaryType() == VisualizationType.DURATION) {
                	jsonOneNode.put("name", node_name + "\\n\\n" + TimeConverter.convertMilliseconds("" + abs.getNodePrimaryWeight(node)) + ((params.getSecondary()) ? "\\n\\n" + decimalFormat.format(abs.getNodeSecondaryWeight(node)) : ""));
                }
                else {
                	jsonOneNode.put("name", node_name + "\\n\\n" + decimalFormat.format(abs.getNodePrimaryWeight(node)) + ((params.getSecondary()) ? "\\n\\n" + TimeConverter.convertMilliseconds("" + abs.getNodeSecondaryWeight(node)) : ""));
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
            graph.put(jsonDataNode);
            i++;
        }

        double maxWeight = 0.0;
        double minWeight = 0.0;
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : getEdges(bpmnDiagram)) {
//            String number = edge.getLabel();
//            if (number.contains("[")) {
//                if(number.contains("\n")) number = number.substring(1, number.indexOf("\n"));
//                else number = number.substring(1, number.length() - 1);
//            } else {
//                number = "0";
//            }
            double number = abs.getArcPrimaryWeight(edge);
            maxWeight = Math.max(maxWeight, number);
            minWeight = Math.min(minWeight, number);
        }

        for(BPMNEdge<BPMNNode, BPMNNode> edge : getEdges(bpmnDiagram)) {
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
                    	jsonOneLink.put("label", TimeConverter.convertMilliseconds(mainNumber+"") + ((params.getSecondary()) ? "\\n\\n" + decimalFormat.format(Double.parseDouble(secondaryNumber+"")) : ""));
                        jsonOneLink.put("color", "#" + Integer.toHexString(arc_duration_gradient.generateColor(bd.doubleValue() / 100).getRGB()).substring(2));
                    }else {
                        jsonOneLink.put("label", decimalFormat.format(mainNumber) + ((params.getSecondary()) ? "\\n\\n" + TimeConverter.convertMilliseconds(secondaryNumber+"") : ""));
                        jsonOneLink.put("color", "#" + Integer.toHexString(arc_frequency_gradient.generateColor(bd.doubleValue() / 100).getRGB()).substring(2));
                    }
                }
            }else {
                jsonOneLink.put("strength", 0);
                jsonOneLink.put("label", "");
                jsonOneLink.put("color", EDGE_START_COLOR_FREQUENCY);
            }

            JSONObject jsonDataLink = new JSONObject();
            jsonDataLink.put("data", jsonOneLink);
            graph.put(jsonDataLink);
        }

        return graph;    	
    }
    
//    /**
//     * This method is used for a trace abstraction.
//     * TODO: The weights of nodes and arcs do not use NodeInfoCollector or ArcInfoCollector
//     * but only use the numbers contained in the node and edge labels on the diagram. This is inconsistency.
//     * @param type
//     * @param used_bpmn_size
//     * @param secondary
//     * @return
//     * @throws JSONException
//     */
//    public JSONArray generateJSONFromBPMN(VisualizationType type, boolean used_bpmn_size, boolean secondary) throws JSONException {
//    	boolean skip = true; //only take the weights from node and arc labels on the diagram
//        JSONArray graph = new JSONArray();
//        Map<BPMNNode, Integer> mapping = new HashMap<>();
//        int i = 1;
//        int start_node = -1;
//        int end_node = -1;
//
//        String borderwidth = "1";
//        String borderwidth_end = "3";
//
//        String event_height = (used_bpmn_size) ? "37px" : "15px";
//        String event_width = (used_bpmn_size) ? "37px" : "15px";
//        String gateway_height = (used_bpmn_size) ? "50px" : "50px";
//        String gateway_width = (used_bpmn_size) ? "50px" :  "50px";
//        String activity_height = (used_bpmn_size) ? "100px" : "50px";
//        String activity_width = (used_bpmn_size) ? "125px" : "80px";
//
//        String activity_font_size = (used_bpmn_size) ? "15" : "10";
//        String xor_gateway_font_size = (used_bpmn_size) ? "20" : "10";
//        String and_gateway_font_size = (used_bpmn_size) ? "30" : "10";
//
//        int string_length = 0;
//        String textwidth = "90px";
//        BPMNDiagram bpmnDiagram = this.abs.getDiagram(); 
//        for (BPMNNode node : getNodes(bpmnDiagram)) {
//            string_length = Math.max(string_length, escapeChars(node.getLabel()).length());
//        }
//        if(!used_bpmn_size) {
//            if(string_length * 6 > 80) activity_width = (string_length * 6) + "px";
//            textwidth = ((string_length * 6) - 10) + "px";
//        }
//
//        ObjectIntHashMap<Event> boundary = new ObjectIntHashMap<>();
//        Map<BPMNNode, Event> bounded = new HashMap<>();
//        for(Event node : getBoundaryEvents(bpmnDiagram)) {
//            boundary.put(node, i);
//            bounded.put(node.getBoundingNode(), node);
//            i++;
//        }
//
//        ObjectIntHashMap<SubProcess> subprocesses = new ObjectIntHashMap<>();
//        for(SubProcess node : getSubprocesses(bpmnDiagram)) {
//            subprocesses.put(node, i);
//            i++;
//        }
//
//        for(Event node : getBoundaryEvents(bpmnDiagram)) {
//            int invisible_subprocess = boundary.get(node);
//            JSONObject jsonOneNode = new JSONObject();
//            jsonOneNode.put("id", invisible_subprocess);
//            jsonOneNode.put("name", "");//node.getLabel().replaceAll("'", "")); //need to use escapeChars
//            jsonOneNode.put("shape", "roundrectangle");
//            jsonOneNode.put("color", "black");
//            jsonOneNode.put("width", activity_width);
//            jsonOneNode.put("height", activity_height);
//            jsonOneNode.put("textsize", activity_font_size);
//            jsonOneNode.put("textcolor", "black");
//            jsonOneNode.put("textwidth", textwidth);
//            jsonOneNode.put("borderwidth", "0");
//            if(node.getBoundingNode().getParentSubProcess() != null) {
//                jsonOneNode.put("parent", subprocesses.get(node.getBoundingNode().getParentSubProcess()));
//            }
//            JSONObject jsonDataNode = new JSONObject();
//            jsonDataNode.put("data", jsonOneNode);
//            graph.put(jsonDataNode);
//
//            mapping.put(node, i);
//            jsonOneNode = new JSONObject();
//            jsonOneNode.put("id", i);
//            jsonOneNode.put("name", "");//node.getLabel().replaceAll("'", "")); //need to use escapeChars
//            jsonOneNode.put("shape", "ellipse");
//            jsonOneNode.put("color", "white");
//            jsonOneNode.put("width", event_width);
//            jsonOneNode.put("height", event_height);
//            jsonOneNode.put("textsize", activity_font_size);
//            jsonOneNode.put("textcolor", "black");
//            jsonOneNode.put("textwidth", textwidth);
//            jsonOneNode.put("borderwidth", "1");
//            jsonOneNode.put("parent", invisible_subprocess);
//            jsonDataNode = new JSONObject();
//            jsonDataNode.put("data", jsonOneNode);
//            graph.put(jsonDataNode);
//            i++;
//        }
//
//        Double[] minMaxFreq = this.getMinMaxFrequency(bpmnDiagram.getNodes());
//        Double[] minMaxDur = this.getMinMaxDuration(bpmnDiagram.getNodes(), used_bpmn_size);
//        for(BPMNNode node : getNodes(bpmnDiagram)) {
//            if(mapping.containsKey(node)) continue;
//            if(node.getLabel().equals("unknown")) continue;
//
//            JSONObject jsonOneNode = new JSONObject();
//            mapping.put(node, i);
//            jsonOneNode.put("id", i);
//            jsonOneNode.put("name", "");
//            jsonOneNode.put("textcolor", "black");
//            jsonOneNode.put("textwidth", textwidth);
//            if(node.getParentSubProcess() != null) {
//                jsonOneNode.put("parent", subprocesses.get(node.getParentSubProcess()));
//            }
//
//            if(node instanceof Event) {
//                jsonOneNode.put("shape", "ellipse");
//                jsonOneNode.put("width", event_width);
//                jsonOneNode.put("height", event_height);
//                jsonOneNode.put("textsize", activity_font_size);
//                if(((Event) node).getEventType() == Event.EventType.START || node.getLabel().equals(start_name)) {
//                    start_node = i;
//                    jsonOneNode.put("color", START);
//                    jsonOneNode.put("borderwidth", borderwidth);
//                }else if(((Event) node).getEventType() == Event.EventType.END || node.getLabel().equals(end_name)) {
//                    end_node = i;
//                    jsonOneNode.put("color", END);
//                    jsonOneNode.put("borderwidth", borderwidth_end);
//                }
//            }else if(node instanceof Gateway) {
//                jsonOneNode.put("shape", "diamond");
//                jsonOneNode.put("color", GATEWAY);
//                jsonOneNode.put("width", gateway_width);
//                jsonOneNode.put("height", gateway_height);
//                jsonOneNode.put("borderwidth", borderwidth);
//                jsonOneNode.put("gatewayId", node.getId().toString().replace(" ", "_"));
//
//                Gateway gateway = (Gateway) node;
//                if(gateway.getGatewayType() == Gateway.GatewayType.DATABASED) {
//                    jsonOneNode.put("name", "X");
//                    jsonOneNode.put("textsize", xor_gateway_font_size);
//                }else if(gateway.getGatewayType() == Gateway.GatewayType.PARALLEL) {
//                    jsonOneNode.put("name", "+");
//                    jsonOneNode.put("textsize", and_gateway_font_size);
//                }else if(gateway.getGatewayType() == Gateway.GatewayType.INCLUSIVE) {
//                    jsonOneNode.put("name", "O");
//                    jsonOneNode.put("textsize", xor_gateway_font_size);
//                }
//            }else {
//                if(params.getPrimaryType() == null) {
//                	jsonOneNode.put("name", escapeChars(node.getLabel()));
//                } if(params.getPrimaryType() == VisualizationType.DURATION) {
//                	jsonOneNode.put("name", escapeChars(node.getLabel()) + "\\n\\n" + TimeConverter.convertMilliseconds("" + abs.getNodePrimaryWeight(node)) + ((params.getSecondary()) ? "\\n\\n" + decimalFormat.format(abs.getNodeSecondaryWeight(node)) : ""));
//                }
//                else {
//                	jsonOneNode.put("name", escapeChars(node.getLabel()) + "\\n\\n" + decimalFormat.format(abs.getNodePrimaryWeight(node)) + ((params.getSecondary()) ? "\\n\\n" + TimeConverter.convertMilliseconds("" + abs.getNodeSecondaryWeight(node)) : ""));
//                }
//
//                jsonOneNode.put("shape", "roundrectangle");
//
//                String colors[];
//                if(params.getPrimaryType() == null) colors = new String[] {ACTIVITY, "black"};
//                else if(params.getPrimaryType() == VisualizationType.DURATION) colors = getDurationColor(node, minMaxDur[0], minMaxDur[1], false);
//                else colors = getFrequencyColor(node, minMaxFreq[0], minMaxFreq[1]);
//
//                jsonOneNode.put("color", colors[0]);
//                jsonOneNode.put("textcolor", colors[1]);
//                jsonOneNode.put("width", activity_width);
//                jsonOneNode.put("height", activity_height);
//                jsonOneNode.put("textsize", activity_font_size);
//                jsonOneNode.put("borderwidth", borderwidth);
//            }
//            JSONObject jsonDataNode = new JSONObject();
//            jsonDataNode.put("data", jsonOneNode);
//            graph.put(jsonDataNode);
//            i++;
//        }
//
//        double maxWeight = 0.0;
//        double minWeight = 0.0;
//        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : getEdges(bpmnDiagram)) {
//            String number = edge.getLabel();
//            if (number.contains("[")) {
//                if(number.contains("\n")) number = number.substring(1, number.indexOf("\n"));
//                else number = number.substring(1, number.length() - 1);
//            } else {
//                number = "0";
//            }
//            number = fixNumber(number);
//            maxWeight = Math.max(maxWeight, Double.parseDouble(number));
//            minWeight = Math.min(minWeight, Double.parseDouble(number));
//        }
//
//        for(BPMNEdge<BPMNNode, BPMNNode> edge : getEdges(bpmnDiagram)) {
//            Integer source = mapping.get(edge.getSource());
//            Integer target = mapping.get(edge.getTarget());
//
//            JSONObject jsonOneLink = new JSONObject();
//            jsonOneLink.put("source", source);
//            jsonOneLink.put("target", target);
//
//            if(source == start_node) jsonOneLink.put("style", (used_bpmn_size) ? "solid" : "dashed");
//            else if(target == end_node) jsonOneLink.put("style", (used_bpmn_size) ? "solid" : "dashed");
//            else jsonOneLink.put("style", "solid");
//            
//            double mainNumber = abs.getArcPrimaryWeight(edge);
//            double secondaryNumber = abs.getArcSecondaryWeight(edge);
//
//            if(mainNumber != 1.0 || maxWeight != 0) {
//                BigDecimal bd = new BigDecimal(((Double.parseDouble(mainNumber+"") - minWeight) * 100.0 / (maxWeight - minWeight)));
//                bd = bd.setScale(2, RoundingMode.HALF_UP);
//
//                if (params.getPrimaryType() == VisualizationType.DURATION && (source == start_node || target == end_node)) {
//                    jsonOneLink.put("strength", 0);
//                    jsonOneLink.put("label", "");
//                    jsonOneLink.put("color", EDGE_START_COLOR_FREQUENCY);
//                } else {
//                    jsonOneLink.put("strength", bd.doubleValue());
//                    if (params.getPrimaryType() == VisualizationType.DURATION) {
//                    	jsonOneLink.put("label", TimeConverter.convertMilliseconds(mainNumber+"") + ((params.getSecondary()) ? "\\n\\n" + decimalFormat.format(Double.parseDouble(secondaryNumber+"")) : ""));
//                        jsonOneLink.put("color", "#" + Integer.toHexString(arc_duration_gradient.generateColor(bd.doubleValue() / 100).getRGB()).substring(2));
//                    }else {
//                        jsonOneLink.put("label", decimalFormat.format(mainNumber) + ((params.getSecondary()) ? "\\n\\n" + TimeConverter.convertMilliseconds(secondaryNumber+"") : ""));
//                        jsonOneLink.put("color", "#" + Integer.toHexString(arc_frequency_gradient.generateColor(bd.doubleValue() / 100).getRGB()).substring(2));
//                    }
//                }
//            }else {
//                jsonOneLink.put("strength", 0);
//                jsonOneLink.put("label", "");
//                jsonOneLink.put("color", EDGE_START_COLOR_FREQUENCY);
//            }
//
//            JSONObject jsonDataLink = new JSONObject();
//            jsonDataLink.put("data", jsonOneLink);
//            graph.put(jsonDataLink);
//        }
//        return graph;
//    }
   
    /**
     * To make string conform to JSON rules. 
     * See ProcessDiscovererController.display()
     * Note: escape characters are doubled since expression patterns also use the same escape characters 
     * @param value
     */
    private String escapeChars(String value) {
    	return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }

    private String fixNumber(String number) {
        try {
            Double.parseDouble(number);
        }catch (NumberFormatException nfe) {
            number = "0";
        }
        return number;
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
