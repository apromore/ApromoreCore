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
import java.util.stream.Collectors;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.xmlpull.v1.XmlPullParser;

public class BpmnIncomingOutgoing extends BpmnIdName {
	
	protected Collection<BpmnIncoming> incomings;
	protected Collection<BpmnOutgoing> outgoings;
	
	public BpmnIncomingOutgoing(String tag) {
		super(tag);
		
		incomings = new HashSet<BpmnIncoming>();
		outgoings = new HashSet<BpmnOutgoing>();
	}

	protected void marshall(BPMNDiagram diagram, BPMNNode node) {
		super.marshall(node);
		for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: diagram.getEdges()) {
			if(e.getTarget().equals(node)){
				BpmnIncoming in = new BpmnIncoming("incoming");
				in.setText(e.getEdgeID().toString().replace(" ", "_"));
				incomings.add(in);
			}
			if(e.getSource().equals(node)){
				BpmnOutgoing out = new BpmnOutgoing("outgoing");
				out.setText(e.getEdgeID().toString().replace(" ", "_"));
				outgoings.add(out);
			}
		}
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("incoming")) {
			BpmnIncoming incoming = new BpmnIncoming("incoming");
			incoming.importElement(xpp, bpmn);
			incomings.add(incoming);
			return true;
		} else if (xpp.getName().equals("outgoing")) {
			BpmnOutgoing outgoing = new BpmnOutgoing("outgoing");
			outgoing.importElement(xpp, bpmn);
			outgoings.add(outgoing);
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
		for (BpmnIncoming incoming : incomings.stream()
										.sorted((o1, o2) -> o1.getText().compareTo(o2.getText()))
										.collect(Collectors.toList())) {
			s += incoming.exportElement();
		}
		for (BpmnOutgoing outgoing : outgoings.stream()
										.sorted((o1, o2) -> o1.getText().compareTo(o2.getText()))
										.collect(Collectors.toList())) {
			s += outgoing.exportElement();
		}
		return s;
	}

}
