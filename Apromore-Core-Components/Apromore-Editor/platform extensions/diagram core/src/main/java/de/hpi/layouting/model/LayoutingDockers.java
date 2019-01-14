/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.layouting.model;

import java.util.ArrayList;
import java.util.List;

public class LayoutingDockers {
    public static class Point {
        public double x;
        public double y;

        /**
         * @param x
         * @param y
         */
        public Point(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

        /**
         *
         */
        public Point() {
            this(0, 0);
        }

    }

    private List<Point> points = new ArrayList<Point>();

    public void addPoint(double x, double y) {
        points.add(new Point(x, y));
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(double... coords) {
        if (coords.length % 2 != 0) {
            throw new IllegalArgumentException("coords must be of even length");
        }
        points.clear();
        for (int i = 0; i < coords.length; i += 2) {
            this.addPoint(coords[i], coords[i + 1]);
        }
    }

}
