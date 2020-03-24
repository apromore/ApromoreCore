package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataAssociation;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnTask extends BpmnIncomingOutgoing {

	private String startQuantity;
	private String completionQuantity;
	private String isForCompensation;
	private BpmnInputOutputSpecification ioSpecification;
	private Collection<BpmnDataAssociation> inputAssociations;
	private Collection<BpmnDataAssociation> outputAssociations;
	
	private BpmnMultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;
	private BpmnStandardLoopCharacteristics standardLoopCharacteristics;
	
	public BpmnTask(String tag) {
		super(tag);
		
		startQuantity = null;
		completionQuantity = null;
		isForCompensation = null;
		ioSpecification = null;
		
		inputAssociations = new HashSet<BpmnDataAssociation>();
		outputAssociations = new HashSet<BpmnDataAssociation>();
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "startQuantity");
		if (value != null) {
			startQuantity = value;
		}
		value = xpp.getAttributeValue(null, "completionQuantity");
		if (value != null) {
			completionQuantity = value;
		}
		value = xpp.getAttributeValue(null, "isForCompensation");
		if (value != null) {
			isForCompensation = value;
		}
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
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
		if (xpp.getName().equals("ioSpecification")) {
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
		}
		return false;
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (startQuantity != null) {
			s += exportAttribute("startQuantity", startQuantity);
		}
		if (completionQuantity != null) {
			s += exportAttribute("completionQuantity", completionQuantity);
		}
		if (isForCompensation != null) {
			s += exportAttribute("isForCompensation", isForCompensation);
		}
		return s;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if (multiInstanceLoopCharacteristics != null) {
			s += multiInstanceLoopCharacteristics.exportElement();
		}
		if (standardLoopCharacteristics != null) {
			s += standardLoopCharacteristics.exportElement();
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
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane) {
		Activity activity;
		if (multiInstanceLoopCharacteristics != null) {
			activity = diagram.addActivity(name, false, false, false, true, false, lane);
		} else if(standardLoopCharacteristics != null) {
			activity = diagram.addActivity(name, true, false, false, false, false, lane);
		} else {
			activity = diagram.addActivity(name, false, false, false, false, false, lane);
			if(this.getTag()=="sendTask"){
				activity.setBSend(true);
			}else if(this.getTag()=="receiveTask"){
				activity.setBReceive(true);
			}else if(this.getTag()=="serviceTask"){
				activity.setBService(true);
			}else if(this.getTag()=="scriptTask"){
				// HV: Set to script to have decorator added
				activity.setBScript(true);
			}
		}
		
		if(ioSpecification != null) {
			Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
			for(BpmnId dataIncoming : dataIncomings) {
				id2node.put(dataIncoming.getId(), activity);
			}
			Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
			for(BpmnId dataOutgoing : dataOutgoins) {
				id2node.put(dataOutgoing.getId(), activity);
			}
		}
		id2node.put(id, activity);
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, SubProcess subProcess) {
		Activity activity;
		if (multiInstanceLoopCharacteristics != null) {
			activity = diagram.addActivity(name, false, false, false, true, false, subProcess);
		} else if(standardLoopCharacteristics != null) {
			activity = diagram.addActivity(name, true, false, false, false, false, subProcess);
		} else {
			activity = diagram.addActivity(name, false, false, false, false, false, subProcess);
			if(this.getTag()=="sendTask"){
				activity.setBSend(true);
			}else if(this.getTag()=="receiveTask"){
				activity.setBReceive(true);
			}else if(this.getTag()=="serviceTask"){
				activity.setBService(true);
			}else if(this.getTag()=="scriptTask"){
				// HV: Set to script to have decorator added
				activity.setBScript(true);
			}
		}
		
		if(ioSpecification != null) {
			Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
			for(BpmnId dataIncoming : dataIncomings) {
				id2node.put(dataIncoming.getId(), activity);
			}
			Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
			for(BpmnId dataOutgoing : dataOutgoins) {
				id2node.put(dataOutgoing.getId(), activity);
			}
		}
		id2node.put(id, activity);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane) {
		if (elements.contains(id)) {
			Activity activity;
			if (multiInstanceLoopCharacteristics != null) {
				activity = diagram.addActivity(name, false, false, false, true, false, lane);
			} else if(standardLoopCharacteristics != null) {
				activity = diagram.addActivity(name, true, false, false, false, false, lane);
			} else {
				activity = diagram.addActivity(name, false, false, false, false, false, lane);
				if(this.getTag()=="sendTask"){
					activity.setBSend(true);
				}else if(this.getTag()=="receiveTask"){
					activity.setBReceive(true);
				}else if(this.getTag()=="serviceTask"){
					activity.setBService(true);
				}else if(this.getTag()=="scriptTask"){
					// HV: Set to script to have decorator added
					activity.setBScript(true);
				}
			}
			
			if(ioSpecification != null) {
				Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
				for(BpmnId dataIncoming : dataIncomings) {
					id2node.put(dataIncoming.getId(), activity);
				}
				Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
				for(BpmnId dataOutgoing : dataOutgoins) {
					id2node.put(dataOutgoing.getId(), activity);
				}
			}
			activity.getAttributeMap().put("Original id", id);
			id2node.put(id, activity);
		}
	}
	
	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, SubProcess subProcess) {
		if (elements.contains(id)) {
			Activity activity;
			if (multiInstanceLoopCharacteristics != null) {
				activity = diagram.addActivity(name, false, false, false, true, false, subProcess);
			} else if(standardLoopCharacteristics != null) {
				activity = diagram.addActivity(name, true, false, false, false, false, subProcess);
			} else {
				activity = diagram.addActivity(name, false, false, false, false, false, subProcess);
				if(this.getTag()=="sendTask"){
					activity.setBSend(true);
				}else if(this.getTag()=="receiveTask"){
					activity.setBReceive(true);
				}else if(this.getTag()=="serviceTask"){
					activity.setBService(true);
				}else if(this.getTag()=="scriptTask"){
					// HV: Set to script to have decorator added
					activity.setBScript(true);
				}
			}
			
			if(ioSpecification != null) {
				Collection<BpmnId> dataIncomings = ioSpecification.getDataInputs();
				for(BpmnId dataIncoming : dataIncomings) {
					id2node.put(dataIncoming.getId(), activity);
				}
				Collection<BpmnId> dataOutgoins = ioSpecification.getDataOutputs();
				for(BpmnId dataOutgoing : dataOutgoins) {
					id2node.put(dataOutgoing.getId(), activity);
				}
			}
			activity.getAttributeMap().put("Original id", id);
			id2node.put(id, activity);
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
	
	public void marshall(Activity activity, BPMNDiagram diagram) {
		super.marshall(activity);
		if(activity.isBMultiinstance()) {
			multiInstanceLoopCharacteristics = 
					new BpmnMultiInstanceLoopCharacteristics("multiInstanceLoopCharacteristics");
		}
		if(activity.isBLooped()) {
			standardLoopCharacteristics = 
					new BpmnStandardLoopCharacteristics("standardLoopCharacteristics");
		}
		for (DataAssociation inputDataAssociation : retriveIncomingDataAssociations(activity, diagram)) {
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
            bpmnDataAssociation.marshall(inputDataAssociation);
			inputAssociations.add(bpmnDataAssociation);
		}

		for (DataAssociation outputDataAssociation : retriveOutgoingDataAssociations(activity, diagram)) {
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
            bpmnDataAssociation.marshall(outputDataAssociation);
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
