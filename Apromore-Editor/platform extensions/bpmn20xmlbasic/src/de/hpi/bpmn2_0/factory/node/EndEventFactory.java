/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.misc.Operation;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.CancelEventDefinition;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.Escalation;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.TerminateEventDefinition;
import de.hpi.bpmn2_0.model.misc.Error;
import de.hpi.bpmn2_0.model.misc.Signal;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory to create end events
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"EndNoneEvent",
	"EndMessageEvent",
	"EndEscalationEvent",
	"EndErrorEvent",
	"EndCancelEvent",
	"EndCompensationEvent",
	"EndSignalEvent",
	"EndMultipleEvent",
	"EndTerminateEvent"
})
public class EndEventFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected EndEvent createProcessElement(GenericShape shape) throws BpmnConverterException {
		try {
			EndEvent endEvent = (EndEvent) this.invokeCreatorMethod(shape);
			endEvent.setId(shape.getResourceId());
			endEvent.setName(shape.getProperty("name"));
			
			return endEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/* Methods for different */
	
	@StencilId("EndNoneEvent")
	public EndEvent createEndNoneEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		return endEvent;
	}
	
	@StencilId("EndMessageEvent")
	public EndEvent createEndMessageEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		MessageEventDefinition msgEventDef = new MessageEventDefinition();
		
		
		/* Message name */
		String messageName = shape.getProperty("messagename");
		if(messageName != null && !(messageName.length() == 0)) {
			Message message = new Message();
			message.setName(messageName);
			msgEventDef.setMessageRef(message);
		}
		
		/* Operation name */
		String operationName = shape.getProperty("operationname");
		if(operationName != null && !(operationName.length() == 0)) {
			Operation operation = new Operation();
			operation.setName(operationName);
			msgEventDef.setOperationRef(operation);
		}
		
		endEvent.getEventDefinition().add(msgEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndEscalationEvent")
	public EndEvent createEndEscalationEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		EscalationEventDefinition escalEventDef = new EscalationEventDefinition();
		
		Escalation escalation = new Escalation();
		
		/* Escalation name */
		String escalationName = shape.getProperty("escalationname");
		if(escalationName != null && !(escalationName.length() == 0)) {
			escalation.setName(escalationName);
		}
		
		/* Escalation code */
		String escalationCode = shape.getProperty("escalationcode");
		if(escalationCode != null && !(escalationCode.length() == 0)) {
			escalation.setEscalationCode(escalationCode);
		}
		
		escalEventDef.setEscalationRef(escalation);
		endEvent.getEventDefinition().add(escalEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndErrorEvent")
	public EndEvent createEndErrorEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		ErrorEventDefinition errorEventDef = new ErrorEventDefinition();
		
		Error error = new Error();
		
		/* Error name */
		String errorName = shape.getProperty("errorname");
		if(errorName != null && !(errorName.length() == 0)) {
			error.setName(errorName);
		}
		
		/* Error code */
		String errorCode = shape.getProperty("errorcode");
		if(errorCode != null && !(errorCode.length() == 0)) {
			error.setErrorCode(errorCode);
		}
		
		errorEventDef.setErrorRef(error);
		
		endEvent.getEventDefinition().add(errorEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndCancelEvent")
	public EndEvent createEndCancelEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		CancelEventDefinition cancelEventDef = new CancelEventDefinition();
		endEvent.getEventDefinition().add(cancelEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndCompensationEvent")
	public EndEvent createEndCompensateEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		CompensateEventDefinition compEventDef = new CompensateEventDefinition();
		
		/* Activity Reference */
		String activityRef = shape.getProperty("activityref");
		if(activityRef != null && !(activityRef.length() == 0)) {
			Task taskRef = new Task();
			taskRef.setId(activityRef);
			compEventDef.setActivityRef(taskRef);
		}
		
		/* Wait for Completion */
		String waitForCompletion = shape.getProperty("waitforcompletion");
		if(waitForCompletion != null && waitForCompletion.equals("false")) {
			compEventDef.setWaitForCompletion(false);
		} else {
			compEventDef.setWaitForCompletion(true);
		}
		
		endEvent.getEventDefinition().add(compEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndSignalEvent")
	public EndEvent createEndSignalEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		SignalEventDefinition signalEventDef = new SignalEventDefinition();
		
		Signal signal = new Signal();
		
		/* Signal ID */
		signal.setId(SignavioUUID.generate());
		
		/* Signal name */
		String signalName = shape.getProperty("signalname");
		if(signalName != null && !(signalName.length() == 0)) {
			signal.setName(signalName);
		}
		
		signalEventDef.setSignalRef(signal);
		endEvent.getEventDefinition().add(signalEventDef);
		
		return endEvent;
	}
	
	@StencilId("EndMultipleEvent")
	public EndEvent createEndMultipleEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		endEvent.getEventDefinition().add(new CancelEventDefinition());
		endEvent.getEventDefinition().add(new TerminateEventDefinition());
		
		return endEvent;
	}
	
	@StencilId("EndTerminateEvent")
	public EndEvent createEndTerminateEvent(GenericShape shape) {
		EndEvent endEvent = new EndEvent();
		
		TerminateEventDefinition eventDef = new TerminateEventDefinition();
		endEvent.getEventDefinition().add(eventDef);
		
		return endEvent;
	}
}
