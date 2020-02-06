package org.processmining.models.graphbased.directed;

import org.processmining.models.graphbased.AttributeMapOwner;

/**
 * Interface to represent a directed graph element that can be the child of a
 * node. (E.g.: a task in a subprocess.)
 * 
 * @author Remco Dijkman
 * 
 */
public interface ContainableDirectedGraphElement extends AttributeMapOwner {

	ContainingDirectedGraphNode getParent();

}
