package org.apromore.processmining.plugins.bpmn;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.xmlpull.v1.XmlPullParser;

public class BpmnFlow extends BpmnIdName {

	protected String sourceRef;
	protected String targetRef;

	public BpmnFlow(String tag) {
		super(tag);
		
		sourceRef = null;
		targetRef = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "sourceRef");
		if (value != null) {
			sourceRef = value;
		}
		value = xpp.getAttributeValue(null, "targetRef");
		if (value != null) {
			targetRef = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (sourceRef != null) {
			s += exportAttribute("sourceRef", sourceRef);
		}
		if (targetRef != null) {
			s += exportAttribute("targetRef", targetRef);
		}
		return s;
	}
	
	public void marshall(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		super.marshall(edge);
		sourceRef = edge.getSource().getId().toString().replace(' ', '_');
		targetRef = edge.getTarget().getId().toString().replace(' ', '_');
	}
}
