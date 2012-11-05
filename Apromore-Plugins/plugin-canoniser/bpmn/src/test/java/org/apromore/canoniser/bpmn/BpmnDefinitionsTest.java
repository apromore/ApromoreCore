package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
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
import org.w3c.dom.Element;
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
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfIDResolver;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
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
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.di.Plane;

/**
 * Test suite for {@link BpmnDefinitions}.
 * These are decanonization tests, exercising the constructor {@link BpmnDefinitions#(CanonicalProcessType, AnnotationsType)}.
 * Canonization is tested instead by {@link BPMN20CanoniserTest}.
 *
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *     Service for AProMoRe</a>, page 24-25
 */
public class BpmnDefinitionsTest implements TestConstants {

    /**
     * Common code for decanonisation tests.
     *
     * This method validates the CPF and ANF source files, decanonises them to produce a BPMN file, writes it out and validates it.
     *
     * @param filename  the filename for the input CPF and ANF files and for the output BPMN file,
     *     minus the respective <code>.cpf</code>, <code>.anf</code> and <code>.bpmn.xml</code> file extensions
     * @return the decanonised BPMN model
     */
    private final BpmnDefinitions testDecanonise(final String filename) throws CanoniserException, FileNotFoundException, JAXBException, SAXException, TransformerException {

        // Read the CPF source file
        CpfCanonicalProcessType cpf = CpfCanonicalProcessType.newInstance(new FileInputStream(new File(TESTCASES_DIR, filename + ".cpf")), true);

        // Read the ANF source file
        AnnotationsType anf = AnfAnnotationsType.newInstance(new FileInputStream(new File(TESTCASES_DIR, filename + ".anf")), true);

        // Confirm constraints that can't be expressed in the CPF or ANF schemas
        assertEquals(cpf.getUri(), anf.getUri());
        
        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.correctFlowNodeRefs(new BpmnDefinitions(cpf, anf), new BpmnObjectFactory());

        // Serialize the test instance for offline inspection
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".bpmn")), false);

        // Validate the test instance
        definitions.marshal(new NullOutputStream(), true);

        return definitions;
    }

    /**
     * Test parsing of <code>Case 2.bpmn</code> via {@link BpmnDefinitions#newInstance}.
     *
     * Looking primarily to see that the lists of incoming and outgoing sequence flows on the flow nodes are correct, as
     * as the gateway direction on the XOR gateway.
     */
    @Test
    public final void testNewInstance() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, "Case 2.bpmn")), true);

        // TODO - Inspect the test instance

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "Case 2.bpmn")), true);
    }

    /**
     * Test parsing of <code>Test1.bpmn</code>.
     */
    @Test
    public final void testNewInstance2() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, "Test1.bpmn")), false);

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());
        assertTrue(definitions.getRootElement().get(0).getValue() instanceof TProcess);
        assertEquals(10, ((TProcess) definitions.getRootElement().get(0).getValue()).getFlowElement().size());

        assertNotNull(definitions.getBPMNDiagram());
        assertEquals(1, definitions.getBPMNDiagram().size());
        assertEquals("sid-db4fcdfb-67a0-4ef0-9a45-3167bfd77e4f", definitions.getBPMNDiagram().get(0).getId());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane());
        assertEquals("sid-69a9f6ba-9421-44ee-a6fb-f50fc5e881e4", definitions.getBPMNDiagram().get(0).getBPMNPlane().getId());
        assertEquals(new QName("http://www.omg.org/spec/BPMN/20100524/MODEL", "sid-68aefed9-f32a-4503-895c-b26b0ee8dded"),
                     definitions.getBPMNDiagram().get(0).getBPMNPlane().getBpmnElement());
        assertNotNull(definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement());
        assertEquals(10, definitions.getBPMNDiagram().get(0).getBPMNPlane().getDiagramElement().size());
    }

    /**
     * Test parsing of <code>Request_For_Advance_Payment.bpmn</code>.
     */
    @Test
    public final void testNewInstance3() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, "Request_For_Advance_Payment.bpmn")), true);

        // TODO - Inspect the test instance

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "Request_For_Advance_Payment.bpmn")), true);
    }

    @Test
    public final void testNewInstance4() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, "ch9_loan5.bpmn")), true);

        // TODO - Inspect the test instance
        TDataOutputAssociation doa = (TDataOutputAssociation) definitions.findElementById("sid-9413B494-364E-4F60-9C85-F313479C96F0");
        assertNotNull(doa);
        assertNotNull(doa.getSourceRef());
        //assertNotNull(doa.getTargetRef());

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "ch9_loan5.bpmn")), true);
    }

    @Test
    public final void testNewInstance5() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, "ch9_PurchaseOrder4Complete.bpmn")), true);

        // TODO - Inspect the test instance

        // Serialize the test instance
        definitions.marshal(new FileOutputStream(new File(OUTPUT_DIR, "ch9_PurchaseOrder4Complete.bpmn")), true);
    }

    /**
     * Test decanonisation of <code>Basic.cpf</code> and <code>Basic.anf</code>.
     */
    @Test
    public final void testDecanoniseBasic() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Basic");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process c6
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
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
     * Test decanonisation of <code>Extension.cpf</code> and <code>Extension.anf</code>.
     */
    @Test
    public final void testDecanoniseExtension() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Extension");

        // Inspect the test instance
        assertNotNull(definitions);

        assertNotNull(definitions.getRootElement());
        assertEquals(1, definitions.getRootElement().size());

        // Process c6
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess c6 = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("c6", c6.getId());

        // Expect 5 flow elements
        assertEquals(5, c6.getFlowElement().size());

        // Start event c1
        TStartEvent c1 = (TStartEvent) c6.getFlowElement().get(2).getValue();
        assertEquals("c1", c1.getId());
        assertNull(c1.getExtensionElements());

        // Task c2
        TTask c2 = (TTask) c6.getFlowElement().get(3).getValue();
        assertEquals("c2", c2.getId());
        /*
        assertNotNull(c2.getExtensionElements());
        List anys = (List) c2.getExtensionElements().getAny();
        assertEquals(1, anys.size());
        Element element = (Element) anys.get(0);
        */

        // End event c1
        TEndEvent c3 = (TEndEvent) c6.getFlowElement().get(4).getValue();
        assertEquals("c3", c3.getId());
        assertNull(c3.getExtensionElements());

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
     * Test decanonization to <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.bpmn">expectation #1</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Expected 1.svg"/></div>
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
        BpmnDefinitions definitions = testDecanonise("Pool");
    }

    /**
     * Test decanonisation of <code>TwoLanes.cpf</code> and <code>TwoLanes.anf</code>.
     */
    @Test
    public final void testDecanoniseTwoLanes() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("TwoLanes");
    }

    /**
     * Test decanonisation of <code>TwoPools.cpf</code> and <code>TwoPools.anf</code>.
     */
    @Test
    public final void testDecanoniseTwoPools() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("TwoPools");
    }

    /**
     * Test decanonisaztion of <code>Subprocess.cpf</code> and <code>Subprocess.and</code>.
     */
    @Test
    public final void testDecanoniseSubprocess() throws Exception {
        
        // Obtain the test instance
        BpmnDefinitions definitions = testDecanonise("Subprocess");

        // Process c6
        assertEquals(BpmnProcess.class, definitions.getRootElement().get(0).getValue().getClass());
        TProcess c6 = (TProcess) definitions.getRootElement().get(0).getValue();
        assertEquals("c6", c6.getId());

        // Expect 5 flow elements
        assertEquals(5, c6.getFlowElement().size());

        // Start event c1
        TStartEvent c1 = (TStartEvent) c6.getFlowElement().get(2).getValue();
        assertEquals("c1", c1.getId());

        // SubProcess c2
        TSubProcess c2 = (TSubProcess) c6.getFlowElement().get(3).getValue();
        assertEquals("c2", c2.getId());
        assertEquals(5, c2.getFlowElement().size());
    }
}
