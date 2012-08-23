package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Third party packages
import org.apache.commons.io.output.NullOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.Definitions;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.dd._20100524.di.Plane;

/**
 * Test suite for {@link CanoniserDefinitions}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 */
public class CanoniserDefinitionsTest {

    /**
     * Shared XML schema for BPMN 2.0.
     */
    private Schema definitionsSchema;

    /**
     * Shared JAXB context/
     */
    private JAXBContext context;

    /**
     * Initialize {@link #definitionsSchema}.
     */
    @Before
    public void initializeDefinitionsSchema() throws SAXException {
        definitionsSchema = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File("../../Apromore-Schema/bpmn-schema/src/main/xsd/BPMN20.xsd"));
    }

    /**
     * Initialize {@link #context}.
     *
     * @throws JAXBException
     */
    @Before
    public void initializeContext() throws JAXBException {
        context = JAXBContext.newInstance(CanoniserObjectFactory.class,
                                          org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                          org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                          org.omg.spec.dd._20100524.di.ObjectFactory.class);
    }

    /**
     * Test decanonisation of <code>Basic.cpf</code> and <code>Basic.anf</code>.
     */
    @Test
    public final void testBasic() throws CanoniserException, FileNotFoundException, JAXBException, SAXException {

        // Obtain the test instance
        CanoniserDefinitions definitions = new CanoniserDefinitions(
            JAXBContext.newInstance(Constants.CPF_CONTEXT)
                       .createUnmarshaller()
                       .unmarshal(new StreamSource(new FileInputStream("src/test/resources/BPMN_testcases/Basic.cpf")),
                                  CanonicalProcessType.class)
                       .getValue(),
            JAXBContext.newInstance(Constants.ANF_CONTEXT)
                       .createUnmarshaller()
                       .unmarshal(new StreamSource(new FileInputStream("src/test/resources/BPMN_testcases/Basic.anf")),
                                  AnnotationsType.class)
                       .getValue()
        );

        // Serialize the test instance for offline inspection
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(definitions, new File("target/surefire/Basic.bpmn20.xml"));

        // Validate the test instance
        marshaller.setSchema(definitionsSchema);
        marshaller.marshal(definitions, new NullOutputStream());

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElements());
        assertEquals(1, definitions.getRootElements().size());
    }

    /**
     * Test canonisation of <code>Test1.bpmn20.xml</code>.
     */
    @Test
    public final void test1() throws CanoniserException, FileNotFoundException, JAXBException, SAXException {

        // Obtain the test instance
        CanoniserDefinitions definitions =
            context.createUnmarshaller()
                   .unmarshal(new StreamSource(new FileInputStream("src/test/resources/BPMN_models/Test1.bpmn20.xml")),
                              CanoniserDefinitions.class)
                   .getValue();

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElements());
        assertEquals(1, definitions.getRootElements().size());
        assertTrue(definitions.getRootElements().get(0).getValue() instanceof TProcess);
        assertEquals(10, ((TProcess) definitions.getRootElements().get(0).getValue()).getFlowElements().size());

        assertNotNull(definitions.getBPMNDiagrams());
        assertEquals(1, definitions.getBPMNDiagrams().size());
        assertEquals("sid-db4fcdfb-67a0-4ef0-9a45-3167bfd77e4f", definitions.getBPMNDiagrams().get(0).getId());
        assertNotNull(definitions.getBPMNDiagrams().get(0).getBPMNPlane());
        assertEquals("sid-69a9f6ba-9421-44ee-a6fb-f50fc5e881e4", definitions.getBPMNDiagrams().get(0).getBPMNPlane().getId());
        assertEquals(new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "sid-68aefed9-f32a-4503-895c-b26b0ee8dded"),
                     definitions.getBPMNDiagrams().get(0).getBPMNPlane().getBpmnElement());
        assertNotNull(definitions.getBPMNDiagrams().get(0).getBPMNPlane().getDiagramElements());
        assertEquals(10, definitions.getBPMNDiagrams().get(0).getBPMNPlane().getDiagramElements().size());

        // Validate and serialize the canonised documents to be inspected offline
        Marshaller marshaller = JAXBContext.newInstance(Constants.ANF_CONTEXT).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File("../../Apromore-Schema/anf-schema/src/main/xsd/anf_0.3.xsd")));
        marshaller.marshal(new JAXBElement<AnnotationsType>(new QName("http://www.apromore.org/ANF", "Annotations"),
                                                            AnnotationsType.class,
                                                            definitions.getANF()),
                           new File("target/surefire/Test1.anf"));

        marshaller = JAXBContext.newInstance(Constants.CPF_CONTEXT).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setSchema(SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File("../../Apromore-Schema/cpf-schema/src/main/xsd/cpf_0.5.xsd")));
        marshaller.marshal(new JAXBElement<CanonicalProcessType>(new QName("http://www.apromore.org/CPF", "CanonicalProcess"),
                                                                 CanonicalProcessType.class,
                                                                 definitions.getCPF()),
                           new File("target/surefire/Test1.cpf"));

        // Inspect the ANF property
        assertNotNull(definitions.getANF());

        // Inspect the CPF property
        assertNotNull(definitions.getCPF());
    }
}
