/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.common.converters.bpstruct.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.serialize.SerializationException;
import org.apache.log4j.Logger;
import org.apromore.common.converters.bpstruct.BasicDiagramToProcessModel;
import org.apromore.common.converters.bpstruct.JSON2Process;
import org.apromore.common.converters.bpstruct.Process2JSON;
import org.apromore.common.converters.bpstruct.ProcessModelToBasicDiagram;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;

/**
 * Servlet to access the BPStruct service.
 */
public class BPStructServlet extends HttpServlet {

    private static final long serialVersionUID = 4651531154294830523L;

    private static final Logger LOGGER = Logger.getLogger(BPStructServlet.class);
    private static final String BPSTRUCT_SERVER = "http://localhost:8080/bpstruct/rest/v1/structure/max";

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        doPost(req, res);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String jsonData = req.getParameter("data");
        String diagramType = req.getParameter("type");

        /* Transform and return */
        try {
            if (jsonData == null || jsonData.isEmpty()) {
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                res.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                String diagramJson = convert(jsonData);
                res.setContentType("application/xml; charset=UTF-8");
                res.setStatus(200);
                res.getWriter().write(diagramJson);
            }
        } catch (Exception e) {
            try {
                LOGGER.fatal(e.toString());
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

    private String convert(String jsonData) throws Exception {
        BasicDiagram newDiagram ;
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);

        if (diagram != null) {
            Process process = convertToProcessModel(diagram);
            String json = getJsonFromProcess(process);

            String structuredJson = processWithBPStruct(json);
            Process newProcess = getProcessFromJson(structuredJson);

            newDiagram = convertToBasicDiagram(newProcess);
            newDiagram.setBounds(diagram.getBounds());

            return updateDiagramWithBPStructExtras(newDiagram, structuredJson);
        }

        return null;
    }

    /** Update the json to send back with the BPStruct Extras. */
    private String updateDiagramWithBPStructExtras(BasicDiagram newDiagram, String structuredJson) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject bpJson = new JSONObject(structuredJson);

        json.put("data_json", newDiagram.getJSON());
        if (bpJson.has("hasChanged")) {
            json.put("hasChanged", bpJson.get("hasChanged"));
        }
        if (bpJson.has("errors")) {
            json.put("errors", bpJson.get("errors"));
        }

        return json.toString();
    }


    /* Convert a basicDiagram into the Format required for BPStruct. */
    private de.hpi.bpt.process.Process convertToProcessModel(BasicDiagram diagram) throws SerializationException {
        BasicDiagramToProcessModel bd2pm = new BasicDiagramToProcessModel();
        return bd2pm.convert(diagram);
    }

    /* Convert a basicDiagram into the Format required for BPStruct. */
    private BasicDiagram convertToBasicDiagram(Process process) throws SerializationException {
        ProcessModelToBasicDiagram pm2bd = new ProcessModelToBasicDiagram();
        return pm2bd.convert(process);
    }


    /* Converts the Process to JSon. */
    private String getJsonFromProcess(Process process) {
        String json = null;
        try {
            json = Process2JSON.convert(process);
        } catch (SerializationException e) {
            LOGGER.error(e.getMessage());
        }
        return json;
    }

    /* Converts the JSon to Process. */
    private Process getProcessFromJson(String json) {
        Process process = null;
        try {
            process = JSON2Process.convert(json);
        } catch (SerializationException e) {
            LOGGER.error(e.getMessage());
        }
        return process;
    }



    /* Send the converted document to BPStruct and see if we have any changes. */
    private String processWithBPStruct(String json) throws Exception {
        if (json != null && !json.equals("")) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(BPSTRUCT_SERVER);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(json.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return null;
    }

}