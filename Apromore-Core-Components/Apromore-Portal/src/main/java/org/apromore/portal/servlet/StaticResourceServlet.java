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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;

public class StaticResourceServlet extends HttpServlet {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(StaticResourceServlet.class);
    private static final String FAVICON = "favicon.ico";

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {
        boolean err = false;

        if (("/" + FAVICON).equals(req.getPathInfo())) {
            try {
                InputStream in = getClass().getClassLoader().getResourceAsStream(FAVICON);
                if (in == null) {
                    err = true;
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("image/x-icon");
                    OutputStream out = resp.getOutputStream();
                    ByteStreams.copy(in, out);
                }
            } catch (Exception e) {
                err = true;
            }
        } else {
            err = true;
        }
        if (err) {
            try {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (Exception e) {
                LOGGER.error("Error with getting a static resource", e);
            }

        }
    }
    
}
