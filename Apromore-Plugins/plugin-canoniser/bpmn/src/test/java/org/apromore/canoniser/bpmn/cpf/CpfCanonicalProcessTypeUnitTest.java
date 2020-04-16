/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;

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
import org.apromore.canoniser.bpmn.anf.AnfAnnotationsType;
import org.apromore.cpf.*;

/**
 * Test suite for {@link CpfCanonicalProcessType}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessTypeUnitTest implements TestConstants {

    // Tests

    /** Test {@link CpfCanonicalProcessType#newInstance} at parsing <code>Basic.cpf</code>.. */
    @Test
    public void testNewInstance() throws Exception {

        CpfCanonicalProcessType cpf = CpfCanonicalProcessType.newInstance(new FileInputStream(new File(CANONICAL_MODELS_DIR, "Basic.cpf")), true);

        // TODO - actually test the content here
    }

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
        BpmnDefinitions definitions = BpmnDefinitions.newInstance(new FileInputStream(new File(BPMN_MODELS_DIR, filename)), true);

        // Validate and serialize the canonised documents to be inspected offline
        CpfCanonicalProcessType cpf = new CpfCanonicalProcessType(definitions);
        cpf.setUri("dummy");

        assertEquals(1, definitions.getBPMNDiagram().size());

        AnfAnnotationsType anf = new AnfAnnotationsType(definitions, definitions.getBPMNDiagram().get(0));
        anf.setUri("dummy");

        // Output the CPF
        cpf.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf")), false);
        anf.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".anf")), false);

        // Validate the CPF
        cpf.marshal(new NullOutputStream(), true);
        anf.marshal(new NullOutputStream(), true);

        // Round-trip the CPF back into BPMN
        BpmnDefinitions definitions2 = BpmnDefinitions.newInstance(cpf, anf);
        definitions2.marshal(new FileOutputStream(new File(OUTPUT_DIR, filename + ".cpf+anf.bpmn")), false);
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
        CpfNetType net = (CpfNetType) cpf.getNet().get(0);
        assertEquals(3, net.getNode().size());
    
        // Start event "E1"
        NodeType e1 = net.getNode().get(0);
        assertEquals("E1", e1.getName());
        assertEquals(CpfEventTypeImpl.class, e1.getClass());
        
        // Task "A"
        NodeType a = net.getNode().get(1);
        assertEquals("A", a.getName());
        assertEquals(CpfTaskType.class, a.getClass());

        // End event "E2"
        NodeType e2 = net.getNode().get(2);
        assertEquals("E2", e2.getName());
        assertEquals(CpfEventTypeImpl.class, e2.getClass());

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
        CpfCanonicalProcessType cpf = testCanonise("Case 8.bpmn");

        // Expect 1 graph, 2 resource types
        assertEquals(1, cpf.getNet().size());
        assertEquals(2, cpf.getResourceType().size());

        // Pool "P"
        ResourceTypeType p = (ResourceTypeType) cpf.getResourceType().get(1);//getElement("sid-C08F4656-30B9-4D6D-9086-20469AB54D8B");
        assertNotNull(p);
        assertEquals("P", p.getName());
        assertEquals(CpfResourceTypeTypeImpl.class, p.getClass());

        // Implicit lane within "P"
        ResourceTypeType p_lane = (ResourceTypeType) cpf.getResourceType().get(0);//getElement("sid-C599C65B-4C1E-44B8-9653-E6E849AE31D5");
        assertNotNull(p_lane);
        assertEquals("", p_lane.getName());
        assertEquals(CpfResourceTypeTypeImpl.class, p_lane.getClass());

        // Resource type specialization hierarchy: p_lane in p
        assertEquals(Collections.emptyList(), p_lane.getSpecializationIds());
        assertEquals(Collections.singletonList(p_lane.getId()), p.getSpecializationIds());

        // Expect 3 nodes
        NetType net = cpf.getNet().get(0);
        assertEquals(3, net.getNode().size());

        // Start event "E1"
        NodeType e1 = net.getNode().get(0);
        assertEquals("E1", e1.getName());
        assertEquals(CpfEventTypeImpl.class, e1.getClass());
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
        assertEquals(CpfEventTypeImpl.class, e2.getClass());
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
        ResourceTypeType p = cpf.getResourceType().get(2);
        assertEquals("P", p.getName());
        assertEquals(CpfResourceTypeTypeImpl.class, p.getClass());

        // Anonymous lane inside pool "P"
        ResourceTypeType p_lane = cpf.getResourceType().get(0);
        assertEquals("", p_lane.getName());
        assertEquals(CpfResourceTypeTypeImpl.class, p_lane.getClass());

        // Lane "L"
        ResourceTypeType l = cpf.getResourceType().get(1);
        assertEquals("L", l.getName());
        assertEquals(CpfResourceTypeTypeImpl.class, l.getClass());

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
        assertEquals(CpfEventTypeImpl.class, e1.getClass());
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
        assertEquals(CpfEventTypeImpl.class, e2.getClass());
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
        CpfCanonicalProcessType cpf = testCanonise("Case 12.bpmn");

        // Check that the timer has the correct date
        CpfTimerType timer = (CpfTimerType) cpf.getElement("sid-9901B6DB-42A9-48EF-B0D6-8EA51944CA42");
        assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar("2012-11-12T20:44:00"), timer.getTimeDate());
        assertNull(timer.getTimeDuration());
        assertNull(timer.getTimeExpression());

        // Check that A and its timer boundary event cancel each other
        CpfTaskType a = (CpfTaskType) cpf.getElement("sid-FA5E54FC-9090-45F4-8649-49052F106ABE");
        assertEquals(Collections.singleton(timer), a.getBoundaryEvents());
        assertEquals(1, a.getCancelNodeId().size());
        assertEquals(timer.getId(), a.getCancelNodeId().get(0).getRefId());
        assertEquals(1, timer.getCancelNodeId().size());
        assertEquals(a.getId(), timer.getCancelNodeId().get(0).getRefId());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Case 13.bpmn">case #13</a>.
     *
     * This is identical to case #12, except that the boundary timer event is non-interrupting.
     */
    @Test
    public void testCanonise13() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Case 13.bpmn");

        // Check that the timer has the correct date
        CpfTimerType timer = (CpfTimerType) cpf.getElement("sid-9901B6DB-42A9-48EF-B0D6-8EA51944CA42");
        assertEquals(DatatypeFactory.newInstance().newXMLGregorianCalendar("2012-11-12T20:44:00"), timer.getTimeDate());
        assertNull(timer.getTimeDuration());
        assertNull(timer.getTimeExpression());

        // Check that A cancels its timer boundary event, but that the event doesn't cancel the task
        CpfTaskType a = (CpfTaskType) cpf.getElement("sid-FA5E54FC-9090-45F4-8649-49052F106ABE");
        assertEquals(Collections.singleton(timer), a.getBoundaryEvents());
        assertEquals(1, a.getCancelNodeId().size());
        assertEquals(timer.getId(), a.getCancelNodeId().get(0).getRefId());
        assertEquals(0, timer.getCancelNodeId().size());
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
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Compensation.bpmn">compensation event</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Compensation.pdf"/></div>
     */
    @Ignore("Compensation events aren't supported; test will currently return that Event \"sid-0A3A91D7-91F6-4537-8D74-66C2C90FE23A\" has no edges")
    @Test
    public void testCompensationEvent() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Compensation.bpmn");

        // boundary compensation events are bizarre, and have NO edges in the original BPMN
        CpfEventType event = (CpfEventType) cpf.getElement("sid-0A3A91D7-91F6-4537-8D74-66C2C90FE23A");
        assertNotNull(event);
        assert event.isCompensation() : event.getId() + " ought to be a compensation event";
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Request_For_Advance_Payment.bpmn">request for advance payment</a>.
     */
    @Test
    public void testCanoniseRequestForAdvancePayment() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Request_For_Advance_Payment.bpmn");

        CpfNetType net = (CpfNetType) cpf.getNet().get(0);
        assertEquals(16, net.getNode().size());  // 2 events, 8 tasks, 2 explicit gates, 4 implicit gates
        assertEquals(18, net.getEdge().size());  // 14 explicit edges, 4 implicit edges

        CpfEventType end3 = (CpfEventType) cpf.getElement("End3");
        assertEquals(16, end3.getCancelNodeId().size());
        assertEquals(18, end3.getCancelEdgeId().size());
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch3_Downpayment (not used).bpmn">chapter 3 downpayment example</a>.
     */
    @Test
    public void testCh3Downpayment() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch3_Downpayment (not used).bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch4_CalloverTimer.bpmn">chapter 4 callover timer example</a>.
     */
    @Test
    public void testCh4CalloverTimer() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch4_CalloverTimer.bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch4_ExpenseReport2.bpmn">chapter 4 expense report example</a>.
     */
    @Ignore("Compensation events aren't supported; test will currently return that Event \"sid-F7B97F12-C41D-47E0-ACBE-5D0E42125E64\" has no edges")
    @Test
    public void testCh4ExpenseReport2() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch4_ExpenseReport2.bpmn");

        // boundary compensation events are bizarre, and have NO edges in the original BPMN
        CpfEventType event = (CpfEventType) cpf.getElement("sid-F7B97F12-C41D-47E0-ACBE-5D0E42125E64");
        assertNotNull(event);
        assertEquals(1, event.getIncomingEdges().size());  // this edge was synthesized
        assertEquals(0, event.getOutgoingEdges().size());
        assert event.isCompensation() : event.getId() + " ought to be a compensation event";
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch4_FreightInTransit.bpmn">chapter 4 freight in transit example</a>.
     */
    @Test
    public void testCh4FreightInTransit() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch4_FreightInTransit.bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch4_ISP.bpmn">chapter 4 ISP example</a>.
     */
    @Ignore  // closed subprocess with no content
    @Test
    public void testCh4ISP() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch4_ISP.bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch4_MinisterialCorrespondenceLoop.bpmn">chapter 4 ministerial correspondence loop example</a>.
     */
    @Test
    public void testCh4MinisterialCorrespondenceLoop() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch4_MinisterialCorrespondenceLoop.bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch4_Mortgage5_link1.bpmn">chapter 4 mortage example</a>.
     */
    @Ignore  // missing element sid-0FB009D8-F7AC-41B5-8DCB-F2EFDA327A19
    @Test
    public void testCh4Mortgage5() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch4_Mortgage5_link1.bpmn");
    }

    /**
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Public In-house Course Establishment - To Be Model v7.bpmn">public course example</a>.
     */
    @Ignore  // closed subprocess with no content
    @Test
    public void testPublicCourse() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Public In-house Course Establishment - To Be Model v7.bpmn");
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
     * Test canonization of the <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ch9_PurchaseOrder4Complete.bpmn">chapter 9 purchase order example</a>.
     */
    @Ignore("BPMN contains subprocesses empty of any events or tasks")
    @Test
    public void testCh9PurchaseOrder4Complete() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ch9_PurchaseOrder4Complete.bpmn");
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Defaulting.bpmn">a model with default flows</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Defaulting.svg"/></div>
     */
    @Ignore
    @Test
    public void testDefaulting() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Defaulting.bpmn");

        // Inspect the CPF
        CpfEdgeType edge0 = (CpfEdgeType) cpf.getElement("sid-4FB4D23F-C9EA-4AEE-B2A9-535D2D2F0C94");
        assert !edge0.isDefault() : "Edge of start Event can't be default";

        CpfEdgeType edge1 = (CpfEdgeType) cpf.getElement("sid-596877C3-00D7-425E-97E3-398F3FD8ADB0");
        assert edge1.isDefault() : "Default edge of Gate not found";

        CpfEdgeType edge2 = (CpfEdgeType) cpf.getElement("sid-61F2F275-41F5-4DA4-8A67-1F0384EBC99E");
        assert !edge2.isDefault() : "Non-default edge of Gate not found";

        CpfEdgeType edge3 = (CpfEdgeType) cpf.getElement("sid-91051909-BE2D-44AE-B8CE-B66ED9279A41");
        assert !edge3.isDefault() : "Non-default edge of Task not found";

        CpfEdgeType edge4 = (CpfEdgeType) cpf.getElement("sid-C2248280-A6CE-4167-B485-7C985973698A");
        assert edge4.isDefault() : "Default edge of Task not found";

        // Look for the synthesized AND split
        CpfANDSplitType split = (CpfANDSplitType) cpf.getElement("sid-2A1ABDB0-59AD-424A-A0C8-9A73A0679C21_implicit_split");
        CpfEdgeType splitEdge = (CpfEdgeType) cpf.getElement("sid-2A1ABDB0-59AD-424A-A0C8-9A73A0679C21_implicit_split_edge");
        assertEquals(Collections.singleton(splitEdge), split.getIncomingEdges());
        assertEquals(new HashSet<CpfEdgeType>(Arrays.asList(new CpfEdgeType[] {edge3, edge4})), split.getOutgoingEdges());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitJoin.bpmn">implicit join</a> of
     * multiple sequence flows entering a task..
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitJoin.svg"/></div>
     */
    @Test
    public void testImplicitJoin() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ImplicitJoin.bpmn");

        CpfNetType net = (CpfNetType) cpf.getNet().get(0);
        assertEquals(5, net.getNode().size());
        assertEquals(4, net.getEdge().size());

        CpfTaskType task = (CpfTaskType) cpf.getElement("sid-DD6A3F22-DDB8-4395-ACF3-FB933393BA7A");
        assertEquals(1, task.getIncomingEdges().size());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitSplit.bpmn">implicit split</a> of
     * multiple sequence flows exiting a task..
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/ImplicitSplit.svg"/></div>
     */
    @Test
    public void testImplicitSplit() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("ImplicitSplit.bpmn");

        CpfNetType net = (CpfNetType) cpf.getNet().get(0);
        assertEquals(5, net.getNode().size());
        assertEquals(4, net.getEdge().size());

        CpfTaskType task = (CpfTaskType) cpf.getElement("sid-B8464973-138F-4E6A-8880-AC5664D2E417");
        assertEquals(1, task.getOutgoingEdges().size());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Terminate.bpmn">a terminate event</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Terminate.svg"/></div>
     */
    @Test
    public void testTerminate() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Terminate.bpmn");

        // Check that the 3 nodes and 2 edges of the process containing the Terminate event are in the cancellation list
        CpfEventType event = (CpfEventType) cpf.getElement("sid-EBF52F7C-557C-48A4-8709-B056A04C97E2");
        assertEquals(3, event.getCancelNodeId().size());
        assertEquals(2, event.getCancelEdgeId().size());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Error.bpmn">error events</a>.
     *
     * <div><img src="{@docRoot}/../../../src/test/resources/BPMN_models/Error.svg"/></div>
     */
    @Test
    public void testError() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Error.bpmn");
    }

    /**
     * Test canonization of a <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Mixed gateway.bpmn">mixed gateway</a>.
     */
    @Test
    public void testMixedGateway() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Mixed gateway.bpmn");

        // Check that the single BPMN gateway has been canonized into separate CPF join and split routings
        CpfXORJoinType join = (CpfXORJoinType) cpf.getElement("sid-A6F282A2-5979-480D-A0AE-144386BE2CBF");
        assertNotNull("Join not found", join);
        CpfXORSplitType split = (CpfXORSplitType) cpf.getElement("sid-A6F282A2-5979-480D-A0AE-144386BE2CBF_mixed_split");
        assertNotNull("Split not found", split);
        CpfEdgeType edge = (CpfEdgeType) cpf.getElement("sid-A6F282A2-5979-480D-A0AE-144386BE2CBF_mixed_edge");
        assertNotNull("Edge connecting join and split not found", edge);
        //assertEquals(join, edge.getSourceRef());
        //assertEquals(split, edge.getTargetRef());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Vanilla.c.bpmn">a model with no configuration extensions</a>.
     */
    @Test
    public void testVanilla() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Vanilla.c.bpmn");
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Caramel.c.bpmn">a model with a configurable XOR gateway</a>.
     */
    @Test
    public void testCaramel() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Caramel.c.bpmn");
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/Chocolate.c.bpmn">a model with various configuration extensions</a>.
     */
    @Test
    @Ignore
    public void testChocolate() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("Chocolate.c.bpmn");
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/DataInput.bpmn">data object inputs</a>.
     */
    @Test
    public void testDataInput() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("DataInput.bpmn");

        // bpmn:dataInputAssociation.targetRef -> cpf:objectRef/attribute[@name='bpmn:dataInputAssociation.targetRef']
        CpfObjectRefType objectRef = (CpfObjectRefType) cpf.getElement("inputAssociation");
        assertNotNull("ObjectRef not found", objectRef);
        List<TypeAttribute> attributes = objectRef.getAttribute();
        assertEquals(1, attributes.size());
        TypeAttribute attribute = attributes.get(0);
        assertEquals("bpmn:dataInputAssociation.targetRef", attribute.getName());
        assertEquals("in", attribute.getValue());

        // bpmn.ioSpecification -> cpf:TaskType/attribute[@name='bpmn:ioSpecification']
        CpfTaskType task = (CpfTaskType) cpf.getElement("task");
        assertNotNull("Task not found", task);
        attributes = task.getAttribute();
        assertEquals(1, attributes.size());
        attribute = attributes.get(0);
        assertEquals("bpmn:ioSpecification", attribute.getName());
        assertNotNull(attribute.getValue());
    }

    /**
     * Test canonization of <a href="{@docRoot}/../../../src/test/resources/BPMN_models/DataOutput.bpmn">data object outputs</a>.
     */
    @Test
    public void testDataOutput() throws Exception {
        CpfCanonicalProcessType cpf = testCanonise("DataOutput.bpmn");

        // bpmn:dataOutputAssociation.sourceRef -> cpf:objectRef/attribute[@name='bpmn:dataOutputAssociation.sourceRef']
        CpfObjectRefType objectRef = (CpfObjectRefType) cpf.getElement("outputAssociation");
        assertNotNull("ObjectRef not found", objectRef);
        List<TypeAttribute> attributes = objectRef.getAttribute();
        assertEquals(2, attributes.size());
        TypeAttribute attribute = attributes.get(0);
        assertEquals("bpmn:dataOutputAssociation.sourceRef", attribute.getName());
        assertEquals("out", attribute.getValue());
        attribute = attributes.get(1);
        assertEquals("bpmn:dataOutputAssociation.targetRef", attribute.getName());
        assertEquals("object", attribute.getValue());

        // bpmn.ioSpecification -> cpf:TaskType/attribute[@name='bpmn:ioSpecification']
        CpfTaskType task = (CpfTaskType) cpf.getElement("task");
        assertNotNull("Task not found", task);
        attributes = task.getAttribute();
        assertEquals(1, attributes.size());
        attribute = attributes.get(0);
        assertEquals("bpmn:ioSpecification", attribute.getName());
        assertNotNull(attribute.getValue());
    }
}
