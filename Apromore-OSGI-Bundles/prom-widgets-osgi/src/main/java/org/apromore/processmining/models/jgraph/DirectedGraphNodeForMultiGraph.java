package org.apromore.processmining.models.jgraph;

import java.awt.Dimension;

import org.apromore.processmining.models.graphbased.AbstractGraphElement;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.NodeID;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.shapes.Rectangle;

public final class DirectedGraphNodeForMultiGraph extends AbstractGraphElement implements DirectedGraphNode,
		DirectedGraphElementForMultiEdge {
	private final DirectedGraphEdge<?, ?> e;
	private NodeID id = new NodeID();

	public DirectedGraphNodeForMultiGraph(DirectedGraphEdge<?, ?> e) {
		this.e = e;
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle());
		getAttributeMap().put(AttributeMap.BORDERWIDTH, 1);
		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { 0f, 10f });

		if (e.getAttributeMap().get(AttributeMap.SHOWLABEL, false)) {
			getAttributeMap().put(AttributeMap.INSET, 0);
			getAttributeMap().put(AttributeMap.AUTOSIZE, true);
			getAttributeMap().put(AttributeMap.SHOWLABEL, true);

		} else {
			getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			getAttributeMap().put(AttributeMap.SIZE, new Dimension(5, 5));
			getAttributeMap().put(AttributeMap.RESIZABLE, false);
		}
	}

	public int compareTo(DirectedGraphNode o) {
		throw new RuntimeException("Not implemented!");
	}

	public String getLabel() {
		return e.getLabel();
	}

	public DirectedGraph<?, ?> getGraph() {
		return e.getGraph();
	}

	public NodeID getId() {
		return id;
	}

	public int hashCode() {
		return e.hashCode();
	}

	public boolean equals(Object o) {
		return (o instanceof DirectedGraphNodeForMultiGraph && ((DirectedGraphNodeForMultiGraph) o).e.equals(e));
	}

	public DirectedGraphEdge<?, ?> getMultiEdge() {
		return e;
	}

}