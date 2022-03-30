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

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.model.PermissionType;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Prevent users without login permissions from logging in.
 */
public class ApromoreKeycloakAuthenticationSuccessHandler extends KeycloakAuthenticationSuccessHandler {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreKeycloakAuthenticationSuccessHandler.class);

    public ApromoreKeycloakAuthenticationSuccessHandler (AuthenticationSuccessHandler fallback) {
        super(fallback);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation logs out a user if they are authenticated but do not have portal login permissions.
     * Otherwise, it redirects to <code>index.zul</code>.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        //Logout if the user does not have a role with login permissions
        if (authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .noneMatch(a -> PermissionType.PORTAL_LOGIN.getName().equals(a))) {

            LOGGER.info("User \"{}\" does not have login permissions", authentication.getName());
            response.sendRedirect("/logout");

        } else {
            LOGGER.info("User \"{}\" login via keycloak", authentication.getName());
            super.onAuthenticationSuccess(request, response, authentication);
        }


    }
}
