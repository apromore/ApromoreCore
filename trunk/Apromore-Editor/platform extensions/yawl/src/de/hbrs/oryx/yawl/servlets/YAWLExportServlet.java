/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See: http://www.gnu.org/licenses/lgpl-3.0
 *
 */
package de.hbrs.oryx.yawl.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.YAWLResult;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

/**
 * YAWLExportServlet converts a Oryx JSON diagram to a YAWL specification (.yawl file) It only supports POST requests with the JSON submitted as
 * parameter "data".
 *
 * It should be accessible at: /yawlexport
 *
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 *
 */
public class YAWLExportServlet extends HttpServlet {

    private static final long serialVersionUID = 6881808890572459223L;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {

        String jsonData = req.getParameter("data");
        String standalone = req.getParameter("standalone");
        boolean isStandalone = (standalone != null && standalone.equals("true"));

        /* Transform and return as YAWL XML */
        try {
            if (jsonData == null || jsonData.isEmpty()) {
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                res.getWriter().write("Empty or missing parameter 'data'!");
            } else if (isStandalone) {
                JSONObject exportJson = getYAWLFromJSON(jsonData);
                res.setContentType("application/json; charset=UTF-8");
                res.setStatus(200);
                exportJson.write(res.getWriter());
            } else {
                String yawlXml = getYAWLXmlFromJSON(jsonData);
                res.setContentType("application/xml; charset=UTF-8");
                res.setStatus(200);
                res.getWriter().write(yawlXml);
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

    private String getYAWLXmlFromJSON(final String jsonData) {
        // Convert Oryx Diagram
        YAWLConverter yawlConverter = new YAWLConverter();
        //TODO change for multiple subnets
        // For the moment we don't get any subnets from Signavio, but the YAWLConverter expects the JSON 
        // to contain an array of subnets. So we just wrap the JSON into our own format here:
        String rewrittenJSONData = String.format("{subDiagrams: [], rootDiagram: %1$s}",jsonData);
        YAWLResult yawlResult = yawlConverter.convertOryxToYAWL(rewrittenJSONData);
        return yawlResult.getYAWLAsXML();
    }

    /**
     * Default visibility to ease JUnit testing without having to mock a ServletRequest
     *
     * @param jsonData
     * @return
     * @throws JSONException
     */
    JSONObject getYAWLFromJSON(final String jsonData) throws JSONException {

        // Convert Oryx Diagram
        YAWLConverter yawlConverter = new YAWLConverter();

        YAWLResult yawlResult = yawlConverter.convertOryxToYAWL(jsonData);

        String warningMessage = "";
        for (ConversionException e : yawlResult.getWarnings()) {
            warningMessage += "- ";
            warningMessage += e.getMessage();
            if (e.getCause() != null) {
                warningMessage += " (" + e.getCause().getMessage();
            }
            warningMessage += ")\n";
        }

        // Write Conversion Result:
        JSONObject jsonExportObject = new JSONObject();
        jsonExportObject.put("yawlXML", yawlResult.getYAWLAsXML());
        jsonExportObject.put("warnings", warningMessage);
        jsonExportObject.put("hasFailed", yawlResult.hasFailed());
        jsonExportObject.put("hasWarnings", yawlResult.hasWarnings());
        jsonExportObject.put("filename", yawlResult.getFilename());

        return jsonExportObject;
    }

}