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

import org.apache.commons.lang3.StringUtils;

public final class AssertUtils {

    public static final void notNullAssert(final Object paramValue, final String paramName) {
        AssertUtils.paramUnspecifiedMessage(paramValue, paramName, true);
    }

    public static final void hasTextAssert(final String strParamValue, final String paramName) {
        AssertUtils.paramUnspecifiedMessage(strParamValue, paramName, false);
    }

    /**
     * Helper method that accepts either an object, or String - and, checks for either not null or empty string
     * respectively.
     *
     * @param paramValue Parameter object to check.
     * @param paramName Optional parameter name.
     * @param notNullCheck <code>true</code> if checking for null, otherwise <code>false</code> for empty string check.
     */
    private static final void paramUnspecifiedMessage(
            final Object paramValue,
            final String paramName,
            final boolean notNullCheck) {
        if (notNullCheck) {
            if (paramValue == null) {
                String exceptionMsg = "parameter must not be null";
                if (paramName != null) {
                    exceptionMsg = "'" + paramName + "' " + exceptionMsg;
                }
                throw new IllegalArgumentException(exceptionMsg);
            }
        } else {
            if (StringUtils.isEmpty((String)paramValue)) {
                String exceptionMsg = "parameter must not be empty";
                if (paramName != null) {
                    exceptionMsg = "'" + paramName + "' " + exceptionMsg;
                }
                throw new IllegalArgumentException(exceptionMsg);
            }
        }
    }
}
