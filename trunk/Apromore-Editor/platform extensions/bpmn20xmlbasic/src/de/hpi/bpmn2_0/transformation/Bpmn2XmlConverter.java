package de.hpi.bpmn2_0.transformation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.ExportValidationEventCollector;
import de.hpi.bpmn2_0.model.Definitions;

public class Bpmn2XmlConverter {

	private Definitions bpmnDefinitions;
	private String bpmn20XsdPath;

	public Bpmn2XmlConverter() {}
	
	public Bpmn2XmlConverter(Definitions bpmnDefinitions, String bpmn20XsdPath) {
		this.bpmnDefinitions = bpmnDefinitions;
		this.bpmn20XsdPath = bpmn20XsdPath;
	}

	public StringWriter getXml() throws JAXBException, SAXException,
			ParserConfigurationException, TransformerException {

		final Map<String, Object> properties = new HashMap<String, Object>();

		Class[] classes = { Definitions.class };

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
		
		for(String prefix : bpmnDefinitions.unusedNamespaceDeclarations) {
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

		Class[] classes = { Definitions.class };
		
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
