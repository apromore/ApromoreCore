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
