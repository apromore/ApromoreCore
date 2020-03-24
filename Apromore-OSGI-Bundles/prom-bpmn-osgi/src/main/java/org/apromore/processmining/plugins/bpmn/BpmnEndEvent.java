package org.apromore.processmining.plugins.bpmn;

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.xmlpull.v1.XmlPullParser;

public class BpmnEndEvent extends BpmnEvent {

	BpmnMessageEventDefinition messageDefinition;
	BpmnErrorEventDefinition errorDefinition;
	
	public BpmnEndEvent(String tag) {
		super(tag, EventType.END);
		eventUse = EventUse.THROW;
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
}
