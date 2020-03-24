package org.apromore.processmining.plugins.bpmn.diagram;

import java.util.Collection;
import java.util.HashSet;

import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnElement;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiPlane extends BpmnElement {

	protected Collection<BpmnDiShape> shapes;
	protected Collection<BpmnDiEdge> edges;
	
	private String bpmnElement;
	
	public BpmnDiPlane(String tag) {
		super(tag);
		
		shapes = new HashSet<BpmnDiShape>();
		edges = new HashSet<BpmnDiEdge>();
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
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("BPMNShape")) {
			BpmnDiShape shape = new BpmnDiShape("BPMNShape");
			shape.importElement(xpp, bpmn);
			shapes.add(shape);
			return true;
		} else if (xpp.getName().equals("BPMNEdge")) {
			BpmnDiEdge edge = new BpmnDiEdge("BPMNEdge");
			edge.importElement(xpp, bpmn);
			edges.add(edge);
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
		for (BpmnDiShape shape : shapes) {
			s += shape.exportElement();
		}
		for (BpmnDiEdge edge : edges) {
			s += edge.exportElement();
		}
		return s;
	}
	
	public Collection<String> getElements() {
		Collection<String> elements = new HashSet<String>();
		for (BpmnDiShape shape : shapes) {
			shape.addElement(elements);
		}
		for (BpmnDiEdge edge : edges) {
			edge.addElement(elements);
		}
		return elements;
	}
	
	public void addShape(BpmnDiShape shape) {
		shapes.add(shape);
	}
	
	public void addEdge(BpmnDiEdge edge) {
		edges.add(edge);
	}
	
	public void setBpmnElement(String bpmnElement) {
		this.bpmnElement = bpmnElement;
	}
	
	public Collection<BpmnDiShape> getShapes() {
		return shapes;
	}
}
