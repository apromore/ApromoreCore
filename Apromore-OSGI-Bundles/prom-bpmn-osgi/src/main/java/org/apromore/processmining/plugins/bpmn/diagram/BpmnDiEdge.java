package org.apromore.processmining.plugins.bpmn.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiEdge extends BpmnElement {

	private String bpmnElement;
	private List<BpmnDiWaypoint> waypoints = new ArrayList<BpmnDiWaypoint>();
	
	public BpmnDiEdge(String tag) {
		super(tag);
	}
	
	public BpmnDiEdge(String tag, String bpmnElement) {
		super(tag);		
		this.bpmnElement = bpmnElement;	
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "bpmnElement");
		if (value != null) {
			bpmnElement = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (bpmnElement != null) {
			s += exportAttribute("bpmnElement", bpmnElement);
		}
		return s;
	}
	
	/**
	 * Exports all elements.
	 */
	protected String exportElements() {
		String s = "";
		for(BpmnDiWaypoint waypoint : waypoints) {
			s += waypoint.exportElement();
		}
		return s;
	}
	
	public void addWaypoint(BpmnDiWaypoint waypoint) {
		waypoints.add(waypoint);
	}

	public void addElement(Collection<String> elements) {
		elements.add(bpmnElement);
	}
}
