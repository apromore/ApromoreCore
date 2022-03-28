/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.service.logimporter.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apromore.service.logimporter.constants.ColumnType;
import org.junit.jupiter.api.Test;

class ImporterStringUtilsTest {

    /**
     * Test that strings are given the correct type.
     */
    @Test
    public void stringTest() {
        assertEquals(ColumnType.STRING, ImporterStringUtils.getColumnType("abc"));
        assertEquals(ColumnType.STRING, ImporterStringUtils.getColumnType(""));
    }

    /**
     * Test that ints are given the correct type.
     */
    @Test
    public void intTest() {
        assertEquals(ColumnType.INT, ImporterStringUtils.getColumnType("123"));
    }

    /**
     * Test that doubles are given the correct type.
     */
    @Test
    public void doubleTest() {
        assertEquals(ColumnType.DOUBLE, ImporterStringUtils.getColumnType("123.456"));
    }

    /**
     * Test that booleans are given the correct type.
     */
    @Test
    public void booleanTest() {
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("true"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("True"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("TRUE"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("false"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("False"));
        assertEquals(ColumnType.BOOLEAN, ImporterStringUtils.getColumnType("FALSE"));
    }

    /**
     * Test that timestamps are given the correct type.
     */
    @Test
    public void timestampTest() {
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020-10-10 23:00:00"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020-10-10"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020/10/10  23:00:00"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("2020/10/10"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("30/01/2021"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("1/1/20"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("1/31/2015"));
        assertEquals(ColumnType.TIMESTAMP, ImporterStringUtils.getColumnType("01/31/2021"));
    }
}