/*
 * @(#)BasicMarqueeHandler.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.apromore.jgraph.JGraph;

/**
 * A simple implementation of a marquee handler for JGraph.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class BasicMarqueeHandler {

	/* Restore previous cursor after operation. */
	protected transient Cursor previousCursor = null;

	/* The rectangle that defines the current marquee selection. */
	protected Rectangle2D marqueeBounds;

	/* The start start and current point of the marquee session. */
	protected Point2D startPoint, currentPoint;

	/* Return true if this handler should be preferred over other handlers. */
	public boolean isForceMarqueeEvent(MouseEvent event) {
		return event.isAltDown();
	}

	/**
	 * Stops the current marquee selection.
	 */
	public void mouseReleased(MouseEvent e) {
		try {
			if (e != null && marqueeBounds != null) {
				if (!(e.getSource() instanceof JGraph))
					throw new IllegalArgumentException("MarqueeHandler cannot "
							+ "handle event from unknown source: " + e);
				JGraph graph = (JGraph) e.getSource();
				Rectangle2D bounds = graph
						.fromScreen((Rectangle2D) marqueeBounds.clone());
				handleMarqueeEvent(e, graph, bounds);
				graph.setCursor(previousCursor);
				Rectangle dirty = new Rectangle((int) marqueeBounds.getX(),
						(int) marqueeBounds.getY(), (int) marqueeBounds
								.getWidth() + 1, (int) marqueeBounds
								.getHeight() + 1);
				dirty.width++;
				dirty.height++;
				graph.repaint(dirty);
			}
		} finally {
			currentPoint = null;
			startPoint = null;
			marqueeBounds = null;
			previousCursor = null;
		}
	}

	/**
	 * Hook for subclassers. Current implementation checks if graph selection is
	 * enabled. This is called from mouseReleased to execute the marquee
	 * selection.
	 */
	public void handleMarqueeEvent(MouseEvent e, JGraph graph,
			Rectangle2D bounds) {
		CellView[] views = graph.getGraphLayoutCache().getRoots(bounds);
		ArrayList list = new ArrayList();
		for (int i = 0; i < views.length; i++)
			// above returns intersection, we want containment
			if (bounds.contains(views[i].getBounds()))
				list.add(views[i].getCell());
		Object[] cells = list.toArray();
		graph.getUI().selectCellsForEvent(graph, cells, e);
	}

	/**
	 * Includes the specified startPoint in the marquee selection. Calls
	 * overlay.
	 */
	public void mouseDragged(MouseEvent e) {
		if (startPoint != null) {
			if (!(e.getSource() instanceof JGraph))
				throw new IllegalArgumentException(
						"MarqueeHandler cannot handle event from unknown source: "
								+ e);
			JGraph graph = (JGraph) e.getSource();
			Graphics g = graph.getGraphics();

			// Xor the current display
			Color bg = graph.getBackground();
			Color fg = graph.getMarqueeColor();
			if (graph.isXorEnabled()) {
				g.setColor(fg);
				g.setXORMode(bg);
				overlay(graph, g, true);
			}
			Rectangle2D dirty = (Rectangle2D) marqueeBounds.clone();
			processMouseDraggedEvent(e);

			// Repaint
			if (graph.isXorEnabled()) {
				g.setColor(bg);
				g.setXORMode(fg);
				overlay(graph, g, false);
			} else {
				dirty.add(marqueeBounds);
				graph.repaint((int) dirty.getX()-1, (int) dirty.getY()-1,
						(int) dirty.getWidth()+2, (int) dirty.getHeight()+2);
			}
		}
	}

	/**
	 * Called from mouse dragged to update the marquee state during a repaint.
	 */
	protected void processMouseDraggedEvent(MouseEvent e) {
		if (startPoint != null) {
			currentPoint = e.getPoint();
			marqueeBounds = new Rectangle2D.Double(startPoint.getX(),
					startPoint.getY(), 0, 0);
			marqueeBounds.add(currentPoint);
		}
	}

	/**
	 * Called after the component was repainted (after autoscroll). This is used
	 * to indicate that the graphics is no more dirty.
	 */
	public void paint(JGraph graph, Graphics g) {
		if (marqueeBounds != null) {
			if (graph.isXorEnabled()) {
				Color bg = graph.getBackground();
				Color fg = graph.getMarqueeColor();
				g.setColor(fg);
				g.setXORMode(bg);
			}
			overlay(graph, g, false);
		}
	}

	/**
	 * Draw the current state of the handler. This is called twice by the
	 * overlay method and also by the paint method. The caller's intention is
	 * that the overlay method draws exactly the current state of the handler so
	 * that it may be used for XOR paint. The drag method calls overlay, changes
	 * the state, and calls overlay again to use this. However, since it is not
	 * always possible to clear the screen with an exact repaint the caller
	 * passes a flag to indicate if the graphics object should be cleared with
	 * this call (eg. if a subsequent call follows).
	 * 
	 * @param g
	 */
	public void overlay(JGraph graph, Graphics g, boolean clear) {
		if (marqueeBounds != null) {
			if (!graph.isXorEnabled()) {
				g.setColor(graph.getMarqueeColor());
			}
			g.drawRect((int) marqueeBounds.getX(), (int) marqueeBounds.getY(),
					(int) marqueeBounds.getWidth(), (int) marqueeBounds
							.getHeight());
		}
	}

	/**
	 * Start the marquee at the specified startPoint. This invokes
	 * expandMarqueeToPoint to initialize marquee selection.
	 */
	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
		marqueeBounds = new Rectangle2D.Double(startPoint.getX(), startPoint
				.getY(), 0, 0);
		if (!(e.getSource() instanceof JGraph))
			throw new IllegalArgumentException(
					"MarqueeHandler cannot handle event from unknown source: "
							+ e);
		JGraph graph = (JGraph) e.getSource();
		if (isMarqueeTriggerEvent(e, graph)) {
			previousCursor = graph.getCursor();
			graph.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	/**
	 * Hook for subclassers. Current implementation checks if graph selection is
	 * enabled. This is called from mousePressed before initiating the marquee
	 * selection.
	 */
	public boolean isMarqueeTriggerEvent(MouseEvent e, JGraph graph) {
		return graph.isSelectionEnabled();
	}

	/**
	 * Empty.
	 */
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}

	/**
	 * Returns the currentPoint.
	 * 
	 * @return Point
	 */
	public Point2D getCurrentPoint() {
		return currentPoint;
	}

	/**
	 * Returns the marqueeBounds.
	 * 
	 * @return Rectangle
	 */
	public Rectangle2D getMarqueeBounds() {
		return marqueeBounds;
	}

	/**
	 * Returns the previousCursor.
	 * 
	 * @return Cursor
	 */
	public Cursor getPreviousCursor() {
		return previousCursor;
	}

	/**
	 * Returns the startPoint.
	 * 
	 * @return Point
	 */
	public Point2D getStartPoint() {
		return startPoint;
	}

	/**
	 * Sets the currentPoint.
	 * 
	 * @param currentPoint
	 *            The currentPoint to set
	 */
	public void setCurrentPoint(Point2D currentPoint) {
		this.currentPoint = currentPoint;
	}

	/**
	 * Sets the marqueeBounds.
	 * 
	 * @param marqueeBounds
	 *            The marqueeBounds to set
	 */
	public void setMarqueeBounds(Rectangle2D marqueeBounds) {
		this.marqueeBounds = marqueeBounds;
	}

	/**
	 * Sets the previousCursor.
	 * 
	 * @param previousCursor
	 *            The previousCursor to set
	 */
	public void setPreviousCursor(Cursor previousCursor) {
		this.previousCursor = previousCursor;
	}

	/**
	 * Sets the startPoint.
	 * 
	 * @param startPoint
	 *            The startPoint to set
	 */
	public void setStartPoint(Point2D startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return Returns the source of the event as a graph.
	 */
	public static JGraph getGraphForEvent(MouseEvent event) {
		if (event.getSource() instanceof JGraph)
			return (JGraph) event.getSource();
		return null;
	}

}