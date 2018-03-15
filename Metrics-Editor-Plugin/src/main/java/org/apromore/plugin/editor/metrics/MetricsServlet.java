/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.editor.metrics;

// Java 2 Standard
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

// Java 2 Enterprise
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;

// Third party
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.springframework.web.context.support.WebApplicationContextUtils;

// Apromore
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.metrics.MetricsService;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;


public class MetricsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MetricsServlet.class);

    private BPMNDiagramImporter importerService;
    private MetricsService metricsService;

    public void init(ServletConfig config) throws ServletException {
        importerService = (BPMNDiagramImporter)
            WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
                                      .getAutowireCapableBeanFactory()
                                      .getBean("importerService");
        metricsService = (MetricsService)
            WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
                                      .getAutowireCapableBeanFactory()
                                      .getBean("metricsService");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String jsonData = request.getParameter("data");
        String diagramType = request.getParameter("type");

        /* Transform and return */
        try {
            if (jsonData == null || jsonData.isEmpty()) {
                response.setStatus(500);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                String metrics = convert(jsonData);
                response.setContentType("application/json");
                response.setStatus(200);
                response.getWriter().write(metrics);
            }
        } catch (Exception e) {
            try {
                LOGGER.error("Measurement failed: " + e.toString(), e);
                JSONObject json = new JSONObject();
                json.put("errors", e.toString());
                response.setStatus(500);
                response.setContentType("text/plain; charset=UTF-8");
            } catch (Exception e1) {
                LOGGER.error("Unable to report servlet error to JSON: " + e1.toString(), e1);
            }
        }
    }


    private String convert(String jsonData) throws Exception {

        // JSON-formatted String -> Signavio Diagram
        BasicDiagram basicDiagram = BasicDiagramBuilder.parseJson(jsonData);
        if (basicDiagram == null) {
            return null;
        }
        assert basicDiagram != null;

        // Signavio Diagram -> BPMN DOM
        Diagram2BpmnConverter diagram2BpmnConverter = new Diagram2BpmnConverter(basicDiagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions bpmn = diagram2BpmnConverter.getDefinitionsFromDiagram();

        // BPMN DOM -> BPMN-formatted String
        JAXBContext jaxbContext = JAXBContext.newInstance(  Definitions.class, Configurable.class,
                                                            ConfigurationAnnotationAssociation.class,
                                                            ConfigurationAnnotationShape.class, Variants.class );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbContext.createMarshaller().marshal(bpmn, baos);
        String process = baos.toString("utf-8");
        BPMNDiagram bpmnDiagram = importerService.importBPMNDiagram(process);

        /* result already in json format */
        Map<String, String> metrics = metricsService.computeMetrics(bpmnDiagram, true, true, true, true, true, true, true, true, true);

        JSONObject result = new JSONObject();

        for (String key : metrics.keySet()) {
            LOGGER.info(key + " : " + metrics.get(key));
            result.put(key, metrics.get(key));
        }
        LOGGER.info("metrics: " + metrics);

        return result.toString();
    }

}
