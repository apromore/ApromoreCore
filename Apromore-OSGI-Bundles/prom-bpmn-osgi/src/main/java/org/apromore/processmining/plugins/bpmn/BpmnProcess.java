package org.apromore.processmining.plugins.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.xmlpull.v1.XmlPullParser;

public class BpmnProcess extends BpmnIdName {

	private Collection<BpmnStartEvent> startEvents;
	private Collection<BpmnTask> tasks;
	private Collection<BpmnSubProcess> subprocess;
	private Collection<BpmnExclusiveGateway> exclusiveGateways;
	private Collection<BpmnParallelGateway> parallelGateways;
	private Collection<BpmnInclusiveGateway> inclusiveGateways;
    private Collection<BpmnEventBasedGateway> eventBasedGateways;
    private Collection<BpmnCallActivity> callActivities;
	private Collection<BpmnSequenceFlow> sequenceFlows;
	private Collection<BpmnEndEvent> endEvents;
	private Collection<BpmnIntermediateEvent> intermediateEvents;
	private Collection<BpmnDataObject> dataObjects;
	private Collection<BpmnDataObjectReference> dataObjectsRefs;
	private Collection<BpmnTextAnnotation> textAnnotations;
	private Collection<BpmnAssociation> associations;
	private BpmnLaneSet laneSet;

