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
