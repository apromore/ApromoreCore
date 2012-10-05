package com.processconfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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
    private static final File testsDirectory = new File(System.getProperty("tests.dir"));

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
    @Test public final void test1() throws IOException, JAXBException, SAXException {

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

        FileWriter writer = new FileWriter(new File(new File(testsDirectory, "out"), filename));

        ValidationEventCollector vec = new ValidationEventCollector();

        Marshaller marshaller = JAXBContext.newInstance(Definitions.class,
                                                        ConfigurationAnnotationAssociation.class,
                                                        ConfigurationAnnotationShape.class)
                                           .createMarshaller();
        marshaller.setEventHandler(vec);
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BPMNPrefixMapper());
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                                          .newSchema(new File("xml/BPMN20.xsd")));

        try {
             marshaller.marshal(definitions, writer);
        } catch (MarshalException e) {
             assertTrue("XML serialization failed, but no diagnostic message available", vec.hasEvents());
        }

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
