package org.apromore.canoniser.bpmn;

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
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.bpmn.cpf.CpfIDResolver;
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.bpmn.cpf.CpfUnmarshallerListener;
import org.apromore.canoniser.bpmn.cpf.CpfXORJoinType;
import org.apromore.canoniser.bpmn.cpf.CpfXORSplitType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
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
 * Test suite for {@link BPMN20Canoniser}.
 *
 * A number of these tests are from <cite>Canonization Service for AProMoRe</cite>.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 * @see <a href="http://apromore.org/wp-content/uploads/2010/12/AProMoReCanonization_v1.0.pdf">Canonization
 *     Service for AProMoRe</a>, page 24-25
 */
public class BPMN20CanoniserTest implements TestConstants {

    // Tests

    /**
     * Test {@link BPMN20Canoniser#canonise(InputStream, List<AnnotationsType>, List<CanonicalProcessType>, PluginRequest)}.
     */
    @Test
    public final void testCanonise() throws Exception {

        // Construct test instance
        BPMN20Canoniser canoniser = new BPMN20Canoniser();
        PluginRequest request = null;
        InputStream bpmnInput = new FileInputStream(new File(MODELS_DIR, "Case 1.bpmn20.xml"));
        List<AnnotationsType> anfs = new ArrayList<AnnotationsType>();
        List<CanonicalProcessType> cpfs = new ArrayList<CanonicalProcessType>();
        PluginResult result = canoniser.canonise(bpmnInput, anfs, cpfs, request);

        // Inspect the result
        assertEquals(1, anfs.size());
        assertEquals(1, cpfs.size());
        testCanonise1(cpfs.get(0));
    }

    /**
     * Test {@link BPMN20Canoniser#createInitialNativeFormat}.
     */
    @Test
    public final void testCreateInitialNativeFormat() throws Exception {

        // Construct test instance
        ByteArrayOutputStream initialBPMN = new ByteArrayOutputStream();
        BPMN20Canoniser canoniser = new BPMN20Canoniser();
        Date now = new Date();
        PluginRequest request = null;
        PluginResult result = canoniser.createInitialNativeFormat(initialBPMN,
                                                                  "Test",                         // process name
                                                                  "0.0",                          // process version
                                                                  getClass().getCanonicalName(),  // process author
                                                                  now,                            // creation timestamp
                                                                  request);
        initialBPMN.close();

        // Serialize out the empty BPMN model for offline inspection
        OutputStream out = new FileOutputStream(new File(OUTPUT_DIR, "initial.bpmn20.xml"));
        out.write(initialBPMN.toByteArray());
        out.close();

        // Validate the empty BPMN model
        TDefinitions definitions = BpmnDefinitions.newInstance(new ByteArrayInputStream(initialBPMN.toByteArray()), true);
    }

    /**
     * Test {@link BPMN20Canoniser#deCanonise}.
     */
    @Test
    public final void testDeCanonise() throws Exception {

        CanonicalProcessType cpf = CpfCanonicalProcessType.newInstance(new FileInputStream(new File(TESTCASES_DIR, "Basic.cpf")), true);
        AnnotationsType anf = null;
        ByteArrayOutputStream bpmnOutput = new ByteArrayOutputStream();
        PluginRequest request = null;
        BPMN20Canoniser canoniser = new BPMN20Canoniser();
        PluginResult result = canoniser.deCanonise(cpf, anf, bpmnOutput, request);
    }

    // Canonisation tests - these exercise the static version of BPMN20Canoniser#canonise, rather than the API method

