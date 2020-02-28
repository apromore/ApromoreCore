/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.graph.canonical;

/**
 * Non-Human Resource Type Enumeration.
 */
public enum NonHumanTypeEnum {

    SOFTWARE_SYSTEM("SoftwareSystem"), EQUIPMENT("Equipment");

    private final String value;

    NonHumanTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NonHumanTypeEnum fromValue(String v) {
        for (NonHumanTypeEnum c: NonHumanTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
