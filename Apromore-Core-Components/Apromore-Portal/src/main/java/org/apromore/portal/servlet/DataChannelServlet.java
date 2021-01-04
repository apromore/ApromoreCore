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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.plugincontrol.PluginExecution;
import org.apromore.portal.plugincontrol.PluginExecutionManager;

/**
 * This servlet is used to establish a communication channel for the browsers to send data request
 * to a particular running plugin, and then the plugin can respond with data.
 * This servlet will connect with the current Portal Web App to identify what is the target plugin 
 * for the data request, and then forward the request to that plugin. 
 * @todo Call MainController as class variable will create unexpected result from different machines.
 * 
 * @author Bruce Nguyen
 *
 */
public class DataChannelServlet extends HttpServlet {
    //private WebManager webManager;

    @Override
    public void init() throws ServletException {
        super.init();
        //final ServletContext ctx = getServletContext();
        //webManager = WebManager.getWebManagerIfAny(ctx);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        //ApromoreWebApp webApp = (ApromoreWebApp)webManager.getWebApp();
        String executionId = req.getParameter("pluginExecutionId");
        PluginExecutionManager pluginEM = MainController.getController().getPluginExecutionManager(); 
        PluginExecution pluginExec = pluginEM.getPluginExecution(executionId);
        if (pluginExec == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No PluginExecution found from pluginExecutionId=" + executionId);
        }
        else {
            pluginExec.processRequest(req, resp);
        }
    }
}
