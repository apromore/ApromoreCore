package org.apromore.jgraph.io.svg;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.VertexRenderer;
import org.apromore.jgraph.graph.VertexView;

public class SVGVertexRenderer extends VertexRenderer {

		/**
		 * Holds the shape of the current view.
		 */
		protected int shape = 0;

		public Point2D getPerimeterPoint(VertexView view, Point2D source,
				Point2D p) {
			int shape = SVGGraphConstants
					.getShape(view.getAllAttributes());
			if (shape == SVGGraphConstants.SHAPE_ELLIPSE) {
				return getEllipsePerimeterPoint(view, source, p);
			}
			return super.getPerimeterPoint(view, source, p);
		}

		public void installAttributes(CellView view) {
			super.installAttributes(view);
//			Map map = view.getAllAttributes();
			shape = SVGGraphConstants.getShape(view.getAllAttributes());
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
		 * @return The interaction of the circle and the line between source and
		 *         p.
		 */
		public Point2D getEllipsePerimeterPoint(VertexView view,
				Point2D source, Point2D p) {
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

			// Calculates straight line equation through point and ellipse
			// center
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
			double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b
					* b;

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

	}