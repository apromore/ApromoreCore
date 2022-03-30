/*-
 * #%L This file is part of "Apromore Enterprise Edition". %% Copyright (C) 2019 - 2021 Apromore Pty
 * Ltd. All Rights Reserved. %% NOTICE: All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any. The intellectual and technical concepts
 * contained herein are proprietary to Apromore Pty Ltd and its suppliers and may be covered by U.S.
 * and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless
 * prior written permission is obtained from Apromore Pty Ltd. #L%
 */
package org.apromore.portal.servlet.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class SameSiteFilter extends GenericFilterBean {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse resp = (HttpServletResponse) response;
    resp.setHeader("Set-Cookie", "HttpOnly; Secure; SameSite=strict");
    resp.setHeader("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains ; preload");
    chain.doFilter(request, response);
  }


}
