/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.rest;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ResourceUtilities {

    /**
     * Logger.
     *
     * Named after the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtilities.class);

    /**
     * Regular expression for HTTP Authorization headers.
     *
     * The named capturing group "basicAuthorization" contains the Base64 payload.
     */
    private static final Pattern AUTHORIZATION_PATTERN =
        Pattern.compile("Basic\\s+(?<basicAuthorization>[\\d\\p{Alpha}+/]+=*)");

    /**
     * Regular expression for decoded HTTP Basic authorization payload.
     *
     * The named capturing groups "name" and "password" contain the payload fields.
     */
    private static final Pattern BASIC_PAYLOAD_PATTERN =
        Pattern.compile("(?<name>[^:]*):(?<password>.*)");

    /**
     * Perform HTTP Basic authentication and authorization.
     *
     * @param authorization  the HTTP Authorization header; <code>null</code> indicates absent header
     * @throws ResourceException if either authentication or authorization fail
     */
    static void auth(final String authorization) throws ResourceException {
        LOGGER.info("Authorization: " + authorization);
        if (authorization == null) {
            throw new ResourceException(Response.Status.UNAUTHORIZED, "Anonymous access denied.");
        }

        // Validate the presence of HTTP Basic authentication
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
        if (!matcher.matches()) {
            throw new ResourceException(Response.Status.UNAUTHORIZED, "Basic authentication required");
        }

        // Validate the HTTP Basic authentication payload
        String base64 = matcher.group("basicAuthorization");
        String decoded = new String(Base64.getDecoder().decode(base64), ISO_8859_1);
        Matcher matcher2 = BASIC_PAYLOAD_PATTERN.matcher(decoded);
        if (!matcher2.matches()) {
            throw new ResourceException(Response.Status.BAD_REQUEST, "Malformed Basic authorization header");
        }

        LOGGER.info("User " + matcher2.group("name") + ", password " +  matcher2.group("password"));
    }

    /**
     * Access a unique OSGi service.
     *
     * This obtains the bundle context from the servlet context of the web application.
     *
     * @param clazz  the type of the service; there must be exactly one registered service of this type
     * @param context  the servlet context
     * @return the service instance
     */
    static <T> T getOSGiService(final Class<T> clazz, final ServletContext context) {
        BundleContext bundleContext = (BundleContext) context.getAttribute("osgi-bundlecontext");
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz);
        T service = (T) bundleContext.getService(serviceReference);

        return service;
    }
}
