/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.stagemining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jbpt.hypergraph.abs.IVertex;
import org.jfree.data.time.TimeTableXYDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.utils.GraphUtils;

/**
 *
 * @author Administrator
 */
public class Visualization_cytoscape {
    public static JSONArray createJson(DecompositionTree tree) throws JSONException, Exception {
        List<Set<IVertex>> stages = new ArrayList<>();
        
        Set<IVertex> start = new HashSet<IVertex>();
        start.add(tree.getGraph().getSource());
        stages.add(start);
        
        Set<IVertex> end = new HashSet<IVertex>();
        end.add(tree.getGraph().getSink());
        stages.add(end);
        
        stages.addAll(tree.getStageList(tree.getMaxLevelIndex()));
        
        double[][] adjMatrix = new double[stages.size()][stages.size()];
        double maxWeight = 0.0;
        for (int i=0;i<adjMatrix.length;i++) {
            for (int j=0;j<adjMatrix.length;j++) {
                if (i==j) continue;
                
                if (stages.get(i).contains(tree.getGraph().getSource())) {
                    adjMatrix[i][j] = GraphUtils.getDirectedConnectionWeightFromSource(
                                                    tree.getGraph(), 
                                                    tree.getGraph().getSource(), 
                                                    stages.get(j));
                }
                else if (stages.get(j).contains(tree.getGraph().getSink())) {
                    adjMatrix[i][j] = GraphUtils.getDirectedConnectionWeightToSink(
                                                    tree.getGraph(), 
                                                    stages.get(i),
                                                    tree.getGraph().getSink());
                }
                else {
                    adjMatrix[i][j] = GraphUtils.getDirectedConnectionWeight(
                                                    tree.getGraph(), 
                                                    stages.get(i), 
                                                    stages.get(j));
                }
                if (adjMatrix[i][j] > maxWeight) maxWeight = adjMatrix[i][j];
            }
        }
        
        JSONObject json = new JSONObject();
        
        //-----------------------------------------
        // For node array
        //-----------------------------------------
        JSONArray jsonNodeAndArcArray = new JSONArray();
        
        for (int i=0;i<stages.size();i++) {
            JSONObject jsonOneNode = new JSONObject();
            jsonOneNode.put("id", i);
            String nodeName = "";
            for (IVertex v : stages.get(i)) {
                if (nodeName.equals("")) {
                    nodeName = v.getName();
                }
                else {
                    nodeName += (", " + v.getName());
                }
            }
            jsonOneNode.put("name", nodeName);
            JSONObject jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            jsonNodeAndArcArray.put(jsonDataNode);
        }
        
        //-----------------------------------------
        // For link array
        //-----------------------------------------
//        JSONArray jsonLinkArray = new JSONArray();
        for (int i=0;i<adjMatrix.length;i++) {
            for (int j=0;j<adjMatrix.length;j++) {
                if (adjMatrix[i][j] > 0) {
                    JSONObject jsonOneLink = new JSONObject();
                    jsonOneLink.put("source", i);
                    jsonOneLink.put("target", j);
                    jsonOneLink.put("strength", adjMatrix[i][j]*100/maxWeight);
                    jsonOneLink.put("label", adjMatrix[i][j]);
                    JSONObject jsonDataLink = new JSONObject();
                    jsonDataLink.put("data", jsonOneLink);
                    jsonNodeAndArcArray.put(jsonDataLink);
                }
            }
        }
        
        //json.put("elements", jsonNodeAndArcArray);
        //return json;
        return jsonNodeAndArcArray;
    }
}
