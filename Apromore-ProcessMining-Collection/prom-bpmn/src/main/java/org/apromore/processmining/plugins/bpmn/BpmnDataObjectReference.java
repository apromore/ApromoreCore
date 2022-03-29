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
package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDataObjectReference extends BpmnIdName {
	
	private String dataObjectRef;
	
	public BpmnDataObjectReference(String tag) {
		super(tag);
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "dataObjectRef");
		if (value != null) {
			dataObjectRef = value;
		}
	}
	
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (dataObjectRef != null) {
			s += exportAttribute("dataObjectRef", dataObjectRef);
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		diagram.setNextId(id);
        DataObject dataObject = diagram.addDataObject(name);
		id2node.put(id, dataObject);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			diagram.setNextId(id);
			DataObject dataObject = diagram.addDataObject(name);
			id2node.put(id, dataObject);
		}
	}
	
	public void marshall(DataObject dataObject) {
		super.marshall(dataObject);
			
		dataObjectRef = "dataobj_" + dataObject.getId().toString().replace(' ', '_');
	}
}
