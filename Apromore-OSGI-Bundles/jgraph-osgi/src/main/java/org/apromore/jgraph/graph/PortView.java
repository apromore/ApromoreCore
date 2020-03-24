/*
 * @(#)PortView.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * The default implementation of a port view.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class PortView extends AbstractCellView {

	/** Default size for all ports is 6. */
	public static transient int SIZE = 6;

	/** Renderer for the class. */
	public static transient PortRenderer renderer = new PortRenderer();

	/**
	 * Controls if port magic should be allowed. Default is true. This is an
	 * easy switch to disable port magic for all instances of graphs.
	 */
	public static boolean allowPortMagic = true;

	/** Cache of the last valid parent. //FIX: Better solution? */
	protected transient CellView lastParent;

	/**
	 * Constructs an empty portview.
	 */
	public PortView() {
		super();
	}

	/**
	 * Constructs a view that holds a reference to the specified cell, anchor
	 * and parent vertex.
	 * 
	 * @param cell
	 *            reference to the cell in the model
	 */
	public PortView(Object cell) {
		super(cell);
	}

	//
	// CellView interface
	//

	/**
	 * This method ensures a non-null value. If the super method returns null
	 * then the last valid parent is returned. Note: If a vertex is removed, all
	 * ports will be replaced in connected edges. The ports are replaced by the
	 * center point of the <i>last </i> valid vertex view.
	 */
	public CellView getParentView() {
		CellView parent = super.getParentView();
		if (parent == null)
			parent = lastParent;
		else
			lastParent = parent;
		return parent;
	}

	/**
	 * Returns the bounds for the port view.
	 */
	public Rectangle2D getBounds() {
		Point2D loc = getLocation();
		double x = 0;
		double y = 0;
		if (loc != null) {
			x = loc.getX();
			y = loc.getY();
		}
		Rectangle2D bounds = new Rectangle2D.Double(x, y, 0, 0);
		bounds.setFrame(bounds.getX() - getPortSize() / 2, bounds.getY() - getPortSize() / 2,
				getPortSize(), getPortSize());
		return bounds;
	}

	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns <code>null</code>.
	 */
	public CellHandle getHandle(GraphContext context) {
		return null;
	}

	//
	// Special Methods
	//

	/**
	 * Shortcut method to getLocation(null, null)
	 */
	public Point2D getLocation() {
		return getLocation(null, null);
	}

	/**
	 * For backwards compatibility.
	 */
	public Point2D getLocation(EdgeView edge) {
		return getLocation(edge, null);
	}

	/**
	 * Returns the point that the port represents with respect to
	 * <code>edge</code> and <code>point</code>, which is the nearest point
	 * to this port view on the edge. <code>edge</code> and <code>point</code>
	 * may be <code>null</code>.
	 */
	public Point2D getLocation(EdgeView edge, Point2D nearest) {
		CellView vertex = getParentView();
		Point2D pos = null;
		if (vertex != null) {
			PortView anchor = null; // FIXME: (PortView)
			// mapper.getMapping(modelAnchor, false); //
			// Use refresh to get anchor view
			Point2D offset = GraphConstants.getOffset(allAttributes);
			// If No Edge Return Center
			if (edge == null && offset == null)
				pos = getCenterPoint(vertex);
			// Apply Offset
			if (offset != null) {
				double x = offset.getX();
				double y = offset.getY();
				Rectangle2D r = vertex.getBounds();
				// Absolute Offset
				boolean isAbsoluteX = GraphConstants.isAbsoluteX(allAttributes);
				boolean isAbsoluteY = GraphConstants.isAbsoluteY(allAttributes);
				if (!isAbsoluteX) {
					x = x * (r.getWidth() - 1) / GraphConstants.PERMILLE;
				}
				if (!isAbsoluteY) {
					y = y * (r.getHeight() - 1) / GraphConstants.PERMILLE;
				} // Offset from Anchor
				pos = (anchor != null) ? anchor.getLocation(edge, nearest)
						: new Point2D.Double(r.getX(), r.getY());
				pos = new Point2D.Double(pos.getX() + x, pos.getY() + y);
			} else if (edge != null) {
				// Floating Port
				if (nearest == null) {
					// If "Dangling" Port Return Center
					return getCenterPoint(vertex);
				}
				pos = vertex.getPerimeterPoint(edge, pos, nearest);
				if (shouldInvokePortMagic(edge)) {
					if (nearest != null) {
						Rectangle2D r = vertex.getBounds();
						if (nearest.getX() >= r.getX()
								&& nearest.getX() <= r.getX() + r.getWidth()) {
							pos.setLocation(nearest.getX(), pos.getY());
						} else if (nearest.getY() >= r.getY()
								&& nearest.getY() <= r.getY() + r.getHeight()) { // vertical
							pos.setLocation(pos.getX(), nearest.getY());
						}
						if (nearest.getX() < r.getX())
							pos.setLocation(r.getX(), pos.getY());
						else if (nearest.getX() > r.getX() + r.getWidth())
							pos
									.setLocation(r.getX() + r.getWidth(), pos
											.getY());
						if (nearest.getY() < r.getY())
							pos.setLocation(pos.getX(), r.getY());
						else if (nearest.getY() > r.getY() + r.getHeight())
							pos.setLocation(pos.getX(), r.getY()
									+ r.getHeight());
					}
				}
			}
		}
		return pos;
	}

	/**
	 * Subclassers can override this to decide whether or not "port magic"
	 * should appear on a given edge. (Port magic means the port tries to make
	 * the edge horizontal or vertical if the closest control point lies within
	 * the bounds of the parent vertex.)
	 */
	protected boolean shouldInvokePortMagic(EdgeView edge) {
		return allowPortMagic
				&& !(getParentView() instanceof EdgeView)
				&& edge.getPointCount() > 2
				&& GraphConstants.getLineStyle(edge.getAllAttributes()) == GraphConstants.STYLE_ORTHOGONAL;
	}

	/**
	 * @return the port size
	 */
	public int getPortSize() {
		return PortView.SIZE;
	}

	/**
	 * @param size the port size to set
	 */
	public void setPortSize(int size) {
		PortView.SIZE = size;
	}

}