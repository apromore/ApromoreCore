/*
 * @(#)PortRenderer.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.apromore.jgraph.JGraph;

/**
 * This renderer displays entries that implement the CellView interface and
 * supports the following attributes:
 * <li> GraphConstants.OFFSET GraphConstants.ABSOLUTE </li
 * 
 * @version 1.0 28/11/01
 * @author Gaudenz Alder
 */

public class PortRenderer extends JComponent implements CellViewRenderer,
		Serializable {

	/** Cache the current graph for drawing */
	// protected transient JGraph graph;
	/** Cache the current edgeview for drawing. */
	protected transient PortView view;

	/** Cache the current graph background. */
	protected Color graphBackground = Color.white;

	/** Cached hasFocus and selected value. */
	transient protected boolean hasFocus, selected, preview, xorEnabled;

	/**
	 * Constructs a renderer that may be used to render ports.
	 */
	public PortRenderer() {
		setForeground(UIManager.getColor("MenuItem.selectionBackground"));
		setBackground(UIManager.getColor("Tree.selectionBorderColor"));
	}

	/**
	 * Configure and return the renderer based on the passed in components. The
	 * value is typically set from messaging the graph with
	 * <code>convertValueToString</code>.
	 * 
	 * @param graph
	 *            the graph that that defines the rendering context.
	 * @param view
	 *            the cell view that should be rendered.
	 * @param sel
	 *            whether the object is selected.
	 * @param focus
	 *            whether the object has the focus.
	 * @param preview
	 *            whether we are drawing a preview.
	 * @return the component used to render the value.
	 */
	public Component getRendererComponent(JGraph graph, CellView view,
			boolean sel, boolean focus, boolean preview) {
		// Check type
		if (view instanceof PortView && graph != null) {
			graphBackground = graph.getBackground();
			this.view = (PortView) view;
			this.hasFocus = focus;
			this.selected = sel;
			this.preview = preview;
			this.xorEnabled = graph.isXorEnabled();
			return this;
		}
		return null;
	}

	/**
	 * Paint the renderer. Overrides superclass paint to add specific painting.
	 * Note: The preview flag is interpreted as "highlight" in this context.
	 * (This is used to highlight the port if the mouse is over it.)
	 */
	public void paint(Graphics g) {
		Dimension d = getSize();
		if (xorEnabled) {
			g.setColor(graphBackground);
			g.setXORMode(graphBackground);
		}
		super.paint(g);
		if (preview) {
			g.fill3DRect(0, 0, d.width, d.height, true);
		} else {
			g.fillRect(0, 0, d.width, d.height);
		}
		boolean offset = (GraphConstants.getOffset(view.getAllAttributes()) != null);
		g.setColor(getForeground());
		if (!offset)
			g.fillRect(1, 1, d.width - 2, d.height - 2);
		else if (!preview)
			g.drawRect(1, 1, d.width - 3, d.height - 3);
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void validate() {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void revalidate() {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void repaint(Rectangle r) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		// Strings get interned...
		if (propertyName == "text")
			super.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, byte oldValue,
			byte newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, char oldValue,
			char newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, short oldValue,
			short newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, long oldValue,
			long newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, float oldValue,
			float newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, double oldValue,
			double newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a
	 * href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
	}

}