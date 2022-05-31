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

package org.apromore.service.logimporter.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import org.apromore.service.logimporter.constants.ColumnType;
import org.junit.jupiter.api.Test;

class ImporterStringUtilsTest {

    /**
     * Test that strings are given the correct type.
     */
    @Test
    void stringTest() {
        assertEquals(ColumnType.STRING, ImporterStringUtils.getColumnType("abc"));
        assertEquals(ColumnType.STRING, ImporterStringUtils.getColumnType(""));
        assertEquals(ColumnType.STRING, ImporterStringUtils.getColumnType("1.0.0"));
    }

    /**
     * Test that ints are given the correct type.
     */
    @Test
    void intTest() {
        assertEquals(ColumnType.INT, ImporterStringUtils.getColumnType("123"));
        assertEquals(ColumnType.INT, ImporterStringUtils.getColumnType(new ArrayList<>(Arrays.asList(
            "1", null, "2", null, "3", "", "4"))));
    }

    /**
     * Test that doubles are given the correct type.
     */
    @Test
    void doubleTest() {
        assertEquals(ColumnType.DOUBLE, ImporterStringUtils.getColumnType("123.456"));
        assertEquals(ColumnType.DOUBLE, ImporterStringUtils.getColumnType(new ArrayList<>(Arrays.asList(
            "1.1", null, "2.2", null, "3.3", "", "4.4"))));
    }

    /**
     * Test that booleans are given the correct type.
     */
    @Test
    void booleanTest() {
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("true"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("True"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("TRUE"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("false"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("False"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("FALSE"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType(new ArrayList<>(Arrays.asList(
            "True", null, "TRUE", null, "false", "", "FALSE"))));
    }

    /**
     * Test that timestamps are given the correct type.
     */
    @Test
    void timestampTest() {
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020-10-10 23:00:00"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020-10-10"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020/10/10  23:00:00"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020/10/10"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("30/01/2021"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("1/1/20"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("1/31/2015"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("01/31/2021"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType(new ArrayList<>(Arrays.asList(
            "1/1/20", null, "2/1/20", null, "3/1/20", "", "4/1/20"))));
    }
}