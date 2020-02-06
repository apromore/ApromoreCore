package org.processmining.models.shapes;

import java.awt.geom.Point2D;

public class Hexagon extends Polygon {

	private final double cornerOffset;

	public Hexagon(double cornerOffset) {
		this.cornerOffset = cornerOffset;
	}

	protected Point2D[] getPoints(double x, double y, double width, double height) {
		Point2D[] points = new Point2D[6];
		double offset = width * cornerOffset;

		points[0] = new Point2D.Double(x + offset, y);
		points[1] = new Point2D.Double(x, y + (height - 1) / 2);
		points[2] = new Point2D.Double(x + offset, y + height - 1);
		points[3] = new Point2D.Double(x + width - 1 - offset, y + height - 1);
		points[4] = new Point2D.Double(x + width - 1, y + (height - 1) / 2);
		points[5] = new Point2D.Double(x + width - 1 - offset, y);
		return points;
	}

}
