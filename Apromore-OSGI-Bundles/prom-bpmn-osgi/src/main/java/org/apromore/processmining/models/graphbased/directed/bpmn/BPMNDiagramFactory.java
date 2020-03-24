package org.apromore.processmining.models.graphbased.directed.bpmn;

public class BPMNDiagramFactory {

	public static BPMNDiagram newBPMNDiagram(String label) {
		return new BPMNDiagramImpl(label);
	}

	public static BPMNDiagram cloneBPMNDiagram(BPMNDiagram diagram) {
		BPMNDiagramImpl newDiagram = new BPMNDiagramImpl(diagram.getLabel());
		newDiagram.cloneFrom(diagram);
		return newDiagram;
	}
}
