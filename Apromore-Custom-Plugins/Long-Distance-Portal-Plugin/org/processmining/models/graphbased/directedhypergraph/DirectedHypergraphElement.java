package org.processmining.models.graphbased.directedhypergraph;

import org.processmining.models.graphbased.AttributeMapOwner;

public interface DirectedHypergraphElement extends AttributeMapOwner, Cloneable {

	String getLabel();

	DirectedHypergraph<?, ?, ?> getGraph();

	boolean equals(Object o);

	int hashCode();
}