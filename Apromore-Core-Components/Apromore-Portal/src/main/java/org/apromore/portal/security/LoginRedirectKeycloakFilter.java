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
package org.apromore.portal.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class LoginRedirectKeycloakFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRedirectKeycloakFilter.class);

    private static final String ENV_KEYCLOAK_REALM_NAME_KEY = "KEYCLOAK_REALM_NAME";
    private static final String KEYCLOAK_REALM_PLACEHOLDER = "<keycloakRealm>";
    private static final String STATE_UUID_PLACEHOLDER = "<state_uuid>";
    private static final String FULL_RETURN_PATH_PLACEHOLDER = "<full_return_path>";
    private String fullConfigurableReturnPath = "http://localhost:8181/"; // @2do: Inject, and get via ConfigBean getFullProtocolHostPortUrl()

    private static final String TRAD_LOGIN_REQUEST_URI = "/login.zul";

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
            tmpUrl = tmpUrl.replaceFirst(FULL_RETURN_PATH_PLACEHOLDER, fullConfigurableReturnPath);

            LOGGER.info("\n\n>>>>> >>> > tmpUrl=[" + tmpUrl + "]");

            this.keycloakLoginFormUrl = tmpUrl;
        }
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {
        LOGGER.trace("\n\n>>>>> In " + this.getClass() + ".doFilter(..)");

        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        final String requestURI = servletRequest.getRequestURI();
        LOGGER.trace("\n\nrequestURI is: " + requestURI);

        if ((requestURI != null) && (requestURI.contains(TRAD_LOGIN_REQUEST_URI))) {
            LOGGER.info("\n\n>>>>> Detected [" + TRAD_LOGIN_REQUEST_URI + "] URI request <<<<<\n\n");

            String urlToUseKeycloakLoginPage = this.determineUrlToUseForLoginRequest();
            LOGGER.info("\n\n##### [INITIAL] urlToUseKeycloakLoginPage " + urlToUseKeycloakLoginPage);

            HttpServletResponse httpServletResponse = ((HttpServletResponse) response);

            httpServletResponse.sendRedirect(urlToUseKeycloakLoginPage);
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private String determineUrlToUseForLoginRequest() {
        final String loginFormPattern = getKeycloakLoginFormUrl();

        final String keycloakRealmOfCustomer = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);;

        final String randomStateUuid = UUID.randomUUID().toString();
        LOGGER.info("\n\n>>>>> randomStateUuid: {}", randomStateUuid);

        String loginUrl = loginFormPattern.replaceAll(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealmOfCustomer);
        loginUrl = loginUrl.replaceFirst(STATE_UUID_PLACEHOLDER, randomStateUuid);
        loginUrl = loginUrl.replaceFirst(FULL_RETURN_PATH_PLACEHOLDER, fullConfigurableReturnPath);

        LOGGER.info("\n\n>>> >>> >>> Resolved/populated substitution placeholders Keycloak loginUrl: {}", loginUrl);

        return loginUrl;
    }
}
