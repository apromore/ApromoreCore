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
package org.apromore.portal.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AssertUtilsTest {

    @Test
    void notNullAssert_nullParamNameParam_paramNameNotMentionedInAssertOutput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            AssertUtils.notNullAssert(null, null));
        assertEquals("parameter must not be null", exception.getMessage());
    }

    @Test
    void notNullAssert_suppliedParamNameParam_suppliedParamNameMentionedInAssertOutput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            AssertUtils.notNullAssert(null, "testing"));
        assertEquals("'testing' parameter must not be null", exception.getMessage());
    }

    @Test
    void notNullAssert_nonNullParam_noIllegalArgumentExceptionThrown() {
        assertDoesNotThrow(() -> AssertUtils.notNullAssert(new String(""), "testing"));

    }

    @Test
    void hasTextAssert_nullParamNameParam_paramNameNotMentionedInAssertOutput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            AssertUtils.hasTextAssert(null, null));
        assertEquals("parameter must not be empty", exception.getMessage());
    }

    @Test
    void hasTextAssert_suppliedParamNameParam_suppliedParamNameMentionedInAssertOutput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            AssertUtils.hasTextAssert(null, "testing"));
        assertEquals("'testing' parameter must not be empty", exception.getMessage());
    }

    @Test
    void hasTextAssert_nonNullParam_noIllegalArgumentExceptionThrown() {
        assertDoesNotThrow(() -> AssertUtils.hasTextAssert(new String("yip"), "testing"));
    }
}
