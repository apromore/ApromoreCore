package org.processmining.models.shapes;

import java.awt.geom.Point2D;

public class Octagon extends Polygon {

	private final double cornerOffset;

	public Octagon(double cornerOffset) {
		this.cornerOffset = cornerOffset;
	}

	protected Point2D[] getPoints(double x, double y, double width, double height) {
		Point2D[] points = new Point2D[8];
		double offset = width * cornerOffset;

		points[0] = new Point2D.Double(x + offset, y);
		points[1] = new Point2D.Double(x, y + (height - 1) / 3);
		points[2] = new Point2D.Double(x, y + 2 * (height - 1) / 3);
		points[3] = new Point2D.Double(x + offset, y + height - 1);
		points[4] = new Point2D.Double(x + width - 1 - offset, y + height - 1);
		points[5] = new Point2D.Double(x + width - 1, y + 2 * (height - 1) / 3);
		points[6] = new Point2D.Double(x + width - 1, y + (height - 1) / 3);
		points[7] = new Point2D.Double(x + width - 1 - offset, y);
		return points;
	}

}