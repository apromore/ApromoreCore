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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import java.text.NumberFormat;
import java.text.ParseException;

import org.apromore.canoniser.exception.CanoniserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;

/**
 * Very basic auto layout for YAWL (sub)nets. Based on a breadth first traversal and some YAWL default sizes.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class YAWLAutoLayouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWLAutoLayouter.class);

    private static final int AUTO_LAYOUT_START_SPACE = 10;
    private static final int AUTO_LAYOUT_SPACE_BETWEEN_ELEMENTS = 100;

    private static final int ELEMENT_DEFAULT_WIDTH = 32;
    private static final int ELEMENT_DEFAULT_HEIGHT = 32;

    private static final int LABEL_DEFAULT_WIDTH = 96;
    private static final int LABEL_DEFAULT_HEIGHT = 64;

    private static final int DECORATOR_DEFAULT_WIDTH = 11;
    private static final int DECORATOR_DEFAULT_HEIGHT = 32;

    private static final int DECORATOR_X_OFFSET = 31;
    private static final int DECORATOR_Y_OFFSET = 10;

    private final NumberFormat nf;

    private LayoutRectangleType lastBounds;

    private int lastDistance = 0;

    private int currentDistance = 0;

    private int currentBreadth = 1;

    private int maxBreadth = 1;

    public YAWLAutoLayouter(final NumberFormat nf) {
        this.nf = nf;
    }

    public LayoutRectangleType getLastElementBounds() {
        return lastBounds;
    }

    public void setLastElementBounds(final LayoutRectangleType lastElementBounds) {
        this.lastBounds = lastElementBounds;
    }

    public int getLastElementDistance() {
        return lastDistance;
    }

    public void setLastElementDistance(final int lastElementDistance) {
        this.lastDistance = lastElementDistance;
    }

    public int getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(final int layoutDistance) {
        this.currentDistance = layoutDistance;
    }

    public int getCurrentBreadth() {
        return currentBreadth;
    }

    public void setCurrentBreadth(final int currentBreadth) {
        this.currentBreadth = currentBreadth;
    }

    public int getElementHeight(final ExternalNetElementType yawlElement) {
        return ELEMENT_DEFAULT_HEIGHT;
    }

    public int getElementWidth(final ExternalNetElementType yawlElement) {
        return ELEMENT_DEFAULT_WIDTH;
    }

    public double getElementX() throws CanoniserException {
        try {
            if (getLastElementBounds() != null) {
                LOGGER.debug("Aut-Layout: distance: {}, breadth: {}, lastDistance: {}", new Integer[] { getCurrentDistance(), getCurrentBreadth(),
                        getLastElementDistance() });
                final Number x = nf.parse(getLastElementBounds().getX());
                if (getCurrentDistance() > getLastElementDistance()) {
                    // Different Distance move on X avis
                    return x.intValue() + AUTO_LAYOUT_SPACE_BETWEEN_ELEMENTS;
                } else {
                    return x.intValue();
                }
            } else {
                return AUTO_LAYOUT_START_SPACE;
            }
        } catch (final ParseException e) {
            throw new CanoniserException("", e);
        }
    }

    public double getElementY() throws CanoniserException {
        try {
            if (getLastElementBounds() != null) {
                final Number y = nf.parse(getLastElementBounds().getY());
                if (getCurrentDistance() == getLastElementDistance()) {
                    // Same Distance move on Y axis
                    return y.intValue() + calculateCurrentSlotHeight();
                } else {
                    return calculateCurrentSlotHeight();
                }
            } else {
                return calculateCurrentSlotHeight();
            }
        } catch (final ParseException e) {
            throw new CanoniserException("Could not parse last element bounds for YAWL auto layout!", e);
        }
    }

    private double calculateCurrentSlotHeight() {
        return (getMaxBreadth() * AUTO_LAYOUT_SPACE_BETWEEN_ELEMENTS) / (getCurrentBreadth() + 1);
    }

    public int getLabelHeight(final ExternalNetElementType yawlElement) {
        return LABEL_DEFAULT_HEIGHT;
    }

    public int getLabelWidth(final ExternalNetElementType yawlElement) {
        return LABEL_DEFAULT_WIDTH;
    }

    public double getLabelX(final ExternalNetElementType yawlElement, final LayoutRectangleType elementBounds, final boolean joinRouting,
            final boolean splitRouting) throws CanoniserException {
        try {
            final Number x = nf.parse(elementBounds.getX());
            if (joinRouting && splitRouting) {
                return x.doubleValue() - (getElementWidth(yawlElement) / 2) - getDecoratorWidth();
            } else if (splitRouting) {
                return x.doubleValue() - (getElementWidth(yawlElement) / 2) - (getDecoratorWidth() / 2);
            } else if (joinRouting) {
                return x.doubleValue() - (getElementWidth(yawlElement) / 2) - (getDecoratorWidth() / 2);
            } else {
                return x.doubleValue() - (getElementWidth(yawlElement) / 2);
            }
        } catch (final ParseException e) {
            throw new CanoniserException("Can not parse label X position in YAWLAutoLayouter. Internal Error, probably a programming error!", e);
        }
    }

    public double getLabelY(final ExternalNetElementType yawlElement, final LayoutRectangleType elementBounds, final boolean joinRouting,
            final boolean splitRouting) throws CanoniserException {
        try {
            final Number y = nf.parse(elementBounds.getY());
            return y.doubleValue() + getElementHeight(yawlElement);
        } catch (final ParseException e) {
            throw new CanoniserException("Can not parse labels Y position in YAWLAutoLayouter. Internal Error, probably a programming error!", e);
        }
    }

    public int getDecoratorHeight() {
        return DECORATOR_DEFAULT_HEIGHT;
    }

    public int getDecoratorWidth() {
        return DECORATOR_DEFAULT_WIDTH;
    }

    public double getDecoratorX(final String routingType, final ExternalNetElementType yawlElement, final LayoutRectangleType elementBounds)
            throws CanoniserException {
        try {
            if (routingType.equals("split")) {
                return nf.parse(elementBounds.getX()).doubleValue() + DECORATOR_X_OFFSET;
            } else {
                return nf.parse(elementBounds.getX()).doubleValue() - DECORATOR_Y_OFFSET;
            }
        } catch (final ParseException e) {
            throw new CanoniserException("Can not parse decorators X position in YAWLAutoLayouter. Internal Error, probably a programming error!", e);
        }
    }

    public double getDecoratorY(final String routingType, final ExternalNetElementType yawlElement, final LayoutRectangleType elementBounds)
            throws CanoniserException {
        try {
            return nf.parse(elementBounds.getY()).doubleValue();
        } catch (final ParseException e) {
            throw new CanoniserException("Can not parse decorators Y position in YAWLAutoLayouter. Internal Error, probably a programming error!", e);
        }
    }

    public void setMaxBreadth(final int maxBreadth) {
        this.maxBreadth = maxBreadth;

    }

    public int getMaxBreadth() {
        return maxBreadth;
    }

}