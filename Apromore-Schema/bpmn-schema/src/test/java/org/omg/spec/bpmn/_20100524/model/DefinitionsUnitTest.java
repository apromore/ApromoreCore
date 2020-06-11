/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.omg.spec.bpmn._20100524.model;

// Java 2 Standard packages

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// Third party packages

/**
 * Test suite for {@link TDefinitions}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class DefinitionsUnitTest {

    /**
     * Shared JAXB parsing context.
     */
    private JAXBContext context;

    /**
     * BPMN 2.0 XML schema.
     */
    private Schema bpmnSchema;

    /**
     * Initialize {@link #context}.
     * This ought to be invoked before each of the test methods.
     *
     * @throws JAXBException if {@link #context} can't be initialized
     * @throws SAXException if {@link #bpmnSchema} can't be initialized
     */
    @Before
    public void initializeContext() throws JAXBException, SAXException {
        context = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                          org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                          org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                          org.omg.spec.dd._20100524.di.ObjectFactory.class);

        bpmnSchema = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/resources/xsd/BPMN20.xsd"));
        /*
        ClassLoader loader = getClass().getClassLoader();
        bpmnSchema = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                                  .newSchema(new StreamSource[] {
            new StreamSource(loader.getResourceAsStream("xsd/BPMN20.xsd")),
            new StreamSource(loader.getResourceAsStream("xsd/BPMNDI.xsd")),
            new StreamSource(loader.getResourceAsStream("xsd/DC.xsd")),
            new StreamSource(loader.getResourceAsStream("xsd/DI.xsd")),
            new StreamSource(loader.getResourceAsStream("xsd/Semantics.xsd"))
        });
        */
    }

    /**
     * Test parsing of <a href="{@docRoot}/Test1.bpmn20.xml">Test1.bpmn20.xml</a>.
     *
     * @throws FileNotFoundException if the input file of test data is absent
     * @throws JAXBException
     */
    @Test
    public final void test1() throws FileNotFoundException, JAXBException {
        // Obtain the test instance
        JAXBElement<TDefinitions> element = (JAXBElement<TDefinitions>) context.createUnmarshaller().unmarshal(
            new StreamSource(getClass().getClassLoader().getResourceAsStream("Test1.bpmn20.xml"))
        );
        TDefinitions definitions = element.getValue();

        // Serialize the test instance out again
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(bpmnSchema);
        marshaller.marshal(element, new FileOutputStream("target/surefire/Test1.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());

        // Inspect the test instance

        // Expect a single <process> element
        assertNotNull(definitions);
        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());
        assertTrue(definitions.getRootElement().get(0).getValue() instanceof TProcess);
        TProcess process = (TProcess) definitions.getRootElement().get(0).getValue();
        assertFalse(process.isIsClosed());
        assertFalse(process.isIsExecutable());

        // Expect process to have a <variants> extension element
        assertNotNull(process.getExtensionElements());
        assertNotNull(process.getExtensionElements().getAny());
        assertEquals(1, process.getExtensionElements().getAny().size());

        /*
        assertSame(Variants.class, process.getExtensionElements().getAny().get(0).getClass());
        Variants variants = (Variants) process.getExtensionElements().getAny().get(0);
        assertEquals(3, variants.getVariant().size());
        assertEquals("vid-1940931807", variants.getVariant().get(0).getId());
        assertEquals("A", variants.getVariant().get(0).getName());
        */

        // Expect process to have 10 flow elements, the first of which is a task named "Airbus"
        assertNotNull(process.getFlowElement());
        assertEquals(10, process.getFlowElement().size());
        TTask airbus = (TTask) process.getFlowElement().get(0).getValue();
        assertEquals("Airbus", airbus.getName());

        // Expect "Airbus" to have two extension elements
        assertEquals(2, airbus.getExtensionElements().getAny().size());

        /*
        assertSame(SignavioMetaData.class,        airbus.getExtensionElements().getAny().get(0).getClass());
        assertSame(ConfigurationAnnotation.class, airbus.getExtensionElements().getAny().get(1).getClass());
        */

        // Expect a single <BPMNDiagram> element
        assertNotNull(definitions.getBPMNDiagram());
        assertEquals(1, definitions.getBPMNDiagram().size());
        assertEquals("sid-db4fcdfb-67a0-4ef0-9a45-3167bfd77e4f", definitions.getBPMNDiagram().get(0).getId());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane());
        assertEquals("sid-69a9f6ba-9421-44ee-a6fb-f50fc5e881e4", definitions.getBPMNDiagram().get(0).getBPMNPlane().getId());
        // TODO - the following result almost certainly has the wrong namespace for the QName
        assertEquals(new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "sid-68aefed9-f32a-4503-895c-b26b0ee8dded"),
                definitions.getBPMNDiagram().get(0).getBPMNPlane().getBpmnElement());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement());

        // Expect 10 diagram elements to match the 10 process elements
        assertEquals(10, definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().size());
    }

    /**
     * Test parsing of <a href="{@docRoot}/TrivialGateway.bpmn20.xml">TrivialGateway.bpmn20.xml</a>.
     *
     * @throws FileNotFoundException if the input file of test data is absent
     * @throws JAXBException
     */
    @Test
    public final void testTrivialGateway() throws FileNotFoundException, JAXBException {
        // Obtain the test instance
        JAXBElement<TDefinitions> element = context.createUnmarshaller().unmarshal(
            new StreamSource(getClass().getClassLoader().getResourceAsStream("TrivialGateway.bpmn20.xml")),
            TDefinitions.class
        );
        TDefinitions definitions = element.getValue();

        // Serialize the test instance out again
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(bpmnSchema);
        marshaller.marshal(element, new FileOutputStream("target/surefire/TrivialGateway.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());
    }

    /**
     * Test parsing of <a href="{@docRoot}/Case 8.bpmn20.xml">Case 8.bpmn20.xml</a>.
     *
     * @throws FileNotFoundException if the input file of test data is absent
     * @throws JAXBException
     */
    @Test
    public final void testCase8() throws FileNotFoundException, JAXBException {
        // Obtain the test instance
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(bpmnSchema);
        JAXBElement<TDefinitions> element = (JAXBElement<TDefinitions>) unmarshaller.unmarshal(
            new StreamSource(getClass().getClassLoader().getResourceAsStream("Case 8.bpmn20.xml"))
        );
        TDefinitions definitions = element.getValue();

        // Inspect the test instance
        TProcess process = (TProcess) definitions.getRootElement().get(1).getValue();
        List<JAXBElement<Object>> list = process.getLaneSet().get(0).getLane().get(0).getFlowNodeRef();
        JAXBElement<Object> e = list.get(0);
        assertEquals(TStartEvent.class , e.getValue().getClass());
        TStartEvent start = (TStartEvent) e.getValue();
        assertEquals("sid-B7D16DE3-ADC1-4F94-9447-DB2DCC467CF1", start.getId());

        // Modify the test instance
        list.add(e);  // It's surprisingly easy to break this operation in the .xjb binding

        // Serialize the test instance out again, with the duplicate flowNodeRef
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(bpmnSchema);
        marshaller.marshal(element, new FileOutputStream("target/surefire/Case 8.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());
    }

    /**
     * Test the construction of a BPMN document from scratch, rather than by unmarshalling.
     */
    @Test
    public final void testConstruction() throws Exception {
        ObjectFactory factory = new ObjectFactory();

        TDefinitions definitions = factory.createTDefinitions();
        definitions.setId("definitionsId");
        definitions.setExporter("exporter");
        definitions.setTargetNamespace("targetNS");

        TProcess process = factory.createTProcess();
        process.setId("processId");
        definitions.getRootElement().add(factory.createProcess(process));

        TLaneSet laneSet = factory.createTLaneSet();
        laneSet.setId("laneSetId");
        process.getLaneSet().add(laneSet);

        TLane lane = factory.createTLane();
        lane.setId("laneId");
        laneSet.getLane().add(lane);

        TTask task = factory.createTTask();
        task.setId("taskId");
        JAXBElement<TTask> wrappedTask = factory.createTask(task);
        process.getFlowElement().add(wrappedTask);
        lane.getFlowNodeRef().add((JAXBElement) wrappedTask);

        // Serialize the constructed document into a DOM tree
        DOMResult intermediateResult = new DOMResult();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(factory.createDefinitions(definitions), intermediateResult);

        // Because I can't figure out how to make JAXB do the right thing, I postprocess with XSLT
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", new Integer(2));
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(ClassLoader.getSystemResourceAsStream("xsd/fix-flowNodeRef.xsl")));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource finalSource = new DOMSource(intermediateResult.getNode());
        DOMResult finalResult = new DOMResult();
        transformer.transform(finalSource, new StreamResult(new OutputStreamWriter(new FileOutputStream("target/surefire/Construction.bpmn20.xml"), "utf-8")));
        transformer.transform(finalSource, finalResult);

        // Unmarshal back to JAXB
        Object def2 = context.createUnmarshaller().unmarshal(finalResult.getNode());

        // Serialize and validate the corrected document
        marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        //marshaller.setSchema(bpmnSchema);
        marshaller.marshal(def2, new FileOutputStream("target/surefire/Construction2.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());
    }

    /**
     * Produce helpful diagnostic text when the XML output of tests fails XML schema validation.
     *
     * @param vec the collection of validation errors
     * @return a human-legible error listing in English text
     */
    private static String formatValidationEvents(final ValidationEventCollector vec) {
        StringBuilder builder = new StringBuilder("Validation errors:");

        for (ValidationEvent event : Arrays.asList(vec.getEvents())) {
            builder.append("\n");
            if (event.getLocator() == null) {
                builder.append("Global: ");
            } else {
                builder.append("Line ")
                        .append(event.getLocator().getLineNumber())
                        .append(", column ")
                        .append(event.getLocator().getColumnNumber())
                        .append(": ");
            }
            builder.append(event.getMessage());
        }

        return builder.toString();
    }
}
