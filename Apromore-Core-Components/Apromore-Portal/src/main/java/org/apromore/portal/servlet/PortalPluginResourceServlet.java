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

package org.apromore.portal.servlet;

import com.google.common.io.ByteStreams;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apromore.plugin.portal.PortalPlugin;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.util.Locales;

/**
 * Serves resources with <code>.png</code> or <code>.svg</code> extensions
 * from {@link PortalPlugin} bundles.
 *
 * The required path format is <code>portalPluginResource/<var>groupLabel</var>/<var>label</var>/<var>path</var>.<var>extension</var></code>
 * where:
 * <ul>
 * <li><var>groupLabel</var> and <var>label</var> identify the {@link PortalPlugin}</li>
 * <li><var>path</var>.<var>extension</var> names a resource within the {@link PortalPlugin} bundle</li>
 * <li><var>extension</var> is either <code>png</code> or <code>svg</code></li>
 * </ul>
 *
 * The path subfields are URL encoded.  Particularly, this is required to allow the path separator character '/' to occur.  It means that '+' will be treated as space, however.
 *
 * Only GET requests are supported.  The content encoding will match the extension (PNG or SVG).
 */
public class PortalPluginResourceServlet extends HttpServlet {

    private Map<String, String> contentTypeMap = new HashMap<>();

    @Override
    public void init() {
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
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        //log("Pathinfo " + req.getPathInfo());
        Pattern p = Pattern.compile("/(?<groupLabel>[^/]+)/(?<label>[^/]+)/(?<resource>[^\\.]*\\.(?<extension>[^/\\.]*))");
        Matcher m = p.matcher(req.getPathInfo());
        if (m.find()) {
            String groupLabel = URLDecoder.decode(m.group("groupLabel"), "utf-8");
            String label = URLDecoder.decode(m.group("label"), "utf-8");
            String resource = URLDecoder.decode(m.group("resource"), "utf-8");
            String extension = URLDecoder.decode(m.group("extension"), "utf-8");

            //log("Group label " + groupLabel);
            //log("Label " + label);
            //log("Resource " + resource);
            //log("Extension " + extension);

            AutowireCapableBeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getAutowireCapableBeanFactory();
            
            for (PortalPlugin portalPlugin: (List<PortalPlugin>) beanFactory.getBean("portalPlugins")){
                if (groupLabel.equals(portalPlugin.getGroup(Locales.getCurrent())) &&
                    label.equals(portalPlugin.getItemCode(Locales.getCurrent()))) {

                    //log("Portal plugin " + portalPlugin);
                    
                    try (InputStream in = portalPlugin.getResourceAsStream(resource)) {
                        if (in == null) {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to find resource for " + req.getPathInfo());
                            return;
                        }

                        String contentType = contentTypeMap.get(extension.toLowerCase());
                        if (contentType == null) {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unsupported resource extension \"" + extension + "\" for " + req.getPathInfo());
                            return;
                        }

                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.setContentType(contentType);
                        try (OutputStream out = resp.getOutputStream()) {
                            ByteStreams.copy(in, out);
                        }
                    }
                    return;
                }
            }

            // Fall through
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to find portal plugin for " + req.getPathInfo());

        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to parse path " + req.getPathInfo());
        }
    }
}
