
package de.hpi.bpmn2_0.transformation;

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

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import de.hpi.bpmn2_0.ExportValidationEventCollector;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Bpmn2XmlConverter {

    private Definitions bpmnDefinitions;
    private String bpmn20XsdPath;

    public Bpmn2XmlConverter() {
    }

    public Bpmn2XmlConverter(Definitions bpmnDefinitions, String bpmn20XsdPath) {
        this.bpmnDefinitions = bpmnDefinitions;
        this.bpmn20XsdPath = bpmn20XsdPath;
    }

    public StringWriter getXml() throws JAXBException, SAXException,
            ParserConfigurationException, TransformerException {

        final Map<String, Object> properties = new HashMap<String, Object>();

        Class[] classes = { Definitions.class,
                            ConfigurationAnnotationAssociation.class,
                            ConfigurationAnnotationShape.class };

        /* Perform XML creation */
        JAXBContext context = JAXBContext.newInstance(classes, properties);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd");

        NamespacePrefixMapper nsp = new BPMNPrefixMapper();
        ((BPMNPrefixMapper) nsp).setNsDefs(bpmnDefinitions.externalNSDefs);

        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);

        /* Marshal BPMN 2.0 XML */

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        marshaller.marshal(bpmnDefinitions, doc);

        /*
           * Remove unused namespace prefixes
           */

        for (String prefix : bpmnDefinitions.unusedNamespaceDeclarations) {
            doc.getDocumentElement().removeAttribute("xmlns:" + prefix);
        }

        String styleSheet = "<!DOCTYPE stylesheet [  <!ENTITY cr \"<xsl:text></xsl:text>\">]> <xsl:stylesheet    xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"     xmlns:xalan=\"http://xml.apache.org/xslt\"     version=\"1.0\">        <xsl:output method=\"xml\" indent=\"yes\" xalan:indent-amount=\"3\"/>           <!-- copy out the xml -->    <xsl:template match=\"* | @*\">        <xsl:copy><xsl:copy-of select=\"@*\"/><xsl:apply-templates/></xsl:copy>    </xsl:template> </xsl:stylesheet>";
        StreamSource styleStream = new StreamSource(new ByteArrayInputStream(styleSheet.getBytes()));
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
//		tf.setAttribute("indent-amount", new Integer(4));
        Transformer transformer = tf.newTransformer(styleStream);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, result);
        return writer;

    }

    public StringBuilder getValidationResults() throws JAXBException,
            SAXException {

        final Map<String, Object> properties = new HashMap<String, Object>();

        Class[] classes = {Definitions.class};

        JAXBContext context = JAXBContext.newInstance(classes, properties);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        NamespacePrefixMapper nsp = new BPMNPrefixMapper();
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);

        /* Set Schema validation properties */
        SchemaFactory sf = SchemaFactory
                .newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = sf.newSchema(new File(bpmn20XsdPath));
        marshaller.setSchema(schema);

        ExportValidationEventCollector vec = new ExportValidationEventCollector();
        marshaller.setEventHandler(vec);

        StringWriter writer = new StringWriter();

        /* Marshal BPMN 2.0 XML */
        marshaller.marshal(bpmnDefinitions, writer);

        /* Retrieve validation results */
        ValidationEvent[] events = vec.getEvents();

        StringBuilder builder = new StringBuilder();
        builder.append("Validation Errors: \n\n");

        for (ValidationEvent event : Arrays.asList(events)) {

            builder.append("\nError: ");
            builder.append(event.getMessage());
            builder.append("\n\n\n");
        }

        return builder;
    }
}
