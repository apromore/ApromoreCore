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
package org.apromore.portal.plugincontrol;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.portal.dialogController.BaseController;

/**
 * This class represents a running plugin.
 * 
 * @author Bruce Nguyen
 *
 */
public class PluginExecution {
    private BaseController pluginController;
    
    public PluginExecution(BaseController pluginController) {
        this.pluginController = pluginController;
    }
    
    public BaseController getPluginController() {
        return this.pluginController;
    }
    
    public void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String result = pluginController.processRequest(request.getParameterMap());
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            byte[] data = result.getBytes();
            os.write(data);
            response.flushBuffer();
            
//            PrintWriter out = response.getWriter();
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            out.print(result);
//            out.flush(); 
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }
    }
}
