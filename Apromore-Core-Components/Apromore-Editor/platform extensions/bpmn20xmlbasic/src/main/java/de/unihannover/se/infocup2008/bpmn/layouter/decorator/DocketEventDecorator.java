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
 * This decorator describes, how an attached event is positioned relative to the
 * task
 *
 * @author Team Royal Fawn
 */
public class DocketEventDecorator extends AbstractDecorator {

    private LayoutingBounds relative;

    private int positionFromLeft;

    public DocketEventDecorator(LayoutingBounds target, LayoutingBounds relative,
                                int positionFromLeft) {
        super(target);
        this.relative = relative;
        this.positionFromLeft = positionFromLeft;
    }

    @Override
    public double getX() {
        double firstX = this.relative.getX()
                + LayoutConstants.EVENT_DOCKERS_MARGIN;
        // + LayoutConstants.EVENT_DIAMETER;

        double relPostion = this.positionFromLeft
                * (LayoutConstants.EVENT_DOCKERS_MARGIN + LayoutConstants.EVENT_DIAMETER);
        double newX = firstX + relPostion;
        return newX;
    }

    @Override
    public double getY() {
        return this.relative.getY2() - 18;
    }

}
