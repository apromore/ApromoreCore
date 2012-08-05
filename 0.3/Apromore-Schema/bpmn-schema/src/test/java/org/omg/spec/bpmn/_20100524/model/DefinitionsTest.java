package org.omg.spec.bpmn._20100524.model;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link TDefinitions}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class DefinitionsTest {

    /**
     * Shared JAXB parsing context.
     */
    private JAXBContext context;

    /**
     * Initialize {@link #context}.
     *
     * This ought to be invoked before each of the test methods.
     */
    @Before
    public void initializeContext() throws JAXBException {
        context = JAXBContext.newInstance(TDefinitions.class);
        //context = JAXBContext.newInstance("org.omg.spec.bpmn._20100524.model");
    }

    /**
     * Test parsing of <a href="{@docRoot}/Test1.bpmn20.xml">Test1.bpmn20.xml</a>.
     */
    @Test
    public final void test1() throws FileNotFoundException, JAXBException, SAXException {

        // Obtain the test instance
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<TDefinitions> tDefinitions = unmarshaller.unmarshal(
            new StreamSource(new FileInputStream("src/test/resources/Test1.bpmn20.xml")),
            TDefinitions.class
        );

        // Serialize the test instance out again
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                                          .newSchema(new File("src/main/xsd/BPMN20.xsd")));
        marshaller.marshal(tDefinitions, new FileOutputStream("target/surefire/Test1.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());

        // Inspect the test instance

        // Expect a single <process> element
        TDefinitions definitions = tDefinitions.getValue();
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
        assertEquals(new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "sid-68aefed9-f32a-4503-895c-b26b0ee8dded"),
                     definitions.getBPMNDiagram().get(0).getBPMNPlane().getBpmnElement());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement());

        // INCORRECT - ought to be 10 diagram elements to match the 10 process elements
        assertEquals(0, definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().size());
    }

    /**
     * Test parsing of <a href="{@docRoot}/TrivialGateway.bpmn20.xml">TrivialGateway.bpmn20.xml</a>.
     */
    @Test
    public final void testTrivialGateway() throws FileNotFoundException, JAXBException, SAXException {

        // Obtain the test instance
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<TDefinitions> tDefinitions = unmarshaller.unmarshal(
            new StreamSource(new FileInputStream("src/test/resources/TrivialGateway.bpmn20.xml")),
            TDefinitions.class
        );

        // Serialize the test instance out again
        Marshaller marshaller = context.createMarshaller();
        ValidationEventCollector vec = new ValidationEventCollector();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                                          .newSchema(new File("src/main/xsd/BPMN20.xsd")));
        marshaller.marshal(tDefinitions, new FileOutputStream("target/surefire/TrivialGateway.bpmn20.xml"));
        assertFalse(formatValidationEvents(vec), vec.hasEvents());
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
