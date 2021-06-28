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
package org.apromore.portal;

import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Blanket header modifications for all web traffic through the Portal.
 *
 * This filter currently performs the following modifications:
 * <ul>
 * <li>If a <code>site.contentSecurityPolicy</code> property is present in <code>site.cfg</code>,
 *     add a <code>Content-Security-Policy</code> HTTP header with the given value</li>
 * <li>If a <code>site.reportingEndpoints</code> property is present in <code>site.cfg</code>,
 *     add a <code>Reporting-Endpoints</code> HTTP header with the given value</li>
 * <li>If a <code>site.server</code> property is present in <code>site.cfg</code>,
 *     override the <code>Server</code> HTTP header with the given value</li>
 * <li>If a <code>site.strictTransportSecurity</code> property is present in <code>site.cfg</code>,
 *     add a <code>Strict-Transport-Security</code> HTTP header with the given value</li>
 * </ul>
 */
public class PortalFilter implements Filter {

    /**
     * If this property key appears in <code>site.cfg</code>, a Content-Security-Policy HTTP header
     * will added using the property's value.
     */
    public static final String CONTENT_SECURITY_POLICY = "site.contentSecurityPolicy";

    /**
     * If this property key appears in <code>site.cfg</code>, a Reporting-Endpoints HTTP
     * header will added using the property's value.
     */
    public static final String REPORTING_ENDPOINTS = "site.reportingEndpoints";

    /**
     * If this property key appears in <code>site.cfg</code>, the Server HTTP header will be
     * overwritten with the property's value.
     */
    public static final String SERVER = "site.server";

    /**
     * If this property key appears in <code>site.cfg</code>, a Strict-Transport-Security HTTP
     * header will added using the property's value.
     */
    public static final String STRICT_TRANSPORT_SECURITY = "site.strictTransportSecurity";

    private String contentSecurityPolicy;
    private String reportingEndpoints;
    private String server;
    private String strictTransportSecurity;

    // Filter interface methods

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        try {
            Map<String, Object> siteConfiguration = OSGi.getConfiguration("site", filterConfig.getServletContext());

            contentSecurityPolicy   = (String) siteConfiguration.get(CONTENT_SECURITY_POLICY);
            reportingEndpoints      = (String) siteConfiguration.get(REPORTING_ENDPOINTS);
            server                  = (String) siteConfiguration.get(SERVER);
            strictTransportSecurity = (String) siteConfiguration.get(STRICT_TRANSPORT_SECURITY);

        } catch (IOException e) {
            throw new ServletException("Unable to initialize filter from site.cfg", e);
        }
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (contentSecurityPolicy != null) {
            httpResponse.setHeader("Content-Security-Policy", contentSecurityPolicy);
        }

        if (reportingEndpoints != null) {
            httpResponse.setHeader("Reporting-Endpoints", reportingEndpoints);
        }

        if (server != null) {
            httpResponse.setHeader("Server", server);
        }

        if (strictTransportSecurity != null) {
            httpResponse.setHeader("Strict-Transport-Security", strictTransportSecurity);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        contentSecurityPolicy   = null;
        reportingEndpoints      = null;
        server                  = null;
        strictTransportSecurity = null;
    }
}
