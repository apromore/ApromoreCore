package org.processmining.models.graphbased.undirected;

import java.util.Collection;

public interface UndirectedGraph<N extends UndirectedGraphNode, E extends UndirectedGraphEdge<? extends N>> extends
		UndirectedGraphElement, Comparable<UndirectedGraph<N, E>> {

	Collection<N> getNodes();

	Collection<E> getEdges();

	Collection<E> getEdges(UndirectedGraphNode node);

	@SuppressWarnings("unchecked")
	void removeEdge(UndirectedGraphEdge edge);

}
