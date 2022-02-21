/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.processdiscoverer.utils.BPMNHelper;
import org.apromore.plugin.portal.processdiscoverer.vis.InvalidOutputException;
import org.apromore.plugin.portal.processdiscoverer.vis.UnsupportedElementException;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualContext;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualSettings;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.layout.LayoutElement;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Generate specific JSON fields for edges.
 * 
 * @author Bruce Nguyen
 *
 */
public class EdgeVisualizer extends AbstractElementVisualizer {
    public EdgeVisualizer(VisualContext visContext, VisualSettings visSettings) {
        super(visContext, visSettings);
    }
    
	@Override
	public JSONObject generateJSON(ContainableDirectedGraphElement element) 
	            throws UnsupportedElementException, JSONException, InvalidOutputException {
		if (!(element instanceof BPMNEdge<?, ?>)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMNEdge object.");
		}
		
        if (visContext.getProcessAbstraction().getLayout() == null) {
            throw new InvalidOutputException("Missing layout of the process map for generating JSON.");
        }
		
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge = (BPMNEdge<?,?>)element;
        Abstraction abs = visContext.getProcessAbstraction();
        AbstractionParams params = abs.getAbstractionParams();
        double primaryWeight = abs.getArcPrimaryWeight(edge);
        double secondaryWeight = abs.getArcSecondaryWeight(edge);
        double relativeWeight = abs.getEdgeRelativePrimaryWeight(edge);
        relativeWeight = (relativeWeight < 0 ? 0 : relativeWeight);
        if (MeasureType.COST.equals(params.getPrimaryType())) {
            relativeWeight = 1;
        }

        JSONObject jsonData = new JSONObject();
        jsonData.put("id", edge.getEdgeID().toString());
        jsonData.put("source", edge.getSource().getId().toString());
        jsonData.put("target", edge.getTarget().getId().toString());
        jsonData.put("style", BPMNHelper.isStartingOrEndingEdge(edge, abs.getDiagram()) ? "dashed" : "solid");
        jsonData.put("strength", relativeWeight*100);
        jsonData.put("color", visSettings.getColorSettings().getEdgeColor(element, visContext, visSettings));
        
        String label = (MeasureType.COST.equals(params.getPrimaryType())) ?
            "" : getWeightString(primaryWeight, "", params.getPrimaryType(), params.getPrimaryRelation());
        if (params.getSecondary()) {
            label +=
                (MeasureType.COST.equals(params.getSecondaryType())) ?
                "" : getWeightString(secondaryWeight, "\\n", params.getSecondaryType(), params.getSecondaryRelation());
        }
        jsonData.put("label", label);
        
        //Add (distance, weight) points for the edge
        LayoutElement edgeLayout = abs.getLayout().getLayoutElement(edge.getEdgeID().toString());
        if (edgeLayout == null) {
            throw new InvalidOutputException("Missing layout info for the edge with id=" + edge.getEdgeID().toString());
        }
        else {
            if (edge.getSource() != edge.getTarget()) {
                if (!edgeLayout.getDWPoints().isEmpty()) {
                	DecimalFormat df = new DecimalFormat("0.00");
    	            String point_distances = "";
    	            String point_weights = "";
    	            for (Point2D dw : edgeLayout.getDWPoints()) {
    	            	point_distances += (df.format(dw.getX()) + " ");
    	            	point_weights += (df.format(dw.getY()) + " ");
    	            }
    	            jsonData.put("edge-style", "unbundled-bezier");
    	            jsonData.put("point-distances", point_distances.trim());
    	            jsonData.put("point-weights", point_weights.trim());
                }
                else {
                	jsonData.put("edge-style", "unbundled-bezier");
                	jsonData.put("point-distances", "0");
    	            jsonData.put("point-weights", "0.5");
                }
            }
            else {
            	jsonData.put("edge-style", "bezier");
            }
        }

        JSONObject jsonEdge = new JSONObject();
        jsonEdge.put("data", jsonData);
        
        return jsonEdge;
	}
	
    private void addWeightValueToLabel(JSONObject jsonData, double weightValue, String separator, MeasureType measureType, 
            MeasureRelation measureRelation) throws JSONException {
        if (!jsonData.has("label")) return;
  
        if (measureRelation == MeasureRelation.ABSOLUTE) {
            if (measureType == MeasureType.FREQUENCY) {
                jsonData.put("label", jsonData.get("label") + separator + 
                visSettings.getDecimalFormatter().format(weightValue));
            }
            else if (measureType == MeasureType.COST) {
                jsonData.put("label", jsonData.get("label") + separator +
                    visSettings.getCurrency() + " " + visSettings.getDecimalFormatter().format(weightValue));
            }
            else if (measureType == MeasureType.DURATION) {
                jsonData.put("label", jsonData.get("label") + separator + 
                visSettings.getTimeConverter().convertMilliseconds("" + weightValue));
            }
        }
        else {
            jsonData.put("label", jsonData.get("label") + separator + 
            visSettings.getDecimalFormatter().format(weightValue*100) + "%");
        }
    }
	
	
}
