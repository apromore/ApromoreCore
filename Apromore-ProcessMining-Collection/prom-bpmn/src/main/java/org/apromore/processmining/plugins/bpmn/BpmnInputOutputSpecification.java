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
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;

public class BpmnInputOutputSpecification extends BpmnId {

	private Collection<BpmnId> dataInputs;
	private Collection<BpmnId> dataOutputs;
	
	public BpmnInputOutputSpecification(String tag) {
		super(tag);
		
		dataInputs = new HashSet<BpmnId>();
		dataOutputs = new HashSet<BpmnId>();
	}
	
	public Collection<BpmnId> getDataInputs() {
		
		return dataInputs;
	}
	
	public Collection<BpmnId> getDataOutputs() {
		
		return dataOutputs;
	}
	
	public void addDataInput(BpmnId dataInput) {
		
		dataInputs.add(dataInput);
	}
	
	public void addDataOutput(BpmnId dataOutput) {
		
		dataOutputs.add(dataOutput);
	}
	
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("dataInput")) {
			BpmnId input = new BpmnId("dataInput");
			input.importElement(xpp, bpmn);
			dataInputs.add(input);
			return true;
		} else if (xpp.getName().equals("dataOutput")) {
			BpmnId output = new BpmnId("dataOutput");
			output.importElement(xpp, bpmn);
			dataInputs.add(output);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnId input : dataInputs) {
			s += input.exportElement();
		}
		for (BpmnId output : dataOutputs) {
			s += output.exportElement();
		}
		return s;
	}
	
	protected void checkValidity(Bpmn bpmn) {
				// do not require id
	}
}
