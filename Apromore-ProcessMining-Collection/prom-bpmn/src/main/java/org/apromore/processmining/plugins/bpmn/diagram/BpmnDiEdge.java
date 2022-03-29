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
package org.apromore.processmining.plugins.bpmn.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiEdge extends BpmnElement {

	private String bpmnElement;
	private List<BpmnDiWaypoint> waypoints = new ArrayList<BpmnDiWaypoint>();
	
	public BpmnDiEdge(String tag) {
		super(tag);
	}
	
	public BpmnDiEdge(String tag, String bpmnElement) {
		super(tag);		
		this.bpmnElement = bpmnElement;	
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "bpmnElement");
		if (value != null) {
			bpmnElement = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (bpmnElement != null) {
			s += exportAttribute("bpmnElement", bpmnElement);
		}
		return s;
	}
	
	/**
	 * Exports all elements.
	 */
	protected String exportElements() {
		String s = "";
		for(BpmnDiWaypoint waypoint : waypoints) {
			s += waypoint.exportElement();
		}
		return s;
	}
	
	public void addWaypoint(BpmnDiWaypoint waypoint) {
		waypoints.add(waypoint);
	}

	public void addElement(Collection<String> elements) {
		elements.add(bpmnElement);
	}
}
