package org.processmining.models.shapes;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Rectangle extends AbstractShape {

	private final boolean rounded;

	public Rectangle() {
		this(false);
	}

	public Rectangle(boolean rounded) {
		this.rounded = rounded;
	}

	public GeneralPath getPath(double x, double y, double width, double height) {

		java.awt.Shape rect;
		if (rounded) {
			double m = Math.max(width, height) * .125;
			rect = new RoundRectangle2D.Double(x, y, width - 1, height - 1, m, m);
		} else {
			rect = new Rectangle2D.Double(x, y, width - 1, height - 1);
		}
		// Width and height have correct ratio;
		GeneralPath path = new GeneralPath();
		path.append(rect, false);
		return path;

	}

}
