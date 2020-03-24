package org.apromore.processmining.plugins.bpmn.diagram;

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiWaypoint extends BpmnElement {
	
	private double x;
	private double y;
	
	public BpmnDiWaypoint(String tag, double x, double y) {
		super(tag);

		this.x = x;
		this.y = y;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		
		String x = xpp.getAttributeValue(null, "x");
		if (x != null) {
			this.x = Double.parseDouble(x);
		}
		
		String y = xpp.getAttributeValue(null, "y");
		if (y != null) {
			this.y = Double.parseDouble(y);
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		s += exportAttribute("x", new Double(x).toString());
		s += exportAttribute("y", new Double(y).toString());
		return s;
	}
}
