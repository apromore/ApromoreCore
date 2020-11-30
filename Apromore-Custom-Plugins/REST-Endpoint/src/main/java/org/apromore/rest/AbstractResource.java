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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apromore.mapper.UserMapper;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.UserType;
import org.apromore.service.SecurityService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Common methods shared by {@link ArtifactResource} and {@link UserResource}.
 */
abstract class AbstractResource {

    /**
     * Logger.
     *
     * Named after the class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);

    /**
     * Regular expression for HTTP Authorization headers.
     *
     * The named capturing group "basicAuthorization" contains the Base64 payload.
     */
    private static final Pattern AUTHORIZATION_PATTERN =
        Pattern.compile("Basic\\s+(?<basicAuthorization>[\\d\\p{Alpha}+/]+=*)");

    /**
     * Regular expression for decoded HTTP Basic authentication payload.
     *
     * The named capturing groups "name" and "password" contain the payload fields.
     */
    private static final Pattern BASIC_PAYLOAD_PATTERN = Pattern.compile("(?<name>[^:]*):(?<password>.*)");

    @Context
    private ServletContext servletContext;

    /**
     * Perform HTTP Basic authentication.
     *
     * @param authorization  the HTTP Authorization header; <code>null</code> indicates absent header
     * @return the authenticated user
     * @throws ResourceException if authentication fails
     */
    protected UserType authenticatedUser(final String authorization) throws ResourceException {

        // Validate the presence of HTTP Basic authentication
        if (authorization == null) {
            throw new ResourceException(Response.Status.UNAUTHORIZED, "Anonymous access denied.");
        }
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

        // Authenticate using Spring Security
        AuthenticationManager authenticationManager = osgiService(AuthenticationManager.class);
        try {
            String name = matcher2.group("name");
            String password = matcher2.group("password");
            Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
            assert authentication.isAuthenticated();

            // Success!  Return the authenticated user DTO
            SecurityService securityService = osgiService(SecurityService.class);
            return UserMapper.convertUserTypes(securityService.getUserByName(name), securityService);

        } catch (AuthenticationException e) {
            throw new ResourceException(Response.Status.UNAUTHORIZED, e.getMessage());
        }
    }

    /**
     * Authorize a user against a role.
     *
     * @param user  a user, usually obtained via {@link #authenticateUser}
     * @param role  the role while the <var>user</var> ought to have
     * @throws ResourceException if the <var>user</var> lacks the <var>role</var>
     */
    protected void authorize(final UserType user, final String role) throws ResourceException {
        for (RoleType userRole: user.getRoles()) {
            if (userRole.getName().equals(role)) {
                return;  // Success
            }
        }

        // User was not in the authorized role
        throw new ResourceException(Response.Status.FORBIDDEN,
            "User " + user.getUsername() + " not in role " + role);
    }

    /**
     * Access a unique OSGi service.
     *
     * This obtains the bundle context from the servlet context of the web application.
     *
     * @param clazz  the type of the service; there must be exactly one registered service of this type
     * @return the service instance
     */
    protected <T> T osgiService(final Class<T> clazz) {
        BundleContext bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz);
        T service = (T) bundleContext.getService(serviceReference);

        return service;
    }
}
