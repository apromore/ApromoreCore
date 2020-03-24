package org.apromore.processmining.plugins.bpmn.parameters;

import org.apromore.processmining.plugins.bpmn.diagram.BpmnDiagram;

public class BpmnSelectDiagramParameters {

	final public static BpmnDiagram NODIAGRAM = new BpmnDiagram("No name");

	private BpmnDiagram diagram = NODIAGRAM;
	
	public void setDiagram(BpmnDiagram diagram) {
		this.diagram = diagram;
	}
	
	public BpmnDiagram getDiagram() {
		return diagram;
	}
}
