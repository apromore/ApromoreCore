package org.apromore.processmining.models.graphbased.directed;

import org.apromore.processmining.models.graphbased.NodeID;

public interface DirectedGraphNode extends DirectedGraphElement, Comparable<DirectedGraphNode> {

	NodeID getId();

}
