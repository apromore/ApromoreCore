package org.apromore.processmining.models.jgraph.views;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apromore.jgraph.graph.EdgeView;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.renderers.ProMEdgeRenderer;

public class JGraphEdgeView extends EdgeView implements Cleanable {

	private static final long serialVersionUID = -2874236692967529775L;
	private static ProMEdgeRenderer renderer;
	private DirectedGraphEdge<?, ?> edge;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;

	public transient Shape middleShape;

	@SuppressWarnings("unchecked")
	public JGraphEdgeView(ProMGraphEdge cell, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		super(cell);
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;
		edge = cell.getEdge();
		points = new ArrayList(2);
		points.add(cell.getSource().getView());
		points.addAll(cell.getInternalPoints());
		points.add(cell.getTarget().getView());

		groupBounds = null;
	}

	public void setPoints(List<Point2D> list) {
		points = list;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributeMap() {
		return viewSpecificAttributes;
	}

	@Override
	public ProMEdgeRenderer getRenderer() {
		if (renderer == null) {
			renderer = edge.getAttributeMap().get(AttributeMap.RENDERER, null);
			if (renderer == null) {
				renderer = new ProMEdgeRenderer();
			}
		}
		return renderer;
	}

	public void cleanUp() {
		edge = null;
		setCell(null);
		viewSpecificAttributes.clearViewSpecific(edge);
		source = null;
		target = null;
		if (renderer != null) {
			renderer.cleanUp();
			renderer = null;
		}
	}

	public DirectedGraphEdge<?, ?> getEdge() {
		return edge;
	}

	public boolean isPIP() {
		return isPIP;
	}

}
