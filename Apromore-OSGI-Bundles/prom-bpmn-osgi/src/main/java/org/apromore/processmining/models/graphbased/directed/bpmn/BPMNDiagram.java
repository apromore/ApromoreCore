package org.apromore.processmining.models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.Set;

import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataAssociation;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.apromore.processmining.plugins.bpmn.BpmnAssociation.AssociationDirection;

public interface BPMNDiagram extends DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> {

	String getLabel();

	//Activities
	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed);

	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, SubProcess parentSubProcess);

	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, Swimlane parentSwimlane);

	Activity removeActivity(Activity activity);

	Collection<Activity> getActivities();
	
	Collection<Activity> getActivities(Swimlane pool);

    //callActivities
    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed);

    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed, SubProcess parentSubProcess);

    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed, Swimlane parentSwimlane);

    CallActivity removeCallActivity(CallActivity activity);

    Collection<CallActivity> getCallActivities();

    Collection<CallActivity> getCallActivities(Swimlane pool);

	//SubProcesses
	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane);
	
	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent, SubProcess parentSubProcess);

	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent, Swimlane parentSwimlane);


	Activity removeSubProcess(SubProcess subprocess);

	Collection<SubProcess> getSubProcesses();
	
	Collection<SubProcess> getSubProcesses(Swimlane pool);

	//Events
	@Deprecated
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Activity exceptionFor);

	@Deprecated
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, Activity exceptionFor);

	@Deprecated
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, Activity exceptionFor);
	
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			boolean isInterrupting, Activity exceptionFor);

	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, boolean isInterrupting, Activity exceptionFor);

	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, boolean isInterrupting, Activity exceptionFor);

	Event removeEvent(Event event);

	Collection<Event> getEvents();
	
	Collection<Event> getEvents(Swimlane pool);

	//Gateways
	Gateway addGateway(String label, GatewayType gatewayType);

	Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess);

	Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane);

	Gateway removeGateway(Gateway gateway);

	Collection<Gateway> getGateways();
	
	Collection<Gateway> getGateways(Swimlane pool);
	
	//Data objects
	DataObject addDataObject(String label);

	DataObject removeDataObject(DataObject dataObject);
	
	Collection<DataObject> getDataObjects();
	
	//Artifacts
	TextAnnotation addTextAnnotation(String label);
	
	TextAnnotation removeTextAnnotation(TextAnnotation textAnnotation);
	
	Collection<TextAnnotation> getTextAnnotations();
	
	Collection<TextAnnotation> getTextAnnotations(Swimlane pool);
	
	Association addAssociation(BPMNNode source, BPMNNode target, AssociationDirection direction);
	
	Collection<Association> getAssociations();
	
	Collection<Association> getAssociations(Swimlane pool);

	//Flows
	Flow addFlow(BPMNNode source, BPMNNode target, String label);

	@Deprecated	
	Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

	@Deprecated
	Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

	Collection<Flow> getFlows();
	
	Collection<Flow> getFlows(Swimlane pool);
	
	Collection<Flow> getFlows(SubProcess subProcess);

	//MessageFlows
	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, String label);

	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

	MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

	Set<MessageFlow> getMessageFlows();
	
	//DataAssociatons
	DataAssociation addDataAssociation(BPMNNode source, BPMNNode target, String label);
	
	Collection<DataAssociation> getDataAssociations();
	
	//TextAnnotations
	TextAnnotation addTextAnnotations(TextAnnotation textAnnotation);
	
	Collection<TextAnnotation> getTextannotations();

	Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent);
	
	Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent, SwimlaneType type);

	Swimlane removeSwimlane(Swimlane swimlane);

	Collection<Swimlane> getSwimlanes();
	
	Collection<Swimlane> getPools();
	
	Collection<Swimlane> getLanes(ContainingDirectedGraphNode parent);
}
