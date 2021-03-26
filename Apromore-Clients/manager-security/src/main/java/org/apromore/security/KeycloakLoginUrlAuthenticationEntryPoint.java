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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class KeycloakLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakLoginUrlAuthenticationEntryPoint.class);

    private static final String ENV_KEYCLOAK_REALM_NAME_KEY = "KEYCLOAK_REALM_NAME";
    private static final String KEYCLOAK_REALM_PLACEHOLDER = "<keycloakRealm>";
    private static final String STATE_UUID_PLACEHOLDER = "<state_uuid>";

    private String keycloakLoginFormUrl;

    public String getKeycloakLoginFormUrl() {
        return keycloakLoginFormUrl;
    }

    public void setKeycloakLoginFormUrl(String keycloakLoginFormUrl) {
        if ((this.keycloakLoginFormUrl == null) ||
                (this.keycloakLoginFormUrl.contains(KEYCLOAK_REALM_PLACEHOLDER))) {
            final String keycloakRealm = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);
            LOGGER.info("\n\nFROM environment property keycloakRealm[" + keycloakRealm + "]");

            String tmpUrl = keycloakLoginFormUrl;

            final String randomStateUuid = UUID.randomUUID().toString();
            LOGGER.info("\n\nrandomStateUuid: {}", randomStateUuid);

            tmpUrl = tmpUrl.replaceFirst(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealm);
            tmpUrl = tmpUrl.replaceFirst(STATE_UUID_PLACEHOLDER, randomStateUuid);
            LOGGER.info("\n\n>>>>> >>> > tmpUrl=[" + tmpUrl + "]");

            this.keycloakLoginFormUrl = tmpUrl;
        }
    }

    @Override
    public void commence(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse,
                         final AuthenticationException authenticationException) throws IOException, ServletException {
        super.commence(httpServletRequest, httpServletResponse, authenticationException);
    }

    @Override
    protected String buildRedirectUrlToLoginPage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException) {
        return super.buildRedirectUrlToLoginPage(request, response, authException);
    }

    /**
     * Allows subclasses to modify the login form URL that should be applicable for a
     * given request.
     * @param request the request
     * @param response the response
     * @param exception the exception
     * @return the URL (cannot be null or empty; defaults to {@link #getLoginFormUrl()})
     */
    @Override
    protected String determineUrlToUseForThisRequest(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception) {
        final String loginFormPattern = getKeycloakLoginFormUrl();

        final String keycloakRealmOfCustomer = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);;

        String loginUrl = loginFormPattern.replaceAll(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealmOfCustomer);

        LOGGER.info("\n\n>>> Resolved Keycloak loginUrl (via securityms): {}", loginUrl);

        return loginUrl;
    }
}
