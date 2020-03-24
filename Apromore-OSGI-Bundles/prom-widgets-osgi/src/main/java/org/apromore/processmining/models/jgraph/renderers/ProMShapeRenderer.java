package org.apromore.processmining.models.jgraph.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.VertexRenderer;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.views.JGraphShapeView;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Shape;

// ************************************************************************
public class ProMShapeRenderer extends VertexRenderer implements Cleanable {

	static {
		// Change the default font
		GraphConstants.DEFAULTFONT = GraphConstants.DEFAULTFONT.deriveFont(Font.PLAIN);
		GraphConstants.DEFAULTFONT = GraphConstants.DEFAULTFONT.deriveFont(9.0f);
		GraphConstants.DEFAULTFONT = GraphConstants.DEFAULTFONT.deriveFont(AffineTransform.getScaleInstance(0.75, 1.0));
	}

	private static final long serialVersionUID = 9118304969661088440L;
	private static final Stroke SELECTIONSTROKE = new BasicStroke(2f);

	/**
	 * Return a slightly larger preferred size than for a rectangle.
	 */
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.setSize(d.getWidth() * 1.4, d.getHeight() * 1.4);
		return d;
	}

	/**
	 */
	public void paint(Graphics g) {
		ViewSpecificAttributeMap map = ((JGraphShapeView) view).getViewSpecificAttributeMap();

		highlightColor = highlightColor != null ? highlightColor : Color.ORANGE;
		lockedHandleColor = lockedHandleColor != null ? lockedHandleColor : Color.RED;
		DirectedGraphNode node = ((JGraphShapeView) view).getNode();
		//		Dimension d = (Dimension) map.get(node,AttributeMap.SIZE);
		//		d.setSize(d.getWidth() * 1.4, d.getHeight() * 1.4);

		Dimension d = (Dimension) map.get(node, AttributeMap.SIZE);
		if (d == null) {
			d = getSize();
		} else {
			Rectangle2D bounds = view.getBounds();
			if (bounds.getWidth() != d.getWidth() || bounds.getHeight() != d.getHeight()) {
				view.setBounds(new Rectangle2D.Double(bounds.getX(), bounds.getY(), d.getWidth(), d.getHeight()));
			}
			setSize(d);
		}

		//assert(d.equals(node.getAttributeMap().get(AttributeMap.SIZE)));

		if (!map.get(node, AttributeMap.SHOWLABEL, true) || ((JGraphShapeView) view).isPIP()) {
			setText(null);
		} else {
			setVerticalAlignment(map.get(node, AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP));
			setHorizontalAlignment(map.get(node, AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER));
			String text = map.get(node, AttributeMap.LABEL, getText());
			if (!text.toLowerCase().startsWith("<html>")) {
				text = "<html>" + text + "</html>";
			}
			setText(text);
		}

		Shape shape = map.get(node, AttributeMap.SHAPE, JGraphShapeView.RECTANGLE);
		Decorated shapeDecorator = map.get(node, AttributeMap.SHAPEDECORATOR, null);
		if (shapeDecorator == null && node instanceof Decorated) {
			shapeDecorator = (Decorated) node;
		}

		Icon icon = (Icon) map.get(node, AttributeMap.ICON);
		if ((icon != null) && (icon instanceof ImageIcon)) {
			Image image = ((ImageIcon) icon).getImage();
			if ((icon.getIconHeight() > d.height) || (icon.getIconWidth() > d.width)) {
				image = image.getScaledInstance(d.height, d.width, Image.SCALE_SMOOTH);
			}
			icon = new ImageIcon(image);
		}
		if (!((JGraphShapeView) view).isPIP()) {
			setIcon(icon);
		} else {
			setIcon(null);
		}

		borderWidth = map.get(node, AttributeMap.BORDERWIDTH, borderWidth);
		int b = borderWidth - 1;
		Graphics2D g2 = (Graphics2D) g;

		boolean tmp = selected;

		GeneralPath path = shape.getPath(b, b, d.width - 2 * b, d.height - 2 * b);

		Color fill = (Color) map.get(((JGraphShapeView) view).getNode(), AttributeMap.FILLCOLOR);
		Color gradient = (Color) map.get(((JGraphShapeView) view).getNode(), AttributeMap.GRADIENTCOLOR);
		if (gradient == null || gradient.equals(fill)) {
			g.setColor(fill);
			setOpaque(fill != null);
		} else {
			Paint paint = new GradientPaint(0, 0, fill, d.width, 0, gradient);
			g2.setPaint(paint);
			setOpaque(paint != null);
		}
		if (fill != null) {
			g2.fill(path);
		}
		//g.setColor(Color.BLACK);
		g.setColor(map.get(node, AttributeMap.STROKECOLOR, Color.BLACK));
		//		g2.setStroke(new BasicStroke(borderWidth));
		float[] pattern = map.get(node, AttributeMap.DASHPATTERN, new float[0]);
		if (pattern.length > 0f) {

			float offset = map.get(node, AttributeMap.DASHOFFSET, 0f);
			g2.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, pattern,
					offset));
		} else {
			g2.setStroke(new BasicStroke(borderWidth));
		}

		g2.draw(path);

		try {
			setBorder(null);
			setOpaque(false);
			// selected = false;
			super.paint(g);
			if (shapeDecorator != null) {
				shapeDecorator.decorate(g2, b, b, d.width - 2 * b, d.height - 2 * b);
			}

			//			if (isGroup) {
			//				g.setColor(handleColor);
			//				g.fill3DRect(handle.x, handle.y, handle.width, handle.height, true);
			//				g.setColor(graphForeground);
			//				g.drawRect(handle.x, handle.y, handle.width, handle.height);
			//				g.drawLine(handle.x + 1, handle.y + handle.height / 2, handle.x + handle.width - 2, handle.y
			//						+ handle.height / 2);
			//				if (view.isLeaf()) {
			//					g.drawLine(handle.x + handle.width / 2, handle.y + 1, handle.x + handle.width / 2, handle.y
			//							+ handle.height - 2);
			//				}
			//			}

		} finally {
			selected = tmp;
		}
	}

	protected void paintSelectionBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Stroke previousStroke = g2.getStroke();
		g2.setStroke(SELECTIONSTROKE);
		if (childrenSelected || selected) {
			if (childrenSelected) {
				g.setColor(gridColor);
			} else if (hasFocus && selected) {
				g.setColor(lockedHandleColor);
			} else if (selected) {
				g.setColor(highlightColor);
			}
			Dimension d = getSize();
			g.drawRect(0, 0, d.width - 1, d.height - 1);
		}
		g2.setStroke(previousStroke);
	}

	public void cleanUp() {
		view = null;
	}

	/**
	 * Default handle bounds for renderer, '+' or '-'
	 */
	public static Rectangle handle = new Rectangle(0, 0, 7, 7);

	//	/**
	//	 * Specifies whether the current view is a rich text value, and if the image
	//	 * should be stretched.
	//	 */
	//	protected boolean isGroup = false;

	/**
	 * Holds the background and foreground of the graph.
	 */
	protected Color handleColor = Color.white, graphForeground = Color.black;

	/**
	 * Overrides the parent implementation to return the value component stored
	 * in the user object instead of this renderer if a value component exists.
	 * This applies some of the values installed to this renderer to the value
	 * component (border, opaque) if the latter is a JComponent.
	 * 
	 * @return Returns a configured renderer for the specified view.
	 */
	@Override
	public Component getRendererComponent(JGraph graph, CellView view, boolean sel, boolean focus, boolean preview) {
		handleColor = graph.getHandleColor();
		graphForeground = graph.getForeground();
		return super.getRendererComponent(graph, view, sel, focus, preview);
	}

	/**
	 * Detect whether or not a point has hit the group/ungroup image
	 * 
	 * @param pt
	 *            the point to check
	 * @return whether or not the point lies within the handle
	 */
	public boolean inHitRegion(Point2D pt) {
		return handle.contains(pt.getX(), pt.getY());
	}

}
