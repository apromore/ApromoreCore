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
package org.apromore.portal;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.model.PermissionType;
import org.slf4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Log login attempts.
 */
@Component("authenticationHandler")
public class AuthenticationHandler implements AuthenticationFailureHandler, AuthenticationSuccessHandler {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(AuthenticationHandler.class);

    /**
     * {@inheritDoc}
     *
     * This implementation logs unsuccessful authentication attempts.
     * It records the remote host and user agent header to aid security follow-ups.
     * It will redirect the user to the <code>login.zul</code> page with an appropriate error notification.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest      request,
                                        HttpServletResponse     response,
                                        AuthenticationException exception) throws IOException {

        if (exception instanceof BadCredentialsException ||
            exception instanceof UsernameNotFoundException) {

            LOGGER.warn("Failed login attempt from {}, user agent {}", request.getRemoteHost(), request.getHeader("user-agent"));
            response.sendRedirect("/zkau/web/login.zul?error=1");  // display auth_credInvalid_message

        } else {
            LOGGER.error("Unable to authenticate", exception);
            response.sendRedirect("/zkau/web/login.zul?error=3");  // display auth_failed_message
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation logs when a user authenticates successfully.
     * It redirects to <code>index.zul</code>.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest  request,
                                        HttpServletResponse response,
                                        Authentication      authentication) throws IOException {

        //Invalidate the session if the user does not have a role with login permissions
        if (authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .noneMatch(a -> PermissionType.PORTAL_LOGIN.getName().equals(a))) {

            LOGGER.info("User \"{}\" does not have login permissions", authentication.getName());
            request.getSession().invalidate();
            response.sendRedirect("/zkau/web/login.zul?error=5");

        } else {
            LOGGER.info("User \"{}\" login", authentication.getName());
            response.sendRedirect("/zkau/web/index.zul");
        }
    }
}
