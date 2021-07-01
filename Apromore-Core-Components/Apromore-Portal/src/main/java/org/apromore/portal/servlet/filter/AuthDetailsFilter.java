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
package org.apromore.portal.servlet.filter;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apromore.manager.client.ManagerService;
import org.apromore.portal.model.UserType;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component("authDetailsFilter")
public class AuthDetailsFilter implements Filter {

  Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  ManagerService managerService;

  @Value("${site.useKeycloakSso}")
  private boolean useKeyCloak = true;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String requestPath = req.getRequestURI().substring(req.getContextPath().length());
    HttpSession existingSession = req.getSession(false);

    boolean isExistingSession = existingSession == null ? false
        : existingSession.getAttribute("USER") == null ? false : true;


    if (useKeyCloak && isExistingSession && requestPath.equals("/j_spring_security_logout")) {

      req.logout();
      existingSession.invalidate();
      res.sendRedirect("/");
      return;
    }



    if (!useKeyCloak || FilterRegexUtil.isMatchingFilterRegex(requestPath) || isExistingSession) {
      chain.doFilter(request, response);
      return;
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request
        .getAttribute("org.keycloak.KeycloakSecurityContext");

    AccessToken token = context.getToken();

    String userName = token.getPreferredUsername();
    userName = userName == null ? token.getEmail() : userName;
    String email = token.getEmail();
    UserType userType = managerService.readUserByUsername(userName);
    try {
      userType = userType == null ? managerService.readUserByEmail(email) : userType;
    } catch (Exception e1) {
      logger.warn("User not in system ", ExceptionUtils.getStackTrace(e1));
    }

    if (Objects.isNull(userType)) {
      userType = new UserType(userName, email, token.getGivenName(), token.getFamilyName());
      try {
        userType = managerService.writeUser(userType);
      } catch (Exception e) {
        ExceptionUtils.getStackTrace(e);
        throw new RuntimeException("Error while creating user");
      }
    }

    httpRequest.getSession().setAttribute("USER", userType);

    chain.doFilter(httpRequest, response);

  }

  @Override
  public void destroy() {

  }

}
