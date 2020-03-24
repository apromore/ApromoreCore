package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnCollaboration extends BpmnId {

	private Collection<BpmnParticipant> participants;
	private Collection<BpmnMessageFlow> messageFlows;
	private Collection<BpmnTextAnnotation> textAnnotations;
	private Collection<BpmnAssociation> associations;
	
	public BpmnCollaboration(String tag) {
		super(tag);
		
		participants = new HashSet<BpmnParticipant>();
		messageFlows = new HashSet<BpmnMessageFlow>();
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
		if (xpp.getName().equals("participant")) {
			BpmnParticipant participant = new BpmnParticipant("participant");
			participant.importElement(xpp, bpmn);
			participants.add(participant);
			return true;
		} else if (xpp.getName().equals("messageFlow")) {
			BpmnMessageFlow messageFlow = new BpmnMessageFlow("messageFlow");
			messageFlow.importElement(xpp, bpmn);
			messageFlows.add(messageFlow);
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
		for (BpmnParticipant participant : participants) {
			s += participant.exportElement();
		}
		for (BpmnMessageFlow messageFlow : messageFlows) {
			s += messageFlow.exportElement();
		}
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			s += textAnnotation.exportElement();
		}
		for (BpmnAssociation association : associations) {
			s += association.exportElement();
		}
		return s;
	}

	public void unmarshallParticipants(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnParticipant participant : participants) {
			participant.unmarshall(diagram, id2node, id2lane);
		}
	}

	public void unmarshallParticipants(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnParticipant participant : participants) {
			participant.unmarshall(diagram, elements, id2node, id2lane);
		}
	}

	public void unmarshallMessageFlows(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		for (BpmnMessageFlow messageFlow : messageFlows) {
			messageFlow.unmarshall(diagram, id2node);
		}
	}

	public void unmarshallMessageFlows(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		for (BpmnMessageFlow messageFlow : messageFlows) {
			messageFlow.unmarshall(diagram, elements, id2node);
		}
	}
	
	public void unmarshallTextAnnotations(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, id2node);
		}
	}

	public void unmarshallTextAnnotations(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		for (BpmnTextAnnotation textAnnotation : textAnnotations) {
			textAnnotation.unmarshall(diagram, elements, id2node);
		}
	}
	
	public void unmarshallAssociations(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, id2node);
		}
	}

	public void unmarshallAssociations(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		for (BpmnAssociation association : associations) {
			association.unmarshall(diagram, elements, id2node);
		}
	}
	
	public void addParticipant(BpmnParticipant participant) {
		participants.add(participant);
	}
	
	public void addMessageFlow(BpmnMessageFlow messageFlow) {
		messageFlows.add(messageFlow);
	}
	
	public void addTextAnnotation(BpmnTextAnnotation textAnnotation) {
		textAnnotations.add(textAnnotation);
	}
	
	public void addAssociation(BpmnAssociation association) {
		associations.add(association);
	}
}
