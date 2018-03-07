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
package de.hbrs.oryx.yawl.converter.layout;

import org.oryxeditor.server.diagram.Bounds;

/**
 * Store information about the Layout of YAWL elements. <br>
 * TODO: Maybe create a class for each YAWL element. (condition, task, ..)
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NetElementLayout {

    public enum DecoratorType {
        TOP, LEFT, RIGHT, BOTTOM, NONE
    }

    /**
     * True if this element is a condition
     */
    private final boolean isCondition;

    private DecoratorType splitDecorator = DecoratorType.NONE;
    private DecoratorType joinDecorator = DecoratorType.NONE;
    private Bounds bounds;
    private String iconPath;

    public NetElementLayout(final boolean isCondition) {
        super();
        this.isCondition = isCondition;
    }

    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setSplitDecorator(final DecoratorType splitDecoratorType) {
        this.splitDecorator = splitDecoratorType;
    }

    public DecoratorType getSplitDecorator() {
        return splitDecorator;
    }

    public void setJoinDecorator(final DecoratorType joinDecoratorType) {
        this.joinDecorator = joinDecoratorType;
    }

    public DecoratorType getJoinDecorator() {
        return joinDecorator;
    }

    public boolean isCondition() {
        return isCondition;
    }

    public boolean hasJoinDecorator() {
        return !getJoinDecorator().equals(DecoratorType.NONE);
    }

    public boolean hasSplitDecorator() {
        return !getSplitDecorator().equals(DecoratorType.NONE);
    }

    public void setIconPath(final String iconPath) {
        this.iconPath = iconPath;
    }

    public String getIconPath() {
        return iconPath;
    }

}
