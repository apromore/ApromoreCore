package org.processmining.models.shapes;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class AbstractShape implements Shape {

	protected Point2D intersection(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {

		double ua_t = (b2.getX() - b1.getX()) * (a1.getY() - b1.getY()) - (b2.getY() - b1.getY())
				* (a1.getX() - b1.getX());
		double ub_t = (a2.getX() - a1.getX()) * (a1.getY() - b1.getY()) - (a2.getY() - a1.getY())
				* (a1.getX() - b1.getX());
		double u_b = (b2.getY() - b1.getY()) * (a2.getX() - a1.getX()) - (b2.getX() - b1.getX())
				* (a2.getY() - a1.getY());

		if (u_b != 0) {
			double ua = ua_t / u_b;
			double ub = ub_t / u_b;

			if ((0 <= ua) && (ua <= 1) && (0 <= ub) && (ub <= 1)) {
				return new Point2D.Double(a1.getX() + ua * (a2.getX() - a1.getX()), a1.getY() + ua
						* (a2.getY() - a1.getY()));
			}
		}
		return null;
	}

	public Point2D getPerimeterPoint(Rectangle2D bounds, Point2D source, Point2D p) {

		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth() - 1;
		double height = bounds.getHeight() - 1;

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
		if ((alpha < -pi + t) || (alpha > pi - t)) { // Left edge
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
