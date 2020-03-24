package org.apromore.processmining.models.jgraph.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Map;

import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.EdgeRenderer;
import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.views.JGraphEdgeView;

public class ProMEdgeRenderer extends EdgeRenderer implements Cleanable {

	private static final long serialVersionUID = 4470395577059556630L;

	private transient int middleDeco, middleSize;
	private transient boolean middleFill;
	private transient int numLines;
	private transient float lineWidth;

	public void cleanUp() {
		view = null;
	}

	protected void paintLabel(Graphics g, String label, Point2D p, boolean mainLabel) {
		ViewSpecificAttributeMap map = ((JGraphEdgeView) view).getViewSpecificAttributeMap();
		DirectedGraphEdge<?, ?> edge = ((JGraphEdgeView) view).getEdge();
		if (map.get(edge, AttributeMap.SHOWLABEL, false) && !((JGraphEdgeView) view).isPIP()) {
			super.paintLabel(g, mainLabel ? map.get(edge, AttributeMap.LABEL, label) : label, p, mainLabel);
		}
	}

	/**
	 * Returns the label size of the specified view in the given graph.
	 */
	public Dimension getLabelSize(EdgeView view, String label) {
		ViewSpecificAttributeMap map = ((JGraphEdgeView) view).getViewSpecificAttributeMap();
		DirectedGraphEdge<?, ?> edge = ((JGraphEdgeView) view).getEdge();
		if (map.get(edge, AttributeMap.SHOWLABEL, false) && !((JGraphEdgeView) view).isPIP()) {
			return super.getLabelSize(view, map.get(edge, AttributeMap.LABEL, ""));
		} else {
			return new Dimension(0, 0);
		}
	}

	@Override
	protected void installAttributes(final CellView view) {
		super.installAttributes(view);
		Map map = view.getAllAttributes();

		//		JGraphEdgeView v = (JGraphEdgeView) view;
		//		ViewSpecificAttributeMap vMap = v.getViewSpecificAttributeMap();
		//		DirectedGraphEdge<?, ?> edge = v.getEdge();

		lineWidth = (Float) map.get(ProMGraphEdge.LINEWIDTH);

		numLines = (Integer) map.get(ProMGraphEdge.NUMBERLINES);
		middleDeco = (Integer) map.get(ProMGraphEdge.LINEMIDDLE);
		middleSize = 20;
		middleFill = (Boolean) map.get(ProMGraphEdge.MIDDLEFILL) && isFillable(middleDeco);

	}

