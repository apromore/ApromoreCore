/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.common.converters.measurement.servlet;

import java.io.ByteArrayOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet to access the BPStruct service.
 */
public class MeasurementServlet extends HttpServlet {

    private static final long serialVersionUID = 5762642265305971634L;

    private static final Logger LOGGER = Logger.getLogger(MeasurementServlet.class);

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
                String measurements = convert(jsonData);
                res.setContentType("application/json");
                res.setStatus(200);
                res.getWriter().write(measurements);
            }
        } catch (Exception e) {
            try {
                LOGGER.error("Measurement failed: " + e.toString(), e);
                JSONObject json = new JSONObject();
                json.put("errors", e.toString());
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
            } catch (Exception e1) {
                LOGGER.error("Unable to report servlet error to JSON: " + e1.toString(), e1);
            }
        }
    }

    private String convert(String jsonData) throws Exception {

        // JSON-formatted String -> Signavio Diagram
        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        if (diagram == null) {
            return null;
        }
        assert diagram != null;

        // Signavio Diagram -> BPMN DOM
        Diagram2BpmnConverter diagram2BpmnConverter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions bpmn = diagram2BpmnConverter.getDefinitionsFromDiagram();

        // BPMN DOM -> BPMN-formatted String
        JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class,
                                                          Configurable.class,
                                                          ConfigurationAnnotationAssociation.class,
                                                          ConfigurationAnnotationShape.class,
                                                          Variants.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbContext.createMarshaller().marshal(bpmn, baos);
        String process = baos.toString("utf-8");

        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext());
        org.apromore.service.MeasurementsService measurementsService = (org.apromore.service.MeasurementsService) applicationContext.getAutowireCapableBeanFactory().getBean("measurementsService");

        /* result already in json format */
        String result = measurementsService.computeSimplicity(process);

        return result;
    }
}
