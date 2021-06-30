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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class KeycloakLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private static final Logger LOGGER =
      PortalLoggerFactory.getLogger(KeycloakLoginUrlAuthenticationEntryPoint.class);

  private static final String ENV_KEYCLOAK_REALM_NAME_KEY = "KEYCLOAK_REALM_NAME";
  private static final String KEYCLOAK_REALM_PLACEHOLDER = "<keycloakRealm>";
  private static final String STATE_UUID_PLACEHOLDER = "<state_uuid>";
  private static final String FULL_RETURN_PATH_PLACEHOLDER = "<full_return_path>";

  private String fullConfigurableReturnPath =
      new String(Base64.getEncoder().encode("http://localhost:8181/".getBytes()));
  private boolean utiliseKeycloakSso = false;

  private String keycloakLoginFormUrl;

  public void setFullProtocolHostPortUrl(final String fullProtocolHostPortUrl) {
    fullConfigurableReturnPath =
        new String(Base64.getEncoder().encode(fullProtocolHostPortUrl.getBytes()));

    LOGGER.trace("Set fullConfigurableReturnPath to {}", fullConfigurableReturnPath);
  }

  public void setUseKeycloakSso(final boolean useKeycloakSso) {
    utiliseKeycloakSso = useKeycloakSso;

    LOGGER.trace("Set useKeycloakSso to {}", utiliseKeycloakSso);
  }

  public String getKeycloakLoginFormUrl() {
    return keycloakLoginFormUrl;
  }

  public void setKeycloakLoginFormUrl(final String keycloakLoginFormUrl) {
    if ((this.keycloakLoginFormUrl == null)
        || (this.keycloakLoginFormUrl.contains(KEYCLOAK_REALM_PLACEHOLDER))) {
      final String keycloakRealm = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);
      LOGGER.trace("FROM environment property keycloakRealm[" + keycloakRealm + "]");

      if (keycloakRealm != null) {
        String tmpUrl = keycloakLoginFormUrl;

        final String randomStateUuid = UUID.randomUUID().toString();
        LOGGER.trace("randomStateUuid: {}", randomStateUuid);

        tmpUrl = tmpUrl.replaceFirst(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealm);
        tmpUrl = tmpUrl.replaceFirst(STATE_UUID_PLACEHOLDER, randomStateUuid);
        tmpUrl = tmpUrl.replaceFirst(FULL_RETURN_PATH_PLACEHOLDER, fullConfigurableReturnPath);
        LOGGER.trace(">>>>> >>> > tmpUrl=[" + tmpUrl + "]");

        this.keycloakLoginFormUrl = tmpUrl;
      } else {
        LOGGER.trace(
            "Keycloak login realm was null - maybe keycloak feature turned-off? [proceeding]");
      }
    }
  }

  @Override
  public void commence(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse,
      final AuthenticationException authenticationException) throws IOException, ServletException {
    final String requestServletPath = httpServletRequest.getServletPath();
    final String requestURL = httpServletRequest.getRequestURL().toString();
    LOGGER.trace("requestServletPath {}", requestServletPath);
    LOGGER.trace("requestURL {}", requestURL);

    super.commence(httpServletRequest, httpServletResponse, authenticationException);
  }

  @Override
  protected String buildRedirectUrlToLoginPage(final HttpServletRequest request,
      final HttpServletResponse response, final AuthenticationException authException) {

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
  protected String determineUrlToUseForThisRequest(final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse, final AuthenticationException exception) {
    if (utiliseKeycloakSso) {
      LOGGER.trace("[ Utilising keycloak ]");

      final String loginFormPattern = getKeycloakLoginFormUrl();
      LOGGER.trace("### loginFormPattern: {}", loginFormPattern);

      final String keycloakRealmOfCustomer = System.getenv(ENV_KEYCLOAK_REALM_NAME_KEY);
      LOGGER.trace("keycloakRealmOfCustomer {}", keycloakRealmOfCustomer);

      String loginUrl =
          loginFormPattern.replaceAll(KEYCLOAK_REALM_PLACEHOLDER, keycloakRealmOfCustomer);
      LOGGER.trace("loginUrl[1] {}", loginUrl);
      loginUrl = loginUrl.replaceAll(FULL_RETURN_PATH_PLACEHOLDER, fullConfigurableReturnPath);
      LOGGER.trace("loginUrl[2] {}", loginUrl);

      LOGGER.trace(">>> Resolved Keycloak loginUrl (via securityms): {}", loginUrl);

      return loginUrl;
    } else {
      LOGGER.trace("[ Keycloak SSO turned off ]");

      String requestUriStr = httpServletRequest.getRequestURL().toString().trim();
      HttpSession session = httpServletRequest.getSession(false);
      URI uri = null;
      try {
        uri = new URI(requestUriStr);
      } catch (URISyntaxException e) {
        LOGGER.error("Bad request URI", e);
      }
      if (session == null || session.getAttribute("USER") == null) {
        requestUriStr = uri.resolve("/login.zul").toString();
      } else {
        requestUriStr = uri.resolve("").toString();
      }

      return requestUriStr;
    }
  }
}
