package org.apromore.processmining.plugins.bpmn.diagram;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.plugins.bpmn.Bpmn;
import org.apromore.processmining.plugins.bpmn.BpmnIdName;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDiagram extends BpmnIdName {

	protected Collection<BpmnDiPlane> planes;
	
	public BpmnDiagram(String tag) {
		super(tag);
		
		planes = new HashSet<BpmnDiPlane>();
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("BPMNPlane")) {
			BpmnDiPlane plane = new BpmnDiPlane("BPMNPlane");
			plane.importElement(xpp, bpmn);
			planes.add(plane);
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
		for (BpmnDiPlane plane : planes) {
			s += plane.exportElement();
		}
		return s;
	}
	
	public Collection<String> getElements() {
		Collection<String> elements = new HashSet<String>();
		for (BpmnDiPlane plane : planes) {
			elements.addAll(plane.getElements());
		}
		return elements;
	}
	
	public String toString() {
		return name != null && !name.isEmpty() ? name : "No name";
	}
	
	public void addPlane(BpmnDiPlane plane) {
		planes.add(plane);
	}
	
	public void unmarshallIsExpanded(Map<String, BPMNNode> id2node) {
		for (BpmnDiPlane plane : planes) {
			Collection<BpmnDiShape> shapes = plane.getShapes();
			for (BpmnDiShape shape : shapes) {
				String bpmnElement = shape.getBpmnElement();
				Object o = id2node.get(bpmnElement);
				if (o instanceof SubProcess) {
					SubProcess subProcess = (SubProcess)o;
					subProcess.setBCollapsed(!shape.isExpanded());
				}
			}
		}
	}
}
