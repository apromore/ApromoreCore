/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.servlets;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.OryxResult;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

/**
 * YAWLImportServlet converts a YAWL specification (.yawl file) to the JSON representation of an Oryx diagram. It only supports POST requests with the
 * YAWL specification submitted as parameter "data".
 *
 * It should be accessible at: /yawlimport
 *
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 *
 */
public class YAWLImportServlet extends HttpServlet {

    private static final long serialVersionUID = 3547545139919885168L;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException {

        String yawlData = req.getParameter("data");
        String standalone = req.getParameter("standalone");
        boolean isStandalone = (standalone != null && standalone.equals("true"));

        /* Transform and return as JSON */
        try {

            if (yawlData == null || yawlData.isEmpty()) {
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                res.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                JSONObject importJson = getJsonFromYAWL(yawlData, isStandalone);
                res.setContentType("application/json; charset=UTF-8");
                res.setStatus(200);
                importJson.write(res.getWriter());
            }

        } catch (Exception e) {
            try {
                e.printStackTrace();
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                if (e.getCause() != null) {
                    res.getWriter().write(e.getCause().getMessage());
                } else {
                    res.getWriter().write(e.getMessage());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * Call the conversion from YAWL to the Oryx JSON format. Default visibility to ease JUnit testing without having to mock a ServletRequest
     *
     * @param yawlXml
     *            a valid YAWL specification
     * @param isStandalone
     * @return a valid Oryx diagram in its JSON representation
     * @throws JSONException
     *             in case of an invalid JSON conversion
     * @throws YSyntaxException
     *             in case of an invalid YAWL specification
     * @throws IOException
     *             in case of ?? (error in Loading Layout)
     * @throws JDOMException
     *             in case of ?? (error in Loading Layout)
     */
    JSONObject getJsonFromYAWL(final String yawlXml, final boolean isStandalone) throws JSONException, YSyntaxException, JDOMException, IOException {

        // Convert YAWL Model
        YAWLConverter yawlConverter = new YAWLConverter();
        OryxResult oryxResult = yawlConverter.convertYAWLToOryx(yawlXml);

        if (isStandalone) {
            String warningMessage = "";
            for (ConversionException e : oryxResult.getWarnings()) {
                warningMessage += "- ";
                warningMessage += e.getMessage();
                if (e.getCause() != null) {
                    warningMessage += " (" + e.getCause().getMessage();
                }
                warningMessage += ")\n";
            }

            // Write Converted Model
            JSONArray jsonDiagramList = new JSONArray();

            for (Entry<String, BasicDiagram> diagramEntry : oryxResult.getDiagrams()) {
                JSONObject model = GenericJSONBuilder.parseModel(diagramEntry.getValue());
                jsonDiagramList.put(model);
            }

            JSONObject jsonImportObject = new JSONObject();
            jsonImportObject.put("diagrams", jsonDiagramList);
            jsonImportObject.put("rootNetName", oryxResult.getRootNetId());
            jsonImportObject.put("hasFailed", oryxResult.hasFailed());
            jsonImportObject.put("hasWarnings", oryxResult.hasWarnings());
            jsonImportObject.put("warnings", warningMessage);

            return jsonImportObject;
        } else {
            return GenericJSONBuilder.parseModel(oryxResult.getRootDiagram());
        }
    }

}