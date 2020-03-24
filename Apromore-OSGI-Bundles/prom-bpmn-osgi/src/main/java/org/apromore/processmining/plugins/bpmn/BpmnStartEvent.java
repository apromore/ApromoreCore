package org.apromore.processmining.plugins.bpmn;

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.xmlpull.v1.XmlPullParser;

public class BpmnStartEvent extends BpmnEvent {

	BpmnMessageEventDefinition messageDefinition;
	BpmnTimerEventDefinition timerDefinition;
	BpmnErrorEventDefinition errorDefinition;
	
	public BpmnStartEvent(String tag) {
		super(tag, EventType.START);
		eventUse = EventUse.CATCH;
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
}
