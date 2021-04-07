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
package org.apromore.security.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public final class AssertUtils {

    private static final String PROTOCOL_HTTP = "http";
    private static final String PROTOCOL_HTTPS = "https";

    public static final void notNullAssert(final Object paramValue, final String paramName) {
        if (paramName == null) {
            notNull(paramValue, "parameter must not be null");
        } else {
            notNull(paramValue, "'" + paramName + "' must not be null");
        }
    }

    public static final void hasTextAssert(final String strParamValue, final String paramName) {
        if (paramName == null) {
            hasText(strParamValue, "parameter must not be empty");
        } else {
            hasText(strParamValue, "'" + paramName + "' must not be empty");
        }
    }

    public static final void validHttpProtocolUrl(
            final String purportedHttpUrl,
            boolean includeBothHttpAndHttps) {
        boolean validIpAddress = true;

        if (StringUtils.isBlank(purportedHttpUrl)) {
            validIpAddress = false;
        }

        final List<String> supportedSchemes = new ArrayList<>();

        supportedSchemes.add(PROTOCOL_HTTP);

        if (includeBothHttpAndHttps) {
            supportedSchemes.add(PROTOCOL_HTTPS);
        }

        final UrlValidator urlValidator = new UrlValidator(supportedSchemes.toArray(new String[0]));

        if (! urlValidator.isValid(purportedHttpUrl)) {
            throw new IllegalArgumentException("Purported http URL '" + purportedHttpUrl + "' is invalid");
        }
    }

    public static final void validIpAddress(final String ipAddress) {
        final InetAddressValidator inetAddressValidator = new InetAddressValidator();

        if (! inetAddressValidator.isValid(ipAddress)) {
            throw new IllegalArgumentException(("IP address '" + ipAddress + "' is invalid"));
        }
    }

}
