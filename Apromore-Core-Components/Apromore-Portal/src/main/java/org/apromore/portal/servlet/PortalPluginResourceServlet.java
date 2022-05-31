/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves resources with <code>.png</code> or <code>.svg</code> extensions
 * from the classloader.
 *
 * The required path format is <code>portalPluginResource/<var>path</var>.<var>extension</var></code>
 * where:
 * <ul>
 * <li><var>path</var>.<var>extension</var> names a resource within the classloader</li>
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
        contentTypeMap.put("ico", "image/x-icon");  // for legacy favicon.ico
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
        Pattern p = Pattern.compile("/(?<resource>.*\\.(?<extension>[^/\\.]*))");
        Matcher m = p.matcher(req.getPathInfo() == null ? req.getRequestURI() : req.getPathInfo());
        if (!m.matches()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unable to parse path " + req.getPathInfo());
            return;
        }

        String resource = URLDecoder.decode(m.group("resource"), "utf-8");
        String extension = URLDecoder.decode(m.group("extension"), "utf-8");

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
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
    }
}
