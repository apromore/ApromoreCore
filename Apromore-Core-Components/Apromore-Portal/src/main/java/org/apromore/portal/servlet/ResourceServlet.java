/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.portal.servlet;

import com.google.common.io.ByteStreams;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apromore.plugin.portal.WebContentService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * The portal's default servlet.
 *
 * Serves static resources by checking the available {@link WebContentService}s
 * and POST requests by checking {@link HttpServlet} OSGi services.
 *
 * Only GET requests are supported for static content.
 * For ZUML templates, use the <code>zkLoader</code> servlet instead.
 *
 * For POST requests, the service must have a <code>osgi.http.whiteboard.servlet.pattern</code>
 * service property, which must match the servlet path exactly (regex patterns unsupported).
 */
public class ResourceServlet extends HttpServlet {

    private Map<String, String> contentTypeMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();

        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("svg", "image/svg+xml");
        contentTypeMap.put("css", "text/css"); // for customised icon-fonts
        contentTypeMap.put("eot", "application/vnd.ms-fontobject"); // for customised icon-fonts
        contentTypeMap.put("svg", "image/svg+xml"); // for customised icon-fonts
        contentTypeMap.put("ttf", "application/font-sfnt"); // for customised icon-fonts
        contentTypeMap.put("woff", "application/font-woff"); // for customised icon-fonts
    }

    @Override
    public void destroy() {
        contentTypeMap.clear();
        super.destroy();
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            BundleContext bundleContext = (BundleContext) getServletContext().getAttribute("osgi-bundlecontext");
            List<WebContentService> webContentServices = new ArrayList<>();
            for (ServiceReference serviceReference: (Collection<ServiceReference>) bundleContext.getServiceReferences(WebContentService.class, null)) {
                webContentServices.add((WebContentService) bundleContext.getService((ServiceReference) serviceReference));
            }
            for (WebContentService webContentService: webContentServices) {
                String path = req.getServletPath();
                if (webContentService.hasResource(path)) {
                    try (InputStream in = webContentService.getResourceAsStream(path)) {
                        if (in == null) {
                            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, webContentService + " did not produce content for " + path);
                          
                        } else {
                            resp.setContentType(contentType(path));
                            resp.setStatus(HttpServletResponse.SC_OK);
                            try (OutputStream out = resp.getOutputStream()) {
                                ByteStreams.copy(in, out);
                            }
                        }
                    }
                    return;
                }
            }

            // None of the WebContentServices claimed this path, so fall back to the default servlet
            getServletContext().getNamedDispatcher("default").forward(req, resp);

        } catch (InvalidSyntaxException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            BundleContext bundleContext = (BundleContext) getServletContext().getAttribute("osgi-bundlecontext");
            for (ServiceReference serviceReference: (Collection<ServiceReference>) bundleContext.getServiceReferences(HttpServlet.class, null)) {
                HttpServlet servlet = (HttpServlet) bundleContext.getService((ServiceReference) serviceReference);
                if (req.getServletPath().equals(serviceReference.getProperty("osgi.http.whiteboard.servlet.pattern"))) {
                    servlet.init(getServletConfig());  // TODO: create a new servlet config based on service parameters
                    servlet.service(req, resp);
                    return;
                }
            }

            resp.sendError(HttpServletResponse.SC_NOT_FOUND);

        } catch (InvalidSyntaxException e) {
            throw new ServletException(e);
        }
    }

    private String contentType(String path) {
        String extension = path.substring(path.lastIndexOf(".") + 1);
        return contentTypeMap.get(extension);
    }
}
