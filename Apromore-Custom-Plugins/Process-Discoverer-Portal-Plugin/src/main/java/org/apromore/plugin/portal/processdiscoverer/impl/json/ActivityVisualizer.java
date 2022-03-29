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
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.json.JSONException;

/**
 * Generate specific JSON fields for Activity nodes.
 *
 * @author Bruce Nguyen
 *
 */
public class ActivityVisualizer extends AbstractNodeVisualizer {

    public ActivityVisualizer(VisualContext visContext, VisualSettings visSettings) {
        super(visContext, visSettings);
    }

	@Override
	protected void generateSpecifics(ContainableDirectedGraphElement element) throws UnsupportedElementException, JSONException {
		if (!(element instanceof Activity)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMN Activity object.");
		}

		String node_oriname = node.getLabel();
    	// This is only for trace abstraction as the values are stored in the node label
    	if (node_oriname.contains("\\n")) {
    		node_oriname =  node_oriname.substring(0, node_oriname.indexOf("\\n"));
    	}
    	jsonData.put("oriname", visSettings.getStringFormatter().escapeChars(node_oriname));

    	String node_displayname = node_oriname.trim();
    	int fontSize;
		node_displayname = visSettings.getStringFormatter().escapeChars(node_displayname);
        node_displayname = visSettings.getStringFormatter().wrapName(node_displayname, 0);

		if (node_displayname.length() > 25) {
			fontSize = visSettings.getActivityFontSizeSmall();
		} else {
			fontSize = visSettings.getActivityFontSize();
		}
    	Abstraction abs = visContext.getProcessAbstraction();
		AbstractionParams params = abs.getAbstractionParams();

		String name = node_displayname;
        name += getWeightString(abs.getNodePrimaryWeight(node), "\\n\\n", params.getPrimaryType(), params.getPrimaryRelation());
		if (params.getSecondary()) {
		    name += getWeightString(abs.getNodeSecondaryWeight(node), ", ", params.getSecondaryType(), params.getSecondaryRelation());
		}
		jsonData.put("id", ((Activity)element).getId().toString());
		jsonData.put("name", name);
		jsonData.put("shape", "roundrectangle");
        jsonData.put("color", visSettings.getColorSettings().getActivityBackgroundColor(element, visContext, visSettings));
        jsonData.put("textcolor", visSettings.getColorSettings().getActivityTextColor(element, visContext, visSettings));
        jsonData.put("width", visSettings.getActivityWidth() + "px");
        jsonData.put("height", visSettings.getActivityHeight() + "px");
        jsonData.put("textsize", fontSize + "px");
        jsonData.put("borderwidth", visSettings.getBorderWidth() + "px");
	}
}
