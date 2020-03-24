package org.apromore.processmining.plugins.bpmn;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.xmlpull.v1.XmlPullParser;

public class BpmnIntermediateEvent extends BpmnEvent {
	
	BpmnMessageEventDefinition messageDefinition;
	BpmnTimerEventDefinition timerDefinition;
	BpmnErrorEventDefinition errorDefinition;
	String attachedToRef;
	
	public BpmnIntermediateEvent(String tag, EventUse eventUse) {
		super(tag, EventType.INTERMEDIATE);
		this.eventUse = eventUse; 	
	}
	
	public void setAttachedToRef(String attachedToRef) {
		this.attachedToRef = attachedToRef;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);

		String attachedToRef = xpp.getAttributeValue(null, "attachedToRef");
		if (attachedToRef != null) {
			this.attachedToRef = attachedToRef;
		}
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element.
			 */
			return true;
		}
		if (xpp.getName().equals("messageEventDefinition")) {
			BpmnMessageEventDefinition messageDefinition = new BpmnMessageEventDefinition("messageEventDefinition");
			messageDefinition.importElement(xpp, bpmn);
			this.messageDefinition = messageDefinition;
			eventTrigger = EventTrigger.MESSAGE;
			return true;
		}
		if (xpp.getName().equals("timerEventDefinition")) {
			BpmnTimerEventDefinition timerDefinition = new BpmnTimerEventDefinition("timerEventDefinition");
			timerDefinition.importElement(xpp, bpmn);
			this.timerDefinition = timerDefinition;
			eventTrigger = EventTrigger.TIMER;
			return true;
		}
		if (xpp.getName().equals("errorEventDefinition")) {
			BpmnErrorEventDefinition errorDefinition = new BpmnErrorEventDefinition("errorEventDefinition");
			errorDefinition.importElement(xpp, bpmn);
			this.errorDefinition = errorDefinition;
			eventTrigger = EventTrigger.ERROR;
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if(attachedToRef != null) {
			s += exportAttribute("attachedToRef", attachedToRef);
		}
		
		return s;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if(messageDefinition != null) {
			s += messageDefinition.exportElement();
		}
		if(timerDefinition != null) {
			s += timerDefinition.exportElement();
		}	
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		if ((attachedToRef != null) && (id2node.get(attachedToRef) instanceof Activity)){
			Activity boundaryNode = (Activity)id2node.get(attachedToRef);
			super.unmarshall(diagram, id2node, lane, boundaryNode);
		} else {
			super.unmarshall(diagram, id2node, lane);
		}
	}
	
	public void marshall(Event bpmnEvent) {
		super.marshall(bpmnEvent);
		if(bpmnEvent.getBoundingNode() != null) {
			attachedToRef = bpmnEvent.getBoundingNode().getId().toString().replace(' ', '_');
		}
		if(bpmnEvent.getEventTrigger().equals(EventTrigger.MESSAGE)) {
			messageDefinition = new BpmnMessageEventDefinition("messageEventDefinition");
		}
		if(bpmnEvent.getEventTrigger().equals(EventTrigger.TIMER)) {
			timerDefinition = new BpmnTimerEventDefinition("timerEventDefinition");
		}
	}
}
