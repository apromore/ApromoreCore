package org.apromore.processmining.plugins.bpmn.diagram;

import java.util.ArrayList;

import org.apromore.processmining.plugins.bpmn.Bpmn;

public class BpmnDiagrams extends ArrayList<BpmnDiagram> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 531474359170507584L;

	private Bpmn bpmn;
	private String name;
	
	public void setBpmn(Bpmn bpmn) {
		this.bpmn = bpmn;
	}
	public Bpmn getBpmn() {
		return bpmn;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
