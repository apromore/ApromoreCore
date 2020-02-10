
package servlet;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * BPMNOutputServlet converts the diagram (JSON) into a BPMN file.
 * It should be accessible at: /bpmnoutput
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BPMNOutputServlet extends HttpServlet {

    private static final long serialVersionUID = 4651531154294830523L;
    private static final Logger LOGGER = Logger.getLogger(BPMNOutputServlet.class.getCanonicalName());

    private static final String PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";


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
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.setContentType("text/plain; charset=UTF-8");
                res.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                ByteArrayOutputStream bpmn = getBPMNfromJson(req, jsonData);
                res.setContentType("application/xml; charset=UTF-8");
                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().write(bpmn.toString("UTF-8"));
            }
        } catch (Exception e) {
            try {
                LOGGER.log(Level.SEVERE, "JSON to BPMN conversion failed: " + e.getMessage(), e);
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
    private ByteArrayOutputStream getBPMNfromJson(HttpServletRequest req, String jsonData) throws JAXBException, SAXException, JSONException, BpmnConverterException {
        ByteArrayOutputStream bpmn = new ByteArrayOutputStream();

        BasicDiagram diagram = BasicDiagramBuilder.parseJson(jsonData);
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions definitions = converter.getDefinitionsFromDiagram();

        Marshaller marshaller = JAXBContext.newInstance("de.hpi.bpmn2_0.model:de.hpi.bpmn2_0.model.extension.synergia",
                                                        getClass().getClassLoader())
                                           .createMarshaller();
        marshaller.setEventHandler(new BPMNValidationEventHandler());
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(PREFIX_MAPPER, new BPMNPrefixMapper());
        String schemaFile = req.getSession().getServletContext().getRealPath("/") + "WEB-INF" + "/xml/BPMN20.xsd";
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaFile)));
        marshaller.marshal(definitions, bpmn);

        return bpmn;
    }


    public class BPMNValidationEventHandler implements ValidationEventHandler {
        public boolean handleEvent(ValidationEvent event) {
            LOGGER.info("Error:");
            LOGGER.info("MESSAGE:  " + event.getMessage());
            LOGGER.info("LINKED EXCEPTION:  " + event.getLinkedException());
            LOGGER.info("OBJECT:  " + event.getLocator().getObject());
            return true;
        }
    }

}
