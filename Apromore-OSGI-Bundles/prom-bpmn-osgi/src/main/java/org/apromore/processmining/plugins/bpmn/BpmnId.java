package org.apromore.processmining.plugins.bpmn;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.xmlpull.v1.XmlPullParser;

public class BpmnId extends BpmnElement {
	/*
	 * Attributes
	 */
	public String id;

	public BpmnId(String tag) {
		super(tag);

		id = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "id");
		if (value != null) {
			id = value;
		}
	}
	
	protected void marshall(BPMNNode graphNode) {
		if(graphNode.getId() != null) {
			id = graphNode.getId().toString().replace(' ', '_');
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (id != null) {
			s += exportAttribute("id", id);
		}
		return s;
	}

	protected void checkValidity(Bpmn bpmn) {
		checkRequired(bpmn, "id", id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
