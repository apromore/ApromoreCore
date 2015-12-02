package org.apromore.service;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.Map;

public interface StructuringService {

	Map<Long, String> getErrors();

	BPMNDiagram getStructuredDiagram();

	String structureBPMNModel(BPMNDiagram diagram) throws Exception;

	String structureBPMNModel(String xmlProcess) throws Exception;
}
