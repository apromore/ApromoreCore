package org.processmining.models.shapes;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class Polygon extends AbstractShape {

	public Point2D getPerimeterPoint(Rectangle2D bounds, Point2D source, Point2D p) {
		// Use a linde from centerPoint to p, juist like ellipse. This is
		// an "estimate", but the centerpoint is sure to be inside the perimiter

		double x = bounds.getX();
		double y = bounds.getY();
		double height = bounds.getHeight();
		double width = bounds.getWidth();

		// x0,y0 - center of ellipse
		Point2D center = new Point2D.Double(x + (width + 1) / 2, y + (height + 1) / 2);

		Point2D[] points = getPoints(x, y, width, height);

		Point2D point = null;

		for (int i = 0; i < points.length; i++) {
			point = intersection(p, center, points[i], points[(i + 1) % points.length]);
			if (point != null) {
				return point;
			}
		}

		return point;
	}

	protected abstract Point2D[] getPoints(double x, double y, double width, double height);

	public GeneralPath getPath(double x, double y, double width, double height) {
		// Width and height have correct ratio;
		GeneralPath path = new GeneralPath();

		Point2D[] points = getPoints(x, y, width, height);

		path.moveTo((float) points[0].getX(), (float) points[0].getY());
		for (int i = 1; i < points.length; i++) {
			path.lineTo((float) points[i].getX(), (float) points[i].getY());
		}
		path.closePath();
		return path;

	}

}
