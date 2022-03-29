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
