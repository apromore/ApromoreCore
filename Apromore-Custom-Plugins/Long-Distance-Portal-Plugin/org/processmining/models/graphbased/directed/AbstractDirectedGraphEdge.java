package org.processmining.models.graphbased.directed;

import org.processmining.models.graphbased.AbstractGraphEdge;

public abstract class AbstractDirectedGraphEdge<S extends AbstractDirectedGraphNode, T extends AbstractDirectedGraphNode>
		extends AbstractGraphEdge<S, T> implements DirectedGraphEdge<S, T> {

	private final AbstractDirectedGraph<?, ?> graph;

	public AbstractDirectedGraphEdge(S source, T target) {
		super(source, target);
		assert (source.getGraph() == target.getGraph());
		this.graph = source.getGraph();
	}

	public AbstractDirectedGraph<?, ?> getGraph() {
		return graph;
	}

	public int compareTo(AbstractGraphEdge<S, T> edge) {
		int c;
		c = getClass().getName().compareTo(edge.getClass().getName());
		if (c != 0) {
			return c;
		}
		c = source.compareTo(edge.getSource());
		if (c != 0) {
			return c;
		}
		return target.compareTo(edge.getTarget());
	}

}
