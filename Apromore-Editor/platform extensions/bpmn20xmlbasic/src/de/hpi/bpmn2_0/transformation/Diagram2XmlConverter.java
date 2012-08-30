package de.hpi.bpmn2_0.transformation;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.oryxeditor.server.diagram.generic.GenericDiagram;
import org.xml.sax.SAXException;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;

public class Diagram2XmlConverter {

	protected GenericDiagram diagram;
	protected String bpmn20XsdPath;
	protected Map<String, Object> configuration;
	
	public Diagram2XmlConverter(GenericDiagram diagram, String bpmn20XsdPath) {
		this.diagram = diagram;
		this.bpmn20XsdPath = bpmn20XsdPath;
		
	}
	
	public Diagram2XmlConverter(GenericDiagram diagram, String bpmn20XsdPath, Map<String, Object> configuration) {
		this(diagram, bpmn20XsdPath);
		this.configuration = configuration;
	}
	
	public StringWriter getXml() throws BpmnConverterException, JAXBException, SAXException, ParserConfigurationException, TransformerException {
		
		Diagram2BpmnConverter converter;
		
		/* Build up BPMN 2.0 model */
		if(this.configuration != null) {
			converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses(), this.configuration);
		} else {
			converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
		}
		Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
		
		/* Get BPMN 2.0 XML */
		Bpmn2XmlConverter xmlConverter = new Bpmn2XmlConverter(bpmnDefinitions, bpmn20XsdPath);
		return xmlConverter.getXml();
	}
	
	public StringBuilder getValidationResults() throws JAXBException, SAXException, BpmnConverterException {
		/* Build up BPMN 2.0 model */
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
		Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
		
		/* Get BPMN 2.0 XML */
		Bpmn2XmlConverter xmlConverter = new Bpmn2XmlConverter(bpmnDefinitions, bpmn20XsdPath);
		return xmlConverter.getValidationResults();
	}
}
