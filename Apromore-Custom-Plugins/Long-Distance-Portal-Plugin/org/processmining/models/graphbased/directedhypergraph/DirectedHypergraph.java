package org.processmining.models.graphbased.directedhypergraph;

import java.util.Collection;

public interface DirectedHypergraph<N extends DirectedHypergraphNode, I extends DirectedIncomingHyperedge<? extends N, ? extends N>, O extends DirectedOutgoingHyperedge<? extends N, ? extends N>>
		extends DirectedHypergraphElement, Comparable<DirectedHypergraph<N, I, O>> {

	Collection<N> getNodes();

	Collection<I> getInEdges();

	Collection<O> getOutEdges();

	Collection<I> getIncomingInEdges(DirectedHypergraphNode node);

	Collection<O> getOutgoingOutEdges(DirectedHypergraphNode node);

	Collection<O> getIncomingOutEdges(DirectedHypergraphNode node);

	Collection<I> getOutgoingInEdges(DirectedHypergraphNode node);

	@SuppressWarnings("unchecked")
	void removeEdge(DirectedIncomingHyperedge edge);

	@SuppressWarnings("unchecked")
	void removeEdge(DirectedOutgoingHyperedge edge);

}
