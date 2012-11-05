package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;

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
import org.apromore.canoniser.bpmn.TestConstants;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.bpmn.BpmnObjectFactory;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.cpf.*;

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

        // Round-trip the CPF back into BPMN
        BpmnDefinitions definitions2 = new BpmnDefinitions(cpf, null);
        definitions2 = BpmnDefinitions.correctFlowNodeRefs(definitions2, new BpmnObjectFactory());
        definitions2.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf.bpmn")), false);
        definitions2.marshal(new NullOutputStream(), true);

        return cpf;
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 1.bpmn">case #1</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 1.svg"/></div>
     */
    @Test
    public void testCase1() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Case 1.bpmn");

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
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 2.bpmn">case #2</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 2.svg"/></div>
     */
    @Test
    public void testCanonise2() throws Exception {
        NetType net = testCanonise("Case 2.bpmn").getNet().get(0);

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
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 5.bpmn">case #5</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 5.svg"/></div>
     */
    @Test
    public void testCanonise5() throws Exception {
        NetType net = testCanonise("Case 5.bpmn").getNet().get(0);

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
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 8.bpmn">case #8</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 8.svg"/></div>
     */
    @Test
    public void testCanonise8() throws Exception {
        CanonicalProcessType cpf = testCanonise("Case 8.bpmn");

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
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 9.bpmn">case #9</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 9.svg"/></div>
     */
    @Test
    public void testCanonise9() throws Exception {
        CanonicalProcessType cpf = testCanonise("Case 9.bpmn");

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
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 10.bpmn">case #10</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 10.svg"/></div>
     */
    @Test
    public void testCanonise10() throws Exception {
        testCanonise("Case 10.bpmn");

        // not yet implemented
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 11.bpmn">case #11</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 11.svg"/></div>
     */
    @Test
    public void testCanonise11() throws Exception {
        testCanonise("Case 11.bpmn");

        // not yet implemented
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 12.bpmn">case #12</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Case 12.svg"/></div>
     */
    @Test
    public void testCanonise12() throws Exception {
        //BpmnDefinitions definitions = testCanonise("Case 12.bpmn");

        // not yet implemented
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Subprocess.bpmn">subprocess</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Subprocess.svg"/></div>
     */
    @Test
    public void testCanoniseSubprocess() throws Exception {
        CanonicalProcessType cpf = testCanonise("Subprocess.bpmn");
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
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Call Activity.bpmn">call activity</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Call Activity.svg"/></div>
     */
    @Test
    public void testCanoniseCallActivity() throws Exception {
        CanonicalProcessType cpf = testCanonise("Call Activity.bpmn");
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Request_For_Advance_Payment.bpmn">request for advance payment</a>.
     */
    @Test
    public void testCanoniseRequestForAdvancePayment() throws Exception {
        CanonicalProcessType cpf = testCanonise("Request_For_Advance_Payment.bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch9_loan5.bpmn">chapter 9 loan example</a>.
     */
    @Test
    public void testCh9Loan5() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch9_loan5.bpmn");

        // Inspect the CPF
        CpfEdgeType edge = (CpfEdgeType) cpf.getElement("sid-5C7AEE8B-C506-49B1-B8B1-A36DAC925D7B");
        assertNotNull(edge);
        assertEquals("sid-5CEDFABE-7E6F-450C-B84E-11C9917AB563", edge.getSourceId());
        assertEquals("sid-F48A9B5E-671A-42C9-82FB-7A3F231E7876", edge.getTargetId());

        CpfEventType event = (CpfEventType) cpf.getElement("sid-F48A9B5E-671A-42C9-82FB-7A3F231E7876");
        assertNotNull(event);
        assertEquals(0, event.getOutgoingEdges().size());
        assertEquals(1, event.getIncomingEdges().size());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Defaulting.bpmn">a model with default flows</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Defaulting.svg"/></div>
     */
    @Test
    public void testDefaulting() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Defaulting.bpmn");

        // Inspect the CPF
        CpfEdgeType edge0 = (CpfEdgeType) cpf.getElement("sid-4FB4D23F-C9EA-4AEE-B2A9-535D2D2F0C94");
        assert !edge0.isDefault() : "Edge of start Event can't be default";

        CpfEdgeType edge1 = (CpfEdgeType) cpf.getElement("sid-596877C3-00D7-425E-97E3-398F3FD8ADB0");
        assert edge1.isDefault() : "Default edge of XORSplit not found";

        CpfEdgeType edge2 = (CpfEdgeType) cpf.getElement("sid-C2248280-A6CE-4167-B485-7C985973698A");
        assert edge2.isDefault() : "Default edge of Task not found";
    }
}
