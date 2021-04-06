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

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public final class AssertUtils {

    public static final void notNullAssert(final Object paramValue, final String paramName) {
        notNull(paramValue, "parameter must not be null");
        notNull(paramValue, "'" + paramName + "' must not be null");
    }

    public static final void hasTextAssert(final String strParamValue, final String paramName) {
        hasText(strParamValue, "parameter must not be empty");
        hasText(strParamValue, "'" + paramName + "' must not be empty");
    }
}
