package org.processmining.models.graphbased.directed;

import java.util.Collection;
import java.util.Set;

public interface DirectedGraph<N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>>
		extends DirectedGraphElement, Comparable<DirectedGraph<N, E>> {

	Set<N> getNodes();

	Set<E> getEdges();

	Collection<E> getInEdges(DirectedGraphNode node);

	Collection<E> getOutEdges(DirectedGraphNode node);

	/**
	 * Removes the given edge from the graph.
	 * 
	 * @param edge
	 */
	@SuppressWarnings("unchecked")
	void removeEdge(DirectedGraphEdge edge);

	void removeNode(DirectedGraphNode cell);

}
