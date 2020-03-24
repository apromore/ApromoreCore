package org.apromore.processmining.plugins.bpmn.plugins;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions.BpmnDefinitionsBuilder;

/**
 * Export BPMN diagrams to BPMN XML format
 *
 * @author Anna Kalenkova
 * Aug 21, 2013
 */
public class BpmnExportPlugin {
	// xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" 
	
	private static final String ENCODING = "UTF-8";
	
	private static final String EXPORTER = "ProM. http://www.promtools.org/prom6";
	
	private static final String EXPORTER_VERSION = "6.3";
	
	private static final String TARGET_NAMESPACE = "http://www.omg.org/bpmn20";
	
	private static final String BPMN_MODEL_NAMESPACE = 
			"http://www.omg.org/spec/BPMN/20100524/MODEL";
	
	private static final String SCHEMA_LOCATION = 
			"http://www.omg.org/spec/BPMN/20100524/MODEL "
			+ "BPMN20.xsd";
	
	private static final String DI_PREFIX = "bpmndi";
	
	private static final String DI_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/DI";
	
	private static final String DC_PREFIX = "dc";
	
	private static final String DC_NAMESPACE = "http://www.omg.org/spec/DD/20100524/DC";
	
	private static final String DD_DI_PREFIX = "di";
	
	private static final String DD_DI_NAMESPACE = "http://www.omg.org/spec/DD/20100524/DI";
	
	private static final String XSI_PREFIX = "xsi";
	
	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	
	private static final String CONTENT_MARKER = "@CONTENT@";

	/**
	 * Plugin for BPMN XML 2.0 export from BPMN diagram
	 * 
	 * @param context
	 * @param bpmnDiagram
	 * @param file
	 * @throws IOException
	 */
	public void export(BPMNDiagram bpmnDiagram, File file) throws IOException {
		String bodyString = retrieveContent(bpmnDiagram);
		exportWithContent(file, bodyString);
	}
	
	/**
	 * Plugin for BPMN XML 2.0 export from BPMN Definitions
	 * 
	 * @param context
	 * @param definitions
	 * @param file
	 * @throws IOException
	 */
	public void export(BpmnDefinitions definitions, File file) throws IOException {
		String bodyString = definitions.exportElements();
		exportWithContent(file, bodyString);
	}
	
	/**
	 * Export with content
	 * 
	 * @param context
	 * @param file
	 * @param bodyString
	 */
	public void exportWithContent(File file, String bodyString) throws IOException {
		XMLOutputFactory xmlOutput = XMLOutputFactory.newInstance();
		OutputStream outputStream = null, fileOutputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			XMLStreamWriter writer = xmlOutput.createXMLStreamWriter(outputStream, ENCODING);
			writer.writeStartDocument(ENCODING, "1.0");

			// Export envelope
			exportEnvelope(writer);
			
			writer.writeEndDocument();
			writer.flush();
			
			// Add definition content
			String envelopeString = outputStream.toString();
			String contentString = envelopeString.replace(CONTENT_MARKER, bodyString);
			
			// Write to file
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(contentString.getBytes());

		} catch (XMLStreamException e) {
			System.out.println("Error during export BPMN diagram");
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fileOutputStream);
		}
	}

	/**
	 * Export envelope
	 * 
	 * @param context
	 * @param writer
	 * @throws XMLStreamException
	 */
	private void exportEnvelope(XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("definitions");

		writer.setDefaultNamespace(BPMN_MODEL_NAMESPACE);
		writer.writeDefaultNamespace(BPMN_MODEL_NAMESPACE);
		
		writer.setPrefix(DC_PREFIX, DC_NAMESPACE);
		writer.writeNamespace(DC_PREFIX, DC_NAMESPACE);
		
		writer.setPrefix(DI_PREFIX, DI_NAMESPACE);
		writer.writeNamespace(DI_PREFIX, DI_NAMESPACE);
		
		writer.setPrefix(DD_DI_PREFIX, DD_DI_NAMESPACE);
		writer.writeNamespace(DD_DI_PREFIX, DD_DI_NAMESPACE);

		writer.setPrefix(XSI_PREFIX, XSI_NAMESPACE);
		writer.writeNamespace(XSI_PREFIX, XSI_NAMESPACE);

		writer.writeAttribute("targetNamespace", TARGET_NAMESPACE);
		writer.writeAttribute("exporter", EXPORTER);
		// TODO: change the exporter version to context.getGlobalContext().getFrameworkVersion()
		// when this method will be implemented
		writer.writeAttribute("exporterVersion", EXPORTER_VERSION);
		writer.writeAttribute(XSI_NAMESPACE, "schemaLocation", SCHEMA_LOCATION);
		
		// write marker for definition content
		writer.writeCharacters(CONTENT_MARKER);
		
		writer.writeEndElement();
	}
	

	/**
	 * Retrieve definition content from BPMN diagram
	 * 
	 * @param diagram
	 * @return
	 */
	private String retrieveContent(BPMNDiagram diagram) {
		BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitionsBuilder(diagram);
		BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);
		return definitions.exportElements();
	}
}
