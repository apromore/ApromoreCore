package de.hpi.bpmn2_0.transformation;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.AdHocSubProcess;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.Transaction;
import de.hpi.bpmn2_0.model.activity.type.BusinessRuleTask;
import de.hpi.bpmn2_0.model.activity.type.ManualTask;
import de.hpi.bpmn2_0.model.activity.type.ReceiveTask;
import de.hpi.bpmn2_0.model.activity.type.ScriptTask;
import de.hpi.bpmn2_0.model.activity.type.SendTask;
import de.hpi.bpmn2_0.model.activity.type.ServiceTask;
import de.hpi.bpmn2_0.model.activity.type.UserTask;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.artifacts.Group;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.choreography.CallChoreography;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.DataAssociation;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.conversation.CallConversation;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.conversation.SubConversation;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataObjectReference;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CatchEvent;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.ImplicitThrowEvent;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.IntermediateThrowEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.event.ThrowEvent;
import de.hpi.bpmn2_0.model.gateway.ComplexGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.Participant;

public interface Visitor {
	
	public void visitDiagramElement(DiagramElement that);
	
	public void visitBpmnEdge(BPMNEdge that);
	
	public void visitBpmnShape(BPMNShape that);	
	
	public void visitAbstractDataObject(AbstractDataObject that);

	public void visitActivity(Activity that);

	public void visitAdHocSubProcess(AdHocSubProcess that);

	public void visitArtifact(Artifact that);

	public void visitAssociation(Association that);

	public void visitBaseElement(BaseElement that);

	public void visitBoundaryEvent(BoundaryEvent that);

	public void visitBusinessRuleTask(BusinessRuleTask that);

	public void visitCallActivity(CallActivity that);

	public void visitCallChoreography(CallChoreography that);
	
	public void visitCallConversation(CallConversation that);

	public void visitCatchEvent(CatchEvent that);

	public void visitChoreographyActivity(ChoreographyActivity that);

	public void visitChoreographyTask(ChoreographyTask that);

	public void visitComplexGateway(ComplexGateway that);

	public void visitConversation(Conversation that);

	public void visitConversationLink(ConversationLink that);

	public void visitConversationNode(ConversationNode that);

	public void visitDataAssociation(DataAssociation that);

	public void visitDataInput(DataInput that);

	public void visitDataInputAssociation(DataInputAssociation that);

	public void visitDataObject(DataObject that);
	
	public void visitDataObjectReference(DataObjectReference that);

	public void visitDataOutput(DataOutput that);

	public void visitDataOutputAssociation(DataOutputAssociation that);

	public void visitDataStoreReference(DataStoreReference that);

	public void visitEdge(Edge that);

	public void visitEndEvent(EndEvent that);

	public void visitEvent(Event that);

	public void visitEventBasedGateway(EventBasedGateway that);

	public void visitExclusiveGateway(ExclusiveGateway that);

	public void visitFlowElement(FlowElement that);

	public void visitFlowNode(FlowNode that);

	public void visitGateway(Gateway that);

	public void visitGatewayWithDefaultFlow(GatewayWithDefaultFlow that);

	public void visitGroup(Group that);

	public void visitImplicitThrowEvent(ImplicitThrowEvent that);
	
	public void visitInclusiveGateway(InclusiveGateway that);

	public void visitIntermediateCatchEvent(IntermediateCatchEvent that);

	public void visitIntermediateThrowEvent(IntermediateThrowEvent that);

	public void visitLane(Lane that);

	public void visitManualTask(ManualTask that);

	public void visitMessageFlow(MessageFlow that);

	public void visitParallelGateway(ParallelGateway that);

	public void visitParticipant(Participant that);

	public void visitReceiveTask(ReceiveTask that);

	public void visitScriptTask(ScriptTask that);

	public void visitSendTask(SendTask that);

	public void visitSequenceFlow(SequenceFlow that);

	public void visitServiceTask(ServiceTask that);

	public void visitStartEvent(StartEvent that);

	public void visitSubChoreography(SubChoreography that);

	public void visitSubConversation(SubConversation that);

	public void visitSubProcess(SubProcess that);

	public void visitTask(Task that);

	public void visitTextAnnotation(TextAnnotation that);

	public void visitThrowEvent(ThrowEvent that);

	public void visitTransaction(Transaction that);

	public void visitUserTask(UserTask that);

	public void visitMessage(Message that);


}