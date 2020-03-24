package org.apromore.processmining.models.graphbased.undirected;

import org.apromore.processmining.models.graphbased.AbstractGraphNode;
import org.apromore.processmining.models.graphbased.AttributeMap;

public abstract class AbstractUndirectedNode extends AbstractGraphNode implements UndirectedGraphNode {

	private final AbstractUndirectedGraph<?, ?> graph;

	public AbstractUndirectedNode(String label, AbstractUndirectedGraph<?, ?> graph) {
		super();
		this.graph = graph;
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public UndirectedGraph<?, ?> getGraph() {
		return graph;
	}

	public int compareTo(UndirectedGraphNode node) {
		if (node instanceof AbstractUndirectedNode) {
			return getId().compareTo(((AbstractUndirectedNode) node).getId());
		} else {
			return getLabel().compareTo(node.getLabel());
		}
	}

}
