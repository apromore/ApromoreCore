package org.processmining.models.shapes;

import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;

public class Gate extends AbstractShape {

	public final static int RIGHT = 0;
	public final static int TOP = 2;
	public final static int LEFT = 4;
	public final static int BOTTOM = 6;

	private final int direction;

	public Gate(int direction) {
		this.direction = direction;
	}

	public GeneralPath getPath(double x, double y, double width, double height) {

		// Width and height have correct ratio;
		GeneralPath path = new GeneralPath();
		double[] points = new double[] { x, y, x + width, y, x + width, y + height, x, y + height };
		double[] midpts = new double[] { x + width, y + height / 2., x + width / 2., y, x, y + height / 2.,
				x + width / 2., y };

		int d = direction;
		path.moveTo(points[d % 8], points[d % 8]);

		//		path.curveTo(points[d++ % 8], points[d++ % 8], points[d++ % 8], points[d++ % 8], points[d++ % 8],
		//				points[d++ % 8]);

		QuadCurve2D curve = new QuadCurve2D.Double(points[d++ % 8], points[d++ % 8], points[d++ % 8], points[d++ % 8],
				midpts[direction], midpts[direction + 1]);
		path.append(curve, true);

		curve = new QuadCurve2D.Double(midpts[direction], midpts[direction + 1], points[d++ % 8], points[d++ % 8],
				points[d++ % 8], points[d++ % 8]);
		path.append(curve, true);

		path.lineTo(points[d++ % 8], points[d++ % 8]);

		return path;
	}
}
