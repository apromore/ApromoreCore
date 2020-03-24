package org.apromore.processmining.models.graphbased.directedhypergraph;

import org.apromore.processmining.models.graphbased.AttributeMapOwner;

public interface DirectedHypergraphElement extends AttributeMapOwner, Cloneable {

	String getLabel();

	DirectedHypergraph<?, ?, ?> getGraph();

	boolean equals(Object o);

	int hashCode();
}