package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Third party packages
import org.apache.commons.io.output.NullOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apromore.anf.ANFSchema;
// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfIDResolver;
import org.apromore.canoniser.bpmn.cpf.CpfUnmarshallerListener;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.di.Plane;

/**
 * Test suite for {@link CanoniserDefinitions}.
 * These are decanonization tests, exercising the constructor {@link CanoniserDefinitions#(CanonicalProcessType, AnnotationsType)}.
 * Canonization is tested instead by {@link BPMN20CanoniserTest}.
 *
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *     Service for AProMoRe</a>, page 24-25
 */
public class CanoniserDefinitionsTest {

    /** Source for BPMN test data. */
    final static File MODELS_DIR = new File("src/test/resources/BPMN_models/");

    /** Destination for converted documents generated by tests. */
    final static File OUTPUT_DIR = new File("target/surefire/");

    /** Source for ANF and CPF test data. */
    final static File TESTCASES_DIR = new File("src/test/resources/BPMN_testcases/");

    /** XML schema for ANF 0.3. */
    private Schema ANF_SCHEMA;

    /** Qualified name of the root element <code>anf:Annotations</code>. */
    final static QName ANF_ROOT = new QName("http://www.apromore.org/ANF", "Annotations");

    /** XML schema for BPMN 2.0. */
    private Schema BPMN_SCHEMA;

    /** XML schema for CPF 0.5. */
    private Schema CPF_SCHEMA;

    /** Qualified name of the root element <code>cpf:CanonicalProcess</code>. */
    final static QName CPF_ROOT = new QName("http://www.apromore.org/CPF", "CanonicalProcess");

    /** Property name for use with {@link Unmarshaller#setProperty} to configure a {@link com.sun.xml.bind.IDResolver}. */
    final static String ID_RESOLVER = "com.sun.xml.bind.IDResolver";

    /** Property name for use with {@link Unmarshaller#setProperty} to configure an alternate JAXB ObjectFactory. */
    final static String OBJECT_FACTORY = "com.sun.xml.bind.ObjectFactory";

    /**
     * Shared JAXB context/
     */
    private JAXBContext context;

    /**
     * Initialize {@link #ANF_SCHEMA}, {@link #BPMN_SCHEMA} and {@link #CPF_SCHEMA}.
     */
    @Before
    public void initializeDefinitionsSchema() throws SAXException {
        ClassLoader loader = getClass().getClassLoader();

        ANF_SCHEMA  = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
            new StreamSource(loader.getResourceAsStream("xsd/anf_0.3.xsd"))
        );

