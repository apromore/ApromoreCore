package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataAssociation;
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

public class BpmnSubProcess extends BpmnIncomingOutgoing {

	private String triggeredByEvent;
	private BpmnMultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;
	private BpmnStandardLoopCharacteristics standardLoopCharacteristics;
	private BpmnInputOutputSpecification ioSpecification;
	private BpmnLaneSet laneSet;
	

	private Collection<BpmnStartEvent> startEvents;
	private Collection<BpmnTask> tasks;
	private Collection<BpmnSubProcess> subProcesses;
	private Collection<BpmnExclusiveGateway> exclusiveGateways;
	private Collection<BpmnParallelGateway> parallelGateways;
	private Collection<BpmnInclusiveGateway> inclusiveGateways;
	private Collection<BpmnSequenceFlow> sequenceFlows;
	private Collection<BpmnEndEvent> endEvents;
	private Collection<BpmnIntermediateEvent> intermediateEvents;
	private Collection<BpmnDataAssociation> inputAssociations;
	private Collection<BpmnDataAssociation> outputAssociations;
	private Collection<BpmnTextAnnotation> textAnnotations;
	private Collection<BpmnAssociation> associations;
    private Collection<BpmnEventBasedGateway> eventBasedGateways;
    private Collection<BpmnCallActivity> callActivities;

