/*
 * $Id: MultiLineVertexRenderer.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.components.labels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.AbstractCellView;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.VertexRenderer;
import org.apromore.jgraph.graph.VertexView;

/**
 * Universal renderer for vertices and groups. This implementation supports
 * drawing rectangles, circles, diamonds and rounded rectangles and optionally
 * paints a gradient background, stretched background image and folding icon.
 */
public class MultiLineVertexRenderer extends VertexRenderer {

	/**
	 * Client property for JGraph to control the display of the folding icons.
	 * Default is true, eg if the property is missing the icons are painted. To
	 * switch this feature off, use the following code:
	 * 
	 * <PRE>
	 * 
	 * graph.putClientProperty(
	 * JGraphpadVertexRenderer.CLIENTPROPERTY_SHOWFOLDINGICONS, new
	 * Boolean(false));
	 * 
	 * </PRE>
	 */
	public static String CLIENTPROPERTY_SHOWFOLDINGICONS = "showFoldingIcons";

	/**
	 * Defines a dimension of width and height 0.
	 */
	public static Dimension ZERO_DIMENSION = new Dimension(0,0);
	
	/**
	 * Defines the default inset to render rich text.
	 */
	public static int INSET = 4;

	/**
	 * Defines the root handle size and location.
	 */
	public static Rectangle handle = new Rectangle(0, 0, 7, 7);

	/**
	 * Holds a reference to fetch the correct cell value from the model in
	 * paint. This should go into getComponentRenderer.
	 */
	protected JGraph graph;

	/**
	 * Defines the shape constants to be used as values for the
	 * {@link CellConstants#VERTEXSHAPE} attributes.
	 */
	public static final int SHAPE_RECTANGLE = 0, SHAPE_CIRCLE = 1,
			SHAPE_DIAMOND = 2, SHAPE_ROUNDED = 3, SHAPE_CYLINDER = 4,
			SHAPE_TRIANGLE = 5;

	/**
	 * Holds the text pane to be used for rich text rendering.
	 */
	public static JTextPane textPane = new JTextPane();

	/**
	 * Holds the wrapper renderer used for heavyweights.
	 */
	protected static JComponent wrapperRenderer;

	/**
	 * Holds the user object of the current cell.
	 */
	protected Object userObject = null;

	/**
	 * Holds the shape of the current view.
	 */
	protected int shape = 0;

	/**
	 * Specifies whether the current view is a rich text value, and if the image
	 * should be stretched.
	 */
	protected boolean isRichText = false, stretchImage = false,
			isEditing = false, showFoldingIcons = true, isGroup = false;

	/**
	 * Holds the background and foreground of the graph.
	 */
	protected Color graphBackground = Color.white,
			graphForeground = Color.black;

	/**
	 * References the value component of the user object if one exists.
	 */
	protected Component valueComponent;

	/**
	 * Holds the area to be painted for the cylinder shape.
	 */
	protected Area cylinderArea = null;

	/**
	 * Holds the shape to be painted for diamond cells.
	 */
	protected Polygon diamond = null;

	/**
	 * Holds the round rect arc size for rounded rectangles.
	 */
	protected int roundRectArc = 0;

	/**
	 * Specified if a heavyweight should be painted. Default is true.
	 */
	protected transient boolean showHeavyweight = true;

	/**
	 * Constructs a new vertex renderer.
	 */
	public MultiLineVertexRenderer() {
		textPane.setOpaque(false);
		textPane.setBorder(BorderFactory.createEmptyBorder(INSET, INSET, INSET,
				INSET));

		// Makes sure the heavyweights is never returned directly,
		// so that the real component is never touched directly.
		wrapperRenderer = new JComponent() {
			public void paint(Graphics g) {
				if (showHeavyweight) {
					valueComponent.setSize(getSize());
					if (!isEditing)
						valueComponent.paint(g);
				} else {
					g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
					g.drawLine(0, 0, getWidth() - 1, getHeight() - 1);
					g.drawLine(getWidth() - 1, 0, 0, getHeight() - 1);
				}
			}
		};
		wrapperRenderer.setDoubleBuffered(false);
	}

