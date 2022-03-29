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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.portal.plugincontrol.PluginExecution;
import org.apromore.portal.plugincontrol.PluginExecutionException;
import org.apromore.portal.plugincontrol.PluginExecutionManager;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.http.WebManager;

/**
 * This servlet is a supplement to ZK in order to establish a data communication channel: a plugin front-end (browser) 
 * sends data request to a plugin instance and then the plugin instance sends data back to the front-end.
 * ZK's DHtmlLayoutServlet and DHtmlUpdateServlet don't support this kind of communication or it is awkward to do that
 * as some plugin javascript code could reside outside the ZK's front-end engine (e.g. Web Worker).
 *   
 * @author Bruce Nguyen
 *
 */
public class DataChannelServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String executionId = req.getParameter("pluginExecutionId");
        try {
            PluginExecution pluginExec = PluginExecutionManager.getPluginExecution(executionId, getZKSession(req));
            pluginExec.processRequest(req, resp);
        } catch (PluginExecutionException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
    
    // This method connects from Web server request to ZK session
    // It is expected that ZK's session was created before this servlet (via ZK's main servlet)
    // Otherwise an exception must be raised.
    private Session getZKSession(final HttpServletRequest req) throws PluginExecutionException {
        ServletContext ctx = req.getServletContext();
        Session sess = WebManager.getSession(ctx, req, false);
        if (sess == null) {
            throw new PluginExecutionException("User session doesn't exist, lost or destroyed.");
        }
        return sess;
    }
    
}
