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
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class KeycloakLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakLoginUrlAuthenticationEntryPoint.class);

    private static final String ENV_KEYCLOAK_REALM_NAME_KEY = "KEYCLOAK_REALM_NAME";
    private static final String KEYCLOAK_REALM_PLACEHOLDER = "<keycloakRealm>";
    private static final String STATE_UUID_PLACEHOLDER = "<state_uuid>";
    private static final String FULL_RETURN_PATH_PLACEHOLDER = "<full_return_path>";

    private static String s_fullConfigurableReturnPath = "http://localhost:8181/";
    private static boolean s_utiliseKeycloakSso = false;

    private String keycloakLoginFormUrl;

    static {
        final Properties keycloakProperties = readKeycloakProperties();

        s_fullConfigurableReturnPath = keycloakProperties.getProperty("fullProtocolHostPortUrl");
        LOGGER.info("\n\n>>> >>> >>> > [FROM keycloak.properties] keycloakLoginFormUrl: {}",
                s_fullConfigurableReturnPath);

        s_utiliseKeycloakSso = Boolean.valueOf(keycloakProperties.getProperty("useKeycloakSso"));
        LOGGER.info("\n\n>>> utiliseKeycloakSso {}", s_utiliseKeycloakSso);
    }

    private static Properties readKeycloakProperties() {
        final Properties properties = new Properties();

        try (final InputStream inputStream =
                     KeycloakLoginUrlAuthenticationEntryPoint.class.getResourceAsStream(
                "/keycloak.properties")) {

            properties.load(inputStream);
            LOGGER.info("\n\nkeycloak.properties properties file properties {}", properties);

            return properties;
        } catch (final IOException | NullPointerException e) {
            LOGGER.error("Exception reading keycloak properties: {} - stackTrace {}",
                    e.getMessage(), ExceptionUtils.getStackTrace(e));

            return null;
        }
    }

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
            tmpUrl = tmpUrl.replaceFirst(FULL_RETURN_PATH_PLACEHOLDER, s_fullConfigurableReturnPath);
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
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final AuthenticationException exception) {
        if (s_utiliseKeycloakSso) {
            final String loginFormPattern = getKeycloakLoginFormUrl();

            final String keycloakRealmOfCustomer = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);

            String loginUrl = loginFormPattern.replaceAll(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealmOfCustomer);
            loginUrl = loginFormPattern.replaceFirst(FULL_RETURN_PATH_PLACEHOLDER, s_fullConfigurableReturnPath);

            LOGGER.info("\n\n>>> Resolved Keycloak loginUrl (via securityms): {}", loginUrl);

            return loginUrl;
        } else {
            LOGGER.info("\n\n[ Keycloak SSO turned off ]");

            final String requestURI = httpServletRequest.getRequestURI().trim();
            LOGGER.info("\n\nrequestURI: {}", requestURI);

            if (requestURI.endsWith("/") || requestURI.endsWith("81")) {
                final String str = requestURI + "/login.zul";

                LOGGER.info("\n\nstrToReturn: {}", str);

                return str;
            } else {
                return requestURI;
            }
        }
    }
}
