package org.processmining.models.graphbased.directedhypergraph;

import java.awt.Dimension;

import org.processmining.models.graphbased.AbstractGraphNode;
import org.processmining.models.graphbased.AttributeMap;

public class AbstractDirectedHypergraphNode extends AbstractGraphNode implements DirectedHypergraphNode {

	private final AbstractDirectedHypergraph<?, ?, ?> graph;

	public AbstractDirectedHypergraphNode(String label, AbstractDirectedHypergraph<?, ?, ?> graph) {
		super();
		this.graph = graph;
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
	}

	public DirectedHypergraph<?, ?, ?> getGraph() {
		return graph;
	}



	public int compareTo(DirectedHypergraphNode node) {
		if (node instanceof AbstractDirectedHypergraphNode) {
			return getId().compareTo(((AbstractDirectedHypergraphNode) node).getId());
		}
		return getLabel().compareTo(node.getLabel());

	}

}
