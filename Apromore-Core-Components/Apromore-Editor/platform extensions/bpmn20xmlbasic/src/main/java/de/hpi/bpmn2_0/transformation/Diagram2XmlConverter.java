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

package de.hpi.bpmn2_0.transformation;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import org.oryxeditor.server.diagram.generic.GenericDiagram;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.StringWriter;
import java.util.Map;

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
        if (this.configuration != null) {
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
