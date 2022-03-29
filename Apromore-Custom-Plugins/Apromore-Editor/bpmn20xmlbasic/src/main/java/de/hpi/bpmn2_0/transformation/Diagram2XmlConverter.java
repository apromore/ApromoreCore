/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package de.hpi.bpmn2_0.transformation;

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
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
 **/

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
