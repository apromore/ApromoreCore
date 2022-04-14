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

package org.apromore.service.logimporter.constants;

import java.util.Arrays;

public enum ColumnType {
    INT,
    BIGINT,
    DOUBLE,
    FLOAT,
    DECIMAL,
    BOOLEAN,
    STRING,
    TIMESTAMP,
    CUSTOM_COLUMN;

    public static final ColumnType[] numberTypes = {INT, BIGINT, DOUBLE, FLOAT, DECIMAL};
    public static final ColumnType[] nonNumberTypes = {BOOLEAN, STRING, TIMESTAMP, CUSTOM_COLUMN};

    /**
     * Match and Get the ColumnType enum.
     *
     * @param value in string to match for type.
     * @return ColumnType
     */
    public static ColumnType getType(final String value) {
        return Arrays.asList(ColumnType.values()).stream()
            .filter(type -> type.toString().equals(value.toUpperCase()))
            .findFirst()
            .orElse(CUSTOM_COLUMN);
    }
}
