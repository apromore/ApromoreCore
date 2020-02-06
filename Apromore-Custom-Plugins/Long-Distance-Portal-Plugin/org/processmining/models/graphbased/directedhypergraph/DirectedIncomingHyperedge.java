package org.processmining.models.graphbased.directedhypergraph;

import java.util.Set;

/**
 * represents edges going from multiple sources towards one target
 * 
 * @author bfvdonge
 * 
 * @param <S>
 * @param <T>
 */
public interface DirectedIncomingHyperedge<S extends DirectedHypergraphNode, T extends DirectedHypergraphNode> extends
		DirectedHypergraphElement {

	Set<S> getSources();

	T getTarget();

}
