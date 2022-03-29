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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnIdName;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiagram extends BpmnIdName {

	protected Collection<BpmnDiPlane> planes;
	
	public BpmnDiagram(String tag) {
		super(tag);
		
		planes = new HashSet<BpmnDiPlane>();
	}
	
	@Override
    protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("BPMNPlane")) {
			BpmnDiPlane plane = new BpmnDiPlane("BPMNPlane");
			plane.importElement(xpp, bpmn);
			planes.add(plane);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	@Override
    protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnDiPlane plane : planes) {
			s += plane.exportElement();
		}
		return s;
	}
	
	public Collection<String> getElements() {
		Collection<String> elements = new HashSet<String>();
		for (BpmnDiPlane plane : planes) {
			elements.addAll(plane.getElements());
		}
		return elements;
	}
	
	@Override
    public String toString() {
		return name != null && !name.isEmpty() ? name : "No name";
	}
	
	public void addPlane(BpmnDiPlane plane) {
		planes.add(plane);
	}
	
	public Collection<BpmnDiPlane> getPlanes() {
	    return Collections.unmodifiableCollection(planes);
	}
	
	public void unmarshallIsExpanded(Map<String, BPMNNode> id2node) {
		for (BpmnDiPlane plane : planes) {
			Collection<BpmnDiShape> shapes = plane.getShapes();
			for (BpmnDiShape shape : shapes) {
				String bpmnElement = shape.getBpmnElement();
				Object o = id2node.get(bpmnElement);
				if (o instanceof SubProcess) {
					SubProcess subProcess = (SubProcess)o;
					subProcess.setBCollapsed(!shape.isExpanded());
				}
			}
		}
	}
}