	/**
	 * Overrides the parent implementation to return the value component stored
	 * in the user object instead of this renderer if a value component exists.
	 * This applies some of the values installed to this renderer to the value
	 * component (border, opaque) if the latter is a JComponent.
	 * 
	 * @return Returns a configured renderer for the specified view.
	 */
	public Component getRendererComponent(JGraph graph, CellView view,
			boolean sel, boolean focus, boolean preview) {
		this.graph = graph;
		Component c = super.getRendererComponent(graph, view, sel, focus,
				preview);
		graphBackground = graph.getBackground();
		graphForeground = graph.getForeground();
		isEditing = graph.getEditingCell() == view.getCell();
		isGroup = DefaultGraphModel.isGroup(graph.getModel(), view.getCell());
		Boolean bool = (Boolean) graph
				.getClientProperty(CLIENTPROPERTY_SHOWFOLDINGICONS);
		if (bool != null)
			showFoldingIcons = bool.booleanValue();
		else
			showFoldingIcons = true;
		if (valueComponent != null) {
			valueComponent.setSize(getSize());
			valueComponent.setBackground(getBackground());

			// Shows a cross for all scaled heavyweight previews
			// for faster preview performance
			showHeavyweight = !preview || graph.getScale() == 1;

			// Applies the configured properties to the value component
			if (valueComponent instanceof JComponent) {
				JComponent comp = (JComponent) valueComponent;
				comp.setBorder(getBorder());
				comp.setOpaque(isOpaque());

				// Workaround for locking problem in windows
				comp.setDoubleBuffered(false);

				// Do not wrap component hierachies
				if (comp.getComponentCount() > 0 && !isEditing
						&& showHeavyweight) {
					return comp;
				}
			}
			// Do not return the component directly, return a wrapper
			// so that the real component is never touched directly.
			return wrapperRenderer;
		}
		return c;
	}

	/**
	 * Paints the renderer component for the configured view. This
	 * implementation consists of three parts: painting the background, gradient
	 * and stretched image, painting the content by doing a supercall and
	 * calling the rich text renderer if required, and finally paint the border,
	 * selection border and the folding handle.
	 * 
	 * @param g
	 *            The graphics to paint the cell to.
	 */
	public void paint(Graphics g) {
		Border previousBorder = getBorder();
		paintBackground(g);

		// Limits all further painting to the clipping region of the actual
		// shape so that images and text are cropped at the shape bounds.
		// The clipping region is intersected with the "dirty" region.
		Dimension d = getSize();
		int b = borderWidth;
		Shape previousShape = g.getClip();
		if (shape == SHAPE_DIAMOND || shape == SHAPE_TRIANGLE) {
			Area clip = new Area(diamond);
			clip.intersect(new Area(previousShape));
			g.setClip(clip);
		} else if (shape == SHAPE_CYLINDER) {
			Area clip = new Area(cylinderArea);
			cylinderArea.intersect(new Area(previousShape));
			g.setClip(clip);
		} else if (shape == SHAPE_CIRCLE) {
			Area clip = new Area(new java.awt.geom.Ellipse2D.Float(-b, -b,
					d.width + b, d.height + b));
			clip.intersect(new Area(previousShape));
			g.setClip(clip);
		}

		// Stretched images appear in the background of the cell. This sets
		// the icon to null after painting and takes over all image painting
		// in the case of stretched images as the superclass does not support
		// it. No images are drawn for previews to speedup the display.
		if (stretchImage) {
			Image img = null;
			Icon icon = getIcon();
			if (icon != null)
				img = ((ImageIcon) icon).getImage();
			if (img != null && !preview)
				g.drawImage(img, 0, 0, d.width - 1, d.height - 1, this);
			setIcon(null);
		}

		// Makes sure that no border, background or selection border is painted
		// by the superclass' paint in case we already did that or will do that
		// later. This makes sure the superclass' only paints the label and
		// image.
		boolean wasSelected = selected;
		boolean wasOpaque = isOpaque();
		if (shape != SHAPE_RECTANGLE) {
			setBorder(null);
			setOpaque(false);
			selected = false;
		}

		// Makes sure the superclass' paint method paints doesn't paint the
		// label in case we use a rich text box internally to paint the text.
		if (isRichText)
			setText("");

		// Calls the superclass paint method and restores the previous border
		// and selection state.
		super.paint(g);
		setBorder(previousBorder);
		setOpaque(wasOpaque);
		selected = wasSelected;

		// Paints the rich text foreground which is not painted by the
		// superclass. The code below applies the vertical aligment of
		// this renderer to the painted rich text by translating the
		// graphics by the required amount. This is trick to implement
		// vertical alignment in rich text panes which is otherwise
		// not supported.
		if (isRichText)
			paintRichText(g);

		// Finished painting of all content within the shape clipping region so
		// restore the original clipping region.
		g.setClip(previousShape);

		// Paints the border for all non-rectangular shapes using the geometry
		// defined for painting the background.
		Graphics2D g2 = (Graphics2D) g;
		Stroke previousStroke = g2.getStroke();
		if (shape != SHAPE_RECTANGLE && getBorder() != null) {
			g.setColor(bordercolor);
			g2.setStroke(new BasicStroke(b));
			paintShapeBorder(g);
		}

		// Paints the selection border for all non-rectangular shapes using the
		// geometry defined for painting the background.
		if (selected || childrenSelected) {
			if (childrenSelected)
				g.setColor(gridColor);
			else
				g.setColor(highlightColor);
			g2.setStroke(GraphConstants.SELECTION_STROKE);
			paintShapeBorder(g);
		}

		// Restores the previous stroke and paints the folding icon
		g2.setStroke(previousStroke);
		if (showFoldingIcons)
			paintFoldingIcon(g);
	}

