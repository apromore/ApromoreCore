package org.processmining.models.graphbased.directedhypergraph;

import java.util.Set;

/**
 * represents edges going from a source towards multiple targets
 * 
 * @author bfvdonge
 * 
 * @param <S>
 * @param <T>
 */
public interface DirectedOutgoingHyperedge<S extends DirectedHypergraphNode, T extends DirectedHypergraphNode> extends
		DirectedHypergraphElement {

	S getSource();

	Set<T> getTargets();

}