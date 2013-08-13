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
import de.hpi.bpmn2_0.model.event.Escalation;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.IntermediateThrowEvent;
import de.hpi.bpmn2_0.model.event.LinkEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.TerminateEventDefinition;
import de.hpi.bpmn2_0.model.misc.Signal;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory to create intermediate throwing events
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"IntermediateEvent",
	"IntermediateMessageEventThrowing",
	"IntermediateEscalationEventThrowing",
	"IntermediateLinkEventThrowing",
	"IntermediateCompensationEventThrowing",
	"IntermediateSignalEventThrowing",
	"IntermediateMultipleEventThrowing"
})
public class IntermediateThrowEventFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected IntermediateThrowEvent createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			IntermediateThrowEvent itEvent = (IntermediateThrowEvent) this.invokeCreatorMethod(shape);
			itEvent.setId(shape.getResourceId());
			itEvent.setName(shape.getProperty("name"));
			
			return itEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/* Creator methods for different throwing intermediate event definitions */
	
	@StencilId("IntermediateEvent")
	public IntermediateThrowEvent createIntermediateNoneEvent(GenericShape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();
		return itEvent;
	}
	
	@StencilId("IntermediateMessageEventThrowing")
	public IntermediateThrowEvent createIntermediateMessageEvent(GenericShape shape) 
		throws BpmnConverterException {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		MessageEventDefinition msgDef = new MessageEventDefinition();
		
		
		/* Message name */
		String messageName = shape.getProperty("messagename");
		if(messageName != null && !(messageName.length() == 0)) {
			Message message = new Message();
			message.setName(messageName);
			msgDef.setMessageRef(message);
		}
		
		/* Operation name */
		String operationName = shape.getProperty("operationname");
		if(operationName != null && !(operationName.length() == 0)) {
			Operation operation = new Operation();
			operation.setName(operationName);
			msgDef.setOperationRef(operation);
		}
		
		itEvent.getEventDefinition().add(msgDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateEscalationEventThrowing")
	public IntermediateThrowEvent createIntermediateEscalationEvent(GenericShape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		EscalationEventDefinition escalDef = new EscalationEventDefinition();
		
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
		
		escalDef.setEscalationRef(escalation);
		itEvent.getEventDefinition().add(escalDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateLinkEventThrowing")
	public IntermediateThrowEvent createIntermediateLinkEvent(GenericShape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		LinkEventDefinition linkDef = new LinkEventDefinition();
		
		/* Set required name attribute */
		String name = shape.getProperty("name");
		if(name != null && !(name.length() == 0))
			linkDef.setName(name);
		
		/* Set target reference */
		String targetEntry = shape.getProperty("entry");
		if(targetEntry != null && targetEntry.length() != 0) {
			linkDef.setTarget(targetEntry);
		}
		
		itEvent.getEventDefinition().add(linkDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateCompensationEventThrowing")
	public IntermediateThrowEvent createIntermediateCompensationEvent(GenericShape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		CompensateEventDefinition compDef = new CompensateEventDefinition();
		
		/* Activity Reference */
		String activityRef = shape.getProperty("activityref");
		if(activityRef != null && !(activityRef.length() == 0)) {
			Task taskRef = new Task();
			taskRef.setId(activityRef);
			compDef.setActivityRef(taskRef);
		}
		
		/* Wait for Completion */
		String waitForCompletion = shape.getProperty("waitforcompletion");
		if(waitForCompletion != null && waitForCompletion.equals("false")) {
			compDef.setWaitForCompletion(false);
		} else {
			compDef.setWaitForCompletion(true);
		}
		
		itEvent.getEventDefinition().add(compDef);
		
		return itEvent;
	}
	
	
	@StencilId("IntermediateSignalEventThrowing")
	public IntermediateThrowEvent createIntermediateSignalEvent(GenericShape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();

		SignalEventDefinition sigDef = new SignalEventDefinition();
		
		Signal signal = new Signal();
		
		/* Signal ID */
		signal.setId(SignavioUUID.generate());
		
		/* Signal name */
		String signalName = shape.getProperty("signalname");
		if(signalName != null && !(signalName.length() == 0)) {
			signal.setName(signalName);
		}
		
		sigDef.setSignalRef(signal);
		itEvent.getEventDefinition().add(sigDef);
		
		return itEvent;
	}
	
	@StencilId("IntermediateMultipleEventThrowing")
	public IntermediateThrowEvent createIntermediateMultipleEvent(GenericShape shape) {
		IntermediateThrowEvent itEvent = new IntermediateThrowEvent();
		
		itEvent.getEventDefinition().add(new CancelEventDefinition());
		itEvent.getEventDefinition().add(new TerminateEventDefinition());
		
		return itEvent;
	}
}
