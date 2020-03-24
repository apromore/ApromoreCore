package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.xmlpull.v1.XmlPullParser;

public class BpmnParticipant extends BpmnIdName {

	private String processRef;
	
	public BpmnParticipant(String tag) {
		super(tag);
		
		processRef = null;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "processRef");
		if (value != null) {
			processRef = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (processRef != null) {
			s += exportAttribute("processRef", processRef);
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		if (name != null) {
			Swimlane lane = diagram.addSwimlane(name, null, SwimlaneType.POOL);
			lane.getAttributeMap().put("Original id", id);
            id2node.put(id, lane);
			id2lane.put(processRef, lane);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		if (name != null) {
			Swimlane lane = diagram.addSwimlane(name, null, SwimlaneType.POOL);
			lane.getAttributeMap().put("Original id", id);
            id2node.put(id, lane);
			id2lane.put(processRef, lane);
		}
	}
	
	public void setProcessRef(String processRef) {
		this.processRef = processRef;
	}
}
