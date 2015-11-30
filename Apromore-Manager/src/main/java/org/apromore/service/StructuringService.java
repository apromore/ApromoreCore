package org.apromore.service;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

public interface StructuringService {

	BPMNDiagram getStructuredDiagram();

	String structureBPMNModel(BPMNDiagram diagram) throws Exception;

	String structureBPMNModel(String xmlProcess) throws Exception;
}
