package servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.SchemaFactory;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.xml.sax.SAXException;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

/**
 * BPMNOutputServlet converts the diagram (JSON) into a BPMN file.
 * It should be accessible at: /bpmnoutput
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BPMNOutputServlet extends HttpServlet {

    private static final long serialVersionUID = 4651531154294830523L;


    /* (non-Javadoc)
      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        // Read the test JSON
        String jsonData = req.getParameter("data");

        /* Transform and return as YAWL XML */
        try {
            if (jsonData == null || jsonData.isEmpty()) {
                res.setStatus(500);
                res.setContentType("text/plain; charset=UTF-8");
                res.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                ByteArrayOutputStream bpmn = getBPMNfromJson(jsonData);
                res.setContentType("application/xml; charset=UTF-8");
                res.setStatus(200);
                res.getWriter().write(bpmn.toString("UTF-8"));
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

    /* Does the conversion from JSON to BPMN */
    private ByteArrayOutputStream getBPMNfromJson(String jsonData) throws JAXBException, SAXException, JSONException, BpmnConverterException {
        ByteArrayOutputStream bpmn = new ByteArrayOutputStream();

        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        Marshaller marshaller = JAXBContext.newInstance(Definitions.class, ConfigurationAnnotationAssociation.class, ConfigurationAnnotationShape.class).createMarshaller();
        marshaller.setEventHandler(new ValidationEventCollector());
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BPMNPrefixMapper());
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File("xml/BPMN20.xsd")));
        marshaller.marshal(definitions, bpmn);

        return bpmn;
    }

}
