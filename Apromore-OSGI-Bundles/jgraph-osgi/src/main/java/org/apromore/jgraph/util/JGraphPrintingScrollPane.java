/* 
 * $Id: JGraphPrintingScrollPane.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;

import org.apromore.jgraph.JGraph;

/**
 * Wrapper panel for a diagram/JGraph-pair that implements automatic sizing,
 * backgrounds, printing and undo support. When wrapped in a scrollpane this
 * panel adds rulers to the enclosing scrollpane. Furthermore, it automatically
 * sets the minimum size and scale of the graph based on its settings.
 */
public class JGraphPrintingScrollPane  extends JScrollPane implements Printable {

	/**
	 * Specifies the default page scale. Default is 1.5
	 */
	public static final double DEFAULT_PAGESCALE = 1.5;

	/**
	 * Background page format.
	 */
	protected PageFormat pageFormat = new PageFormat();

	/**
	 * Specifies if the background page is visible. Default is true.
	 */
	protected boolean isPageVisible = true;

	/**
	 * Defines the scaling for the background page metrics. Default is
	 * {@link #DEFAULT_PAGESCALE}.
	 */
	protected double pageScale = DEFAULT_PAGESCALE;

	/**
	 * References the inner graph.
	 */
	protected JGraph graph;

	/**
	 * Bound property names for the respective properties.
	 */
	public static String PROPERTY_METRIC = "metric",
			PROPERTY_PAGEVISIBLE = "pageVisible",
			PROPERTY_BACKGROUNDIMAGE = "backgroundImage",
			PROPERTY_RULERSVISIBLE = "rulersVisible",
			PROPERTY_PAGEFORMAT = "pageFormat",
			PROPERTY_AUTOSCALEPOLICY = "autoScalePolicy",
			PROPERTY_PAGESCALE = "pageScale";

	/**
	 * Returns the inner graph.
	 * 
	 * @return Returns the graph.
	 */
	public JGraph getGraph() {
		return graph;
	}

	/**
	 * Returns the page format of the background page.
	 * 
	 * @return Returns the pageFormat.
	 */
	public PageFormat getPageFormat() {
		return pageFormat;
	}

	/**
	 * Sets the page format of the background page.Fires a property change event
	 * for {@link #PROPERTY_PAGEFORMAT}.
	 * 
	 * @param pageFormat
	 *            The pageFormat to set.
	 */
	public void setPageFormat(PageFormat pageFormat) {
		Object oldValue = this.pageFormat;
		this.pageFormat = pageFormat;
		updateMinimumSize();
		firePropertyChange(PROPERTY_PAGEFORMAT, oldValue, pageFormat);
	}

	/**
	 * Returns the scale of the page metrics.
	 * 
	 * @return Returns the pageScale.
	 */
	public double getPageScale() {
		return pageScale;
	}

	/**
	 * Sets the scale of the page metrics.Fires a property change event for
	 * {@link #PROPERTY_PAGESCALE}.
	 * 
	 * @param pageScale
	 *            The pageScale to set.
	 */
	public void setPageScale(double pageScale) {
		double oldValue = this.pageScale;
		this.pageScale = pageScale;
		firePropertyChange(PROPERTY_PAGESCALE, oldValue, pageScale);
	}

	/**
	 * Updates the minimum size of the graph according to the current state of
	 * the background page: if the page is not visible then the minimum size is
	 * set to <code>null</code>, otherwise the minimum size is set to the
	 * smallest area of pages containing the graph.
	 */
	protected void updateMinimumSize() {
		if (isPageVisible() && pageFormat != null) {
			Rectangle2D bounds = graph.getCellBounds(graph.getRoots());
			Dimension size = (bounds != null) ? new Dimension((int) (bounds
					.getX() + bounds.getWidth()), (int) (bounds.getY() + bounds
					.getHeight())) : new Dimension(1, 1);
			int w = (int) (pageFormat.getWidth() * pageScale);
			int h = (int) (pageFormat.getHeight() * pageScale);
			int cols = (int) Math.ceil((double) (size.width - 5) / (double) w);
			int rows = (int) Math.ceil((double) (size.height - 5) / (double) h);
			size = new Dimension(Math.max(cols, 1) * w + 5, Math.max(rows, 1)
					* h + 5);
			graph.setMinimumSize(size);
		} else {
			graph.setMinimumSize(null);
		}
		graph.revalidate();
	}

