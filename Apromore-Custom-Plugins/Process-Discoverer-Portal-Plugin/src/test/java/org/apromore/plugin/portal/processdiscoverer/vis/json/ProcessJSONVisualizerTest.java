/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.vis.json;

import static org.junit.Assert.fail;

import org.apromore.logman.ALog;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.plugin.portal.processdiscoverer.TestDataSetup;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDCustomFactory;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class ProcessJSONVisualizerTest extends TestDataSetup {
    private String NODE_KEY = "shape"; 
//    private String[] nodeCompareKeys = new String[] {"shape", "color", "borderwidth", 
//                                                    "name", "oriname", 
//                                                    "textsize", "textwidth", "textcolor",
//                                                    "width", "height"};
//    private String[] edgeCompareKeys = new String[] {"strength", "color", "edge-style", 
//                                                    "style", "label"};    
    private String[] nodeCompareKeys = new String[] {"name", "oriname"};
    private String[] edgeCompareKeys = new String[] {"style", "label"};    

    
    private Abstraction discoverProcess(XLog xlog, double nodeSlider, double arcSlider, double paraSlider,
                        MeasureType structureType, MeasureAggregation structureAggregate, MeasureRelation structureRelation,
                        MeasureType primaryType, MeasureAggregation primaryAggregate, MeasureRelation primaryRelation,
                        MeasureType secondaryType, MeasureAggregation secondaryAggregate, MeasureRelation secondaryRelation,
                        boolean useSecondary,
                        boolean bpmn) throws Exception {
        ALog log = new ALog(xlog);
        IndexableAttribute mainAttribute = log.getAttributeStore().getStandardEventConceptName();
        AttributeLog attLog = new AttributeLog(log, mainAttribute);
        ProcessDiscoverer pd = new ProcessDiscoverer(attLog);
        AbstractionParams params = new AbstractionParams(
                mainAttribute,
                nodeSlider,
                arcSlider,
                paraSlider,
                true, true,
                false,
                false,
                useSecondary,
                structureType,
                structureAggregate,
                structureRelation,
                primaryType,
                primaryAggregate,
                primaryRelation,
                secondaryType,
                secondaryAggregate,
                secondaryRelation,
                null,
                null);
        
        Abstraction dfgAbs = pd.generateDFGAbstraction(params);
        return (!bpmn ? dfgAbs : pd.generateBPMNAbstraction(params, dfgAbs));
    }
    
    public boolean findSimilarNodeObject(JSONObject node, JSONArray array) throws JSONException {
        for (int i=0; i<array.length(); i++) {
            JSONObject data = array.getJSONObject(i).getJSONObject("data");
            if (data.has(NODE_KEY)) {
                boolean found = true;
                for (String key: nodeCompareKeys) {
                    if (!data.get(key).equals(node.get(key))) {
                        found = false;
                    }
                }
                if (found) return true;
            }
        }
        return false;
    }
    
    private boolean findSimilarEdgeObject(JSONObject edge, JSONArray result, JSONArray expected) throws JSONException {
        for (int i=0; i<expected.length(); i++) {
            JSONObject data = expected.getJSONObject(i).getJSONObject("data");
            if (!data.has(NODE_KEY)) {
                boolean found = true;
                for (String key: edgeCompareKeys) {
                    if (!data.get(key).equals(edge.get(key))) {
                        found = false;
                    }
                }
                if (found) { //find edge, now check the source and target nodes
			        String edgeSourceId = edge.get("source").toString();
			        String edgeTargetId = edge.get("target").toString();
			        JSONObject edgeTargetObject = findNodeObjectWithId(edgeTargetId, result);
			    	JSONObject edgeSourceObject = findNodeObjectWithId(edgeSourceId, result);
			    	if (edgeTargetObject != null && edgeSourceObject != null) {
				    	boolean findSourceNode = findSimilarNodeObject(edgeSourceObject, expected);
				    	boolean findTargetNode = findSimilarNodeObject(edgeTargetObject, expected);
				    	if (findSourceNode && findTargetNode) {
				    		return true;
				    	}
			    	}
                }
            }
        }
        
    	return false;
    }
    
    
    
    private JSONObject findNodeObjectWithId(String id, JSONArray data) throws JSONException {
    	for (int i=0; i<data.length(); i++) {
            JSONObject elementObject = data.getJSONObject(i).getJSONObject("data");
            if (elementObject.has(NODE_KEY) && elementObject.get("id").toString().equals(id)) { //this is a node
            	return elementObject;
            }
    	}
    	return null;
    }
    
    private boolean isSimilar(JSONArray array1, JSONArray array2) throws JSONException {
        for (int i=0; i<array1.length(); i++) {
            JSONObject data = array1.getJSONObject(i).getJSONObject("data");
            if (data.has(NODE_KEY)) {
                if (!findSimilarNodeObject(data, array2)) {
                    System.out.println("Failed to find this JSON data in the expected result: " + data.toString());
                    return false;
                }
            }
            else {
                if (!findSimilarEdgeObject(data, array1, array2)) {
                    System.out.println("Failed to find this JSON data in the expected result: " + data.toString());
                    return false;
                }
            }
        }
        
        for (int i=0; i<array2.length(); i++) {
            JSONObject data = array2.getJSONObject(i).getJSONObject("data");
            if (data.has(NODE_KEY)) {
                if (!findSimilarNodeObject(data, array1)) {
                    System.out.println("Failed to find an expected JSON data in the result: " + data.toString());
                    return false;
                }
            }
            else {
                if (!findSimilarEdgeObject(data, array2, array1)) {
                    System.out.println("Failed to find an expected JSON data in the result: " + data.toString());
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Test
    public void testGenerateJSON_DFG_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false,
                                                false);
            ProcessVisualizer visualizer = (new PDCustomFactory()).createProcessVisualizer(null);
            String visText = visualizer.generateVisualizationText(abs);
            JSONArray result = new JSONArray(visText);
            JSONArray expected = readJSON_DFG_Frequency_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testGenerateJSON_DFG_Duration() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false,
                                                false);
            ProcessVisualizer visualizer = (new PDCustomFactory()).createProcessVisualizer(null);
            String visText = visualizer.generateVisualizationText(abs);
            JSONArray result = new JSONArray(visText);
            JSONArray expected = readJSON_DFG_Duration_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testGenerateJSON_DFG_Frequency_DoubleWeight() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true,
                                                false);
            ProcessVisualizer visualizer = (new PDCustomFactory()).createProcessVisualizer(null);
            String visText = visualizer.generateVisualizationText(abs);
            JSONArray result = new JSONArray(visText);
            JSONArray expected = readJSON_DFG_DoubleWeight_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testGenerateJSON_BPMN_Frequency() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false,
                                                true);
            ProcessVisualizer visualizer = (new PDCustomFactory()).createProcessVisualizer(null);
            String visText = visualizer.generateVisualizationText(abs);
            JSONArray result = new JSONArray(visText);
            JSONArray expected = readJSON_BPMN_Frequency_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testGenerateJSON_BPMN_Duration() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                false,
                                                true);
            ProcessVisualizer visualizer = (new PDCustomFactory()).createProcessVisualizer(null);
            String visText = visualizer.generateVisualizationText(abs);
            JSONArray result = new JSONArray(visText);
            JSONArray expected = readJSON_BPMN_Duration_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    public void testGenerateJSON_BPMN_Frequency_DoubleWeight() {
        try {
            Abstraction abs = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(), 
                                                1.0, 1.0, 0.4, 
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.FREQUENCY,
                                                MeasureAggregation.CASES,
                                                MeasureRelation.ABSOLUTE,
                                                MeasureType.DURATION,
                                                MeasureAggregation.MEAN,
                                                MeasureRelation.ABSOLUTE,
                                                true,
                                                true);
            ProcessVisualizer visualizer = (new PDCustomFactory()).createProcessVisualizer(null);
            String visText = visualizer.generateVisualizationText(abs);
            JSONArray result = new JSONArray(visText);
            JSONArray expected = readJSON_BPMN_DoubleWeight_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}
