package org.apromore.processmining.plugins.bpmn;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.xmlpull.v1.XmlPullParser;

public class BpmnIdName extends BpmnId {

	public String name;
	
	public BpmnIdName(String tag) {
		super(tag);
		
		name = null;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "name");
		if (value != null) {
			name = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (name != null) {
			s += exportAttribute("name", name);
		}
		return s;
	}
	
	protected void marshall(BPMNNode node) {
		super.marshall(node);
		name = node.getLabel();
	}
	
	protected void marshall(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		name = edge.getLabel();
		id = edge.getEdgeID().toString().replace(' ', '_');
	}
}
