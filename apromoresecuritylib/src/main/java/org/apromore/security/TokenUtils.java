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
package org.apromore.security;

import org.apromore.security.util.DateTimeUtils;

import static org.apromore.security.util.AssertUtils.hasTextAssert;

public final class TokenUtils {

    private static final String ATTRIBUTE_DELIMETER = ";";
    private static final String KEY_VALUE_DELIMETER = "=";
    private static final String USERNAME_KEY = "username";
    private static final String EMAIL_ADDRESS_KEY = "email";
    private static final String TIMESTAMP_KEY = "timestamp";

    private TokenUtils() {
    }

    public static String createToken(final String username, final String emailAddress) {
        return TokenUtils.createToken(username, emailAddress, DateTimeUtils.dateTimeStampNoSpaces());
    }

    public static String createToken(final String username, final String emailAddress, final String timestamp) {
        hasTextAssert(username, "'username' must not be empty");
        hasTextAssert(emailAddress, "'emailAddress' must not be empty");
        hasTextAssert(timestamp, "'timestamp' must not be empty");

        final StringBuffer stringBuffer = new StringBuffer("");
        stringBuffer.append(USERNAME_KEY);
        stringBuffer.append(KEY_VALUE_DELIMETER);
        stringBuffer.append(username);

        stringBuffer.append(ATTRIBUTE_DELIMETER);

        stringBuffer.append(EMAIL_ADDRESS_KEY);
        stringBuffer.append(KEY_VALUE_DELIMETER);
        stringBuffer.append(emailAddress);

        stringBuffer.append(ATTRIBUTE_DELIMETER);

        stringBuffer.append(TIMESTAMP_KEY);
        stringBuffer.append(KEY_VALUE_DELIMETER);
        stringBuffer.append(timestamp);

        return stringBuffer.toString();
    }
}
