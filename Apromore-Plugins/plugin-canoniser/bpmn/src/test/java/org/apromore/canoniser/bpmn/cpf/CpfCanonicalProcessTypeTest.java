package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

// Local packages
import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.BPMN20Canoniser;
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.CanoniserResult;
import org.apromore.canoniser.bpmn.TestConstants;
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.bpmn.cpf.CpfIDResolver;
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
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
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.di.Plane;

/**
 * Test suite for {@link CpfCanonicalProcessType}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessTypeTest implements TestConstants {

    // Tests

    /**
     * Common canonisation test code.
     *
     * Parses <code>filename</code> and validates the resulting CPF.
     *
     * @param filename  the source filename within the {@link #TEST_MODELS} directory
     * @return a validated CPF that's been written out for inspection
     * @throws Exception if anything goes amiss setting up the test
     */
    private CpfCanonicalProcessType testCanonise(String filename) throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, filename)), true);

        // Validate and serialize the canonised documents to be inspected offline
        CpfCanonicalProcessType cpf = new CpfCanonicalProcessType(definitions);
        cpf.setUri("dummy");

        // Output the CPF
        cpf.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf")), false);

        // Validate the CPF
        cpf.marshal(new NullOutputStream(), true);

        /*
        // Round-trip the CPF back into BPMN
        BpmnDefinitions definitions2 = new BpmnDefinitions(cpf, null);
        definitions2.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf.bpmn")), false);
        definitions2.marshal(new NullOutputStream(), true);
        */

        return cpf;
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch9_loan5.bpmn">chapter 9 loan example</a>.
     */
    @Test
    public void testCh9Loan5() throws Exception {
        CanonicalProcessType cpf = testCanonise("ch9_loan5.bpmn");
    }
}
