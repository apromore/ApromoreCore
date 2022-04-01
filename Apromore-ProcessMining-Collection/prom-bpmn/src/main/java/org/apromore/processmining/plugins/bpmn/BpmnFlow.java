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

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.xmlpull.v1.XmlPullParser;

public class BpmnFlow extends BpmnIdName {

	protected String sourceRef;
	protected String targetRef;

	public BpmnFlow(String tag) {
		super(tag);
		
		sourceRef = null;
		targetRef = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "sourceRef");
		if (value != null) {
			sourceRef = value;
		}
		value = xpp.getAttributeValue(null, "targetRef");
		if (value != null) {
			targetRef = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (sourceRef != null) {
			s += exportAttribute("sourceRef", sourceRef);
		}
		if (targetRef != null) {
			s += exportAttribute("targetRef", targetRef);
		}
		return s;
	}
	
	public void marshall(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		super.marshall(edge);
		sourceRef = edge.getSource().getId().toString().replace(' ', '_');
		targetRef = edge.getTarget().getId().toString().replace(' ', '_');
	}
}
