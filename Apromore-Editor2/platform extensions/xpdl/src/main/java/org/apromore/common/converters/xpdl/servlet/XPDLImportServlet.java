/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.common.converters.xpdl.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.bpmn2xpdl.XPDLPackage;
import org.apache.commons.httpclient.HttpStatus;
import org.apromore.common.converters.xpdl.XPDLToJSONConverter;

/**
 * XPDLImportServlet: Used to import and visualise an XPDL model.
 * It should be accessible at: /xpdlimport
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class XPDLImportServlet extends HttpServlet {

    private static final long serialVersionUID = -7357279951080137753L;

    /* The GET Method for this servlet. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /* The POST Method for this servlet. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String data = req.getParameter("data");

        try {
            XPDLToJSONConverter converter = new XPDLToJSONConverter();
            XPDLPackage xpdlModel = converter.getXPDLModel(data);

            res.setStatus(HttpStatus.SC_OK);
            res.setContentType("application/json");
            res.getWriter().print(converter.getXPDL(xpdlModel));
            res.getWriter().flush();
            res.getWriter().close();
        } catch (Exception e) {
            res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            res.setContentType("application/json");
            res.getWriter().write("{success: false, data: \"" + e.getMessage() + "\"}");
        }
    }

}
