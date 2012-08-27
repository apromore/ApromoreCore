package org.omg.spec.bpmn._20100524.model;

// Java 2 Standard packages

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Before;
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
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class DefinitionsTest {

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
        Definitions definitions = context.createUnmarshaller().unmarshal(
            new StreamSource(getClass().getClassLoader().getResourceAsStream("Test1.bpmn20.xml")),
            Definitions.class
        ).getValue();

        // Serialize the test instance out again
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(bpmnSchema);
        marshaller.marshal(definitions, new FileOutputStream("target/surefire/Test1.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());

        // Inspect the test instance

        // Expect a single <process> element
        assertNotNull(definitions);
        assertNotNull(definitions.getRootElements());
        assertEquals(1, definitions.getRootElements().size());
        assertTrue(definitions.getRootElements().get(0).getValue() instanceof TProcess);
        TProcess process = (TProcess) definitions.getRootElements().get(0).getValue();
        assertFalse(process.isIsClosed());
        assertFalse(process.isIsExecutable());

        // Expect process to have a <variants> extension element
        assertNotNull(process.getExtensionElements());
        assertNotNull(process.getExtensionElements().getAnies());
        assertEquals(1, process.getExtensionElements().getAnies().size());

        /*
        assertSame(Variants.class, process.getExtensionElements().getAnies().get(0).getClass());
        Variants variants = (Variants) process.getExtensionElements().getAnies().get(0);
        assertEquals(3, variants.getVariant().size());
        assertEquals("vid-1940931807", variants.getVariant().get(0).getId());
        assertEquals("A", variants.getVariant().get(0).getName());
        */

        // Expect process to have 10 flow elements, the first of which is a task named "Airbus"
        assertNotNull(process.getFlowElements());
        assertEquals(10, process.getFlowElements().size());
        TTask airbus = (TTask) process.getFlowElements().get(0).getValue();
        assertEquals("Airbus", airbus.getName());

        // Expect "Airbus" to have two extension elements
        assertEquals(2, airbus.getExtensionElements().getAnies().size());

        /*
        assertSame(SignavioMetaData.class,        airbus.getExtensionElements().getAnies().get(0).getClass());
        assertSame(ConfigurationAnnotation.class, airbus.getExtensionElements().getAnies().get(1).getClass());
        */

        // Expect a single <BPMNDiagram> element
        assertNotNull(definitions.getBPMNDiagrams());
        assertEquals(1, definitions.getBPMNDiagrams().size());
        assertEquals("sid-db4fcdfb-67a0-4ef0-9a45-3167bfd77e4f", definitions.getBPMNDiagrams().get(0).getId());
        assertNotNull(definitions.getBPMNDiagrams().get(0).getBPMNPlane());
        assertEquals("sid-69a9f6ba-9421-44ee-a6fb-f50fc5e881e4", definitions.getBPMNDiagrams().get(0).getBPMNPlane().getId());
        // TODO - the following result almost certainly has the wrong namespace for the QName
        assertEquals(new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "sid-68aefed9-f32a-4503-895c-b26b0ee8dded"),
                definitions.getBPMNDiagrams().get(0).getBPMNPlane().getBpmnElement());
        assertNotNull(definitions.getBPMNDiagrams().get(0).getBPMNPlane().getDiagramElements());

        // Expect 10 diagram elements to match the 10 process elements
        assertEquals(10, definitions.getBPMNDiagrams().get(0).getBPMNPlane().getDiagramElements().size());
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
        Definitions definitions = context.createUnmarshaller().unmarshal(
            new StreamSource(getClass().getClassLoader().getResourceAsStream("TrivialGateway.bpmn20.xml")),
            Definitions.class
        ).getValue();

        // Serialize the test instance out again
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(bpmnSchema);
        marshaller.marshal(definitions, new FileOutputStream("target/surefire/TrivialGateway.bpmn20.xml"));
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
