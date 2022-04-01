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
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.json.JSONException;

/**
 * Generate specific JSON fields for Gateways.
 * 
 * @author Bruce Nguyen
 *
 */
public class GatewayVisualizer extends AbstractNodeVisualizer {
    public GatewayVisualizer(VisualContext visContext, VisualSettings visSettings) {
        super(visContext, visSettings);
    }
    
	@Override
	protected void generateSpecifics(ContainableDirectedGraphElement element) throws UnsupportedElementException, JSONException {
		if (!(element instanceof Gateway)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMN Gateway object.");
		}
		
		Gateway gateway = (Gateway)node;
		jsonData.put("id", gateway.getId().toString());
		jsonData.put("shape", "diamond");
        jsonData.put("color", visSettings.getColorSettings().getGatewayBackgroundColor());
        jsonData.put("width", visSettings.getGatewayHeight()+"px");
        jsonData.put("height", visSettings.getGatewayWidth()+"px");
        jsonData.put("borderwidth", visSettings.getBorderWidth()+"px");
        jsonData.put("gatewayId", node.getId().toString().replace(" ", "_"));
        
        if (gateway.getGatewayType() == Gateway.GatewayType.DATABASED) {
        	jsonData.put("name", "X");
        	jsonData.put("oriname", "X");
        	jsonData.put("textsize", visSettings.getXORGatewayFontSize()+"px");
        }
        else if (gateway.getGatewayType() == Gateway.GatewayType.PARALLEL) {
        	jsonData.put("name", "+");
        	jsonData.put("oriname", "+");
        	jsonData.put("textsize", visSettings.getANDGatewayFontSize()+"px");
        }
        else if (gateway.getGatewayType() == Gateway.GatewayType.INCLUSIVE) {
        	jsonData.put("name", "O");
        	jsonData.put("oriname", "O");
        	jsonData.put("textsize", visSettings.getXORGatewayFontSize()+"px");
        }
	}
}
