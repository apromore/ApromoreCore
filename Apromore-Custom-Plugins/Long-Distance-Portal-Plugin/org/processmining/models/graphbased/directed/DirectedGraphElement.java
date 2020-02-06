package org.processmining.models.graphbased.directed;

import org.processmining.models.graphbased.AttributeMapOwner;

/**
 * Main interface for elements of a directed graph. Elements are nodes and
 * edges. All implementing classes of this interface should implement an equals
 * method based on some globally unique ID.
 * 
 * @author bfvdonge
 * 
 */
public interface DirectedGraphElement extends AttributeMapOwner, Cloneable {

	String getLabel();

	DirectedGraph<?, ?> getGraph();

	boolean equals(Object o);

	int hashCode();
}