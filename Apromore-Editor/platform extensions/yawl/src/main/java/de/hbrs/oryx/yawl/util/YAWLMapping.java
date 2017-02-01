/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
package de.hbrs.oryx.yawl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.oryxeditor.server.diagram.Point;

import de.hbrs.oryx.yawl.converter.layout.NetElementLayout.DecoratorType;

/**
 * Contains static mappings between YAWL properties and their corresponding Oryx counterparts. All properties are static and should be initialized
 * using the double bracket method.
 * 
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public final class YAWLMapping {

    public static final Map<Integer, DecoratorType> DECORATOR_TYPE_MAP = new HashMap<Integer, DecoratorType>() {

        private static final long serialVersionUID = 3887893686889925570L;

        {
            put(10, DecoratorType.TOP);
            put(11, DecoratorType.BOTTOM);
            put(12, DecoratorType.LEFT);
            put(13, DecoratorType.RIGHT);
            put(14, DecoratorType.NONE);
        }
    };

    public static final Map<Integer, Point> TASK_PORT_MAP = new HashMap<Integer, Point>() {

        private static final long serialVersionUID = -5420166595450623507L;

        {
            // TOP
            put(10, new Point(28.0, 12.0));
            // BOTTOM
            put(11, new Point(28.0, 44.0));
            // LEFT
            put(12, new Point(12.0, 28.0));
            // RIGHT
            put(13, new Point(44.0, 28.0));
            // NOWHERE, maps to Oryx Default
            put(14, new Point(28.0, 28.0));
        }
    };
    public static final Map<Integer, Point> CONDITION_PORT_MAP = new HashMap<Integer, Point>() {

        private static final long serialVersionUID = -468390840359626099L;

        {
            // TOP
            put(10, new Point(16.0, 0.0));
            // BOTTOM
            put(11, new Point(16.0, 32.0));
            // LEFT
            put(12, new Point(0.0, 16.0));
            // RIGHT
            put(13, new Point(32.0, 16.0));
            // NOWHERE, maps to the Oryx Default
            put(14, new Point(16.0, 16.0));
        }
    };

    public static final Map<Integer, Point> TOP_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

        private static final long serialVersionUID = 1164532423345798945L;

        {
            // From Left to Right
            put(0, new Point(12.0, 6.0));
            put(1, new Point(20.0, 0.0));
            put(2, new Point(28.0, 0.0));
            put(3, new Point(36.0, 0.0));
            put(4, new Point(44.0, 6.0));
        }
    };

    public static final Map<Integer, Point> BOTTOM_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

        private static final long serialVersionUID = -4866739586620322203L;

        {
            // From Right to Left
            put(0, new Point(44.0, 50.0));
            put(1, new Point(36.0, 56.0));
            put(2, new Point(28.0, 56.0));
            put(3, new Point(20.0, 56.0));
            put(4, new Point(12.0, 50.0));
        }
    };

    public static final Map<Integer, Point> LEFT_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

        private static final long serialVersionUID = -3451374695282471736L;

        {
            // From Bottom to Top
            put(0, new Point(6.0, 44.0));
            put(1, new Point(0.0, 20.0));
            put(2, new Point(0.0, 28.0));
            put(3, new Point(0.0, 36.0));
            put(4, new Point(6.0, 12.0));
        }
    };

    public static final Map<Integer, Point> RIGHT_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

        private static final long serialVersionUID = -7299926129758801645L;

        {
            // From Top to Bottom
            put(0, new Point(50.0, 12.0));
            put(1, new Point(56.0, 20.0));
            put(2, new Point(56.0, 28.0));
            put(3, new Point(56.0, 36.0));
            put(4, new Point(50.0, 44.0));
        }
    };

    public static <T, E> T getKeyByValue(final Map<T, E> map, final E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
