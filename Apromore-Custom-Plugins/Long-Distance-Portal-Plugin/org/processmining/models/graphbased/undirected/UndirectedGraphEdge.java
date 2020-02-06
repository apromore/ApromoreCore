package org.processmining.models.graphbased.undirected;

public interface UndirectedGraphEdge<T extends UndirectedGraphNode> extends UndirectedGraphElement {

	T getSource();

	T getTarget();

}