	public BpmnProcess(String tag) {
		super(tag);

		startEvents = new HashSet<BpmnStartEvent>();
		tasks = new ArrayList<BpmnTask>();
		exclusiveGateways = new ArrayList<BpmnExclusiveGateway>();
		parallelGateways = new ArrayList<BpmnParallelGateway>();
		inclusiveGateways = new ArrayList<BpmnInclusiveGateway>();
        eventBasedGateways =new ArrayList<BpmnEventBasedGateway>();
        callActivities=new HashSet<BpmnCallActivity>();
		sequenceFlows = new ArrayList<BpmnSequenceFlow>();
		endEvents = new HashSet<BpmnEndEvent>();
		intermediateEvents = new HashSet<BpmnIntermediateEvent>();
		subprocess = new HashSet<BpmnSubProcess>();
		dataObjects = new HashSet<BpmnDataObject>();
		dataObjectsRefs = new HashSet<BpmnDataObjectReference>();
		textAnnotations = new HashSet<BpmnTextAnnotation>();
		associations = new HashSet<BpmnAssociation>();
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("startEvent")) {
			BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");
			startEvent.importElement(xpp, bpmn);
			startEvents.add(startEvent);
			return true;
		} else if (xpp.getName().equals("task")) {
			BpmnTask task = new BpmnTask("task");
			task.importElement(xpp, bpmn);
			tasks.add(task);
			return true;		
		} else if (xpp.getName().equals("businessRuleTask")) {
			BpmnTask task = new BpmnTask("businessRuleTask");
			task.importElement(xpp, bpmn);
			tasks.add(task);
			return true;		
		}
		else if (xpp.getName().equals("receiveTask")) {
			BpmnTask task = new BpmnTask("receiveTask");
			task.importElement(xpp, bpmn);
			tasks.add(task);
			return true;
		}
		else if (xpp.getName().equals("sendTask")) {
			BpmnTask task = new BpmnTask("sendTask");
			task.importElement(xpp, bpmn);
			tasks.add(task);
			return true;
		}
		else if (xpp.getName().equals("exclusiveGateway")) {
			BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
			exclusiveGateway.importElement(xpp, bpmn);
			exclusiveGateways.add(exclusiveGateway);
			return true;
		} else if (xpp.getName().equals("parallelGateway")) {
			BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
			parallelGateway.importElement(xpp, bpmn);
			parallelGateways.add(parallelGateway);
			return true;
		} else if (xpp.getName().equals("inclusiveGateway")) {
			BpmnInclusiveGateway inclusiveGateway = new BpmnInclusiveGateway("inclusiveGateway");
			inclusiveGateway.importElement(xpp, bpmn);
			inclusiveGateways.add(inclusiveGateway);
			return true;
		} else if (xpp.getName().equals("eventBasedGateway")) {
            BpmnEventBasedGateway eventBasedGateway = new BpmnEventBasedGateway("eventBasedGateway");
            eventBasedGateway.importElement(xpp, bpmn);
            eventBasedGateways.add(eventBasedGateway);
            return true;
        } else if (xpp.getName().equals("callActivity")) {
            BpmnCallActivity callActivity=new BpmnCallActivity("callActivity");
            callActivity.importElement(xpp, bpmn);
            callActivities.add(callActivity);
            return true;
		} else if (xpp.getName().equals("subProcess")) {
			BpmnSubProcess subPro = new BpmnSubProcess("subProcess");
			subPro.importElement(xpp, bpmn);
			subprocess.add(subPro);
			return true;
		} else if (xpp.getName().equals("sequenceFlow")) {
			BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
			sequenceFlow.importElement(xpp, bpmn);
			sequenceFlows.add(sequenceFlow);
			return true;
		} else if (xpp.getName().equals("endEvent")) {
			BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");
			endEvent.importElement(xpp, bpmn);
			endEvents.add(endEvent);
			return true;
		} else if (xpp.getName().equals("intermediateCatchEvent")) {
			BpmnIntermediateEvent intCatchEvent = new BpmnIntermediateEvent("intermediateCatchEvent", 
					EventUse.CATCH);
			intCatchEvent.importElement(xpp, bpmn);
			intermediateEvents.add(intCatchEvent);
			return true;		
		} else if (xpp.getName().equals("boundaryEvent")) {
			BpmnIntermediateEvent intCatchEvent = new BpmnIntermediateEvent("boundaryEvent", 
					EventUse.CATCH);
			intCatchEvent.importElement(xpp, bpmn);
			intermediateEvents.add(intCatchEvent);
			return true;
		} else if (xpp.getName().equals("intermediateThrowEvent")) {
			BpmnIntermediateEvent intThrowEvent = new BpmnIntermediateEvent("intermediateThrowEvent", 
					EventUse.THROW);
			intThrowEvent.importElement(xpp, bpmn);
			intermediateEvents.add(intThrowEvent);
			return true;
		} else if (xpp.getName().equals("dataObjectReference")) {
			BpmnDataObjectReference dataObjectRef = new BpmnDataObjectReference("dataObjectReference");
			dataObjectRef.importElement(xpp, bpmn);
			dataObjectsRefs.add(dataObjectRef);
			return true;
		} else if (xpp.getName().equals("laneSet")) {
			laneSet = new BpmnLaneSet("laneSet");
			laneSet.importElement(xpp, bpmn);
			return true;
		} else if (xpp.getName().equals("textAnnotation")) {
			BpmnTextAnnotation textAnnotation = new BpmnTextAnnotation("textAnnotation");
			textAnnotation.importElement(xpp, bpmn);
			textAnnotations.add(textAnnotation);
			return true;
		} else if (xpp.getName().equals("association")) {
			BpmnAssociation association = new BpmnAssociation("association");
			association.importElement(xpp, bpmn);
			associations.add(association);
			return true;
		} else if (xpp.getName().equals("userTask")) {
            BpmnUserTask task = new BpmnUserTask("userTask");
            task.importElement(xpp, bpmn);
            tasks.add(task);
            return true;
        } else if (xpp.getName().equals("serviceTask")) {
            BpmnServiceTask task = new BpmnServiceTask("serviceTask");
            task.importElement(xpp, bpmn);
            tasks.add(task);
            return true;
        } else if (xpp.getName().equals("sendTask")) {
            BpmnSendTask task = new BpmnSendTask("sendTask");
            task.importElement(xpp, bpmn);
            tasks.add(task);
            return true;
		}else if (xpp.getName().equals("receiveTask")) {
			BpmnReceiveTask task = new BpmnReceiveTask("receiveTask");
            task.importElement(xpp, bpmn);
            tasks.add(task);
            return true;
		}else if (xpp.getName().equals("scriptTask")) {
			// HV: Import script tasks.
			BpmnScriptTask task = new BpmnScriptTask("scriptTask");
            task.importElement(xpp, bpmn);
            tasks.add(task);
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
		if (laneSet != null) {
			s += laneSet.exportElement();
		}
		for (BpmnStartEvent startEvent : startEvents) {
			s += startEvent.exportElement();
		}
		for (BpmnEndEvent endEvent : endEvents) {
				s += endEvent.exportElement();
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			s += intermediateEvent.exportElement();
		}
		for (BpmnTask task : tasks) {
			s += task.exportElement();
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			s += exclusiveGateway.exportElement();
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			s += parallelGateway.exportElement();
		}
		for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
			s += inclusiveGateway.exportElement();
		}
		for (BpmnSubProcess subPro : subprocess) {
			s += subPro.exportElement();
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			s += sequenceFlow.exportElement();
		}
		for (BpmnDataObject dataObject : dataObjects) {
			s += dataObject.exportElement();
		}
		for (BpmnDataObjectReference dataObjectRef : dataObjectsRefs) {
			s += dataObjectRef.exportElement();
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			s += textAnnotation.exportElement();
		}
		for (BpmnAssociation association : associations) {
			s += association.exportElement();
		}
        for (BpmnEventBasedGateway eventBasedGateway: eventBasedGateways) {
            s += eventBasedGateway.exportElement();
        }
        for(BpmnCallActivity callActivity:callActivities){
            s+=callActivity.exportElement();
        }
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		Swimlane lane = id2lane.get(id);
		if(laneSet != null) {
			laneSet.unmarshall(diagram, id2node,  id2lane, lane);
		}
		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(startEvent, id2lane));
		}
		for (BpmnDataObjectReference dataObjectRef : dataObjectsRefs) {
			dataObjectRef.unmarshall(diagram, id2node);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, id2node, retrieveParentSwimlane(task, id2lane));
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, id2node, retrieveParentSwimlane(exclusiveGateway, id2lane));
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, id2node, retrieveParentSwimlane(parallelGateway, id2lane));
		}
		for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
			inclusiveGateway.unmarshall(diagram, id2node, retrieveParentSwimlane(inclusiveGateway, id2lane));
		}
        for (BpmnCallActivity callActivity:callActivities){
            callActivity.unmarshall(diagram, id2node, retrieveParentSwimlane(callActivity, id2lane));
        }
        for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways){
            eventBasedGateway.unmarshall(diagram, id2node, retrieveParentSwimlane(eventBasedGateway, id2lane));
        }
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(endEvent, id2lane));
		}
		for (BpmnSubProcess subProcess : subprocess) {
			subProcess.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnSubProcess subProcess : subprocess) {
			subProcess.unmarshall(diagram, id2node, id2lane, retrieveParentSwimlane(subProcess, id2lane));
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			intermediateEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(intermediateEvent, id2lane));
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, id2node);
		}
		for (BpmnTask task : tasks) {
			task.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, id2node);
		}
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, id2node);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node,
			Map<String, Swimlane> id2lane) {
		Swimlane lane = id2lane.get(id);
		if(laneSet != null) {
			laneSet.unmarshall(diagram, elements, id2node, id2lane, lane);
		}
		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(startEvent, id2lane));
		}
        for (BpmnDataObjectReference dataObjectRef : dataObjectsRefs) {
            dataObjectRef.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(dataObjectRef, id2lane));
        }
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(task, id2lane));
		}
        for (BpmnCallActivity callActivity : callActivities) {
            callActivity.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(callActivity, id2lane));
        }
        for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways){
            eventBasedGateway.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(eventBasedGateway, id2lane));
        }
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(exclusiveGateway, id2lane));
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(parallelGateway, id2lane));
		}
		for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
			inclusiveGateway.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(inclusiveGateway, id2lane));
		}
		for (BpmnSubProcess subPro : subprocess) {
			subPro.unmarshall(diagram, elements, id2node, id2lane, retrieveParentSwimlane(subPro, id2lane));
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, elements, id2node, retrieveParentSwimlane(endEvent, id2lane));
		}
		for (BpmnIntermediateEvent intEvent : intermediateEvents) {
			intEvent.unmarshall(diagram, id2node, retrieveParentSwimlane(intEvent, id2lane));
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, elements, id2node);
		}
		for (BpmnTask task : tasks) {
			task.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnSubProcess subProcess : subprocess) {
			subProcess.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, elements, id2node);
		}
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, elements, id2node);
		}
	}
	
	private Swimlane retrieveParentSwimlane(BpmnId bpmnFlow, Map<String, Swimlane> id2lane) {
		if(laneSet != null) {
			Collection<BpmnLane> lanes = laneSet.getAllChildLanes();	
			for(BpmnLane bpmnLane : lanes) {
				for(BpmnText flowNodeRef : bpmnLane.getFlowNodeRef()) {
					if((flowNodeRef.getText() != null) 
							&& flowNodeRef.getText().equals(bpmnFlow.getId())) {
						return id2lane.get(bpmnLane.getId());
					}
				}
			}
		}
		return id2lane.get(id);
	}
	
	/**
	 * Constructs a process model from diagram
	 * 
	 * @param diagram
	 * @param pool
	 * @return "true" if at least one element has been added
	 */
	public boolean marshall(BPMNDiagram diagram, Swimlane pool) {
		
		clearAll();
		
		// Marshall events
		marshallEvents(diagram, pool);
		
		// Marshall activities
		marshallActivities(diagram, pool);

        // Marshall callActivities
        marshallCallActivities(diagram, pool);

		// Marshall gateways
		marshallGateways(diagram, pool);
		
		// Marshall dataObjects
		marshallDataObjects(diagram, pool);
		
		// Marshall SubProcess
		marshallSubProcesses(diagram, pool);
		
		// Marshall control flows
		marshallControlFlows(diagram, pool);
		
		// Marshall lane set
		marshallLaneSet(diagram, pool);
		
		// Marshall artifacts
		marshallArtifacts(diagram, pool);

        // Marshall associations
        marshallAssociations(diagram,pool);

		return !(startEvents.isEmpty() && endEvents.isEmpty() && tasks.isEmpty() 
					&& exclusiveGateways.isEmpty() && parallelGateways.isEmpty() 
					&& textAnnotations.isEmpty() && associations.isEmpty() && (laneSet == null));
	}

    private void marshallAssociations(BPMNDiagram diagram, Swimlane pool) {
        for (Association association: diagram.getAssociations()) {
            BpmnAssociation bpmnAssociation=new BpmnAssociation("association");
            bpmnAssociation .marshall(association);
            associations.add(bpmnAssociation);
        }
    }

    private void marshallCallActivities(BPMNDiagram diagram, Swimlane pool) {
        for (CallActivity activity : diagram.getCallActivities(pool)) {
        	if (activity.getParentSubProcess() == null) {
        		BpmnCallActivity callActivity=new BpmnCallActivity("callActivity");
        		callActivity .marshall(activity, diagram);
        		callActivities.add(callActivity);}
        }
    }
	
	private void marshallEvents(BPMNDiagram diagram, Swimlane pool) {		
		for (Event event : diagram.getEvents(pool)) {
			if (event.getAncestorSubProcess() == null) {
				if (event.getEventType() == EventType.START) {
					BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");
					startEvent.marshall(event);
					startEvents.add(startEvent);
				} else if (event.getEventType() == EventType.END) {
					BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");
					endEvent.marshall(event);
					endEvents.add(endEvent);
				} else if (event.getEventType() == EventType.INTERMEDIATE) {
					BpmnIntermediateEvent intermediateEvent = null;
					if (event.getEventUse() == EventUse.CATCH) {
						if (event.getBoundingNode() != null) {
							intermediateEvent = new BpmnIntermediateEvent("boundaryEvent", EventUse.CATCH);
						} else {
							intermediateEvent = new BpmnIntermediateEvent("intermediateCatchEvent", EventUse.CATCH);
						}
					} else {
						intermediateEvent = new BpmnIntermediateEvent("intermediateThrowEvent", EventUse.THROW);
					}
					intermediateEvent.marshall(event);
					intermediateEvents.add(intermediateEvent);
				}
			}
		}
	}
	
	private void marshallActivities(BPMNDiagram diagram, Swimlane pool) {
		for (Activity activity : diagram.getActivities(pool)) {
			if (activity.getAncestorSubProcess() == null) {
				BpmnTask task = new BpmnTask("task");
				// HV: Set to special task if special activity.
				if (activity.isBReceive()) {
					task = new BpmnReceiveTask("receiveTask");				
				} else if (activity.isBSend()) {
					task = new BpmnSendTask("sendTask");
				} else if (activity.isBService()) {
					task = new BpmnServiceTask("serviceTask");
				} else if (activity.isBScript()) {
					task = new BpmnScriptTask("scriptTask");
				}
				task.marshall(activity, diagram);
				tasks.add(task);
			}
		}
	}
	
	private void marshallGateways(BPMNDiagram diagram, Swimlane pool) {
		for (Gateway gateway : diagram.getGateways(pool)) {
			if (gateway.getAncestorSubProcess() == null) {
				if (gateway.getGatewayType() == GatewayType.DATABASED) {
					BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
					exclusiveGateway.marshall(diagram, gateway);
					exclusiveGateways.add(exclusiveGateway);
				} else if (gateway.getGatewayType() == GatewayType.PARALLEL) {
					BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
					parallelGateway.marshall(diagram, gateway);
					parallelGateways.add(parallelGateway);
				} else if (gateway.getGatewayType() == GatewayType.INCLUSIVE) {
					BpmnInclusiveGateway inclusiveGateway = new BpmnInclusiveGateway("inclusiveGateway");
					inclusiveGateway.marshall(diagram, gateway);
					inclusiveGateways.add(inclusiveGateway);
				} else if (gateway.getGatewayType() == GatewayType.EVENTBASED) {
	                BpmnEventBasedGateway eventBasedGateway = new BpmnEventBasedGateway("eventBasedGateway");
	                eventBasedGateway.marshall(diagram, gateway);
	                eventBasedGateways.add(eventBasedGateway);
				}
			}
		}
	}
	
	private void marshallDataObjects(BPMNDiagram diagram, Swimlane pool) {
		for (DataObject dataObject : diagram.getDataObjects()) {
			if (dataObject.getAncestorSubProcess() == null) {
				BpmnDataObject bpmnDataObject = new BpmnDataObject("dataObject");
				bpmnDataObject.marshall(dataObject);
				bpmnDataObject.setId("dataobj_" + dataObject.getId().toString().replace(' ', '_'));
				dataObjects.add(bpmnDataObject);

				BpmnDataObjectReference bpmnDataObjectRef = new BpmnDataObjectReference("dataObjectReference");
				bpmnDataObjectRef.marshall(dataObject);
				dataObjectsRefs.add(bpmnDataObjectRef);
			}
		}
	}
	
	private void marshallSubProcesses(BPMNDiagram diagram, Swimlane pool) {
		for (SubProcess sub : diagram.getSubProcesses(pool)) {
			if (sub.getAncestorSubProcess() == null) {
				BpmnSubProcess subProcess = new BpmnSubProcess("subProcess");
				subProcess.marshall(sub, diagram);
				subprocess.add(subProcess);
			}
		}
	}
	
	private void marshallControlFlows(BPMNDiagram diagram, Swimlane pool) {
		for (Flow flow : diagram.getFlows(pool)) {
			if (flow.getAncestorSubProcess() == null) {
				BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
				sequenceFlow.marshall(flow);
				sequenceFlows.add(sequenceFlow);
			}
		}
	}
	
	private void marshallLaneSet(BPMNDiagram diagram, ContainingDirectedGraphNode pool) {
		if(diagram.getLanes(pool).size() > 0) {
			laneSet = new BpmnLaneSet("laneSet");
			laneSet.marshall(diagram, pool);
		}
	}
	
	private void marshallArtifacts(BPMNDiagram diagram, Swimlane pool) {
		for (TextAnnotation textAnnotation : diagram.getTextAnnotations(pool)) {
			if (textAnnotation.getParentSubProcess() == null) {
				BpmnTextAnnotation bpmnTextAnnotation = new BpmnTextAnnotation("textAnnotation");
				bpmnTextAnnotation.marshall(textAnnotation);
				textAnnotations.add(bpmnTextAnnotation);
			}
		}
		for (Association association : diagram.getAssociations(pool)) {
				BpmnAssociation bpmnAssociation = new BpmnAssociation("association");
				bpmnAssociation.marshall(association);
				associations.add(bpmnAssociation);
		}
	}
	
	/**
	 * Clear all process contents
	 * 
	 */
	private void clearAll() {
		
		startEvents.clear();
		tasks.clear();
		exclusiveGateways.clear();
		parallelGateways.clear();
		inclusiveGateways.clear();
        callActivities.clear();
        eventBasedGateways.clear();
		sequenceFlows.clear();
		subprocess.clear();
		endEvents.clear();
		intermediateEvents.clear();
		dataObjects.clear();
		textAnnotations.clear();
		associations.clear();
	}
}
