package org.processmining.models.graphbased.undirected;

import java.util.ArrayList;
import java.util.Collection;

import org.processmining.models.graphbased.AbstractGraph;

public abstract class AbstractUndirectedGraph<N extends UndirectedGraphNode, E extends UndirectedGraphEdge<? extends N>>
		extends AbstractGraph implements UndirectedGraph<N, E> {

	public AbstractUndirectedGraph() {
		super();
	}

	public AbstractUndirectedGraph<?, ?> getGraph() {
		return this;
	}

	protected abstract AbstractUndirectedGraph<N, E> getEmptyClone();

	protected abstract void cloneFrom(UndirectedGraph<N, E> graph);

	@SuppressWarnings("unchecked")
	public abstract void removeEdge(UndirectedGraphEdge edge);

	protected void checkAddEdge(N source, N target) {
		Collection<N> nodes = getNodes();
		if (!nodes.contains(source) && !nodes.contains(target)) {
			throw new IllegalArgumentException("Cannot add an arc between " + source.toString() + " and "
					+ target.toString() + ", since one of these nodes is not in the graph.");
		}

	}

	public Collection<E> getEdges(UndirectedGraphNode node) {
		Collection<E> edges = new ArrayList<E>();
		for (E edge : getEdges()) {
			if (edge.getTarget().equals(node) || edge.getSource().equals(node)) {
				edges.add(edge);
			}
		}
		return edges;
	}

	public int compareTo(UndirectedGraph<N, E> o) {
		if (!(o instanceof AbstractUndirectedGraph<?, ?>)) {
			return getLabel().compareTo(o.getLabel());
		}
		AbstractUndirectedGraph<?, ?> graph = (AbstractUndirectedGraph<?, ?>) o;
		return id.compareTo(graph.id);
	}

}