	/**
	 * Computes the scale for the window autoscale policy.
	 * 
	 * @param border
	 *            The border to use.
	 * @return Returns the scale to use for the graph.
	 */
	protected double computeWindowScale(int border) {
		Dimension size = getViewport().getExtentSize();
		Rectangle2D p = getGraph().getCellBounds(getGraph().getRoots());
		if (p != null) {
			return Math.min((double) size.getWidth()
					/ (p.getX() + p.getWidth() + border), (double) size
					.getHeight()
					/ (p.getY() + p.getHeight() + border));
		}
		return 0;
	}

	/**
	 * Computes the scale for the page autoscale policy.
	 * 
	 * @return Returns the scale to use for the graph.
	 */
	protected double computePageScale() {
		Dimension size = getViewport().getExtentSize();
		Dimension p = getGraph().getMinimumSize();
		if (p != null && (p.getWidth() != 0 || p.getHeight() != 0)) {
			return Math.min((double) size.getWidth() / (double) p.getWidth(),
					(double) size.getHeight() / (double) p.getHeight());
		}
		return 0;
	}

	/**
	 * Computes the scale for the pagewidth autoscale policy.
	 * 
	 * @param border
	 *            The border to use.
	 * @return Returns the scale to use for the graph.
	 */
	protected double computePageWidthScale(int border) {
		Dimension size = getViewport().getExtentSize();
		Dimension p = getGraph().getMinimumSize();
		if (p != null && (p.getWidth() != 0 || p.getHeight() != 0)) {
			size.width = size.width - border;
			return (double) size.getWidth() / (double) p.getWidth();
		}
		return 0;
	}

	/**
	 * Prints the specified page on the specified graphics using
	 * <code>pageForm</code> for the page format.
	 * 
	 * @param g
	 *            The graphics to paint the graph on.
	 * @param printFormat
	 *            The page format to use for printing.
	 * @param page
	 *            The page to print
	 * @return Returns {@link Printable#PAGE_EXISTS} or
	 *         {@link Printable#NO_SUCH_PAGE}.
	 */
	public int print(Graphics g, PageFormat printFormat, int page) {
		Dimension pSize = graph.getPreferredSize();
		int w = (int) (printFormat.getWidth() * pageScale);
		int h = (int) (printFormat.getHeight() * pageScale);
		int cols = (int) Math.max(Math.ceil((double) (pSize.width - 5)
				/ (double) w), 1);
		int rows = (int) Math.max(Math.ceil((double) (pSize.height - 5)
				/ (double) h), 1);
		if (page < cols * rows) {

			// Configures graph for printing
			RepaintManager currentManager = RepaintManager.currentManager(this);
			currentManager.setDoubleBufferingEnabled(false);
			double oldScale = getGraph().getScale();
			getGraph().setScale(1 / pageScale);
			int dx = (int) ((page % cols) * printFormat.getWidth());
			int dy = (int) ((page % rows) * printFormat.getHeight());
			g.translate(-dx, -dy);
			g.setClip(dx, dy, (int) (dx + printFormat.getWidth()),
					(int) (dy + printFormat.getHeight()));

			// Prints the graph on the graphics.
			getGraph().paint(g);

			// Restores graph
			g.translate(dx, dy);
			graph.setScale(oldScale);
			currentManager.setDoubleBufferingEnabled(true);
			return PAGE_EXISTS;
		} else {
			return NO_SUCH_PAGE;
		}
	}

