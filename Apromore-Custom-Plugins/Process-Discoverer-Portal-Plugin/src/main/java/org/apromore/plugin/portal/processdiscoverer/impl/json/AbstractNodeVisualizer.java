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

import org.apromore.plugin.portal.processdiscoverer.vis.InvalidOutputException;
import org.apromore.plugin.portal.processdiscoverer.vis.UnsupportedElementException;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualContext;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualSettings;
import org.apromore.processdiscoverer.layout.LayoutElement;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Generate common JSON fields for nodes.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class AbstractNodeVisualizer extends AbstractElementVisualizer {
	protected BPMNNode node;
	protected JSONObject jsonData;
	protected JSONObject jsonPosition;
	protected JSONObject jsonNode;
	
	public AbstractNodeVisualizer(VisualContext visContext, VisualSettings visSettings) {
	    super(visContext, visSettings);
    }
	
	@Override
	public JSONObject generateJSON(ContainableDirectedGraphElement element) 
	        throws UnsupportedElementException, JSONException, InvalidOutputException {
		if (!(element instanceof BPMNNode)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMNNode object.");
		}
		
        if (visContext.getProcessAbstraction().getLayout() == null) {
            throw new InvalidOutputException("Missing layout for the process map for generating JSON.");
        }
		
		this.node = (BPMNNode)element;
	    jsonData = new JSONObject();
	    jsonPosition = new JSONObject();
	    jsonNode = new JSONObject();
		
		// Common visual data
		jsonData.put("id", visContext.getProcessAbstraction().getNodeId(node));
		jsonData.put("name", "");
		jsonData.put("textcolor", "black");
		jsonData.put("textwidth", visSettings.getTextWidth() + "px");
		
		// Specific visual data
		generateSpecifics(element);
		jsonNode.put("data", jsonData);
		
		// Position data
		LayoutElement nodeLayout = visContext.getProcessAbstraction().getLayout().getLayoutElement(node);
        if (nodeLayout == null) {
            throw new InvalidOutputException("Missing layout info for the node with id=" + node.getId().toString());
        }
        else {
			double[] nodeCoords = visSettings.getCenterXY(node, nodeLayout.getX(), nodeLayout.getY());
			double nodeX = nodeCoords[0];
			double nodeY = nodeCoords[1];
			jsonPosition.put("x", nodeX);
			jsonPosition.put("y", nodeY);
			jsonNode.put("position", jsonPosition);
		}
		
		return jsonNode;
	}
	
	protected abstract void generateSpecifics(ContainableDirectedGraphElement element) throws UnsupportedElementException, JSONException;
	
}
