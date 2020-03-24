package org.apromore.processmining.models.jgraph.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apromore.jgraph.graph.DefaultPort;
import org.apromore.jgraph.graph.PortRenderer;
import org.apromore.jgraph.graph.PortView;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.graphbased.directed.BoundaryDirectedGraphNode;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.views.JGraphPortView;
import org.apromore.processmining.models.jgraph.views.JGraphShapeView;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Shape;

public class ProMPortRenderer extends PortRenderer implements Cleanable {

	private static final long serialVersionUID = -4077623888811778878L;

	private static LabelRenderer renderer = new LabelRenderer();

	public ProMPortRenderer() {
		super();
	}

	public void cleanUp() {
		view = null;
	}

	public void paint(Graphics g) {
		renderer.setSize(getWidth(), getHeight());
		renderer.paint(view, selected, g);
	}

	public Point2D getPerimeterPoint(PortView view, Point2D source, Point2D p) {
		return renderer.getPerimeterPoint(view, source, p);

	}
}

class LabelRenderer extends JLabel {

	private static final long serialVersionUID = 4310963545024487311L;

	public void paint(PortView view, boolean selected, Graphics g) {
		Object representedObject = ((DefaultPort) view.getCell()).getUserObject();
		if ((representedObject instanceof BoundaryDirectedGraphNode) ? ((BoundaryDirectedGraphNode) representedObject)
				.getBoundingNode() != null : false) {
			BoundaryDirectedGraphNode node = (BoundaryDirectedGraphNode) representedObject;
			Dimension d = getSize();
			ViewSpecificAttributeMap map = ((JGraphPortView) view).getViewSpecificAttributeMap();

			if (!map.get(node, AttributeMap.SHOWLABEL, true) || ((JGraphPortView) view).isPIP()) {
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
			if (icon != null && icon instanceof ImageIcon) {
				Image image = ((ImageIcon) icon).getImage();
				if (icon.getIconHeight() > d.height || icon.getIconWidth() > d.width) {
					image = image.getScaledInstance(d.height, d.width, Image.SCALE_SMOOTH);
				}
				icon = new ImageIcon(image);
			}
			if (!((JGraphPortView) view).isPIP()) {
				setIcon(icon);
			} else {
				setIcon(null);
			}

			int b = map.get(node, AttributeMap.BORDERWIDTH, 1);
			Graphics2D g2 = (Graphics2D) g;

			boolean tmp = selected;

			GeneralPath path = shape.getPath(b, b, d.width - 2 * b, d.height - 2 * b);

			Color fill = (Color) map.get(node, AttributeMap.FILLCOLOR);
			g.setColor(fill);
			setOpaque(fill != null);
			if (fill != null) {
				g2.fill(path);
			}
			//g.setColor(Color.BLACK);
			g.setColor(map.get(node, AttributeMap.STROKECOLOR, Color.BLACK));
			//		g2.setStroke(new BasicStroke(borderWidth));
			float[] pattern = map.get(node, AttributeMap.DASHPATTERN, new float[0]);
			if (pattern.length > 0f) {

				float offset = map.get(node, AttributeMap.DASHOFFSET, 0f);
				g2.setStroke(new BasicStroke(b, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, pattern, offset));
			} else {
				g2.setStroke(new BasicStroke(b));
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
			} finally {
				selected = tmp;
			}
		}
	}

	public Point2D getPerimeterPoint(PortView view, Point2D source, Point2D p) {
		Rectangle2D bounds = view.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
	}
}