	/**
	 * Viewport for diagram panes that is in charge of painting the background
	 * image or page.
	 */
	public class Viewport extends JViewport {

		/**
		 * Paints the background.
		 * 
		 * @param g
		 *            The graphics object to paint the background on.
		 */
		public void paint(Graphics g) {
			if (isPageVisible())
				paintBackgroundPages((Graphics2D) g);
			else
				setBackground(graph.getBackground());
			if (graph.getBackgroundImage() != null) {
				paintBackgroundImage((Graphics2D) g);
			}
			setOpaque(!isPageVisible() && graph.getBackgroundImage() == null);
			super.paint(g);
			setOpaque(true);
		}

		/**
		 * Hook for subclassers to paint the background image.
		 * 
		 * @param g2
		 *            The graphics object to paint the image on.
		 */
		protected void paintBackgroundImage(Graphics2D g2) {
			// Clears the background
			if (!isPageVisible()) {
				g2.setColor(graph.getBackground());
				g2.fillRect(0, 0, graph.getWidth(), graph.getHeight());
			}
			// Paints the image
			AffineTransform tmp = g2.getTransform();
			Point offset = getViewPosition();
			g2.translate(-offset.x, -offset.y);
			g2.scale(graph.getScale(), graph.getScale());
			Image img = graph.getBackgroundImage().getImage();
			g2.drawImage(img, 0, 0, graph);
			g2.setTransform(tmp);
		}

		/**
		 * Hook for subclassers to paint the background page(s).
		 * 
		 * @param g2
		 *            The graphics object to paint the background page(s) on.
		 */
		protected void paintBackgroundPages(Graphics2D g2) {
			Point2D p = graph.toScreen(new Point2D.Double(
					pageFormat.getWidth(), pageFormat.getHeight()));
			Dimension pSize = graph.getPreferredSize();
			int w = (int) (p.getX() * pageScale);
			int h = (int) (p.getY() * pageScale);
			int cols = (int) Math.max(Math.ceil((double) (pSize.width - 5)
					/ (double) w), 1);
			int rows = (int) Math.max(Math.ceil((double) (pSize.height - 5)
					/ (double) h), 1);
			g2.setColor(graph.getHandleColor());

			// Draws the pages.
			Point offset = getViewPosition();
			g2.translate(-offset.x, -offset.y);
			g2.fillRect(0, 0, graph.getWidth(), graph.getHeight());
			g2.setColor(Color.darkGray);
			g2.fillRect(3, 3, cols * w, rows * h);
			g2.setColor(getGraph().getBackground());
			g2.fillRect(1, 1, cols * w - 1, rows * h - 1);

			// Draws the pagebreaks.
			Stroke previousStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10.0f, new float[] { 1, 2 }, 0));
			g2.setColor(Color.darkGray);
			for (int i = 1; i < cols; i++)
				g2.drawLine(i * w, 1, i * w, rows * h - 1);
			for (int i = 1; i < rows; i++)
				g2.drawLine(1, i * h, cols * w - 1, i * h);

			// Restores the graphics.
			g2.setStroke(previousStroke);
			g2.translate(offset.x, offset.y);
			g2.clipRect(0, 0, cols * w - 1 - offset.x, rows * h - 1 - offset.y);
		}

	}

	/**
	 * Returns true if the background page is visible.
	 * 
	 * @return Returns the isPageVisible.
	 */
	public boolean isPageVisible() {
		return isPageVisible;
	}

	/**
	 * Sets if the background page should be visible.Fires a property change
	 * event for {@link #PROPERTY_PAGEVISIBLE}.
	 * 
	 * @param isPageVisible
	 *            The isPageVisible to set.
	 */
	public void setPageVisible(boolean isPageVisible) {
		boolean oldValue = this.isPageVisible;
		this.isPageVisible = isPageVisible;
		updateMinimumSize();
		firePropertyChange(PROPERTY_PAGEVISIBLE, oldValue, isPageVisible);
	}
}