    /**
     * Test canonisation of <code>Test1.bpmn20.xml</code>.
     */
    @Test
    public final void test1() throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, "Test1.bpmn20.xml")), false);

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

        // Validate and serialize the canonised documents to be inspected offline
        CanoniserResult result = BPMN20Canoniser.canonise(definitions);
        assertEquals(1, result.size());
        assertNotNull(result.getAnf(0));
        assertNotNull(result.getCpf(0));

        ((AnfAnnotationsType) result.getAnf(0)).marshal(new FileOutputStream(new File(OUTPUT_DIR, "Test1.anf")), true);
        ((CpfCanonicalProcessType) result.getCpf(0)).marshal(new FileOutputStream(new File(OUTPUT_DIR, "Test1.cpf")), true);
    }

    /**
     * Common canonisation test code.
     *
     * Parses <code><var>filename</var>.bpmn20.xml</code> and validates the resulting CPF and ANF against their
     * respective XML schemas.
     *
     * @param filename  the unique part of the source filename
     * @return a test instance whose ANF and CPF representations have been XML schema validated
     * @throws Exception if anything goes amiss setting up the test
     */
    private CanoniserResult testCanonise(String filename) throws Exception {

        // Obtain the test instance
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(MODELS_DIR, filename + ".bpmn20.xml")), true);

        // Validate and serialize the canonised documents to be inspected offline
        CanoniserResult result = BPMN20Canoniser.canonise(definitions);
        assertEquals(1, result.size());
        assertNotNull(result.getAnf(0));
        assertNotNull(result.getCpf(0));

        // Output the ANF
        ((AnfAnnotationsType) result.getAnf(0)).marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".anf")), false);

        // Output the CPF
        ((CpfCanonicalProcessType) result.getCpf(0)).marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf")), false);

        // Validate the ANF
        ((AnfAnnotationsType) result.getAnf(0)).marshal(new NullOutputStream(), true);

        // Validate the CPF
        ((CpfCanonicalProcessType) result.getCpf(0)).marshal(new NullOutputStream(), true);

        return result;
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 1.bpmn20.xml">case #1</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 1.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise1() throws Exception {
        testCanonise1(testCanonise("Case 1").getCpf(0));
    }

    /**
     * Shared code between {@link testCanonise()} and {@link testCanonise1()}, since they both read
     * <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 1.bpmn20.xml">case #1</a>.
     */
    private void testCanonise1(CanonicalProcessType cpf) throws Exception {

        // Expect 3 nodes
        NetType net = cpf.getNet().get(0);
        assertEquals(3, net.getNode().size());

        // Start event "E1"
        NodeType e1 = net.getNode().get(0);
        assertEquals("E1", e1.getName());
        assertEquals(CpfEventType.class, e1.getClass());

        // Task "A"
        NodeType a = net.getNode().get(1);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());

        // End event "E2"
        NodeType e2 = net.getNode().get(2);
        assertEquals("E2", e2.getName());
        assertEquals(CpfEventType.class, e2.getClass());

        // Expect 2 edges
        assertEquals(2, net.getEdge().size());

        // Sequence flow from E1 to A
        EdgeType e1_a = net.getEdge().get(0);
        assertNull(e1_a.getConditionExpr());
        assertEquals(e1.getId(), e1_a.getSourceId());
        assertEquals(a.getId(), e1_a.getTargetId());

        // Sequence flow from A to E2
        EdgeType a_e2 = net.getEdge().get(1);
        assertNull(a_e2.getConditionExpr());
        assertEquals(a.getId(), a_e2.getSourceId());
        assertEquals(e2.getId(), a_e2.getTargetId());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 2.bpmn20.xml">case #2</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 2.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise2() throws Exception {
        NetType net = testCanonise("Case 2").getCpf(0).getNet().get(0);

        // Expect 4 nodes
        assertEquals(4, net.getNode().size());

        // Task "A"
        NodeType a = net.getNode().get(0);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());

        // XOR Split
        NodeType xor = net.getNode().get(1);
        assertNull(xor.getName());
        assertEquals(CpfXORSplitType.class, xor.getClass());

        // Task "B"
        NodeType b = net.getNode().get(2);
        assertEquals("B", b.getName());
        assertEquals(CpfTaskType.class, b.getClass());

        // Task "C"
        NodeType c = net.getNode().get(3);
        assertEquals("C", c.getName());
        assertEquals(CpfTaskType.class, c.getClass());

        // Expect 3 edges
        assertEquals(3, net.getEdge().size());

        // Sequence flow from A to XOR
        EdgeType a_xor = net.getEdge().get(0);
        assertNull(a_xor.getConditionExpr());
        assertEquals(a.getId(), a_xor.getSourceId());
        //assertEquals(a, a_xor.getSourceRef());
        assertEquals(xor.getId(), a_xor.getTargetId());
        //assertEquals(xor, a_xor.getTargetRef());

        // Sequence flow "C1" from XOR to B
        EdgeType xor_b = net.getEdge().get(1);
        assertNotNull(xor_b.getConditionExpr());
        assertNotNull(xor_b.getConditionExpr().getExpression());
        assertEquals("C1", xor_b.getConditionExpr().getExpression());
        assertEquals(xor.getId(), xor_b.getSourceId());
        //assertEquals(xor, xor_b.getSourceRef());
        assertEquals(b.getId(), xor_b.getTargetId());
        //assertEquals(b, xor_b.getTargetRef());

        // Sequence flow "C2" from XOR to C
        EdgeType xor_c = net.getEdge().get(2);
        assertEquals("C2", xor_c.getConditionExpr().getExpression());
        assertEquals(xor.getId(), xor_c.getSourceId());
        //assertEquals(xor, xor_c.getSourceRef());
        assertEquals(c.getId(), xor_c.getTargetId());
        //assertEquals(c, xor_c.getTargetRef());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 5.bpmn20.xml">case #5</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 5.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise5() throws Exception {
        NetType net = testCanonise("Case 5").getCpf(0).getNet().get(0);

        // Expect 4 nodes
        assertEquals(4, net.getNode().size());

        // Task "A"
        NodeType a = net.getNode().get(0);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());

        // XOR Join
        NodeType xor = net.getNode().get(1);
        assertNull(xor.getName());
        assertEquals(CpfXORJoinType.class, xor.getClass());

        // Task "B"
        NodeType b = net.getNode().get(2);
        assertEquals("B", b.getName());
        assertEquals(CpfTaskType.class, b.getClass());

        // Task "C"
        NodeType c = net.getNode().get(3);
        assertEquals("C", c.getName());
        assertEquals(CpfTaskType.class, c.getClass());

        // Expect 3 edges
        assertEquals(3, net.getEdge().size());

        // Sequence flow from A to XOR
        EdgeType a_xor = net.getEdge().get(0);
        assertNull(a_xor.getConditionExpr());
        assertEquals(a.getId(), a_xor.getSourceId());
        assertEquals(xor.getId(), a_xor.getTargetId());

        // Sequence flow B to XOR
        EdgeType b_xor = net.getEdge().get(1);
        assertNull(b_xor.getConditionExpr());
        assertEquals(b.getId(), b_xor.getSourceId());
        assertEquals(xor.getId(), b_xor.getTargetId());

        // Sequence flow from XOR to C
        EdgeType xor_c = net.getEdge().get(2);
        assertNull(xor_c.getConditionExpr());
        assertEquals(xor.getId(), xor_c.getSourceId());
        assertEquals(c.getId(), xor_c.getTargetId());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 8.bpmn20.xml">case #8</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 8.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise8() throws Exception {
        CanonicalProcessType cpf = testCanonise("Case 8").getCpf(0);

        // Expect 1 graph, 2 resource types
        assertEquals(1, cpf.getNet().size());
        assertEquals(2, cpf.getResourceType().size());

        // Pool "P"
        ResourceTypeType p = cpf.getResourceType().get(0);
        assertEquals("P", p.getName());
        assertEquals(CpfResourceTypeType.class, p.getClass());

        // Implicit lane within "P"
        ResourceTypeType p_lane = cpf.getResourceType().get(1);
        assertEquals("", p_lane.getName());
        assertEquals(CpfResourceTypeType.class, p_lane.getClass());

        // Resource type specialization hierarchy: p_lane in p
        assertEquals(Collections.emptyList(), p_lane.getSpecializationIds());
        assertEquals(Collections.singletonList(p_lane.getId()), p.getSpecializationIds());

        // Expect 3 nodes
        NetType net = cpf.getNet().get(0);
        assertEquals(3, net.getNode().size());

        // Start event "E1"
        NodeType e1 = net.getNode().get(0);
        assertEquals("E1", e1.getName());
        assertEquals(CpfEventType.class, e1.getClass());
        assertEquals(1, ((EventType) e1).getResourceTypeRef().size()); 
        assertEquals(p_lane.getId(), ((EventType) e1).getResourceTypeRef().get(0).getResourceTypeId()); 

        // Task "A"
        NodeType a = net.getNode().get(1);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());
        assertEquals(1, ((TaskType) a).getResourceTypeRef().size()); 
        assertEquals(p_lane.getId(), ((TaskType) a).getResourceTypeRef().get(0).getResourceTypeId()); 

        // End event "E2"
        NodeType e2 = net.getNode().get(2);
        assertEquals("E2", e2.getName());
        assertEquals(CpfEventType.class, e2.getClass());
        assertEquals(1, ((EventType) e2).getResourceTypeRef().size()); 
        assertEquals(p_lane.getId(), ((EventType) e2).getResourceTypeRef().get(0).getResourceTypeId()); 

        // Expect 2 edges
        assertEquals(2, net.getEdge().size());

        // Sequence flow from E1 to A
        EdgeType e1_a = net.getEdge().get(0);
        assertNull(e1_a.getConditionExpr());
        assertEquals(e1.getId(), e1_a.getSourceId());
        assertEquals(a.getId(), e1_a.getTargetId());

        // Sequence flow A to E1
        EdgeType a_e2 = net.getEdge().get(1);
        assertNull(a_e2.getConditionExpr());
        assertEquals(a.getId(), a_e2.getSourceId());
        assertEquals(e2.getId(), a_e2.getTargetId());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 9.bpmn20.xml">case #9</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 9.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise9() throws Exception {
        CanonicalProcessType cpf = testCanonise("Case 9").getCpf(0);

        // Expect 1 graph, 3 resource types
        assertEquals(1, cpf.getNet().size());
        assertEquals(3, cpf.getResourceType().size());

        // Pool "P"
        ResourceTypeType p = cpf.getResourceType().get(0);
        assertEquals("P", p.getName());
        assertEquals(CpfResourceTypeType.class, p.getClass());

        // Anonymous lane inside pool "P"
        ResourceTypeType p_lane = cpf.getResourceType().get(1);
        assertEquals("", p_lane.getName());
        assertEquals(CpfResourceTypeType.class, p_lane.getClass());

        // Lane "L"
        ResourceTypeType l = cpf.getResourceType().get(2);
        assertEquals("L", l.getName());
        assertEquals(CpfResourceTypeType.class, l.getClass());

        // Resource type specialization hierarchy: l in p_lane in p
        assertEquals(Collections.emptyList(), l.getSpecializationIds());
        assertEquals(Collections.singletonList(l.getId()), p_lane.getSpecializationIds());
        assertEquals(Collections.singletonList(p_lane.getId()), p.getSpecializationIds());

        // Expect 3 nodes
        NetType net = cpf.getNet().get(0);
        assertEquals(3, net.getNode().size());

        // Start event "E1"
        NodeType e1 = net.getNode().get(0);
        assertEquals("E1", e1.getName());
        assertEquals(CpfEventType.class, e1.getClass());
        assertEquals(1, ((EventType) e1).getResourceTypeRef().size()); 
        assertEquals(l.getId(), ((EventType) e1).getResourceTypeRef().get(0).getResourceTypeId()); 

        // Task "A"
        NodeType a = net.getNode().get(1);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());
        assertEquals(1, ((TaskType) a).getResourceTypeRef().size()); 
        assertEquals(l.getId(), ((TaskType) a).getResourceTypeRef().get(0).getResourceTypeId()); 

        // End event "E2"
        NodeType e2 = net.getNode().get(2);
        assertEquals("E2", e2.getName());
        assertEquals(CpfEventType.class, e2.getClass());
        assertEquals(1, ((EventType) e2).getResourceTypeRef().size()); 
        assertEquals(l.getId(), ((EventType) e2).getResourceTypeRef().get(0).getResourceTypeId()); 

        // Expect 2 edges
        assertEquals(2, net.getEdge().size());

        // Sequence flow from E1 to A
        EdgeType e1_a = net.getEdge().get(0);
        assertNull(e1_a.getConditionExpr());
        assertEquals(e1.getId(), e1_a.getSourceId());
        assertEquals(a.getId(), e1_a.getTargetId());

        // Sequence flow A to E1
        EdgeType a_e2 = net.getEdge().get(1);
        assertNull(a_e2.getConditionExpr());
        assertEquals(a.getId(), a_e2.getSourceId());
        assertEquals(e2.getId(), a_e2.getTargetId());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 10.bpmn20.xml">case #10</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 10.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise10() throws Exception {
        testCanonise("Case 10");

        // not yet implemented
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 11.bpmn20.xml">case #11</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 11.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise11() throws Exception {
        testCanonise("Case 11");

        // not yet implemented
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 12.bpmn20.xml">case #12</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 12.bpmn20.svg"/></div>
     */
    @Test
    public void testCanonise12() throws Exception {
        //BpmnDefinitions definitions = testCanonise("Case 12");

        // not yet implemented
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Subprocess.bpmn20.xml">subprocess</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Subprocess.bpmn20.svg"/></div>
     */
    @Test
    public void testCanoniseSubprocess() throws Exception {
        CanonicalProcessType cpf = testCanonise("Subprocess").getCpf(0);
        assertEquals(2, cpf.getNet().size());  // Expecting 2 nets, the root and the BPMN SubProcess

        // Root net
        NetType net = cpf.getNet().get(0);
        assertEquals(2, net.getEdge().size());
        assertEquals(3, net.getNode().size());
        assertEquals(Collections.singletonList(net.getId()), cpf.getRootIds());

        assertEquals("Start", ((EventType) net.getNode().get(0)).getName());
        assertTrue(net.getNode().get(1) instanceof TaskType);
        TaskType task = (TaskType) net.getNode().get(1);  // CPF Task corresponding to the BPMN SubProcess
        assertEquals("Subprocess", task.getName());
        assertEquals("End", ((EventType) net.getNode().get(2)).getName());

        // Subnet
        NetType subnet = cpf.getNet().get(1);
        assertEquals(2, subnet.getEdge().size());
        assertEquals(3, subnet.getNode().size());
        assertEquals(task.getSubnetId(), subnet.getId());

        assertEquals("Start 2", ((EventType) subnet.getNode().get(0)).getName());
        assertEquals("Task",    ((TaskType)  subnet.getNode().get(1)).getName());
        assertEquals("End 2",   ((EventType) subnet.getNode().get(2)).getName());
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Call Activity.bpmn20.xml">call activity</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Call Activity.bpmn20.svg"/></div>
     */
    @Test
    public void testCanoniseCallActivity() throws Exception {
        CanonicalProcessType cpf = testCanonise("Call Activity").getCpf(0);
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Request_For_Advance_Payment.bpmn20.xml">request for advance payment</a>.
     */
    @Test
    public void testCanoniseRequestForAdvancePayment() throws Exception {
        CanonicalProcessType cpf = testCanonise("Request_For_Advance_Payment").getCpf(0);
    }
}
