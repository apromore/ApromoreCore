package org.processmining.models.graphbased.undirected;

import org.processmining.models.graphbased.AttributeMapOwner;

public interface UndirectedGraphElement extends AttributeMapOwner, Cloneable {

	String getLabel();

	UndirectedGraph<?, ?> getGraph();

	boolean equals(Object o);

	int hashCode();

}
