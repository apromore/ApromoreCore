/* 
 * $Id: PanningTool.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2006, Gaudenz Alder
 * Copyright (c) 2006 David Benson
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.navigation;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JViewport;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.BasicMarqueeHandler;

/**
 * Tool that inserts vertices based on a prototype.
 */
public class PanningTool extends BasicMarqueeHandler {

	protected int m_XDifference, m_YDifference, dx, dy;
	
	/** The cursor to display when panning */
	protected Cursor panningCursor;

	/**
	 * The panning tool always has control if it is selected.
	 */
	public boolean isForceMarqueeEvent(MouseEvent e) {
		return true;
	}

	/**
	 * Sets the appropriate panning cursor and check how far the pan is
	 * 
	 * @param event
	 *            The object that describes the event.
	 */
	public void mousePressed(MouseEvent event) {
		JGraph graph = getGraphForEvent(event);
		startPoint = event.getPoint();
		previousCursor = graph.getCursor();
		// Workaround for the fact that you have to click the button
		// twice to get the panning cursor to appear properly, this
		// makes it appear after the first drag, which is a
		// slight improvement -- DCP 29/06/2006 (case 147).
		graph.setCursor(panningCursor);
		marqueeBounds = null;
		m_XDifference = event.getX();
		m_YDifference = event.getY();
		dx = 0;
		dy = 0;
		event.consume();
	}

	/**
	 * Overrides the parent's implementation to update the preview bounds to the
	 * current.
	 * 
	 * @param event
	 *            The object that describes the event.
	 */
	protected void processMouseDraggedEvent(MouseEvent event) {
		JGraph graph = getGraphForEvent(event);
		Component c = graph.getParent();

		// Added this test to move up the hierarchy and get the parent ViewPort,
		// if the graph is actually contained in a JPanel
		if (c instanceof JPanel) {
			c = c.getParent();
		}

		if (c instanceof JViewport) {
			JViewport jv = (JViewport) c;
			Point p = jv.getViewPosition();
			int newX = p.x - (event.getX() - m_XDifference);
			int newY = p.y - (event.getY() - m_YDifference);
			dx += (event.getX() - startPoint.getX());
			dy += (event.getY() - m_YDifference);

			int maxX = graph.getWidth() - jv.getWidth();
			int maxY = graph.getHeight() - jv.getHeight();
			if (newX < 0)
				newX = 0;
			if (newX > maxX)
				newX = maxX;
			if (newY < 0)
				newY = 0;
			if (newY > maxY)
				newY = maxY;

			jv.setViewPosition(new Point(newX, newY));
			event.consume();
		}
	}

	/**
	 * Includes the specified startPoint in the marquee selection. Calls
	 * overlay.
	 */
	public void mouseDragged(MouseEvent e) {
		processMouseDraggedEvent(e);
	}

	/**
	 * Ensures the cursor is set
	 * 
	 * @param event
	 *            The object that describes the event.
	 */
	public void mouseReleased(MouseEvent event) {
		JGraph graph = getGraphForEvent(event);
		if (previousCursor != null) {
			graph.setCursor(previousCursor);
		}
		event.consume();
	}

	/**
	 * @return the panningCursor
	 */
	public Cursor getPanningCursor() {
		return panningCursor;
	}

	/**
	 * @param panningCursor the panningCursor to set
	 */
	public void setPanningCursor(Cursor panningCursor) {
		this.panningCursor = panningCursor;
	}
}