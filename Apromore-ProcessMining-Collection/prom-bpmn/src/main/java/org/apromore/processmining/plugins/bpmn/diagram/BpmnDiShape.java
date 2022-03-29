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

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.apromore.processmining.plugins.bpmn.BpmnText;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiShape extends BpmnElement {

	private String bpmnElement;
	private BpmnDcBounds bounds;
	private boolean isExpanded;
	private boolean isHorizontal;
	
	public BpmnDiShape(String tag) {
		super(tag);			
	}
	
	public BpmnDiShape(String tag, String bpmnElement, BpmnDcBounds bounds, boolean isExpanded, boolean isHorizontal) {
		super(tag);		
		this.bpmnElement = bpmnElement;
		this.bounds = bounds;
		this.isExpanded = isExpanded;
		this.isHorizontal = isHorizontal;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "bpmnElement");
		if (value != null) {
			bpmnElement = value;
		}
		value = xpp.getAttributeValue(null, "isExpanded");
		if (value != null) {
			isExpanded = new Boolean(value);
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
		if(isExpanded) {
			s += exportAttribute("isExpanded", new Boolean(isExpanded).toString());
		}
		if(isHorizontal) {
			s += exportAttribute("isHorizontal", new Boolean(isHorizontal).toString());
		}
		return s;
	}
	
	/**
	 * Exports all elements.
	 */
	protected String exportElements() {
		String s = "";
		if (bounds != null) {
			s += bounds.exportElement();
		}
		BpmnText bpmnLabel = new BpmnText("bpmndi:BPMNLabel");
		s+=bpmnLabel.exportElement();
		return s;
	}

	public void addElement(Collection<String> elements) {
		elements.add(bpmnElement);
	}
	
	public String getBpmnElement() {
		return bpmnElement;
	}
	
	public boolean isExpanded() {
		return isExpanded;
	}
}
