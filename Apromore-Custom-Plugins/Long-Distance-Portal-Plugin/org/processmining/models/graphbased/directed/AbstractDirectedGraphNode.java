package org.processmining.models.graphbased.directed;

import org.processmining.models.graphbased.AbstractGraphNode;

public abstract class AbstractDirectedGraphNode extends AbstractGraphNode implements DirectedGraphNode {

	public AbstractDirectedGraphNode() {
		super();
	}

	public abstract AbstractDirectedGraph<?, ?> getGraph();

	public int compareTo(DirectedGraphNode node) {
		int comp = getId().compareTo(node.getId());
		//		assert (Math.abs(comp) == Math.abs(getLabel().compareTo(getLabel())));
		return comp;

	}

}
