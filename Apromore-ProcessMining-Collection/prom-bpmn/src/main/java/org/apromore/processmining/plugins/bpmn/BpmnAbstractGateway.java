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
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public abstract class BpmnAbstractGateway extends BpmnIncomingOutgoing {

	private String gatewayDirection;
	private String defaultFlow;
	
	public BpmnAbstractGateway(String tag) {
		super(tag);
		
		gatewayDirection = null;
		defaultFlow = null;
	}
	
	public void marshall(BPMNDiagram diagram, Gateway gateway){
		super.marshall(diagram, gateway);
		if(incomings.size() > 1 && outgoings.size() > 1){
			gatewayDirection = "Mixed";
		}
		else if(incomings.size()  == 1 && outgoings.size()  > 1){
			gatewayDirection = "Diverging";
		}
		else if(incomings.size()  > 1 && outgoings.size()  == 1){
			gatewayDirection = "Converging";
		}
		else{
			gatewayDirection = "Unspecified";
		}
		if(gateway.getDefaultFlow() != null){
			defaultFlow = gateway.getDefaultFlow().getEdgeID().toString().replace(" ", "_");
		}
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "gatewayDirection");
		if (value != null) {
			gatewayDirection = value;
		}
		value = xpp.getAttributeValue(null, "default");
		if (value != null) {
			defaultFlow = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		/* Bruce 7 Oct 2021: this attribute is not used in bpmn.io
		if (gatewayDirection != null) {
			s += exportAttribute("gatewayDirection", gatewayDirection);
		}
		*/
		if(defaultFlow != null){
			s += exportAttribute("default", defaultFlow);
		}
		return s;
	}

	public abstract void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane);

	public abstract void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane);
	
	public abstract void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, SubProcess subProcess);

	public abstract void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, SubProcess subProcess);
}
