/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AssertUtilsTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void notNullAssert_nullParamNameParam_paramNameNotMentionedInAssertOutput() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("parameter must not be null");

        AssertUtils.notNullAssert(null, null);
    }

    @Test
    public void notNullAssert_suppliedParamNameParam_suppliedParamNameMentionedInAssertOutput() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'testing' must not be null");

        AssertUtils.notNullAssert(null, "testing");
    }

    @Test
    public void notNullAssert_nonNullParam_noIllegalArgumentExceptionThrown() {
        AssertUtils.notNullAssert(new String(""), "testing");
    }

    @Test
    public void hasTextAssert_nullParamNameParam_paramNameNotMentionedInAssertOutput() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("parameter must not be empty");

        AssertUtils.hasTextAssert(null, null);
    }

    @Test
    public void hasTextAssert_suppliedParamNameParam_suppliedParamNameMentionedInAssertOutput() {
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("'testing' must not be empty");

        AssertUtils.hasTextAssert(null, "testing");
    }

    @Test
    public void hasTextAssert_nonNullParam_noIllegalArgumentExceptionThrown() {
        AssertUtils.hasTextAssert(new String("yip"), "testing");
    }
}