	/**
	 * Utility method to paint the background for all non-rectangular shapes.
	 * 
	 * @param g
	 *            The graphics to paint the background to.
	 */
	protected void paintBackground(Graphics g) {
		Dimension d = getSize();
		int b = borderWidth;

		// Paints the background if the shape is not a rectangle.
		// Rectangles are handled by the superclass' paint method.
		if (shape != SHAPE_RECTANGLE) {

			// Prepares the shapes geometries for painting the background
			// and border. This does not do any actual painting but
			// constructs the mathematical diamond shape.
			if (shape == SHAPE_DIAMOND) {
				int width = d.width - b;
				int height = d.height - b;
				int halfWidth = (d.width - b) / 2;
				int halfHeight = (d.height - b) / 2;
				int[] xpoints = { halfWidth, width, halfWidth, 0 };
				int[] ypoints = { 0, halfHeight, height, halfHeight };
				diamond = new Polygon(xpoints, ypoints, 4);
			}

			if (shape == SHAPE_TRIANGLE) {
				int width = d.width - b;
				int height = d.height - b;
				int halfHeight = (d.height - b) / 2;
				int[] xpoints = { 0, width, 0 };
				int[] ypoints = { 0, halfHeight, height };
				diamond = new Polygon(xpoints, ypoints, 3);
			}
			
			// Computes the area for the cylinder (db-style)
			else if (shape == SHAPE_CYLINDER) {
				int h4 = (int) (d.getHeight() / 4);
				int r = d.width - b - 1;
				cylinderArea = new Area(new Rectangle(b, (h4 - b) / 2 + b, r,
						d.height - h4 - b));
				cylinderArea
						.add(new Area(new Ellipse2D.Double(b, b, r, h4 - b)));
				cylinderArea.add(new Area(new Ellipse2D.Double(b, d.height - h4
						- b, r, h4)));
			}

			// Computes the rounded rect arc for rounded rectangles. This
			// is expensive sothe result is cached.
			else if (shape == SHAPE_ROUNDED)
				roundRectArc = getArcSize(d.width - b, d.height - b);

			// Paints the gradient background or filled background by
			// using the shape geometries created above and the colors
			// that were set in installAttributes.
			if (isOpaque()) {
				g.setColor(super.getBackground());

				// Paints the gradient background only if we're not in
				// preview mode to speedup the previews.
				if (gradientColor != null && !preview)
					((Graphics2D) g).setPaint(new GradientPaint(0, 0,
							getBackground(), getWidth(), getHeight(),
							gradientColor, true));

				// Paints the actual background using the proper shapes.
				// Painting the rectangle background is handled by the
				// superclass' paint method.
				if (shape == SHAPE_CIRCLE)
					g.fillOval(b - 1, b - 1, d.width - b, d.height - b);
				else if (shape == SHAPE_CYLINDER)
					((Graphics2D) g).fill(cylinderArea);
				else if (shape == SHAPE_DIAMOND || shape == SHAPE_TRIANGLE)
					g.fillPolygon(diamond);
				else if (shape == SHAPE_ROUNDED)
					g.fillRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
							d.height - (int) (b * 1.5), roundRectArc,
							roundRectArc);
			}
		}
	}

	/**
	 * Utility method to paint the rich text content for rich text values. This
	 * implementation simulates rich text vertical alignment by translating the
	 * graphics before painting the textPane.
	 * 
	 * @param g
	 *            The graphics to paint the rich text content to.
	 */
	protected void paintRichText(Graphics g) {
		textPane.setSize(getSize());
		int yoffset = 0;

		// Computes the vertical offset to match the vertical alignment
		if (getVerticalAlignment() == CENTER)
			yoffset = (int) ((getHeight() - textPane.getPreferredSize()
					.getHeight()) / 2)
					+ 2 * INSET;
		else if (getVerticalAlignment() == BOTTOM)
			yoffset = (int) (getHeight()
					- textPane.getPreferredSize().getHeight() + 3 * INSET);
		g.translate(0, yoffset);
		textPane.paint(g);
		g.translate(0, -yoffset);
	}

	/**
	 * Utility method to paint the border for all non-rectangular shapes.
	 * 
	 * @param g
	 *            The graphics to paint the border to.
	 */
	protected void paintShapeBorder(Graphics g) {
		Dimension d = getSize();
		int b = borderWidth;
		if (shape == SHAPE_CIRCLE)
			g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
		else if (shape == SHAPE_CYLINDER) {
			int h4 = (int) (d.getHeight() / 4);
			int r = d.width - b - 1;
			g.drawOval(b, b, r, h4 - b);
			g.drawLine(b, (h4 - b) / 2 + 2 + b, b, d.height - (h4 - b) / 2 - 2
					- b);
			g.drawLine(d.width - (b + 1) / 2, (h4 - b) / 2 + 2 + b, d.width
					- (b + 1) / 2, d.height - (h4 - b) / 2 - 2 - b);
			g
					.drawArc(b, d.height - h4 - b, r, h4, 0,
							(isOpaque()) ? -180 : 360);
		} else if (shape == SHAPE_DIAMOND || shape == SHAPE_TRIANGLE)
			g.drawPolygon(diamond);
		else if (shape == SHAPE_ROUNDED)
			g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5) - 1,
					d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
	}

	/**
	 * Utility method to paint the folding icon for groups.
	 * 
	 * @param g
	 *            The graphics to paint the border to.
	 */
	protected void paintFoldingIcon(Graphics g) {
		if (isGroup) {
			g.setColor(graphBackground);
			g.fill3DRect(handle.x, handle.y, handle.width, handle.height, true);
			g.setColor(graphForeground);
			g.drawRect(handle.x, handle.y, handle.width, handle.height);
			int h2 = handle.y + handle.height / 2;
			g.drawLine(handle.x + 1, h2, handle.x + handle.width - 2, h2);
			if (view.isLeaf()) {
				int w2 = handle.x + handle.width / 2;
				g.drawLine(w2, handle.y + 1, w2, handle.y + handle.height - 2);
			}
		}
	}

	/**
	 * Returns an appropriate arc for the corners of the rectangle for boundary
	 * size cases of width and height. The arc width of a rectangle is 1/5th of
	 * the larger of the two of the dimensions passed in, but at most 1/2 of the
	 * smaller of the two. 1/5 because it looks nice and 1/2 so the arc can
	 * complete in the given dimension
	 * 
	 * @param width
	 *            The width to compute the arc size for.
	 * @param height
	 *            The height to compute the arc size for.
	 * @return Returns the arc size.
	 */
	public static int getArcSize(int width, int height) {
		int arcSize;
		if (width <= height) {
			arcSize = height / 5;
			if (arcSize > (width / 2))
				arcSize = width / 2;
		} else {
			arcSize = width / 5;
			if (arcSize > (height / 2))
				arcSize = height / 2;
		}
		return arcSize;
	}

	/**
	 * Overrides the parent's implementation to return the perimeter points for
	 * non-rectangular shapes, namely diamonds and circles. The source point is
	 * typically ignored and the center point is used instead.
	 * 
	 * @param view
	 *            The view to return the perimeter point for.
	 * @param source
	 *            The location of the start point of the line to be intersected
	 *            with the boundaries.
	 * @param p
	 *            The location of the end point of the line to be intersected
	 *            with the boundaries.
	 */
	public Point2D getPerimeterPoint(VertexView view, Point2D source, Point2D p) {
		int shape = CellConstants.getVertexShape(view
				.getAllAttributes());
		if (shape == SHAPE_DIAMOND) {
			return getDiamondPerimeterPoint(view, source, p);
		} else if (shape == SHAPE_CIRCLE) {
			return getCirclePerimeterPoint(view, source, p);
		} else if (shape == SHAPE_TRIANGLE) {
			return getTrianglePerimeterPoint(view, source, p);
		}
		return super.getPerimeterPoint(view, source, p);
	}

	/**
	 * Utility method to return the perimeter point for a circle.
	 * 
	 * @param view
	 *            The view that defines the bounds of the circle.
	 * @param source
	 *            The start point of theline to intersect with the circle.
	 * @param p
	 *            The end point of the line to intersect with the circle.
	 * @return The interaction of the circle and the line between source and p.
	 */
	public Point2D getCirclePerimeterPoint(VertexView view, Point2D source,
			Point2D p) {
		Rectangle2D r = view.getBounds();

		double x = r.getX();
		double y = r.getY();
		double a = (r.getWidth() + 1) / 2;
		double b = (r.getHeight() + 1) / 2;

		// x0,y0 - center of ellipse
		double x0 = x + a;
		double y0 = y + b;

		// x1, y1 - point
		double x1 = p.getX();
		double y1 = p.getY();

		// Calculates straight line equation through point and ellipse center
		// y = d * x + h
		double dx = x1 - x0;
		double dy = y1 - y0;

		if (dx == 0)
			return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));

		double d = dy / dx;
		double h = y0 - d * x0;

		// Calculates intersection
		double e = a * a * d * d + b * b;
		double f = -2 * x0 * e;
		double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

		double det = Math.sqrt(f * f - 4 * e * g);

		// Two solutions (perimeter points)
		double xout1 = (-f + det) / (2 * e);
		double xout2 = (-f - det) / (2 * e);
		double yout1 = d * xout1 + h;
		double yout2 = d * xout2 + h;

		double dist1 = Math.sqrt(Math.pow((xout1 - x1), 2)
				+ Math.pow((yout1 - y1), 2));
		double dist2 = Math.sqrt(Math.pow((xout2 - x1), 2)
				+ Math.pow((yout2 - y1), 2));

		// Correct solution
		double xout, yout;

		if (dist1 < dist2) {
			xout = xout1;
			yout = yout1;
		} else {
			xout = xout2;
			yout = yout2;
		}

		return new Point2D.Double(xout, yout);
	}

	/**
	 * Utility method to return the perimeter point for a diamond.
	 * 
	 * @param view
	 *            The view that defines the bounds of the diamond.
	 * @param source
	 *            The start point of theline to intersect with the diamond.
	 * @param p
	 *            The end point of the line to intersect with the diamond.
	 * @return The interaction of the diamond and the line between source and p.
	 */
	public Point2D getDiamondPerimeterPoint(VertexView view, Point2D source,
			Point2D p) {
		Rectangle2D bounds = view.getBounds();
		Point2D center = AbstractCellView.getCenterPoint(view);
		double halfwidth = bounds.getWidth() / 2;
		double halfheight = bounds.getHeight() / 2;
		Point2D top = new Point2D.Double(center.getX(), center.getY()
				- halfheight);
		Point2D bottom = new Point2D.Double(center.getX(), center.getY()
				+ halfheight);
		Point2D left = new Point2D.Double(center.getX() - halfwidth, center
				.getY());
		Point2D right = new Point2D.Double(center.getX() + halfwidth, center
				.getY());

		// Special case for intersecting the diamond's points
		if (center.getX() == p.getX())
			if (center.getY() > p.getY()) // top point
				return (top);
			else
				return (bottom); // bottom point
		if (center.getY() == p.getY())
			if (center.getX() > p.getX()) // left point
				return (left);
			else
				return (right); // right point

		// In which quadrant will the intersection be?
		// set the slope and offset of the border line accordingly
		Point2D i;
		if (p.getX() < center.getX())
			if (p.getY() < center.getY())
				i = intersection(p, center, top, left);
			else
				i = intersection(p, center, bottom, left);
		else if (p.getY() < center.getY())
			i = intersection(p, center, top, right);
		else
			i = intersection(p, center, bottom, right);
		return i;
	}

	/**
	 * Utility method to return the perimeter point for a triangle.
	 * 
	 * @param view
	 *            The view that defines the bounds of the diamond.
	 * @param source
	 *            The start point of theline to intersect with the diamond.
	 * @param p
	 *            The end point of the line to intersect with the diamond.
	 * @return The interaction of the diamond and the line between source and p.
	 */
	public Point2D getTrianglePerimeterPoint(VertexView view, Point2D source,
			Point2D p) {
		Rectangle2D bounds = view.getBounds();

		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		Point2D center = AbstractCellView.getCenterPoint(view);
		Point2D top = new Point2D.Double(x, y);
		Point2D bottom = new Point2D.Double(x, y + height);
		Point2D right = new Point2D.Double(x + width, yCenter);

		// Compute angle
		double dx = p.getX() - xCenter;
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double t = Math.atan2(height, width);
		Point2D i;
		if (alpha < -Math.PI + t || alpha > Math.PI - t) { // Left edge
			i = new Point2D.Double(x, yCenter - width * Math.tan(alpha) / 2);
		} else if (yCenter > p.getY()) { // Top Slope
			i = intersection(p, center, top, right);
		} else { // Bottom Slope
			i = intersection(p, center, bottom, right);
		}
		return i;
	}

	/**
	 * Find the point of intersection of two straight lines (which follow the
	 * equation y=mx+b) one line is an incoming edge and the other is one side
	 * of the diamond.
	 * 
	 * @param lineOneStart
	 *            The start point of the first line.
	 * @param lineOneEnd
	 *            The end point of the first line.
	 * @param lineTwoStart
	 *            The start point of the second line.
	 * @param lineTwoEnd
	 *            The end point of the second line.
	 * @return Returns the intersection point between the first and the second
	 *         line.
	 */
	protected Point2D intersection(Point2D lineOneStart, Point2D lineOneEnd,
			Point2D lineTwoStart, Point2D lineTwoEnd) {
		// m = delta y / delta x, the slope of a line
		// b = y - mx, the axis intercept
		double m1 = (double) (lineOneEnd.getY() - lineOneStart.getY())
				/ (double) (lineOneEnd.getX() - lineOneStart.getX());
		double b1 = lineOneStart.getY() - m1 * lineOneStart.getX();
		double m2 = (double) (lineTwoEnd.getY() - lineTwoStart.getY())
				/ (double) (lineTwoEnd.getX() - lineTwoStart.getX());
		double b2 = lineTwoStart.getY() - m2 * lineTwoStart.getX();
		double xinter = (b1 - b2) / (m2 - m1);
		double yinter = m1 * xinter + b1;
		Point2D intersection = new Point2D.Double(xinter, yinter);
		return intersection;
	}

	/**
	 * Overrides the parent's implementation to return a slightly larger
	 * preferred size for circles and rounded rectangles.
	 * 
	 * @return Returns the preferreds size for the current view.
	 */
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		if (shape == SHAPE_CIRCLE) {
			d.width += d.width / 8;
			d.height += d.height / 2;
		} else if (shape == SHAPE_ROUNDED)
			d.width += d.height / 5;
		else if (isRichText) {
			textPane.setSize(ZERO_DIMENSION);
			return textPane.getPreferredSize();
		} else if (valueComponent != null)
			return valueComponent.getPreferredSize();
		return d;
	}

	/**
	 * Resets attributes that would affect rendering if the
	 * {@link #installAttributes(CellView)} is not being called, which is the
	 * case if the view is a group and it's groupOpaque attribute is set to
	 * false.
	 */
	protected void resetAttributes() {
		super.resetAttributes();
		shape = CellConstants.getVertexShape(view.getAllAttributes());
		isRichText = false;
		valueComponent = null;
	}

	/**
	 * Extends the parent's method to configure the renderer for displaying the
	 * specified view.
	 * 
	 * @param view
	 *            The view to configure the renderer for.
	 */
	public void installAttributes(CellView view) {
		super.installAttributes(view);
		Map map = view.getAllAttributes();
		shape = CellConstants.getVertexShape(view.getAllAttributes());
		stretchImage = CellConstants.isStretchImage(map);

		// Adds the inset as an empty border to the existing border.
		int i = GraphConstants.getInset(map);
		Border insetBorder = (i > 0) ? BorderFactory.createEmptyBorder(i, i, i,
				i) : null;
		if (insetBorder != null) {
			if (getBorder() == null)
				setBorder(insetBorder);
			else
				setBorder(BorderFactory.createCompoundBorder(getBorder(),
						insetBorder));
		}

		// Configures the rich text or component value
		userObject = graph.getModel().getValue(view.getCell());
		if (userObject instanceof RichTextBusinessObject) {
			RichTextBusinessObject obj = (RichTextBusinessObject) userObject;
			isRichText = obj.isRichText();
			valueComponent = (obj.isComponent()) ? (Component) obj.getValue()
					: null;
		} else {
			isRichText = false;
			valueComponent = null;
		}

		// Configures the rich text box for rendering the rich text
		if (isRichText) {
			StyledDocument document = (StyledDocument) textPane.getDocument();
			((RichTextValue) ((RichTextBusinessObject) userObject)
					.getValue()).insertInto(document);

			// Applies the inset to the rich text renderer
			if (insetBorder != null)
				textPane.setBorder(insetBorder);
			else
				textPane.setBorder(BorderFactory.createEmptyBorder(INSET,
						INSET, INSET, INSET));

			// Uses the label's alignment and sets it on the text pane to work
			// around the problem of the text pane alignments not being stored.
			// Note: As a consequence a text pane can only have one alignment
			// for all text it contains. It is not possible to align the
			// paragraphs individually.
			int align = getHorizontalAlignment();
			SimpleAttributeSet sas = new SimpleAttributeSet();
			align = (align == JLabel.CENTER) ? StyleConstants.ALIGN_CENTER
					: (align == JLabel.RIGHT) ? StyleConstants.ALIGN_RIGHT
							: StyleConstants.ALIGN_LEFT;
			StyleConstants.setAlignment(sas, align);
			document.setParagraphAttributes(0, document.getLength(), sas, true);
		}
	}

	/**
	 * Detects whether or not a point has hit the folding icon. This
	 * implementation never returns true if the
	 * {@link #CLIENTPROPERTY_SHOWFOLDINGICONS} is not set on the enclosing
	 * graph.
	 * 
	 * @param pt
	 *            The point to check
	 * @return Returns true if <code>pt</code> intersects with the folding
	 *         icon.
	 */
	public boolean inHitRegion(Point2D pt) {
		if (showFoldingIcons)
			return handle.contains(Math.max(0, pt.getX() - 1), Math.max(0, pt
					.getY() - 1));
		return false;
	}

}