	public BpmnSubProcess(String tag) {
		
		super(tag);
		triggeredByEvent = null;
		ioSpecification = null;

		startEvents = new HashSet<BpmnStartEvent>();
		tasks = new HashSet<BpmnTask>();
		subProcesses = new HashSet<BpmnSubProcess>();
		exclusiveGateways = new HashSet<BpmnExclusiveGateway>();
		parallelGateways = new HashSet<BpmnParallelGateway>();
		inclusiveGateways = new HashSet<BpmnInclusiveGateway>();
		sequenceFlows = new HashSet<BpmnSequenceFlow>();
		endEvents = new HashSet<BpmnEndEvent>();
		intermediateEvents = new HashSet<BpmnIntermediateEvent>();
		inputAssociations = new HashSet<BpmnDataAssociation>();
		outputAssociations = new HashSet<BpmnDataAssociation>();
		textAnnotations = new HashSet<BpmnTextAnnotation>();
		associations = new HashSet<BpmnAssociation>();
        eventBasedGateways =new HashSet<BpmnEventBasedGateway>();
        callActivities=new HashSet<BpmnCallActivity>();

	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("multiInstanceLoopCharacteristics")) {
			multiInstanceLoopCharacteristics = new BpmnMultiInstanceLoopCharacteristics(
					"multiInstanceLoopCharacteristics");
			multiInstanceLoopCharacteristics.importElement(xpp, bpmn);
			return true;
		}
		if (xpp.getName().equals("standardLoopCharacteristics")) {
			standardLoopCharacteristics = new BpmnStandardLoopCharacteristics(
					"standardLoopCharacteristics");
			standardLoopCharacteristics.importElement(xpp, bpmn);
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
		}
		else if (xpp.getName().equals("businessRuleTask")) {
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
		else if (xpp.getName().equals("subProcess")) {
			BpmnSubProcess subProcess = new BpmnSubProcess("subProcess");
			subProcess.importElement(xpp, bpmn);
			subProcesses.add(subProcess);
			return true;
		} else if (xpp.getName().equals("exclusiveGateway")) {
			BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway(
					"exclusiveGateway");
			exclusiveGateway.importElement(xpp, bpmn);
			exclusiveGateways.add(exclusiveGateway);
			return true;
		} else if (xpp.getName().equals("parallelGateway")) {
			BpmnParallelGateway parallelGateway = new BpmnParallelGateway(
					"parallelGateway");
			parallelGateway.importElement(xpp, bpmn);
			parallelGateways.add(parallelGateway);
			return true;
		} else if (xpp.getName().equals("inclusiveGateway")) {
			BpmnInclusiveGateway inclusiveGateway = new BpmnInclusiveGateway(
					"inclusiveGateway");
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
		} else if (xpp.getName().equals("ioSpecification")) {
			ioSpecification = new BpmnInputOutputSpecification("ioSpecification");
			ioSpecification.importElement(xpp, bpmn);
			return true;
		} else if (xpp.getName().equals("dataInputAssociation")) {
			BpmnDataAssociation inputAssociation = new BpmnDataAssociation("dataInputAssociation");
			inputAssociation.importElement(xpp, bpmn);
			inputAssociations.add(inputAssociation);
			return true;
		} else if (xpp.getName().equals("dataOutputAssociation")) {
			BpmnDataAssociation outputAssociation = new BpmnDataAssociation("dataOutputAssociation");
			outputAssociation.importElement(xpp, bpmn);
			outputAssociations.add(outputAssociation);
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
		}	else if (xpp.getName().equals("laneSet")) {
				laneSet = new BpmnLaneSet("laneSet");
				laneSet.importElement(xpp, bpmn);
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
		if (multiInstanceLoopCharacteristics != null) {
			s += multiInstanceLoopCharacteristics.exportElement();
		}
		if (standardLoopCharacteristics != null) {
			s += standardLoopCharacteristics.exportElement();
		}
		for (BpmnStartEvent startEvent : startEvents) {
			s += startEvent.exportElement();
		}
		for (BpmnTask task : tasks) {
			s += task.exportElement();
		}
		for (BpmnSubProcess subProcess : subProcesses) {
			s += subProcess.exportElement();
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
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			s += sequenceFlow.exportElement();
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			s += intermediateEvent.exportElement();
		}
		for (BpmnEndEvent endEvent : endEvents) {
			s += endEvent.exportElement();
		}
		if (ioSpecification != null) {
			s += ioSpecification.exportElement();
		}
		for(BpmnDataAssociation inputAssociation : inputAssociations) {
			s += inputAssociation.exportElement();
		}
		for(BpmnDataAssociation outputAssociation : outputAssociations) {
			s += outputAssociation.exportElement();
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			s += textAnnotation.exportElement();
		}
		for (BpmnAssociation association : associations) {
			s += association.exportElement();
		}
        for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways) {
            s += eventBasedGateway.exportElement();
        }
        for (BpmnCallActivity callActivity : callActivities){
            s += callActivity.exportElement();
        }
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "triggeredByEvent");
		if (value != null) {
			triggeredByEvent = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (triggeredByEvent != null) {
			s += exportAttribute("triggeredByEvent", triggeredByEvent);
		}

		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane,
			Swimlane lane) {
		SubProcess subProcess;
		boolean triggerByEvent = false;
		if ((triggeredByEvent != null) && (triggeredByEvent.equals("true"))) {
			triggerByEvent = true;
		}
		if (multiInstanceLoopCharacteristics != null) {
			subProcess = diagram.addSubProcess(name, false, false, false, true, true, triggerByEvent,
					lane);
			id2node.put(id, subProcess);
		} else if(standardLoopCharacteristics != null) {
			subProcess = diagram.addSubProcess(name, true, false, false, false, true, triggerByEvent,
					lane);
			id2node.put(id, subProcess);
		} else {
			subProcess = diagram.addSubProcess(name, false, false, false, false,
					false, triggerByEvent, lane);
			id2node.put(id, subProcess);
		}
		if(ioSpecification != null) {
			Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
			for(BpmnId dataIncoming : dataIncomings) {
				id2node.put(dataIncoming.getId(), subProcess);
			}
			Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
			for(BpmnId dataOutgoing : dataOutgoins) {
				id2node.put(dataOutgoing.getId(), subProcess);
			}
		}

		Map<String, BPMNNode> id2nodeSubProcess = new HashMap<String, BPMNNode>();

		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, id2nodeSubProcess, lane);
		}
        for (BpmnCallActivity callActivity : callActivities){
        	callActivity.unmarshall(diagram, id2nodeSubProcess, subProcess);
        }
        for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways){
        	eventBasedGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
        }
		for (BpmnSubProcess childSubProcess : subProcesses) {
			childSubProcess.unmarshall(diagram, id2nodeSubProcess, id2lane, subProcess);
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
			inclusiveGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			intermediateEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, id2nodeSubProcess);
		}
		id2node.putAll(id2nodeSubProcess);
		for (BpmnTask task : tasks) {
			task.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, id2node);
		}
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, id2node);
		}
		if(laneSet != null) {
			laneSet.unmarshall(diagram, id2node, id2lane, subProcess);
		}
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane,
			SubProcess parentSubProcess) {
		SubProcess subProcess;
		boolean triggerByEvent = false;
		if ((triggeredByEvent != null) && (triggeredByEvent.equals("true"))) {
			triggerByEvent = true;
		}
		if (multiInstanceLoopCharacteristics != null) {
			subProcess = diagram.addSubProcess(name, false, false, false, true, true, triggerByEvent,
					parentSubProcess);
			id2node.put(id, subProcess);
		} else if(standardLoopCharacteristics != null) {
			subProcess = diagram.addSubProcess(name, true, false, false, false, true, triggerByEvent,
					parentSubProcess);
			id2node.put(id, subProcess);
		} else {
			subProcess = diagram.addSubProcess(name, false, false, false, false,
					false, triggerByEvent, parentSubProcess);
			id2node.put(id, subProcess);
		}
		if(ioSpecification != null) {
			Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
			for(BpmnId dataIncoming : dataIncomings) {
				id2node.put(dataIncoming.getId(), subProcess);
			}
			Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
			for(BpmnId dataOutgoing : dataOutgoins) {
				id2node.put(dataOutgoing.getId(), subProcess);
			}
		}

		Map<String, BPMNNode> id2nodeSubProcess = new HashMap<String, BPMNNode>();

		for (BpmnStartEvent startEvent : startEvents) {
			startEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnTask task : tasks) {
			task.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
        for (BpmnCallActivity callActivity : callActivities){
        	callActivity.unmarshall(diagram, id2nodeSubProcess, subProcess);
        }
        for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways){
        	eventBasedGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
        }
		for (BpmnSubProcess childSubProcess : subProcesses) {
			childSubProcess.unmarshall(diagram, id2nodeSubProcess, id2lane, subProcess);
		}
		for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
			exclusiveGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnParallelGateway parallelGateway : parallelGateways) {
			parallelGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
			inclusiveGateway.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnEndEvent endEvent : endEvents) {
			endEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
			intermediateEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
		}
		for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
			sequenceFlow.unmarshall(diagram, id2nodeSubProcess);
		}
	
		id2node.putAll(id2nodeSubProcess);
		for (BpmnTask task : tasks) {
			task.unmarshallDataAssociations(diagram, id2node);
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, id2node);
		}
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, id2node);
		}
		if(laneSet != null) {
			laneSet.unmarshall(diagram, id2node, id2lane, subProcess);
		}
	}
	
	public void unmarshall(BPMNDiagram diagram, Collection<String> elements,
			Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane, SubProcess parentSubProcess) {
		SubProcess subProcess = null;
		boolean triggerByEvent = false;
		if ((triggeredByEvent != null) && (triggeredByEvent.equals("true"))) {
			triggerByEvent = true;
		}

		if (elements.contains(id)) {
			if (multiInstanceLoopCharacteristics != null) {
				subProcess = diagram.addSubProcess(name, false, false, false, true, true, triggerByEvent, parentSubProcess);
				subProcess.getAttributeMap().put("Original id", id);
				id2node.put(id, subProcess);
			} else {
				subProcess = diagram.addSubProcess(name, false, false, false, false, false, triggerByEvent, parentSubProcess);
				subProcess.getAttributeMap().put("Original id", id);
				id2node.put(id, subProcess);
			}
			
			if(ioSpecification != null) {
				Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
				for(BpmnId dataIncoming : dataIncomings) {
					id2node.put(dataIncoming.getId(), subProcess);
				}
				Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
				for(BpmnId dataOutgoing : dataOutgoins) {
					id2node.put(dataOutgoing.getId(), subProcess);
				}
			}
			Map<String, BPMNNode> id2nodeSubProcess = new HashMap<String, BPMNNode>();

			for (BpmnStartEvent startEvent : startEvents) {
				System.out.println(startEvent.id);
				startEvent.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnTask task : tasks) {
				task.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnSubProcess childSubProcess : subProcesses) {
				childSubProcess.unmarshall(diagram, elements, id2nodeSubProcess, id2lane, subProcess);
			}
            for (BpmnCallActivity callActivity : callActivities) {
                callActivity.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
            }
            for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways){
                eventBasedGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
            }
			for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
				exclusiveGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnParallelGateway parallelGateway : parallelGateways) {
				parallelGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
				inclusiveGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnEndEvent endEvent : endEvents) {
				endEvent.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
				intermediateEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
			}
			for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
				sequenceFlow.unmarshall(diagram, elements, id2nodeSubProcess);
			}
			id2node.putAll(id2nodeSubProcess);
			for (BpmnTask task : tasks) {
				task.unmarshallDataAssociations(diagram, id2node);
			}
            for (BpmnCallActivity callActivity : callActivities) {
                callActivity.unmarshallDataAssociations(diagram, id2node);
            }
			for (BpmnTextAnnotation textAnnotation : textAnnotations) {
				textAnnotation.unmarshall(diagram, elements, id2node);
			}
			for (BpmnAssociation association : associations) {
				association.unmarshall(diagram, elements, id2node);
			}
			if(laneSet != null) {
				laneSet.unmarshall(diagram, id2node,  id2lane, subProcess);
			}
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements,
			Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane, Swimlane lane) {
		SubProcess subProcess = null;
		boolean triggerByEvent = false;
		
		if ((triggeredByEvent != null) && (triggeredByEvent.equals("true"))) {
			triggerByEvent = true;
		}

		if (elements.contains(id)) {
			if (multiInstanceLoopCharacteristics != null) {
				subProcess = diagram.addSubProcess(name, false, false, false, true, true, triggerByEvent, lane);
				subProcess.getAttributeMap().put("Original id", id);
				id2node.put(id, subProcess);
			} else {
				subProcess = diagram.addSubProcess(name, false, false, false, false, false, triggerByEvent, lane);
				subProcess.getAttributeMap().put("Original id", id);
				id2node.put(id, subProcess);
			}
			
			if(ioSpecification != null) {
				Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
				for(BpmnId dataIncoming : dataIncomings) {
					id2node.put(dataIncoming.getId(), subProcess);
				}
				Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
				for(BpmnId dataOutgoing : dataOutgoins) {
					id2node.put(dataOutgoing.getId(), subProcess);
				}
			}
			Map<String, BPMNNode> id2nodeSubProcess = new HashMap<String, BPMNNode>();

			for (BpmnStartEvent startEvent : startEvents) {
				startEvent.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnTask task : tasks) {
				task.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnSubProcess childSubProcess : subProcesses) {
				childSubProcess.unmarshall(diagram, elements, id2nodeSubProcess, id2lane, subProcess);
			}
            for (BpmnCallActivity callActivity : callActivities) {
                callActivity.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
            }
            for (BpmnEventBasedGateway eventBasedGateway : eventBasedGateways){
                eventBasedGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
            }
			for (BpmnExclusiveGateway exclusiveGateway : exclusiveGateways) {
				exclusiveGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnParallelGateway parallelGateway : parallelGateways) {
				parallelGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnInclusiveGateway inclusiveGateway : inclusiveGateways) {
				inclusiveGateway.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnEndEvent endEvent : endEvents) {
				endEvent.unmarshall(diagram, elements, id2nodeSubProcess, subProcess);
			}
			for (BpmnIntermediateEvent intermediateEvent : intermediateEvents) {
				intermediateEvent.unmarshall(diagram, id2nodeSubProcess, subProcess);
			}
			for (BpmnSequenceFlow sequenceFlow : sequenceFlows) {
				sequenceFlow.unmarshall(diagram, elements, id2nodeSubProcess);
			}
			id2node.putAll(id2nodeSubProcess);
			for (BpmnTask task : tasks) {
				task.unmarshallDataAssociations(diagram, id2node);
			}
            for (BpmnCallActivity callActivity : callActivities) {
                callActivity.unmarshallDataAssociations(diagram, id2node);
            }
			for (BpmnTextAnnotation textAnnotation : textAnnotations) {
				textAnnotation.unmarshall(diagram, elements, id2node);
			}
			for (BpmnAssociation association : associations) {
				association.unmarshall(diagram, elements, id2node);
			}
			if(laneSet != null) {
				laneSet.unmarshall(diagram, id2node,  id2lane, subProcess);
			}
		}
	}
	
	public void unmarshallDataAssociations(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		for (BpmnDataAssociation inputAssociation : inputAssociations) {
			inputAssociation.unmarshall(diagram, id2node);
		}

		for (BpmnDataAssociation outputAssociation : outputAssociations) {
			outputAssociation.unmarshall(diagram, id2node);
		}
	}

	public void marshall(SubProcess subProcess, BPMNDiagram diagram) {
		super.marshall(subProcess);
		triggeredByEvent = new Boolean(subProcess.getTriggeredByEvent()).toString();
		if(subProcess.isBMultiinstance()) {
			multiInstanceLoopCharacteristics = 
					new BpmnMultiInstanceLoopCharacteristics("multiInstanceLoopCharacteristics");
		}
		if(subProcess.isBLooped()) {
			standardLoopCharacteristics = 
					new BpmnStandardLoopCharacteristics("standardLoopCharacteristics");
		}
		Set<ContainableDirectedGraphElement> allChildren = new HashSet<ContainableDirectedGraphElement>();
		allChildren.addAll(subProcess.getChildren());
		for(ContainableDirectedGraphElement child : subProcess.getChildren()) {
			if(child instanceof Swimlane) {
				for(ContainableDirectedGraphElement innerChild : ((Swimlane)child).getChildren()) {
					allChildren.add(innerChild);
				}
			}
		}
		for (ContainableDirectedGraphElement child : allChildren) {
			// Marshall child event
			if (child instanceof Event) {
				if (((Event) child).getEventType() == EventType.START) {
					BpmnStartEvent startEvent = new BpmnStartEvent("startEvent");
					startEvent.marshall((Event) child);
					startEvents.add(startEvent);
				} else if (((Event) child).getEventType() == EventType.END) {
					BpmnEndEvent endEvent = new BpmnEndEvent("endEvent");
					endEvent.marshall((Event) child);
					endEvents.add(endEvent);
				}  else if(((Event) child).getEventType() == EventType.INTERMEDIATE) {
					BpmnIntermediateEvent intermediateEvent = null;
					if(((Event) child).getEventUse() == EventUse.CATCH) {
						if(((Event) child).getBoundingNode() != null) {
							intermediateEvent = new BpmnIntermediateEvent("boundaryEvent",
									EventUse.CATCH);
						} else {
						intermediateEvent = new BpmnIntermediateEvent("intermediateCatchEvent",
								EventUse.CATCH);
						}
					} else {
						intermediateEvent = new BpmnIntermediateEvent("intermediateThrowEvent",
								EventUse.THROW);
					}
					intermediateEvent.marshall(((Event) child));
					intermediateEvents.add(intermediateEvent);
				}
			}
			
			// Marshall child task
			if ((child instanceof Activity) && !(child instanceof SubProcess)) {
				BpmnTask task = new BpmnTask("task");
				task.marshall((Activity) child);
				tasks.add(task);
			}

            // Marshall child callActivity
            if ((child instanceof CallActivity) && !(child instanceof SubProcess)) {
                BpmnCallActivity callActivity = new BpmnCallActivity("callActivity");
                callActivity.marshall((CallActivity) child);
                callActivities.add(callActivity);
            }
			
			// Marshall child subProcess
			if (child instanceof SubProcess) {
				BpmnSubProcess childSubProcess = new BpmnSubProcess("subProcess");
				childSubProcess.marshall((SubProcess) child, diagram);
				subProcesses.add(childSubProcess);
			}
			
			// Marshall child lanes
			if (child instanceof Swimlane) {
				if(diagram.getLanes(subProcess).size() > 0) {
					laneSet = new BpmnLaneSet("laneSet");
					laneSet.marshall(diagram, subProcess);
				}
			}
			
			// Marshall child gateway
			if (child instanceof Gateway) {
				if (((Gateway) child).getGatewayType() == GatewayType.DATABASED) {
					BpmnExclusiveGateway exclusiveGateway = new BpmnExclusiveGateway("exclusiveGateway");
					exclusiveGateway.marshall((Gateway) child);
					exclusiveGateways.add(exclusiveGateway);

				} else if (((Gateway) child).getGatewayType() == GatewayType.PARALLEL) {
					BpmnParallelGateway parallelGateway = new BpmnParallelGateway("parallelGateway");
					parallelGateway.marshall((Gateway) child);
					parallelGateways.add(parallelGateway);
				} else if (((Gateway) child).getGatewayType() == GatewayType.INCLUSIVE) {
					BpmnInclusiveGateway inclusiveGateway = new BpmnInclusiveGateway("inclusiveGateway");
					inclusiveGateway.marshall((Gateway) child);
					inclusiveGateways.add(inclusiveGateway);
				} else if (((Gateway) child).getGatewayType() == GatewayType.EVENTBASED) {
                    BpmnEventBasedGateway eventBasedGateway = new BpmnEventBasedGateway("eventBasedGateway");
                    eventBasedGateway.marshall(diagram, ((Gateway) child));
                    eventBasedGateways.add(eventBasedGateway);
				}
			}
			// Marshall artifacts
			if (child instanceof TextAnnotation) {
				BpmnTextAnnotation bpmnTextAnnotation = new BpmnTextAnnotation("textAnnotation");
				bpmnTextAnnotation.marshall((TextAnnotation) child);
				textAnnotations.add(bpmnTextAnnotation);
			}
			if(child instanceof Association) {
				BpmnAssociation bpmnAssociation = new BpmnAssociation("association");
				bpmnAssociation.marshall((Association) child);
				associations.add(bpmnAssociation);
			}
		}
		
		// Marshall child control flow
		for(Flow flow : diagram.getFlows(subProcess)) {
			BpmnSequenceFlow sequenceFlow = new BpmnSequenceFlow("sequenceFlow");
			sequenceFlow.marshall(flow);
			sequenceFlows.add(sequenceFlow);
		}
		
		// Marshall DataAssociations
		
		for (DataAssociation inputDataAssociation : retriveIncomingDataAssociations(subProcess, diagram)) {
			if (ioSpecification == null) {
				ioSpecification = new BpmnInputOutputSpecification("ioSpecification");
				ioSpecification.setId("io_" + this.getId());
			}
			BpmnDataAssociation bpmnDataAssociation = new BpmnDataAssociation("dataInputAssociation");
			BpmnId dataInput = new BpmnId("dataInput");
			dataInput.setId("input_" + inputDataAssociation.getEdgeID().toString().replace(' ', '_'));
			ioSpecification.addDataInput(dataInput);
			bpmnDataAssociation.setId(inputDataAssociation.getEdgeID().toString().replace(' ', '_'));
			bpmnDataAssociation.setSourceRef(inputDataAssociation.getSource().getId().toString().replace(' ', '_'));
			bpmnDataAssociation.setTargetRef(dataInput.getId());
			inputAssociations.add(bpmnDataAssociation);
		}

		for (DataAssociation outputDataAssociation : retriveOutgoingDataAssociations(subProcess, diagram)) {
			if (ioSpecification == null) {
				ioSpecification = new BpmnInputOutputSpecification("ioSpecification");
				ioSpecification.setId("io_" + this.getId());
			}
			BpmnDataAssociation bpmnDataAssociation = new BpmnDataAssociation("dataOutputAssociation");
			BpmnId dataOutput = new BpmnId("dataOutput");
			dataOutput.setId("output_" + outputDataAssociation.getEdgeID().toString().replace(' ', '_'));
			ioSpecification.addDataOutput(dataOutput);
			bpmnDataAssociation.setId(outputDataAssociation.getEdgeID().toString().replace(' ', '_'));
			bpmnDataAssociation.setSourceRef(dataOutput.getId());
			bpmnDataAssociation.setTargetRef(outputDataAssociation.getTarget().getId().toString().replace(' ', '_'));
			outputAssociations.add(bpmnDataAssociation);
		}
	}
	
	private Collection<DataAssociation> retriveIncomingDataAssociations(Activity activity, BPMNDiagram diagram) {
		Collection<DataAssociation> incomingDataAssociations = new HashSet<DataAssociation>();
		for(DataAssociation dataAssociation : diagram.getDataAssociations()) {	
			if(activity.equals(dataAssociation.getTarget())) {
				incomingDataAssociations.add(dataAssociation);
			}
		}
		return incomingDataAssociations;
	}
	
	private Collection<DataAssociation> retriveOutgoingDataAssociations(Activity activity, BPMNDiagram diagram) {
		Collection<DataAssociation> outgoingDataAssociations = new HashSet<DataAssociation>();
		for(DataAssociation dataAssociation : diagram.getDataAssociations()) {	
			if(activity.equals(dataAssociation.getSource())) {
				outgoingDataAssociations.add(dataAssociation);
			}
		}
		return outgoingDataAssociations;
	}
}
