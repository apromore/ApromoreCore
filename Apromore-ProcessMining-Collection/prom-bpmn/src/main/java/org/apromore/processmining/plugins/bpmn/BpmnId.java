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

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.xmlpull.v1.XmlPullParser;

public class BpmnId extends BpmnElement {
	/*
	 * Attributes
	 */
	public String id;

	public BpmnId(String tag) {
		super(tag);

		id = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "id");
		if (value != null) {
			id = value;
		}
	}
	
	protected void marshall(BPMNNode graphNode) {
		if(graphNode.getId() != null) {
			id = graphNode.getId().toString().replace(' ', '_');
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (id != null) {
			s += exportAttribute("id", id);
		}
		return s;
	}

	protected void checkValidity(Bpmn bpmn) {
		checkRequired(bpmn, "id", id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
