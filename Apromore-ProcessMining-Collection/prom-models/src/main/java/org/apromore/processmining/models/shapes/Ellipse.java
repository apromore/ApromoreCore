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

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Ellipse implements Shape {

	public Ellipse() {
	}

	public GeneralPath getPath(double x, double y, double width, double height) {
		GeneralPath path = new GeneralPath();

		Ellipse2D e = new Ellipse2D.Double(x, y, width - 1, height - 1);

		path.append(e, false);

		return path;
	}

	public Point2D getPerimeterPoint(Rectangle2D bounds, Point2D source, Point2D p) {

		double x = bounds.getX();
		double y = bounds.getY();
		double a = (bounds.getWidth() - 1) / 2;
		double b = (bounds.getHeight() - 1) / 2;

		// x0,y0 - center of ellipse
		double x0 = x + a;
		double y0 = y + b;

		// x1, y1 - point
		double x1 = p.getX();
		double y1 = p.getY();

		// calculate straight line equation through point and ellipse center
		// y = d * x + h
		double dx = x1 - x0;
		double dy = y1 - y0;

		if (dx == 0) {
			return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));
		}

		double d = dy / dx;
		double h = y0 - d * x0;

		// calculate intersection
		double e = a * a * d * d + b * b;
		double f = -2 * x0 * e;
		double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

		double det = Math.sqrt(f * f - 4 * e * g);

		// two solutions (perimeter points)
		double xout1 = (-f + det) / (2 * e);
		double xout2 = (-f - det) / (2 * e);
		double yout1 = d * xout1 + h;
		double yout2 = d * xout2 + h;

		double dist1Squared = Math.pow((xout1 - x1), 2) + Math.pow((yout1 - y1), 2);
		double dist2Squared = Math.pow((xout2 - x1), 2) + Math.pow((yout2 - y1), 2);

		// correct solution
		double xout, yout;

		if (dist1Squared < dist2Squared) {
			xout = xout1;
			yout = yout1;
		} else {
			xout = xout2;
			yout = yout2;
		}

		return new Point2D.Double(xout, yout);
	}

}
