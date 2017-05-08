/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.plugin.editor.ibpstruct;

// Java 2 Standard
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Java 2 Enterprise
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import java.io.StringReader;
import java.util.List;
import javax.xml.transform.stream.StreamSource;

import au.edu.qut.bpmn.exporter.impl.BPMNDiagramExporterImpl;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import org.apache.log4j.Logger;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.ibpstruct.IBPStructService;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class IBPStructServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(IBPStructServlet.class);

    private BPMNDiagramImporter importerService;
    private IBPStructService ibpstructService;

    public void init(ServletConfig config) throws ServletException {
        Object o;
        o = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()).getAutowireCapableBeanFactory().getBean("importerService");
        if(o instanceof  BPMNDiagramImporter) importerService = (BPMNDiagramImporter) o;

        o = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()).getAutowireCapableBeanFactory().getBean("ibpstructService");
        if(o instanceof  IBPStructService) ibpstructService = (IBPStructService) o;
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
                String meatrics = convert(jsonData);
                response.setContentType("application/json");
                response.setStatus(200);
                response.getWriter().write(meatrics);
            }
        } catch (Exception e) {
            try {
                LOGGER.error("Structuring failed: " + e.toString(), e);
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
        JAXBContext jaxbContext = null;

        // BPMN DOM -> BPMN-formatted String
        jaxbContext = JAXBContext.newInstance(  Definitions.class, Configurable.class,
                                                ConfigurationAnnotationAssociation.class,
                                                ConfigurationAnnotationShape.class, Variants.class );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbContext.createMarshaller().marshal(bpmn, baos);
        String unstructuredBpmnModelString = baos.toString("utf-8");

        BPMNDiagram unstructuredDiagram = importerService.importBPMNDiagram(unstructuredBpmnModelString);
        BPMNDiagram structuredDiagram = ibpstructService.structureProcess(unstructuredDiagram);

        BPMNDiagramExporterImpl exporterService = new BPMNDiagramExporterImpl();
        String structuredBpmnModelString = exporterService.exportBPMNDiagram(structuredDiagram);

        // BPMN-formatted String -> BPMN DOM
        StreamSource source = new StreamSource(new StringReader(structuredBpmnModelString));
        Definitions structuredBpmnModel = jaxbContext.createUnmarshaller().unmarshal(source, Definitions.class).getValue();

        // BPMN DOM -> Signavio Diagram
        BPMN2DiagramConverter bpmn2DiagramConverter = new BPMN2DiagramConverter("/signaviocore/editor/");
        List<BasicDiagram> diagrams = bpmn2DiagramConverter.getDiagramFromBpmn20(structuredBpmnModel);
        assert diagrams != null;
        if (diagrams.size() < 1) {
            throw new Exception("Signavio JSON diagram could not be obtained from restructured BPMN model: ");// + structuredBpmnModelString);
        }
        else if (diagrams.size() > 1) {
            log("Multiple diagrams obtained from restructured BPMN model; arbitrarily choosing the first to reload into the editor");
        }
        BasicDiagram newDiagram = diagrams.get(0);
        newDiagram.setBounds(basicDiagram.getBounds());

        // Signavio Diagram -> JSON-formatted String
        JSONObject json = new JSONObject();
        json.put("data_json", newDiagram.getJSON());
        json.put("hasChanged", true);
        //json.put("errors", ...);
        return json.toString();
    }

}
