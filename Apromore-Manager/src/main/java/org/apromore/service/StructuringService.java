package org.apromore.service;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

public interface StructuringService {

	BPMNDiagram getStructuredDiagram();

	String structureBPMNModel(BPMNDiagram diagram);

	String structureBPMNModel(String xmlProcess);
}
