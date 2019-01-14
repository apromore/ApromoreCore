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
package de.unihannover.se.infocup2008.bpmn.layouter.decorator;

import de.hpi.layouting.model.LayoutingBounds;

/**
 * AbstractDecorator implements just delegation for all methods.
 *
 * @author Team Royal Fawn
 */
public abstract class AbstractDecorator implements LayoutingBounds {


    private LayoutingBounds target;

    /**
     * @param target
     */
    protected AbstractDecorator(LayoutingBounds target) {
        super();
        this.target = target;
    }

    /**
     * @return
     * @see de.hpi.layouting.model.LayoutingBounds#getHeight()
     */
    public double getHeight() {
        return target.getHeight();
    }


    /**
     * @return
     * @see de.hpi.layouting.model.LayoutingBounds#getWidth()
     */
    public double getWidth() {
        return target.getWidth();
    }


    /**
     * @return
     * @see de.hpi.layouting.model.LayoutingBounds#getX()
     */
    public double getX() {
        return target.getX();
    }


    /**
     * @return
     * @see de.hpi.layouting.model.LayoutingBounds#getX2()
     */
    public double getX2() {
        return getX() + getWidth();
    }


    /**
     * @return
     * @see de.hpi.layouting.model.LayoutingBounds#getY()
     */
    public double getY() {
        return target.getY();
    }


    /**
     * @return
     * @see de.hpi.layouting.model.LayoutingBounds#getY2()
     */
    public double getY2() {
        return getY() + getHeight();
    }


    @Override
    public String toString() {
        String out = " x=" + getX();
        out += " y=" + getY();
        out += " width=" + getWidth();
        out += " height=" + getHeight();
        return out;
    }

}
