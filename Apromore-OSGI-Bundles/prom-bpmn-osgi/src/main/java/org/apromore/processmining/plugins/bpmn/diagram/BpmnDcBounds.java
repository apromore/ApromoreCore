package org.apromore.processmining.plugins.bpmn.diagram;

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDcBounds extends BpmnElement {
	
	private double x;
	private double y;
	private double width;
	private double height;	
	
	public BpmnDcBounds(String tag, double x, double y, double width, double height) {
		super(tag);
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
		
		String width = xpp.getAttributeValue(null, "width");
		if (width != null) {
			this.width = Double.parseDouble(width);
		}
		
		String height = xpp.getAttributeValue(null, "height");
		if (height != null) {
			this.height = Double.parseDouble(height);
		}
		
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		s += exportAttribute("x", new Double(x).toString());
		s += exportAttribute("y", new Double(y).toString());
		s += exportAttribute("width", new Double(width).toString());
		s += exportAttribute("height", new Double(height).toString());

		return s;
	}
}
