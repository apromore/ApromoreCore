/*
 * @(#)DefaultEdge.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation for an edge.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class DefaultEdge extends DefaultGraphCell implements Edge {

	/** Source and target of the edge. */
	protected Object source, target;

	/**
	 * Constructs an empty edge.
	 */
	public DefaultEdge() {
		this(null);
	}

	/**
	 * Constructs an edge that holds a reference to the specified user object.
	 * 
	 * @param userObject
	 *            reference to the user object
	 */
	public DefaultEdge(Object userObject) {
		this(userObject, null);
	}

	/**
	 * Constructs an edge that holds a reference to the specified user object
	 * and sets default values for points and the label position.
	 * 
	 * @param userObject
	 *            reference to the user object
	 */
	public DefaultEdge(Object userObject, AttributeMap storageMap) {
		super(userObject, storageMap);
	}

	/**
	 * Returns the source of the edge.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Returns the target of the edge.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Sets the source of the edge.
	 */
	public void setSource(Object port) {
		source = port;
	}

	/**
	 * Returns the target of <code>edge</code>.
	 */
	public void setTarget(Object port) {
		target = port;
	}

	/**
	 * Create a clone of the cell. The cloning of the user object is deferred to
	 * the cloneUserObject() method. The source and target references are set to
	 * null.
	 * 
	 * @return Object a clone of this object.
	 */
	public Object clone() {
		DefaultEdge edge = (DefaultEdge) super.clone();
		edge.source = null;
		edge.target = null;
		return edge;
	}

	//
	// Default Routing
	// 

	public static class LoopRouting implements Edge.Routing {

		public List route(GraphLayoutCache cache, EdgeView edge) {
			if (edge.isLoop()) {
				return routeLoop(cache, edge);
			}
			return routeEdge(cache, edge);
		}

		protected List routeLoop(GraphLayoutCache cache, EdgeView edge) {
			List newPoints = new ArrayList();
			newPoints.add(edge.getSource());
			CellView sourceParent = (edge.getSource() != null) ? edge
					.getSource().getParentView() : edge.getSourceParentView();
			if (sourceParent != null) {
				Point2D from = AbstractCellView.getCenterPoint(sourceParent);
				Rectangle2D rect = sourceParent.getBounds();
				double width = rect.getWidth();
				double height2 = rect.getHeight() / 2;
				double loopWidth = Math.min(20, Math.max(10, width / 8));
				double loopHeight = Math.min(30, Math.max(12, Math.max(
						loopWidth + 4, height2 / 2)));
				newPoints.add(edge.getAttributes().createPoint(
						from.getX() - loopWidth,
						from.getY() - height2 - loopHeight * 1.2));
				newPoints.add(edge.getAttributes().createPoint(from.getX(),
						from.getY() - height2 - 1.5 * loopHeight));
				newPoints.add(edge.getAttributes().createPoint(
						from.getX() + loopWidth,
						from.getY() - height2 - loopHeight * 1.2));
				newPoints.add(edge.getTarget());
				return newPoints;
			}
			return null;
		}

		protected List routeEdge(GraphLayoutCache cache, EdgeView edge) {
			return null;
		}

		public int getPreferredLineStyle(EdgeView edge) {
			if (edge.isLoop()) {
				return getLoopStyle();
			}
			return getEdgeStyle();
		}

		protected int getLoopStyle() {
			return GraphConstants.STYLE_BEZIER;
		}

		protected int getEdgeStyle() {
			return NO_PREFERENCE;
		}

	}

	public static class DefaultRouting extends LoopRouting {

		protected List routeEdge(GraphLayoutCache cache, EdgeView edge) {
			List newPoints = new ArrayList();
			int n = edge.getPointCount();
			Point2D from = edge.getPoint(0);
			newPoints.add(from);
			if (edge.getSource() instanceof PortView) {
				newPoints.set(0, edge.getSource());
				from = ((PortView) edge.getSource()).getLocation();
			} else if (edge.getSource() != null) {
				Rectangle2D b = edge.getSource().getBounds();
				from = edge.getAttributes().createPoint(b.getCenterX(),
						b.getCenterY());
			}
			Point2D to = edge.getPoint(n - 1);
			CellView target = edge.getTarget();
			if (target instanceof PortView)
				to = ((PortView) target).getLocation();
			else if (target != null) {
				Rectangle2D b = target.getBounds();
				to = edge.getAttributes().createPoint(b.getCenterX(),
						b.getCenterY());
			}
			if (from != null && to != null) {
				Point2D[] routed;
				double dx = Math.abs(from.getX() - to.getX());
				double dy = Math.abs(from.getY() - to.getY());
				double x2 = from.getX() + ((to.getX() - from.getX()) / 2);
				double y2 = from.getY() + ((to.getY() - from.getY()) / 2);
				routed = new Point2D[2];
				Rectangle2D targetBounds = null;
				Rectangle2D sourceBounds = null;
				if ((edge.getTarget() != null && edge.getTarget()
						.getParentView() != null)
						&& (edge.getSource() != null && edge.getSource()
								.getParentView() != null)) {
					targetBounds = edge.getTarget().getParentView().getBounds();
					sourceBounds = edge.getSource().getParentView().getBounds();
				}
				if (targetBounds != null && sourceBounds != null) {
					if (dx > dy) {
						routed[0] = edge.getAttributes().createPoint(x2,
								from.getY());
						routed[1] = edge.getAttributes().createPoint(x2,
								to.getY());
						if (targetBounds.contains(routed[0])
								|| (sourceBounds.contains(routed[0]))
								|| targetBounds.contains(routed[1])
								|| (sourceBounds.contains(routed[1]))) {
							routed[0] = edge.getAttributes().createPoint(
									from.getX(), y2);
							routed[1] = edge.getAttributes().createPoint(
									to.getX(), y2);
						}
					} else {
						routed[0] = edge.getAttributes().createPoint(
								from.getX(), y2);
						routed[1] = edge.getAttributes().createPoint(to.getX(),
								y2);
						if (targetBounds.contains(routed[0])
								|| (sourceBounds.contains(routed[0]))
								|| targetBounds.contains(routed[1])
								|| (sourceBounds.contains(routed[1]))) {
							routed[0] = edge.getAttributes().createPoint(x2,
									from.getY());
							routed[1] = edge.getAttributes().createPoint(x2,
									to.getY());
						}
					}
					// Set/Add Points
					for (int i = 0; i < routed.length; i++) {
						if (!targetBounds.contains(routed[i])
								&& (!sourceBounds.contains(routed[i]))) {
							newPoints.add(routed[i]);
						}
					}
				}

				// Add target point
				if (target != null)
					newPoints.add(target);
				else
					newPoints.add(to);
				return newPoints;
			}
			return null;
		}
	}

}