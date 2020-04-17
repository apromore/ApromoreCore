/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.processdiscoverer.utils.BPMNHelper;
import org.apromore.plugin.portal.processdiscoverer.vis.MissingLayoutException;
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
	            throws UnsupportedElementException, JSONException, MissingLayoutException {
		if (!(element instanceof BPMNEdge<?, ?>)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMNEdge object.");
		}
		
        if (visContext.getProcessAbstraction().getLayout() == null) {
            throw new MissingLayoutException("Missing layout of the process map for generating JSON.");
        }
		
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge = (BPMNEdge<?,?>)element;
        Abstraction abs = visContext.getProcessAbstraction();
        AbstractionParams params = abs.getAbstractionParams();
        double primaryWeight = abs.getArcPrimaryWeight(edge);
        double secondaryWeight = abs.getArcSecondaryWeight(edge);
        double relativeWeight = abs.getEdgeRelativePrimaryWeight(edge);
        relativeWeight = (relativeWeight < 0 ? 0 : relativeWeight);
        
        JSONObject jsonData = new JSONObject();
        jsonData.put("source", visContext.getProcessAbstraction().getNodeId(edge.getSource()));
        jsonData.put("target", visContext.getProcessAbstraction().getNodeId(edge.getTarget()));
        jsonData.put("style", BPMNHelper.isStartingOrEndingEdge(edge, abs.getDiagram()) ? "dashed" : "solid");
        jsonData.put("strength", relativeWeight*100);
        jsonData.put("color", visSettings.getColorSettings().getEdgeColor(element, visContext, visSettings));
        jsonData.put("label", "");
        if (params.getPrimaryType() == MeasureType.DURATION) {
        	jsonData.put("label", visSettings.getTimeConverter().convertMilliseconds(primaryWeight+"") + ((params.getSecondary()) ? "\\n" + 
        				visSettings.getDecimalFormatter().format(Double.parseDouble(secondaryWeight+"")) : ""));
        }else {
            jsonData.put("label", visSettings.getDecimalFormatter().format(primaryWeight) + ((params.getSecondary()) ? "\\n" + 
            			visSettings.getTimeConverter().convertMilliseconds(secondaryWeight+"") : ""));
        }
        
        //Add (distance, weight) points for the edge
        LayoutElement edgeLayout = abs.getLayout().getLayoutElement(edge.getEdgeID().toString());
        if (edgeLayout == null) {
            throw new MissingLayoutException("Missing layout info for the edge with id=" + edge.getEdgeID().toString());
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
	
}
