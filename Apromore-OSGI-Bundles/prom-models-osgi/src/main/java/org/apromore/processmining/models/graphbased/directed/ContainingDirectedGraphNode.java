package org.apromore.processmining.models.graphbased.directed;

import java.util.Set;

import org.apromore.processmining.models.graphbased.Expandable;

/**
 * Interface to represent a directed graph node that contain other elements.
 * 
 * @author Remco Dijkman
 * 
 */
public interface ContainingDirectedGraphNode extends DirectedGraphNode, Expandable {

	Set<? extends ContainableDirectedGraphElement> getChildren();

	void addChild(ContainableDirectedGraphElement child);
}
