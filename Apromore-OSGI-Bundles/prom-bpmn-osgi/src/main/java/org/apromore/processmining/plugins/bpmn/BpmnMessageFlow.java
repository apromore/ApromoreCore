package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;

public class BpmnMessageFlow extends BpmnFlow {

	public BpmnMessageFlow(String tag) {
		super(tag);
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		MessageFlow flow = diagram.addMessageFlow(id2node.get(sourceRef), id2node.get(targetRef), name);
		flow.getAttributeMap().put("Original id", id);
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(sourceRef) && elements.contains(targetRef)) {
			MessageFlow flow = diagram.addMessageFlow(id2node.get(sourceRef), id2node.get(targetRef), name);
			flow.getAttributeMap().put("Original id", id);
		}
	}
	
	public void marshall(MessageFlow messageFlow) {
		super.marshall(messageFlow);
	}
}
