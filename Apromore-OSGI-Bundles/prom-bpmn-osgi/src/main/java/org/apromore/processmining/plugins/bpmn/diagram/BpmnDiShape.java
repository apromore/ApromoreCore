package org.apromore.processmining.plugins.bpmn.diagram;

import java.util.Collection;

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.apromore.processmining.plugins.bpmn.BpmnText;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiShape extends BpmnElement {

	private String bpmnElement;
	private BpmnDcBounds bounds;
	private boolean isExpanded;
	private boolean isHorizontal;
	
	public BpmnDiShape(String tag) {
		super(tag);			
	}
	
	public BpmnDiShape(String tag, String bpmnElement, BpmnDcBounds bounds, boolean isExpanded, boolean isHorizontal) {
		super(tag);		
		this.bpmnElement = bpmnElement;
		this.bounds = bounds;
		this.isExpanded = isExpanded;
		this.isHorizontal = isHorizontal;
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "bpmnElement");
		if (value != null) {
			bpmnElement = value;
		}
		value = xpp.getAttributeValue(null, "isExpanded");
		if (value != null) {
			isExpanded = new Boolean(value);
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
		if(isExpanded) {
			s += exportAttribute("isExpanded", new Boolean(isExpanded).toString());
		}
		if(isHorizontal) {
			s += exportAttribute("isHorizontal", new Boolean(isHorizontal).toString());
		}
		return s;
	}
	
	/**
	 * Exports all elements.
	 */
	protected String exportElements() {
		String s = "";
		if (bounds != null) {
			s += bounds.exportElement();
		}
		BpmnText bpmnLabel = new BpmnText("bpmndi:BPMNLabel");
		s+=bpmnLabel.exportElement();
		return s;
	}

	public void addElement(Collection<String> elements) {
		elements.add(bpmnElement);
	}
	
	public String getBpmnElement() {
		return bpmnElement;
	}
	
	public boolean isExpanded() {
		return isExpanded;
	}
}