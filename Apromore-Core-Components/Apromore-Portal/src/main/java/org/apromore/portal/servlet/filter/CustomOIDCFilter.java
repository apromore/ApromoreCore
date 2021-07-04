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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.NodesRegistrationManagement;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomOIDCFilter extends KeycloakOIDCFilter {

  Logger logger = LoggerFactory.getLogger(getClass());
  private ServletContext servletContext;

  public CustomOIDCFilter(ServletContext context) {
    this.servletContext = context;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

    skipPattern = FilterRegexUtil.getPattern();

    try (FileInputStream keyCloakJsonFile = new FileInputStream(
        new File(System.getProperty("karaf.etc"), "/config/" + "keycloak.json"));) {

      ObjectMapper mapper = new ObjectMapper();
      AdapterConfig adapterConfig = mapper.readValue(keyCloakJsonFile, AdapterConfig.class);
      KeycloakDeployment kcDeployment = KeycloakDeploymentBuilder.build(adapterConfig);

      deploymentContext = new AdapterDeploymentContext(kcDeployment);
      servletContext.setAttribute(AdapterDeploymentContext.class.getName(), deploymentContext);
      nodesRegistrationManagement = new NodesRegistrationManagement();

    } catch (IOException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      throw new RuntimeException("cannot read kc file");
    }
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String requestPath = request.getRequestURI().substring(request.getContextPath().length());
    HttpSession existingSession = request.getSession(false);

    if (FilterRegexUtil.isMatchingFilterRegex(requestPath)) {
      chain.doFilter(request, response);
      return;
    }

    if (requestPath.startsWith("/login.zul")) {
      String redirecturi = getRedirectUri(request, requestPath);
      response.sendRedirect(redirecturi);
      return;
    }



    super.doFilter(req, res, chain);
  }

  private String getRedirectUri(HttpServletRequest request, String requestPath) {
    return request.getRequestURL().toString().substring(0,
        request.getRequestURL().toString().indexOf(requestPath));
  }



}
