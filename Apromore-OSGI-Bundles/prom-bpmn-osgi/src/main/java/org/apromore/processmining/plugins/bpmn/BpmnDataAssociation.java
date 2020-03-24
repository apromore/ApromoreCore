package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataAssociation;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDataAssociation extends BpmnIdName {
	
	private String sourceRef;
	private String targetRef;
	
	public BpmnDataAssociation(String tag) {
		super(tag);
	}
	
	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}
	
	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("sourceRef")) {
			BpmnText sourceRefElement = new BpmnText("sourceRef");
			sourceRefElement.importElement(xpp, bpmn);
			sourceRef = sourceRefElement.getText();
			return true;
		} else if(xpp.getName().equals("targetRef")) {
			BpmnText targetRefElement = new BpmnText("targetRef");
			targetRefElement.importElement(xpp, bpmn);
			targetRef = targetRefElement.getText();
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
		if(sourceRef != null) {
			BpmnText sourceRefElement = new BpmnText("sourceRef");
			sourceRefElement.setText(sourceRef);
			s += sourceRefElement.exportElement();
		}
		if(targetRef != null) {
			BpmnText targetRefElement = new BpmnText("targetRef");
			targetRefElement.setText(targetRef);
			s += targetRefElement.exportElement();
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		DataAssociation flow = diagram.addDataAssociation(id2node.get(sourceRef), id2node.get(targetRef), name);
		flow.getAttributeMap().put("Original id", id);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(sourceRef) && elements.contains(targetRef)) {
			DataAssociation flow = diagram.addDataAssociation(id2node.get(sourceRef), id2node.get(targetRef), name);
			flow.getAttributeMap().put("Original id", id);
		}
	}	
}
