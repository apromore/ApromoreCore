package org.processmining.models.graphbased.directed;

/**
 * Interface to represent a node that can be drawn on the boundary of another
 * node (e.g.: an exception on a BPMN activity.) The other node will be a
 * ContainingDirectedGraphNode.
 * 
 * @author Remco Dijkman
 * 
 */
public interface BoundaryDirectedGraphNode extends DirectedGraphNode {

	DirectedGraphNode getBoundingNode();

}
