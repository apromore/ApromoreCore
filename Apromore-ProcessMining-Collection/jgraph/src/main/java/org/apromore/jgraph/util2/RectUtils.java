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
/*
 * $Id: RectUtils.java,v 1.2 2008/02/28 14:38:48 david Exp $
 *
 * Copyright (c) 2008 Gaudenz Alder
 *
 */

package org.apromore.jgraph.util2;

import java.awt.geom.Rectangle2D;

public class RectUtils {
	/**
	 * Unions the pair of source <code>Rectangle2D</code> objects and puts the
	 * result into the returned <code>Rectangle2D</code> object. This method
	 * extends the Rectangle2D version by checking for null parameters, the
	 * returned value will also be <code>null</code> if the two input
	 * rectangles are <code>null</code>
	 * 
	 * @param src1
	 *            the first of a pair of <code>Rectangle2D</code> objects to
	 *            be combined with each other
	 * @param src2
	 *            the second of a pair of <code>Rectangle2D</code> objects to
	 *            be combined with each other
	 * 
	 */
	public static Rectangle2D union(Rectangle2D src1, Rectangle2D src2) {
		Rectangle2D result = null;
		if (src1 == null && src2 == null) {
			result = null;
		} else if (src1 != null && src2 != null) {
			double x1 = Math.min(src1.getMinX(), src2.getMinX());
			double y1 = Math.min(src1.getMinY(), src2.getMinY());
			double x2 = Math.max(src1.getMaxX(), src2.getMaxX());
			double y2 = Math.max(src1.getMaxY(), src2.getMaxY());
			result = new Rectangle2D.Double();
			result.setFrameFromDiagonal(x1, y1, x2, y2);
		} else if (src1 != null) {
			double x1 = src1.getMinX();
			double y1 = src1.getMinY();
			double x2 = src1.getMaxX();
			double y2 = src1.getMaxY();
			result = new Rectangle2D.Double();
			result.setFrameFromDiagonal(x1, y1, x2, y2);
		} else {
			// only src2 is non-null
			double x1 = src2.getMinX();
			double y1 = src2.getMinY();
			double x2 = src2.getMaxX();
			double y2 = src2.getMaxY();
			result = new Rectangle2D.Double();
			result.setFrameFromDiagonal(x1, y1, x2, y2);
		}
		return result;
	}
}
