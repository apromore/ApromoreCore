package org.processmining.models.graphbased.directed;

import org.processmining.models.graphbased.NodeID;

public interface DirectedGraphNode extends DirectedGraphElement, Comparable<DirectedGraphNode> {

	NodeID getId();

}
