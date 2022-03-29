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
