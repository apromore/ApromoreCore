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

/**
 * Implements the basic geometry of an element
 *
 * @author Team Royal Fawn
 */
public class LayoutingBoundsImpl implements LayoutingBounds {

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public LayoutingBoundsImpl(double x, double y, double width, double height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public LayoutingBoundsImpl() {
        this(0, 0, 0, 0);
    }

    private double x = 0;
    private double y = 0;
    private double width = 0;
    private double height = 0;

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return getX() + getWidth();
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return getY() + getHeight();
    }

    /**
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    public String toString() {
        String out = " x=" + getX();
        out += " y=" + getY();
        out += " width=" + getWidth();
        out += " height=" + getHeight();
        return out;
    }
}
