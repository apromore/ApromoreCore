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
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.xmlpull.v1.XmlPullParser;

public class BpmnEvent extends BpmnIncomingOutgoing {

	private boolean isInterrupting = true;
	private String parallelMultiple;
	protected EventType eventType;
	protected EventTrigger eventTrigger = EventTrigger.NONE;
	protected EventUse eventUse;
	
	public BpmnEvent(String tag, EventType eventType) {
		super(tag);
		
		parallelMultiple = null;
		this.eventType = eventType;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "isInterrupting");
		if (value != null) {
			isInterrupting = value.equals("false")? false : true;
		}
		value = xpp.getAttributeValue(null, "parallelMultiple");
		if (value != null) {
			parallelMultiple = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if(isInterrupting == false) {
			s += exportAttribute("isInterrupting", "false");
		}
		
		if (parallelMultiple != null) {
			s += exportAttribute("parallelMultiple", parallelMultiple);
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		diagram.setNextId(id);
		id2node.put(id, diagram.addEvent(name, eventType, eventTrigger, eventUse, lane, isInterrupting, null));
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, SubProcess subProcess) {
		diagram.setNextId(id);
		id2node.put(id, diagram.addEvent(name, eventType, eventTrigger, eventUse, subProcess, isInterrupting, null));
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane, Activity boundaryNode) {
		diagram.setNextId(id);
		id2node.put(id, diagram.addEvent(name, eventType, eventTrigger, eventUse, lane, isInterrupting, boundaryNode));
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			diagram.setNextId(id);
			Event startEvent = diagram.addEvent(name, eventType, eventTrigger, eventUse, lane, isInterrupting, null);
			id2node.put(id, startEvent);
		}
	}
	
	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, SubProcess subProcess) {
		if (elements.contains(id)) {
			diagram.setNextId(id);
			Event event = diagram.addEvent(name, eventType, eventTrigger, eventUse, subProcess, isInterrupting, null);
			id2node.put(id, event);
		}
	}
	
	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane, Activity boundaryNode) {
		if (elements.contains(id)) {
			diagram.setNextId(id);
			Event event = diagram.addEvent(name, eventType, eventTrigger, eventUse, lane, isInterrupting, boundaryNode);
			id2node.put(id, event);
		}
	}
	
	public void marshall(BPMNDiagram diagram, Event bpmnEvent) {
		super.marshall(diagram, bpmnEvent);
			
		eventType = bpmnEvent.getEventType();
		eventTrigger = bpmnEvent.getEventTrigger();
		eventUse = bpmnEvent.getEventUse();
		isInterrupting = bpmnEvent.isInterrupting().equals("false")? false : true;
		parallelMultiple = bpmnEvent.getParallelMultiple();
	}
}
