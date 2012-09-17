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

import de.hbrs.oryx.yawl.converter.YAWLConverter;
import de.hbrs.oryx.yawl.converter.YAWLConverter.OryxResult;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map.Entry;

/**
 * YAWLImportServlet converts a YAWL specification (.yawl file) to the JSON
 * representation of an Oryx diagram. It only supports POST requests with the
 * YAWL specification submitted as parameter "data".
 * <p/>
 * It should be accessible at: /yawlimport
 *
 * @author Felix Mannhardt (University of Applied Sciences Bonn-Rhein-Sieg)
 */
public class YAWLImportServlet extends HttpServlet {

    private static final long serialVersionUID = 3547545139919885168L;

    /*
      * (non-Javadoc)
      *
      * @see
      * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
      * , javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {

        String yawlData = req.getParameter("data");

        /* Transform and return as JSON */
        try {
            String jsonString = getJsonFromYAWL(yawlData);
            res.setContentType("application/json");
            res.setStatus(200);
            res.getWriter().print(jsonString);
        } catch (Exception e) {
            try {
                e.printStackTrace();
                res.setStatus(500);
                res.setContentType("text/plain");
                res.getWriter().write(e.getCause().getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * Call the conversion from YAWL to the Oryx JSON format.
     *
     * @param yawlXml a valid YAWL specification
     * @return a valid Oryx diagram in its JSON representation
     * @throws JSONException    in case of an invalid JSON conversion
     * @throws YSyntaxException in case of an invalid YAWL specification
     * @throws IOException      in case of ?? (error in Loading Layout)
     * @throws JDOMException    in case of ?? (error in Loading Layout)
     */
    private String getJsonFromYAWL(String yawlXml) throws JSONException, YSyntaxException, JDOMException, IOException {

        // Convert YAWL Model
        YAWLConverter yawlConverter = new YAWLConverter();
        OryxResult oryxResult = yawlConverter.convertYAWLToOryx(yawlXml);

        String warningMessage = "";
        for (ConversionException e : oryxResult.getWarnings()) {
            warningMessage += "- ";
            warningMessage += e.getMessage();
            if (e.getCause() != null) {
                warningMessage += " (" + e.getCause().getMessage();
            }
            warningMessage += ")\n";
            e.printStackTrace();
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

        return jsonImportObject.toString();
    }

}