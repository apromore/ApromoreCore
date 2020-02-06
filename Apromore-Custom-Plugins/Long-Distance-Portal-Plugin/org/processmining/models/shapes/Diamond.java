package org.processmining.models.shapes;

import java.awt.geom.Point2D;

public class Diamond extends Polygon {

	protected Point2D[] getPoints(double x, double y, double width, double height) {
		Point2D[] points = new Point2D[4];
		points[0] = new Point2D.Double(x, y + (height - 1) / 2);
		points[1] = new Point2D.Double(x + (width - 1) / 2, y);
		points[2] = new Point2D.Double(x + (width - 1), y + (height - 1) / 2);
		points[3] = new Point2D.Double(x + (width - 1) / 2, y + (height - 1));
		return points;
	}

}
