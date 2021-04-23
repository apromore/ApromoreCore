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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class KeycloakLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakLoginUrlAuthenticationEntryPoint.class);

    private static final String ENV_KEYCLOAK_REALM_NAME_KEY = "KEYCLOAK_REALM_NAME";
    private static final String KEYCLOAK_REALM_PLACEHOLDER = "<keycloakRealm>";
    private static final String STATE_UUID_PLACEHOLDER = "<state_uuid>";
    private static final String FULL_RETURN_PATH_PLACEHOLDER = "<full_return_path>";

    private String fullConfigurableReturnPath = "http://localhost:8181/";
    private boolean utiliseKeycloakSso = false;

    private String keycloakLoginFormUrl;

    public void setFullProtocolHostPortUrl(final String fullProtocolHostPortUrl) {
        fullConfigurableReturnPath = fullProtocolHostPortUrl;

        LOGGER.info("Set fullConfigurableReturnPath to {}", fullConfigurableReturnPath);
    }

    public void setUseKeycloakSso(final boolean useKeycloakSso) {
        utiliseKeycloakSso = useKeycloakSso;

        LOGGER.info("Set useKeycloakSso to {}", utiliseKeycloakSso);
    }

    public String getKeycloakLoginFormUrl() {
        return keycloakLoginFormUrl;
    }

    public void setKeycloakLoginFormUrl(final String keycloakLoginFormUrl) {
        if ((this.keycloakLoginFormUrl == null) ||
                (this.keycloakLoginFormUrl.contains(KEYCLOAK_REALM_PLACEHOLDER))) {
            final String keycloakRealm = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);
            LOGGER.info("FROM environment property keycloakRealm[" + keycloakRealm + "]");

            if (keycloakRealm != null) {
                String tmpUrl = keycloakLoginFormUrl;

                final String randomStateUuid = UUID.randomUUID().toString();
                LOGGER.info("randomStateUuid: {}", randomStateUuid);

                tmpUrl = tmpUrl.replaceFirst(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealm);
                tmpUrl = tmpUrl.replaceFirst(STATE_UUID_PLACEHOLDER, randomStateUuid);
                tmpUrl = tmpUrl.replaceFirst(FULL_RETURN_PATH_PLACEHOLDER, fullConfigurableReturnPath);
                LOGGER.info(">>>>> >>> > tmpUrl=[" + tmpUrl + "]");

                this.keycloakLoginFormUrl = tmpUrl;
            }  else {
                LOGGER.info("Keycloak login realm was null - maybe keycloak feature turned-off? [proceeding]");
            }
        }
    }

    @Override
    public void commence(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse,
                         final AuthenticationException authenticationException) throws IOException, ServletException {
        final String requestServletPath = httpServletRequest.getServletPath();
        final String requestURL = httpServletRequest.getRequestURL().toString();
        LOGGER.info(">>> requestServletPath {}", requestServletPath);
        LOGGER.info(">>> requestURL {}", requestURL);

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
     * Allows sub-classes to modify the login form URL that should be applicable for a given request.
     *
     * @param httpServletRequest The HTTP servlet request.
     * @param httpServletResponse The HTTP servlet response.
     * @param exception The exception
     *
     * @return The formulated URL (cannot be null or empty; defaults to {@link #getLoginFormUrl()}).
     */
    @Override
    protected String determineUrlToUseForThisRequest(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final AuthenticationException exception) {
        if (utiliseKeycloakSso) {
            LOGGER.info("[ Utilising keycloak ]");

            final String loginFormPattern = getKeycloakLoginFormUrl();
            LOGGER.info("### loginFormPattern: {}", loginFormPattern);

            final String keycloakRealmOfCustomer = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);
            LOGGER.info("keycloakRealmOfCustomer {}", keycloakRealmOfCustomer);

            String loginUrl = loginFormPattern.replaceAll(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealmOfCustomer);
            LOGGER.info("loginUrl[1] {}", loginUrl);
            loginUrl = loginUrl.replaceAll(FULL_RETURN_PATH_PLACEHOLDER, fullConfigurableReturnPath);
            LOGGER.info("loginUrl[2] {}", loginUrl);

            LOGGER.info(">>> Resolved Keycloak loginUrl (via securityms): {}", loginUrl);

            return loginUrl;
        } else {
            LOGGER.info("[ Keycloak SSO turned off ]");

            String requestUriStr = httpServletRequest.getRequestURL().toString().trim();
            LOGGER.info("requestUriStr: {}", requestUriStr);

            try {
                final URI uri = new URI(requestUriStr);

                final String host = uri.getHost();
                final String path = uri.getPath();
                final int port = uri.getPort();
                LOGGER.info("host {} path {} port {}", host, path, port);

                if (host.endsWith("/") || ( (port == 80) || (port == 8181)) ) {
                    if ( (path == null) ||
                            ( ((port == 80) || (port == 8181)) &&
                                    ((path != null)  && (! path.endsWith("zul"))))) {
                        requestUriStr = requestUriStr + "/login.zul";

                        LOGGER.info("requestUriStr: {}", requestUriStr);
                    }
                }
            } catch (final URISyntaxException use) {
                LOGGER.error("Error in parsing uri: {} - stackTrace {}", use.getMessage(),
                        ExceptionUtils.getStackTrace(use));
            }

            return requestUriStr;
        }
    }
}
