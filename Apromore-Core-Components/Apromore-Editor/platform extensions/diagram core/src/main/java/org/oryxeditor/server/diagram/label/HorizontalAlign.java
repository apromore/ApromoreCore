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

package org.oryxeditor.server.diagram.label;

/**
 * Enumeration of positioning policies for horizontal alignment of labels
 *
 * @author philipp.maschke
 */
public enum HorizontalAlign {

    LEFT("left", 0), CENTER("center", 1), RIGHT("right", 2);

    /**
     * Returns the matching object for the given string
     *
     * @param enumString
     * @return
     * @throws IllegalArgumentException if no matching enumeration object was found
     */
    public static HorizontalAlign fromString(String enumString) {
        return fromString(enumString, true);
    }

    /**
     * Returns the matching object for the given string
     *
     * @param enumString
     * @param exceptionIfNoMatch whether to throw an exception if there was no match
     * @return
     * @throws IllegalArgumentException if no matching enumeration object was found and exceptionIfNoMatch is true
     */
    public static HorizontalAlign fromString(String enumString, boolean exceptionIfNoMatch) {
        for (HorizontalAlign attrEnum : values()) {
            if (attrEnum.label.equals(enumString) || attrEnum.name().equals(enumString))
                return attrEnum;
        }

        if (exceptionIfNoMatch) {
            throw new IllegalArgumentException("No matching enum constant found in '"
                    + HorizontalAlign.class.getSimpleName() + "' for: " + enumString);
        } else {
            return null;
        }
    }

    private String label;
    private int index;


    HorizontalAlign(String label, int index) {
        this.label = label;
        this.index = index;
    }


    /**
     * Returns the alignment label
     */
    @Override
    public String toString() {
        return label;
    }


    public int getIndex() {
        return index;
    }
}
