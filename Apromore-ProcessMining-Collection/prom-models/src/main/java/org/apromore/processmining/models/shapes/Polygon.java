/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.models.shapes;

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