        if (System.getProperty("bpmnvalidation") != null) {

            /* By default, marshallers and unmarshallers don't validate BPMN documents.
             * Validation can be enabled by passing a -Dbpmnvalidation flag to Maven.
             *
             * This switches to loading the BPMN schema from the filesystem, as so:
             */
            BPMN_SCHEMA = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
                new File("../../../Apromore-Schema/bpmn-schema/src/main/resources/xsd/BPMN20.xsd")
            );
            /* The reason this isn't the default behavior is because it only works when
             * executed from the complete Apromore checkout.  When Jenkins runs the
             * tests, it tests each element in isolation and so the BPMN schema can't be loaded.
             *
             * Hence, we have the following code which loads the BPMN from the classpath:
             */
        } else {
            BPMN_SCHEMA = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource[] {
                new StreamSource(loader.getResourceAsStream("xsd/DC.xsd")),
                new StreamSource(loader.getResourceAsStream("xsd/DI.xsd")),
                new StreamSource(loader.getResourceAsStream("xsd/BPMNDI.xsd")),
                new StreamSource(loader.getResourceAsStream("xsd/Semantic.xsd")),
                new StreamSource(loader.getResourceAsStream("xsd/BPMN20.xsd"))
            });
            /* Unfortunately, the above code doesn't work; it fails to parse the root <definitions>.
             * Until someone can figure out why it fails, BPMN validation is off unless explicitly
             * requested by -Dbpmnvalidation.
             */
        }
        assert BPMN_SCHEMA != null;

        CPF_SCHEMA  = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
            new StreamSource(loader.getResourceAsStream("xsd/cpf_1.0.xsd"))
        );
    }

    /**
     * Initialize {@link #context}.
     *
     * @throws JAXBException
     */
    @Before
    public void initializeContext() throws JAXBException {
        context = JAXBContext.newInstance(BpmnObjectFactory.class,
                                          org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                          org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                          org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                          org.omg.spec.dd._20100524.di.ObjectFactory.class);
    }

    /**
     * Common code for decanonisation tests.
     *
     * This method validates the CPF and ANF source files, decanonises them to produce a BPMN file, writes it out and validates it.
     *
     * @param filename  the filename for the input CPF and ANF files and for the output BPMN file,
     *     minus the respective <code>.cpf</code>, <code>.anf</code> and <code>.bpmn.xml</code> file extensions
     * @return the decanonised BPMN model
     */
    private final CanoniserDefinitions testDecanonise(final String filename) throws CanoniserException, FileNotFoundException, JAXBException, SAXException, TransformerException {

        // Read the CPF source file
        Unmarshaller cpfUnmarshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT).createUnmarshaller();
        cpfUnmarshaller.setListener(new CpfUnmarshallerListener());
        cpfUnmarshaller.setProperty(ID_RESOLVER, new CpfIDResolver());
        cpfUnmarshaller.setProperty(OBJECT_FACTORY, new org.apromore.canoniser.bpmn.cpf.ObjectFactory());
        cpfUnmarshaller.setSchema(CPF_SCHEMA);
        CanonicalProcessType cpf = cpfUnmarshaller.unmarshal(
            new StreamSource(new FileInputStream(new File(TESTCASES_DIR, filename + ".cpf"))),
            CanonicalProcessType.class
        ).getValue();

        // Read the ANF source file
        Unmarshaller anfUnmarshaller = JAXBContext.newInstance(ANFSchema.ANF_CONTEXT).createUnmarshaller();
        anfUnmarshaller.setSchema(ANF_SCHEMA);
        AnnotationsType anf = anfUnmarshaller.unmarshal(
            new StreamSource(new FileInputStream(new File(TESTCASES_DIR, filename + ".anf"))),
            AnnotationsType.class
        ).getValue();

        // Confirm constraints that can't be expressed in the CPF or ANF schemas
        assertEquals(cpf.getUri(), anf.getUri());
        
        // Obtain the test instance
        CanoniserDefinitions definitions = CanoniserDefinitions.correctFlowNodeRefs(new CanoniserDefinitions(cpf, anf), new BpmnObjectFactory());

        // Serialize the test instance for offline inspection
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(definitions, new File(OUTPUT_DIR, filename + ".bpmn20.xml"));

        // Validate the test instance
        if (System.getProperty("bpmnvalidation") != null) {
            marshaller.setSchema(BPMN_SCHEMA);
        }
        marshaller.marshal(definitions, new NullOutputStream());

        return definitions;
    }

    /**
     * Test decanonisation of <code>Basic.cpf</code> and <code>Basic.anf</code>.
     */
    @Ignore
    @Test
    public final void testDecanoniseBasic() throws Exception {

        // Obtain the test instance
        CanoniserDefinitions definitions = testDecanonise("Basic");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process c6
        assertEquals(TProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess c6 = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("c6", c6.getId());

        // Expect 5 flow elements
        assertEquals(5, c6.getFlowElement().size());

        // Start event c1
        TStartEvent c1 = (TStartEvent) c6.getFlowElement().get(2).getValue();
        assertEquals("c1", c1.getId());

        // Task c2
        TTask c2 = (TTask) c6.getFlowElement().get(3).getValue();
        assertEquals("c2", c2.getId());

        // End event c1
        TEndEvent c3 = (TEndEvent) c6.getFlowElement().get(4).getValue();
        assertEquals("c3", c3.getId());

        // Sequence flow c4
        TSequenceFlow c4 = (TSequenceFlow) c6.getFlowElement().get(0).getValue();
        assertEquals("c4", c4.getId());
        assertEquals(c1, c4.getSourceRef());
        assertEquals(c2, c4.getTargetRef());

        // Sequence flow c5
        TSequenceFlow c5 = (TSequenceFlow) c6.getFlowElement().get(1).getValue();
        assertEquals("c5", c5.getId());
        assertEquals(c2, c5.getSourceRef());
        assertEquals(c3, c5.getTargetRef());
    }

    /**
     * Test decanonization to <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.bpmn20.xml">expectation #1</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.bpmn20.svg"/></div>
     */
    @Test
    public final void testDecanonise1() {
        // not yet implemented
    }

    /**
     * Test decanonisation of <code>Pool.cpf</code> and <code>Pool.anf</code>.
     */
    @Test
    public final void testDecanonisePool() throws Exception {

        // Obtain the test instance
        CanoniserDefinitions definitions = testDecanonise("Pool");
    }

    /**
     * Test decanonisation of <code>TwoLanes.cpf</code> and <code>TwoLanes.anf</code>.
     */
    @Test
    public final void testDecanoniseTwoLanes() throws Exception {

        // Obtain the test instance
        CanoniserDefinitions definitions = testDecanonise("TwoLanes");
    }

    /**
     * Test decanonisation of <code>TwoPools.cpf</code> and <code>TwoPools.anf</code>.
     */
    @Test
    public final void testDecanoniseTwoPools() throws Exception {

        // Obtain the test instance
        CanoniserDefinitions definitions = testDecanonise("TwoPools");
    }
}
