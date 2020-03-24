/*
 * @(#)VertexView.java	1.0 03-JUL-04
 *
 * Copyright (c) 2001-2004 Gaudenz Alder
 *
 */
package org.apromore.jgraph.graph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.plaf.GraphUI;
import org.apromore.jgraph.plaf.basic.BasicGraphUI;

/**
 * The default implementation of a vertex view.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class VertexView extends AbstractCellView {

	/** Renderer for the class. */
	public static transient VertexRenderer renderer;

	// Headless environment does not allow vertex renderer
	static {
		try {
			renderer = new VertexRenderer();
		} catch (Error e) {
			// No vertex renderer
		}
	}

	public final static Rectangle2D defaultBounds = new Rectangle2D.Double(10,
			10, 20, 20);

	/** Reference to the bounds attribute */
	protected Rectangle2D bounds;

	/**
	 * Constructs an empty vertex view.
	 */
	public VertexView() {
		super();
	}

	/**
	 * Constructs a vertex view for the specified model object and the specified
	 * child views.
	 * 
	 * @param cell
	 *            reference to the model object
	 */
	public VertexView(Object cell) {
		super(cell);
	}

	//
	// CellView Interface
	//

	/**
	 * Overrides the parent method to udpate the cached points.
	 */
	public void update(GraphLayoutCache cache) {
		super.update(cache);
		bounds = GraphConstants.getBounds(allAttributes);
		if (bounds == null) {
			bounds = allAttributes.createRect(defaultBounds);
			GraphConstants.setBounds(allAttributes, bounds);
		}
		groupBounds = null;
	}

	public Rectangle2D getCachedBounds() {
		return bounds;
	}

	public void setCachedBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}

	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns a cell handle for the view, if the graph and the view are
	 * sizeable.
	 */
	public CellHandle getHandle(GraphContext context) {
		if (GraphConstants.isSizeable(getAllAttributes())
				&& !GraphConstants.isAutoSize(getAllAttributes())
				&& context.getGraph().isSizeable())
			return new SizeHandle(this, context);
		return null;
	}

	/**
	 * Returns the cached bounds for the vertex.
	 */
	public Rectangle2D getBounds() {
		Rectangle2D rect = super.getBounds();
		if (rect == null)
			rect = bounds;
		return rect;
	}

	/**
	 * @deprecated replaced by
	 *             {@link AbstractCellView#getCenterPoint(CellView vertex)}
	 * @return the center point of this vertex
	 */
	public Point2D getCenterPoint() {
		return AbstractCellView.getCenterPoint(this);
	}

	/**
	 * @deprecated replaced by
	 *             {@link #getPerimeterPoint(EdgeView edge, Point2D source, Point2D p)}
	 */
	public Point2D getPerimeterPoint(Point2D source, Point2D p) {
		return AbstractCellView.getCenterPoint(this);
	}

	//
	// Special Methods
	//

	/**
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds. Note: You must override this method
	 * if you use a different renderer. This is because this method relies on
	 * the VertexRenderer interface, which can not be safely assumed for
	 * subclassers.
	 */
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (getRenderer() instanceof VertexRenderer)
			return ((VertexRenderer) getRenderer()).getPerimeterPoint(this,
					source, p);
		return super.getPerimeterPoint(edge, source, p);
	}

	/** Array that holds the cursors for the different control points. */
	public static transient int[] defaultCursors = new int[] {
			Cursor.NW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR,
			Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR,
			Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
			Cursor.S_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };

	/** Array that holds the cursors for the different control points. */
	public static transient int[] xCursors = new int[] {
			Cursor.W_RESIZE_CURSOR, 0, Cursor.E_RESIZE_CURSOR,
			Cursor.W_RESIZE_CURSOR, Cursor.E_RESIZE_CURSOR,
			Cursor.W_RESIZE_CURSOR, 0, Cursor.E_RESIZE_CURSOR };

	/** Array that holds the cursors for the different control points. */
	public static transient int[] yCursors = new int[] {
			Cursor.N_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR,
			Cursor.N_RESIZE_CURSOR, 0, 0, Cursor.S_RESIZE_CURSOR,
			Cursor.S_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR };

	public static class SizeHandle implements CellHandle, Serializable {

		/** Reference to graph off screen graphics */
		protected transient Graphics offgraphics;

		protected transient boolean firstDrag = true;

		protected transient JGraph graph;

		/* Reference to the temporary view for this handle. */
		protected transient VertexView vertex;

		protected transient CellView[] portViews;

		protected transient Rectangle2D cachedBounds;

		/* Reference to the context for the specified view. */
		protected transient GraphContext context;

		protected transient Rectangle2D initialBounds;

		protected transient CellView[] contextViews;

		/* Index of the active control point. -1 if none is active. */
		protected transient int index = -1;

		/* Array of control points represented as rectangles. */
		protected transient Rectangle2D[] r = new Rectangle2D[8];

		protected boolean firstOverlayInvocation = true;

		/** Array that holds the cursors for the different control points. */
		public transient int[] cursors = null;

		/**
		 * True if the cell is being edited.
		 */
		protected boolean editing = false;

		public SizeHandle(VertexView vertexview, GraphContext ctx) {
			graph = ctx.getGraph();
			vertex = vertexview;
			editing = graph.getEditingCell() == vertex.getCell();
			int sizeableAxis = GraphConstants.getSizeableAxis(vertex
					.getAllAttributes());
			if (sizeableAxis == GraphConstants.X_AXIS)
				cursors = xCursors;
			else if (sizeableAxis == GraphConstants.Y_AXIS)
				cursors = yCursors;
			else
				cursors = defaultCursors;
			// PortView Preview
			portViews = ctx.createTemporaryPortViews();
			initialBounds = (Rectangle2D) vertex.getBounds().clone();
			context = ctx;
			for (int i = 0; i < r.length; i++)
				r[i] = new Rectangle2D.Double();
			invalidate();
		}

		public boolean isConstrainedSizeEvent(MouseEvent e) {
			GraphUI ui = graph.getUI();
			if (ui instanceof BasicGraphUI)
				return ((BasicGraphUI) ui).isConstrainedMoveEvent(e);
			return false;
		}

		public void paint(Graphics g) {
			invalidate();
			g.setColor((editing) ? graph.getLockedHandleColor() : graph
					.getHandleColor());
			for (int i = 0; i < r.length; i++) {
				if (cursors[i] != 0)
					g
							.fill3DRect((int) r[i].getX(), (int) r[i].getY(),
									(int) r[i].getWidth(), (int) r[i]
											.getHeight(), true);
			}
			if (!graph.isXorEnabled()) {
				firstOverlayInvocation = false;
				overlay(g);
			}
		}

		protected void initOffscreen() {
			if (!graph.isXorEnabled()) {
				return;
			}
			try {
				offgraphics = graph.getOffgraphics();
			} catch (Exception e) {
				offgraphics = null;
			} catch (Error e) {
				offgraphics = null;
			}
		}

		public void overlay(Graphics g) {
			if (!firstOverlayInvocation) {
				if (cachedBounds != null) {
					g.setColor(Color.black);
					Rectangle2D tmp = graph.toScreen((Rectangle2D) cachedBounds
							.clone());
					g.drawRect((int) tmp.getX(), (int) tmp.getY(), (int) tmp
							.getWidth() - 2, (int) tmp.getHeight() - 2);
				} else if (!initialBounds.equals(vertex.getBounds())) {
					Graphics2D g2 = (Graphics2D) g;
					AffineTransform oldTransform = g2.getTransform();
					g2.scale(graph.getScale(), graph.getScale());
					graph.getUI()
							.paintCell(g, vertex, vertex.getBounds(), true);
					if (contextViews != null) {
						for (int i = 0; i < contextViews.length; i++) {
							graph.getUI().paintCell(g, contextViews[i],
									contextViews[i].getBounds(), true);
						}
					}
					if (!graph.isPortsScaled())
						g2.setTransform(oldTransform);
					if (portViews != null && graph.isPortsVisible())
						graph.getUI().paintPorts(g, portViews);
					g2.setTransform(oldTransform);
				}
			}
			firstOverlayInvocation = false;
		}

		/**
		 * Invoked when the mouse pointer has been moved on a component (with no
		 * buttons down).
		 */
		public void mouseMoved(MouseEvent event) {
			if (vertex != null) {
				for (int i = 0; i < r.length; i++) {
					if (r[i].contains(event.getPoint())) {
						graph.setCursor(new Cursor(cursors[i]));
						event.consume();
						return;
					}
				}
			}
		}

		/** Process mouse pressed event. */
		public void mousePressed(MouseEvent event) {
			if (!graph.isSizeable())
				return;
			for (int i = 0; i < r.length; i++) {
				if (r[i].contains(event.getPoint()) && cursors[i] != 0) {
					Set set = new HashSet();
					set.add(vertex.getCell());
					contextViews = context.createTemporaryContextViews(set);
					Object[] all = AbstractCellView
							.getDescendantViews(new CellView[] { vertex });
					if (all.length >= org.apromore.jgraph.plaf.basic.BasicGraphUI.MAXHANDLES)
						cachedBounds = (Rectangle2D) initialBounds.clone();
					event.consume();
					index = i;
					return;
				}
			}
		}

		/** Process mouse dragged event. */
		public void mouseDragged(MouseEvent event) {
			if (firstDrag && graph.isDoubleBuffered() && cachedBounds == null) {
				initOffscreen();
				firstDrag = false;
			}
			Rectangle2D dirty = null;
			Graphics g = (offgraphics != null) ? offgraphics : graph
					.getGraphics();
			if (index == -1)
				return;
			if (offgraphics != null || !graph.isXorEnabled()) {
				dirty = graph
						.toScreen((Rectangle2D) vertex.getBounds().clone());
				Rectangle2D t = graph.toScreen(AbstractCellView
						.getBounds(contextViews));
				if (t != null)
					dirty.add(t);
			}
			Rectangle2D newBounds = computeBounds(event);
			if (graph.isXorEnabled()) {
				g.setColor(graph.getForeground());
				g.setXORMode(graph.getBackground().darker());
				overlay(g);
			} else {
				firstOverlayInvocation = false;
			}
			if (cachedBounds != null)
				cachedBounds = newBounds;
			else {
				// Reset old Bounds
				CellView[] all = AbstractCellView
						.getDescendantViews(new CellView[] { vertex });
				for (int i = 0; i < all.length; i++) {
					CellView orig = graph.getGraphLayoutCache().getMapping(
							all[i].getCell(), false);
					if (orig != null) {
						AttributeMap origAttr = (AttributeMap) orig
								.getAllAttributes().clone();
						all[i].changeAttributes(graph.getGraphLayoutCache(), origAttr);
						all[i].refresh(graph.getGraphLayoutCache(), context, false);
					}
				}
				vertex.setBounds(newBounds);
				if (vertex != null)
					graph.getGraphLayoutCache().update(vertex);
				if (contextViews != null)
					graph.getGraphLayoutCache().update(contextViews);
			}
			if (graph.isXorEnabled()) {
				overlay(g);
			}
			if (offgraphics != null || !graph.isXorEnabled()) {
				dirty.add(graph.toScreen((Rectangle2D) vertex.getBounds()
						.clone()));
				Rectangle2D t = graph.toScreen(AbstractCellView
						.getBounds(contextViews));
				if (t != null)
					dirty.add(t);
				int border = PortView.SIZE + 10;
				if (graph.isPortsScaled())
					border = (int) (graph.getScale() * border);
				int border2 = border / 2;
				dirty.setFrame(dirty.getX() - border2, dirty.getY() - border2,
						dirty.getWidth() + border, dirty.getHeight() + border);
				double sx1 = Math.max(0, dirty.getX());
				double sy1 = Math.max(0, dirty.getY());
				double sx2 = sx1 + dirty.getWidth();
				double sy2 = sy1 + dirty.getHeight();
				if (offgraphics != null) {
					graph.drawImage((int) sx1, (int) sy1, (int) sx2, (int) sy2,
							(int) sx1, (int) sy1, (int) sx2, (int) sy2);
				} else {
					graph.repaint((int) dirty.getX(), (int) dirty.getY(),
							(int) dirty.getWidth(), (int) dirty.getHeight());
				}
			}
		}

		protected Rectangle2D computeBounds(MouseEvent event) {
			double left = initialBounds.getX();
			double right = initialBounds.getX() + initialBounds.getWidth() - 1;
			double top = initialBounds.getY();
			double bottom = initialBounds.getY() + initialBounds.getHeight()
					- 1;
			Point2D p = graph.fromScreen(graph.snap((Point2D) event.getPoint()
					.clone()));
			// Not into negative coordinates
			p.setLocation(Math.max(0, p.getX()), Math.max(0, p.getY()));
			// Bottom row
			if (index > 4)
				bottom = p.getY();
			// Top row
			else if (index < 3)
				top = p.getY();
			// Left col
			if (index == 0 || index == 3 || index == 5)
				left = p.getX();
			// Right col
			else if (index == 2 || index == 4 || index == 7)
				right = p.getX();
			double width = right - left;
			double height = bottom - top;
			if (isConstrainedSizeEvent(event)
					|| GraphConstants.isConstrained(vertex.getAllAttributes())) {
				if (index == 3 || index == 4 || index == 5)
					height = width;
				else if (index == 1 || index == 6 || index == 2 || index == 7)
					width = height;
				else {
					height = width;
					top = bottom - height;
				}
			}
			if (width < 0) { // Flip over left side
				left += width;
				width = Math.abs(width);
			}
			if (height < 0) { // Flip over top side
				top += height;
				height = Math.abs(height);
			}
			return new Rectangle2D.Double(left, top, width + 1, height + 1);
		}

		// Dispatch the edit event
		public void mouseReleased(MouseEvent e) {
			if (index != -1) {
				cachedBounds = computeBounds(e);
				vertex.setBounds(cachedBounds);
				CellView[] views = AbstractCellView
						.getDescendantViews(new CellView[] { vertex });
				Map attributes = GraphConstants.createAttributes(views, null);
				graph.getGraphLayoutCache().edit(attributes, null, null, null);
			}
			e.consume();
			cachedBounds = null;
			initialBounds = null;
			firstDrag = true;
		}

		protected void invalidate() {
			// Retrieve current bounds and set local vars
			Rectangle2D tmp = graph.getCellBounds(vertex.getCell());
			if (tmp != null) {
				tmp = (Rectangle2D) tmp.clone();
				graph.toScreen(tmp);
				int handlesize = graph.getHandleSize();
				int s2 = 2 * handlesize;
				double left = tmp.getX() - handlesize;
				double top = tmp.getY() - handlesize;
				double w2 = tmp.getX() + (tmp.getWidth() / 2) - handlesize;
				double h2 = tmp.getY() + (tmp.getHeight() / 2) - handlesize;
				double right = tmp.getX() + tmp.getWidth() - handlesize;
				double bottom = tmp.getY() + tmp.getHeight() - handlesize;
				// Update control point positions
				r[0].setFrame(left, top, s2, s2);
				r[1].setFrame(w2, top, s2, s2);
				r[2].setFrame(right, top, s2, s2);
				r[3].setFrame(left, h2, s2, s2);
				r[4].setFrame(right, h2, s2, s2);
				r[5].setFrame(left, bottom, s2, s2);
				r[6].setFrame(w2, bottom, s2, s2);
				r[7].setFrame(right, bottom, s2, s2);
			}
		}

	}

}
