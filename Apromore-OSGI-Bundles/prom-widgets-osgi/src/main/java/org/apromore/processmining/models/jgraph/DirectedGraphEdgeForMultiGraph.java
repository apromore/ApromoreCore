package org.apromore.processmining.models.jgraph;

import java.awt.geom.Point2D;
import java.util.Arrays;

import org.apromore.processmining.models.graphbased.AbstractGraphElement;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;

public final class DirectedGraphEdgeForMultiGraph extends AbstractGraphElement implements
		DirectedGraphEdge<DirectedGraphNode, DirectedGraphNode>, DirectedGraphElementForMultiEdge {
	private final DirectedGraphNode source;
	private final DirectedGraphNode target;
	private final DirectedGraphEdge<?, ?> e;
	private boolean toIntermediate;

	public DirectedGraphEdgeForMultiGraph(DirectedGraphNode source, DirectedGraphNodeForMultiGraph intermediate,
			DirectedGraphEdge<?, ?> e) {
		this.source = source;
		this.target = intermediate;
		this.e = e;
		copyAttributes(true, e.getAttributeMap());
	}

	public DirectedGraphEdgeForMultiGraph(DirectedGraphNodeForMultiGraph intermediate, DirectedGraphNode target,
			DirectedGraphEdge<?, ?> e) {
		this.source = intermediate;
		this.target = target;
		this.e = e;
		copyAttributes(false, e.getAttributeMap());
	}

	protected void copyAttributes(boolean toIntermediate, AttributeMap sourceMap) {

		this.toIntermediate = toIntermediate;
		if (sourceMap.containsKey(AttributeMap.STYLE))
			getAttributeMap().put(AttributeMap.STYLE, sourceMap.get(AttributeMap.STYLE));
		if (sourceMap.containsKey(AttributeMap.LINEWIDTH))
			getAttributeMap().put(AttributeMap.LINEWIDTH, sourceMap.get(AttributeMap.LINEWIDTH));
		if (sourceMap.containsKey(AttributeMap.DASHPATTERN))
			getAttributeMap().put(AttributeMap.DASHPATTERN, sourceMap.get(AttributeMap.DASHPATTERN));
		if (sourceMap.containsKey(AttributeMap.DASHOFFSET))
			getAttributeMap().put(AttributeMap.DASHOFFSET, sourceMap.get(AttributeMap.DASHOFFSET));
		if (sourceMap.containsKey(AttributeMap.LABELCOLOR))
			getAttributeMap().put(AttributeMap.LABELCOLOR, sourceMap.get(AttributeMap.LABELCOLOR));

		if (sourceMap.containsKey(AttributeMap.NUMLINES))
			getAttributeMap().put(AttributeMap.NUMLINES, sourceMap.get(AttributeMap.NUMLINES));
		if (sourceMap.containsKey(AttributeMap.LINEWIDTH))
			getAttributeMap().put(AttributeMap.LINEWIDTH, sourceMap.get(AttributeMap.LINEWIDTH));

		if (sourceMap.containsKey(AttributeMap.EDGECOLOR))
			getAttributeMap().put(AttributeMap.EDGECOLOR, sourceMap.get(AttributeMap.EDGECOLOR));

		if (toIntermediate) {
			if (sourceMap.containsKey(AttributeMap.EDGESTART))
				getAttributeMap().put(AttributeMap.EDGESTART, sourceMap.get(AttributeMap.EDGESTART));
			if (sourceMap.containsKey(AttributeMap.EDGESTARTFILLED))
				getAttributeMap().put(AttributeMap.EDGESTARTFILLED, sourceMap.get(AttributeMap.EDGESTARTFILLED));
			if (source == e.getSource()) {
				// draw the middle shape only once if the source is indeed the edge main source
				if (sourceMap.containsKey(AttributeMap.EDGEMIDDLE))
					getAttributeMap().put(AttributeMap.EDGEEND, sourceMap.get(AttributeMap.EDGEMIDDLE));
				if (sourceMap.containsKey(AttributeMap.EDGEMIDDLEFILLED))
					getAttributeMap().put(AttributeMap.EDGEENDFILLED, sourceMap.get(AttributeMap.EDGEMIDDLEFILLED));
			}
		} else {
			if (sourceMap.containsKey(AttributeMap.EDGEEND))
				getAttributeMap().put(AttributeMap.EDGEEND, sourceMap.get(AttributeMap.EDGEEND));
			if (sourceMap.containsKey(AttributeMap.EDGEENDFILLED))
				getAttributeMap().put(AttributeMap.EDGEENDFILLED, sourceMap.get(AttributeMap.EDGEENDFILLED));
		}

		if (sourceMap.containsKey(AttributeMap.EXTRALABELPOSITIONS)) {
			assert ((Point2D[]) sourceMap.get(AttributeMap.EXTRALABELPOSITIONS)).length == ((String[]) sourceMap
					.get(AttributeMap.EXTRALABELS)).length;
			Point2D[] points = (Point2D[]) sourceMap.get(AttributeMap.EXTRALABELPOSITIONS);
			String[] labels = (String[]) sourceMap.get(AttributeMap.EXTRALABELS);
			if (toIntermediate) {
				getAttributeMap().put(AttributeMap.EXTRALABELPOSITIONS,
						Arrays.copyOfRange(points, 0, points.length / 2));
				getAttributeMap().put(AttributeMap.EXTRALABELS, Arrays.copyOfRange(labels, 0, points.length / 2));
			} else {
				getAttributeMap().put(AttributeMap.EXTRALABELPOSITIONS,
						Arrays.copyOfRange(points, points.length / 2, points.length));
				getAttributeMap().put(AttributeMap.EXTRALABELS,
						Arrays.copyOfRange(labels, points.length / 2, points.length));
			}
		}
	}

	public String getLabel() {
		return source.getLabel() + "->" + target.getLabel();
	}

	public DirectedGraph<?, ?> getGraph() {
		return source.getGraph();
	}

	public DirectedGraphNode getSource() {
		return source;
	}

	public DirectedGraphNode getTarget() {
		return target;
	}

	public int hashCode() {
		return e.hashCode() * 31 + (toIntermediate ? source.hashCode() : target.hashCode());
	}

	public boolean equals(Object o) {
		if (o instanceof DirectedGraphEdgeForMultiGraph) {
			DirectedGraphEdgeForMultiGraph edge = (DirectedGraphEdgeForMultiGraph) o;
			return (edge.e.equals(e) && toIntermediate ? edge.source.equals(source) : edge.target.equals(target));
		}
		return false;
	}

	public boolean isToIntermediate() {
		return toIntermediate;
	}

	public DirectedGraphEdge<?, ?> getMultiEdge() {
		return e;
	}
}