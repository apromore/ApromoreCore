package org.apromore.plugin.editor.metrics;

// Java 2 Standard
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

// Java 2 Enterprise
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;

import au.edu.qut.metrics.ComplexityCalculator;
import org.apache.log4j.Logger;
import org.apromore.service.BPMNDiagramImporter;
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

import javax.servlet.ServletConfig;

public class MetricsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MetricsServlet.class);

    private BPMNDiagramImporter importerService;

    public void init(ServletConfig config) throws ServletException {
        Object o = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()).getAutowireCapableBeanFactory().getBean("importerService");
        if(o instanceof  BPMNDiagramImporter) importerService = (BPMNDiagramImporter) o;
        else throw new ServletException("still not working!");
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
                LOGGER.error("Measurement failed: " + e.toString(), e);
                JSONObject json = new JSONObject();
                json.put("errors", e.toString());
                response.setStatus(500);
                response.setContentType("text/plain; charset=UTF-8");
            } catch (Exception e1) {
                LOGGER.error("Unable to report servlet error to JSON: " + e1.toString(), e1);
            }
        }
//
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/plain");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        Writer w = response.getWriter();
//        w.write("Hello world!");
//        w.close();
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
        JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class,
                Configurable.class,
                ConfigurationAnnotationAssociation.class,
                ConfigurationAnnotationShape.class,
                Variants.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbContext.createMarshaller().marshal(bpmn, baos);
        String process = baos.toString("utf-8");

        ComplexityCalculator cc = new ComplexityCalculator();
        BPMNDiagram bpmnDiagram = importerService.importBPMNDiagram(process);

        /* result already in json format */
        Map<String, String> metrics = cc.computeComplexity(bpmnDiagram, true, true, true, true, true, true, true, true, true);

        JSONObject result = new JSONObject();

        for (String key : metrics.keySet()) {
            LOGGER.info(key + " : " + metrics.get(key));
            result.put(key, metrics.get(key));
        }
        LOGGER.info("metrics: " + metrics);

        return result.toString();
    }

}