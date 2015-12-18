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

package org.apromore.common.converters.pnml.servlet;

import org.apromore.common.converters.pnml.JSONToPNMLConverter;
import org.apromore.pnml.PNMLSchema;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Exports the PNML as an output stream.
 */
public class PNMLExportServlet extends HttpServlet {

	private static final long serialVersionUID = -8374877061121257562L;

    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        /* Transform and return as JSON */
        try {
            String jsonString = req.getParameter("data");

            String pnmlString = jsonToPnml(jsonString);

            res.setContentType("application/xml; charset=UTF-8");
            res.setStatus(200);
            res.getWriter().write(pnmlString);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            if (e.getCause() != null) {
                res.setContentType("text/plain");
                assert e.getCause() != null;
                assert e.getCause().getMessage() != null;
                try {
                    res.getWriter().write(e.getCause().getMessage());
                } catch (IOException e1) {
                    throw new ServletException(e1);
                }
            }
        }
    }


    /**
     * @param jsonString  an PetriNet represented in Signavio's serialized JSON, never <code>null</code>
     * @return an PNML serialization of the EPC
     */
    private String jsonToPnml(String jsonString) throws JAXBException, JSONException, SAXException {
        ServletContext context = getServletConfig().getServletContext();

        // Workaround because the JSON we get doesn't have its stencil set namespace set
        JSONObject object = new JSONObject(jsonString);
        JSONObject stencilset = object.getJSONObject("stencilset");
        stencilset.put("namespace", "http://b3mn.org/stencilset/petrinet#");
        object.put("stencilset", stencilset);
        jsonString = object.toString();

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        JSONToPNMLConverter jsonToPnmlConverter = new JSONToPNMLConverter();
        context.log("TALISMAN-JSON: " + jsonString);
        PNMLSchema.marshalPNMLFormat(baos2, jsonToPnmlConverter.toPNML(BasicDiagramBuilder.parseJson(jsonString)), false /* is validating */);

        return baos2.toString();
    }

}
