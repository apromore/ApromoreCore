package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// Local packages
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

/**
 * Test suite for {@link ObjectFactory}.
 *
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *     Service for AProMoRe</a>, page 24-25
 */
public class ObjectFactoryTest {

    /** Source for ANF and CPF test data. */
    final static File TESTCASES_DIR = new File("src/test/resources/BPMN_testcases/");

    /** XML schema for CPF 0.5. */
    private Schema CPF_SCHEMA;

    /** Property name for use with {@link Unmarshaller#setProperty} to configure a {@link com.sun.xml.bind.IDResolver}. */
    final static String ID_RESOLVER = "com.sun.xml.bind.IDResolver";

    /** Property name for use with {@link Unmarshaller#setProperty} to override the JAXB ObjectFactory. */
    final static String OBJECT_FACTORY = "com.sun.xml.bind.ObjectFactory";

    /**
     * Initialize {@link #CPF_SCHEMA}.
     */
    @Before
    public void initializeDefinitionsSchema() throws SAXException {
        ClassLoader loader = getClass().getClassLoader();

        CPF_SCHEMA  = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
            new StreamSource(loader.getResourceAsStream("xsd/cpf_1.0.xsd"))
        );
    }

    /**
     * Test CPF convenience methods.
     */
    @Test
    public void testParseCpf() throws CanoniserException, FileNotFoundException, JAXBException, SAXException {

        final String filename ="Basic";

        // Read the CPF source file
        Unmarshaller cpfUnmarshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT).createUnmarshaller();
        cpfUnmarshaller.setListener(new CpfUnmarshallerListener());
        cpfUnmarshaller.setProperty(ID_RESOLVER, new CpfIDResolver());
        cpfUnmarshaller.setProperty(OBJECT_FACTORY, new ObjectFactory());
        cpfUnmarshaller.setSchema(CPF_SCHEMA);
        CanonicalProcessType cpf = cpfUnmarshaller.unmarshal(
            new StreamSource(new FileInputStream(new File(TESTCASES_DIR, filename + ".cpf"))),
            CanonicalProcessType.class
        ).getValue();

        NetType net = cpf.getNet().get(0);

        assertEquals(3, net.getNode().size());
        CpfEventType e1 = (CpfEventType) net.getNode().get(0);
        CpfTaskType  e2 =  (CpfTaskType) net.getNode().get(1);
        CpfEventType e3 = (CpfEventType) net.getNode().get(2);

        assertEquals(2, net.getEdge().size());
        EdgeType e4 = net.getEdge().get(0);
        EdgeType e5 = net.getEdge().get(1);

        assertEquals(0, e1.getIncomingEdges().size());
        assertEquals(1, e1.getOutgoingEdges().size());

        assertEquals(1, e2.getIncomingEdges().size());
        assertEquals(1, e2.getOutgoingEdges().size());

        assertEquals(1, e3.getIncomingEdges().size());
        assertEquals(0, e3.getOutgoingEdges().size());
    }
}
