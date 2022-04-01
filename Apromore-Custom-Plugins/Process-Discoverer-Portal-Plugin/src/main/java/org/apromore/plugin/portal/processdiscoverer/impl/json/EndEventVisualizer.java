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
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.json.JSONException;

/**
 * Generate specific JSON fields for End Event
 * 
 * @author Bruce Nguyen
 *
 */
public class EndEventVisualizer extends AbstractNodeVisualizer {
    public EndEventVisualizer(VisualContext visContext, VisualSettings visSettings) {
        super(visContext, visSettings);
    }
    
	@Override
	protected void generateSpecifics(ContainableDirectedGraphElement element) throws UnsupportedElementException, JSONException {
		if (!(element instanceof Event && ((Event)element).getEventType() == EventType.END)) {
			throw new UnsupportedElementException("Unsupported element while expecting a BPMN End Event object.");
		}
		jsonData.put("id", ((Event)element).getId().toString());
		jsonData.put("shape", "ellipse");
		jsonData.put("width", visSettings.getEventWidth()+"px");
		jsonData.put("height", visSettings.getEventHeight()+"px");
		jsonData.put("textsize", visSettings.getActivityFontSize()+"px");
		jsonData.put("oriname", visSettings.getEndEventName());
		jsonData.put("color", visSettings.getColorSettings().getEndEventBackgroundColor());
		jsonData.put("borderwidth", visSettings.getBorderWidthEnd()+"px");
	}

}