	@Override
	protected void paintEdge(Graphics g) {
		ViewSpecificAttributeMap map = ((JGraphEdgeView) view).getViewSpecificAttributeMap();
		Color foreground = map.get(((JGraphEdgeView) view).getEdge(), AttributeMap.EDGECOLOR, getForeground());
		setForeground((foreground != null) ? foreground : defaultForeground);
		lineWidth = map.get(((JGraphEdgeView) view).getEdge(), AttributeMap.LINEWIDTH, lineWidth);

		g.setColor(getForeground());
		if (lineWidth > 0) {
			Graphics2D g2 = (Graphics2D) g;
			int c = BasicStroke.CAP_BUTT;
			int j = BasicStroke.JOIN_MITER;
			g2.setStroke(new BasicStroke(lineWidth, c, j));
			if (gradientColor != null && !preview) {
				g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), gradientColor, true));
			}
			if (lineDash != null) // Dash For Line Only
				g2.setStroke(new BasicStroke(lineWidth, c, j, 10.0f, lineDash, dashOffset));
			if (view.lineShape != null) {
				Color color = getForeground();
				drawLine(g2, c, j, color);
			}
			g2.setStroke(new BasicStroke(lineWidth, c, j));
			g2.setColor(getForeground());
			if (view.beginShape != null) {
				if (beginFill) {
					g2.fill(view.beginShape);
					g2.draw(view.beginShape);
				} else {
					g2.setColor(getBackground());
					g2.fill(view.beginShape);
					g2.setColor(getForeground());
					g2.draw(view.beginShape);
				}
			}
			if (view.endShape != null) {
				if (endFill) {
					g2.fill(view.endShape);
					g2.draw(view.endShape);
				} else {
					g2.setColor(getBackground());
					g2.fill(view.endShape);
					g2.setColor(getForeground());
					g2.draw(view.endShape);
				}
			}

			if (((JGraphEdgeView) view).middleShape != null) {
				if (middleFill) {
					g2.fill(((JGraphEdgeView) view).middleShape);
					g2.draw(((JGraphEdgeView) view).middleShape);
				} else {
					g2.setColor(getBackground());
					g2.fill(((JGraphEdgeView) view).middleShape);
					g2.setColor(getForeground());
					g2.draw(((JGraphEdgeView) view).middleShape);
				}
			}

		}
	}

	protected void drawLine(Graphics2D g2, int c, int j, Color color) {
		if (numLines == 1) {
			g2.draw(view.lineShape);
		} else {
			for (int i = 2 * numLines - 1; i > 0; i -= 2) {
				if (lineDash != null) // Dash For Line Only
					g2.setStroke(new BasicStroke(lineWidth * i, c, j, 10.0f, lineDash, dashOffset));
				else
					g2.setStroke(new BasicStroke(lineWidth * i, c, j));
				g2.setColor(color);
				g2.draw(view.lineShape);
				color = switchColor(color);
			}
		}
	}

	@Override
	protected void paintSelection(Graphics g) {
		if (selected) { // Paint Selected
			Graphics2D g2 = (Graphics2D) g;
			int c = BasicStroke.CAP_BUTT;
			int j = BasicStroke.JOIN_MITER;
			g2.setStroke(GraphConstants.SELECTION_STROKE);
			g2.setColor(highlightColor);
			if (view.beginShape != null)
				g2.draw(view.beginShape);
			if (view.lineShape != null)
				//				g2.draw(view.lineShape);
				drawLine(g2, c, j, highlightColor);
			if (view.endShape != null)
				g2.draw(view.endShape);
			//			if (((JGraphEdgeView) view).middleShape != null) {
			//				g2.draw(((JGraphEdgeView) view).middleShape);
			//			}
		}
	}

	private Color switchColor(final Color color) {
		if (color != null) {
			if (color.equals(getForeground())) {
				return getBackground();
			} else {
				return getForeground();
			}
		}
		return getForeground();
	}

	/**
	 * Paint the current view's direction. Sets tmpPoint as a side-effect such
	 * that the invoking method can use it to determine the connection point to
	 * this decoration.
	 * 
	 * @param size
	 *            int
	 * @param style
	 *            int
	 * @param src
	 *            Point2D
	 * @param dst
	 *            Point2D
	 * @return Shape
	 */
	@Override
	protected Shape createLineEnd(final int size, final int style, final Point2D src, final Point2D dst) {
		if (src == null || dst == null) {
			return null;
		}

		switch (style) {
			case ProMGraphEdge.ARROW_TECHNICAL_CIRCLE :
				final Area areaCircle = new Area(super.createLineEnd(size, GraphConstants.ARROW_CIRCLE, src, dst));
				final Shape arrow = super.createLineEnd(size, GraphConstants.ARROW_TECHNICAL, src, dst);
				final Area areaPoly = new Area(arrow);
				areaCircle.add(areaPoly);
				return areaCircle;
			case ProMGraphEdge.ARROW_CROSS :
				final GeneralPath path = new GeneralPath(Path2D.WIND_NON_ZERO, 4);
				path.moveTo((float) (dst.getX() + 5), (float) (dst.getY() + 5));
				path.lineTo((float) (dst.getX() - 5), (float) (dst.getY() - 5));
				path.moveTo((float) (dst.getX() + 5), (float) (dst.getY() - 5));
				path.lineTo((float) (dst.getX() - 5), (float) (dst.getY() + 5));
				return path;
			default :
				return super.createLineEnd(size, style, src, dst);
		}
	}

	@Override
	protected Shape createShape() {
		if (middleDeco != GraphConstants.ARROW_NONE) {
			if (view.getPoints().size() > 1) {
				//				final Point2D p1 = view.getPoint(view.getPoints().size() / 2 - 1);
				//				final Point2D p2;
				//				if ((view.getPoints().size() & 1) == 0) {
				//					// even number of points
				//					p2 = new Point2D.Double((p1.getX() + view.getPoint(view.getPoints().size() / 2).getX()) / 2,
				//							(p1.getY() + view.getPoint(view.getPoints().size() / 2).getY()) / 2);
				//				} else {
				//					p2 = view.getPoint(view.getPoints().size() / 2);
				//				}
				Point2D p1 = view.getPoint(0);
				Point2D p2 = view.getPoint(view.getPoints().size() - 1);
				Point2D p3 = new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / 2, p1.getY()
						+ (p2.getY() - p1.getY()) / 2);

				int pi = -1;
				double min = Double.MAX_VALUE;
				for (int i = 1; i < view.getPointCount() - 1; i++) {
					if (view.getPoint(i).distance(p3) < min) {
						min = view.getPoint(i).distance(p3);
						pi = i;
						p1 = view.getPoint(i - 1);
					}
				}
				if (pi >= 0)
					p3 = view.getPoint(pi);

				// We draw at p3 in the direction of p1

				((JGraphEdgeView) view).middleShape = createLineEnd(middleSize, middleDeco, p1, p3);
			}
		}
		return super.createShape();
	}
}
