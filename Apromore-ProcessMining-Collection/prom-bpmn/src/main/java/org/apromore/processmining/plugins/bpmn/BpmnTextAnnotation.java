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
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.xmlpull.v1.XmlPullParser;

public class BpmnTextAnnotation extends BpmnId {

	private BpmnText text = new BpmnText("text");
	
	public BpmnTextAnnotation(String tag) {		
		super(tag);
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (xpp.getName().equals("text")) {
			text.importElement(xpp, bpmn);
			return true;
		}
		return false;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s ="";
		if (text != null) {
			s += text.exportElement();
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		diagram.setNextId(id);
        TextAnnotation textAnnotation = diagram.addTextAnnotation(text.getText());
        id2node.put(id, textAnnotation);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(id)) {
			diagram.setNextId(id);
			TextAnnotation textAnnotation = diagram.addTextAnnotation(text.getText());
			id2node.put(id, textAnnotation);
		}
	}
	
	public void marshall(TextAnnotation textAnnotation) {
		super.marshall(textAnnotation);
		text.setText(textAnnotation.getLabel());
	}
}
