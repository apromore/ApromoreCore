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

package org.apromore.plugin.portal.processdiscoverer.vis.json;

import static org.junit.jupiter.api.Assertions.fail;

import org.apromore.logman.ALog;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.TestDataSetup;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class ProcessJSONVisualizerTest extends TestDataSetup {
    private String NODE_KEY = "shape";
    private String[] nodeCompareKeys = new String[] {"name", "oriname"};
    private String[] edgeCompareKeys = new String[] {"style", "label"};
    
    private boolean findSimilarNodeObject(JSONObject node, JSONArray array) throws JSONException {
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
    
    /**
     * Check if an <edge> exists in both <result> and <expected>
     * This is true if both <result> and <expected> have same edges with the same label and style
     * and both <result> and <expecte> have the same source and target node as <edge> (same name and oriname)
     * @param edge: an edge in <result>
     * @param result
     * @param expected
     * @return
     * @throws JSONException
     */
    private boolean findSimilarEdgeObject(JSONObject edge, JSONArray result, JSONArray expected) throws JSONException {
        JSONObject edgeSourceResult = findNodeObjectWithId(edge.get("source").toString(), result);
        JSONObject edgeTargetResult= findNodeObjectWithId(edge.get("target").toString(), result);
        
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
			    	if (edgeTargetResult != null && edgeSourceResult != null) {
				    	boolean findExpectedSourceNode = findSimilarNodeObject(edgeSourceResult, expected);
				    	boolean findExpectedTargetNode = findSimilarNodeObject(edgeTargetResult, expected);
				    	if (findExpectedSourceNode && findExpectedTargetNode) {
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
    void testGenerateJSON_DFG_Frequency() {
        try {
            OutputData output = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(),
                                                100, 100, 40,
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
            JSONArray result = new JSONArray(output.getVisualizedText());
            JSONArray expected = readJSON_DFG_Frequency_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testGenerateJSON_DFG_Duration() {
        try {
            OutputData output = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(),
                                                100, 100, 40,
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
            JSONArray result = new JSONArray(output.getVisualizedText());
            JSONArray expected = readJSON_DFG_Duration_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testGenerateJSON_DFG_Frequency_DoubleWeight() {
        try {
            OutputData output = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(),
                                                100, 100, 40,
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
            JSONArray result = new JSONArray(output.getVisualizedText());
            JSONArray expected = readJSON_DFG_DoubleWeight_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testGenerateJSON_BPMN_Frequency() {
        try {
            OutputData output = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(),
                                                100, 100, 40,
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
            JSONArray result = new JSONArray(output.getVisualizedText());
            JSONArray expected = readJSON_BPMN_Frequency_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testGenerateJSON_BPMN_Duration() {
        try {
            OutputData output = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(),
                                                100, 100, 40,
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
            JSONArray result = new JSONArray(output.getVisualizedText());
            JSONArray expected = readJSON_BPMN_Duration_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
    
    @Test
    void testGenerateJSON_BPMN_Frequency_DoubleWeight() {
        try {
            OutputData output = discoverProcess(readLogWithStartCompleteEventsNonOverlapping(),
                                                100, 100, 40,
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
            JSONArray result = new JSONArray(output.getVisualizedText());
            JSONArray expected = readJSON_BPMN_DoubleWeight_LogWithStartCompleteEventsNonOverlapping_100_100();
            if (!isSimilar(result, expected)) {
                fail("JSON is different");
            }
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

}
