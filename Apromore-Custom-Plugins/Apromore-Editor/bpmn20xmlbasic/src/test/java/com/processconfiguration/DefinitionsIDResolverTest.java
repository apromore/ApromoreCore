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

package com.processconfiguration;

/**
 * Copyright (c) 2006
 * <p>
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.sun.xml.bind.IDResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;

/**
 * Test suite for the {@link DefinitionsIDResolver}.
 *
 * This must be configured using the <code>tests.dir</code> property to indicate the
 * project <code>tests/</code> directory where the test data files can be found.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class DefinitionsIDResolverTest {

    /** Logger.  Named after the class. */
    private static final Logger logger = Logger.getLogger(DefinitionsIDResolverTest.class.getCanonicalName());

    /**
     * Test data directory.
     *
     * Initialized from the <code>tests.dir</code> system property.
     */
    private static final File testsDirectory = new File("src/test/resources");

    /**
     * The <a href="{@docRoot}/../tests/data/Test1.bpmn20.xml">test document</a> used in the test suite.
     *
     * <div align="center"><img src="{@docRoot}/svg/Test1.signavio.svg"/></div>
     */
    static final File testFile = new File(new File(testsDirectory, "data"), "Test1.bpmn20.xml");

    /** Factory for JAXB marshallers and unmarshallers. */
    private final JAXBContext context;

    /**
     * @throws JAXBException if {@link #context} cannot be initialized
     */
    public DefinitionsIDResolverTest() throws JAXBException {

        this.context = JAXBContext.newInstance(Definitions.class,
            ConfigurationAnnotationAssociation.class,
            ConfigurationAnnotationShape.class);
    }

    //
    // Tests
    //

    /**
     * Test whether {@link #testFile} unmarshals its IDREF attributes correctly.
     *
     * @throws IOException if the test document can't be read
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    @Test
    final void test1() throws IOException, JAXBException, SAXException {

        // Obtain the test document
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
        Definitions definitions = (Definitions) unmarshaller.unmarshal(testFile);

        // Validate the parsed BPMN document
        assertValidBPMN(definitions, "test-DefinitionsIDResolver1.bpmn.xml");

        // Sample some of the JAXB elements
        Process process = (Process) definitions.getRootElement().get(0);

        SequenceFlow sequenceFlow = (SequenceFlow) process.getFlowElement().get(5);
        assertEquals("sid-A66173B9-B463-4B5E-998D-E4F3EEDE8AE6", sequenceFlow.getId());

        StartEvent startEvent = (StartEvent) process.getFlowElement().get(2);
        assertEquals("sid-E31B8FA9-FB95-4EF4-8E2B-FA7C9AB58B54", startEvent.getId());

        InclusiveGateway gateway = (InclusiveGateway) process.getFlowElement().get(1);
        assertEquals("sid-B044A443-736E-495B-9DD8-90EB4860F9AC", gateway.getId());

        // Verify that the incoming and outgoing fields have been initialized
        assertEquals(1, startEvent.getOutgoing().size());
        assertSame(sequenceFlow, startEvent.getOutgoing().get(0));

        assertEquals(1, gateway.getIncoming().size());
        assertSame(sequenceFlow, gateway.getIncoming().get(0));
    }

    //
    // Internal methods
    //

    /**
     * Write out and validate a BPMN XML document.
     *
     * @param definitions  a configurable BPMN XML model
     * @param filename  output file name
     * @throws AssertionError if the document isn't schema-valid
     * @throws IOException if the test document can't be read or the output document written
     * @throws JAXBException if the test document can't be parsed
     * @throws SAXException if the schema can't be parsed
     */
    public static void assertValidBPMN(final Definitions definitions, final String filename)
        throws AssertionError, IOException, JAXBException, SAXException {

        JAXBContext jaxb = JAXBContext.newInstance(Definitions.class,
            ConfigurationAnnotationAssociation.class,
            ConfigurationAnnotationShape.class);

        // Marshal once without validation so that there's a complete (possibly invalid) file written out
        Marshaller marshaller = jaxb.createMarshaller();
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
        File f = new File("target");
        f.mkdirs();

        marshaller.marshal(definitions, new FileWriter(
            new File(new File("target"), filename)));  // TODO: figure out why marshalling definitions twice fails

        // Marshal the second time to confirm schema-validity
        ValidationEventCollector vec = new ValidationEventCollector();

        OutputStream nullOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        };

        marshaller.setEventHandler(vec);
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BPMNPrefixMapper());
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
            .newSchema(new File("src/test/xsd/BPMN20.xsd")));
        try {
            marshaller.marshal(definitions, nullOutputStream);  // discard the result
        } catch (MarshalException e) {
            assertTrue(vec.hasEvents(), "XML serialization failed, but no diagnostic message available");
        }

        assertFalse(vec.hasEvents(), formatValidationEvents(vec));
    }

    /**
     * Produce helpful diagnostic text when the XML output of tests fails XML schema validation.
     *
     * @param vec  the collection of validation errors
     * @return a human-legible error listing in English text
     */
    private static String formatValidationEvents(final ValidationEventCollector vec) {

        StringBuilder builder = new StringBuilder("Validation errors:");

        for (ValidationEvent event : Arrays.asList(vec.getEvents())) {
            builder.append("\nLine ")
                .append(event.getLocator().getLineNumber())
                .append(", column ")
                .append(event.getLocator().getColumnNumber())
                .append(": ")
                .append(event.getMessage());
        }

        return builder.toString();
    }
}
