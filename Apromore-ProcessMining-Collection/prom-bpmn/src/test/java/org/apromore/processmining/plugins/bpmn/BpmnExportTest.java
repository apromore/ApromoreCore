/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.processmining.plugins.bpmn;

import static org.junit.jupiter.api.Assertions.*;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.junit.jupiter.api.Test;

class BpmnExportTest {
    @Test
    void testStartEvent() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String template =   "<startEvent id=\"nodeID\">" +
                            "<outgoing>outgoingID_0</outgoing>" +
                            "</startEvent>";
        BPMNNode node = TestHelper.getFirstEvent(d, Event.EventType.START);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, node, template)));
    }

    @Test
    void testEndEvent() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String template =   "<endEvent id=\"nodeID\">" +
                            "<incoming>incomingID_0</incoming>" +
                            "</endEvent>";
        BPMNNode node = TestHelper.getFirstEvent(d, Event.EventType.END);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, node, template)));
    }

    @Test
    void testActivity() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String template =   "<task id=\"nodeID\" name=\"A\">" +
                            "<incoming>incomingID_0</incoming>" +
                            "<outgoing>outgoingID_0</outgoing>" +
                            "</task>";
        BPMNNode node = TestHelper.getFirstActivity(d, "A");
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, node, template)));
    }

    @Test
    void testXOR() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String splitTemplate =  "<exclusiveGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "<outgoing>outgoingID_1</outgoing>" +
                                "<outgoing>outgoingID_2</outgoing>" +
                                "</exclusiveGateway>";
        BPMNNode split = TestHelper.getFirstGateway(d, Gateway.GatewayType.DATABASED, true);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, split, splitTemplate)));

        String joinTemplate =   "<exclusiveGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<incoming>incomingID_1</incoming>" +
                                "<incoming>incomingID_2</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "</exclusiveGateway>";
        BPMNNode join = TestHelper.getFirstGateway(d, Gateway.GatewayType.DATABASED, false);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, join, joinTemplate)));

    }

    @Test
    void testAND() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String splitTemplate =  "<parallelGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "<outgoing>outgoingID_1</outgoing>" +
                                "</parallelGateway>";
        BPMNNode split = TestHelper.getFirstGateway(d, Gateway.GatewayType.PARALLEL, true);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, split, splitTemplate)));

        String joinTemplate =   "<parallelGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<incoming>incomingID_1</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "</parallelGateway>";
        BPMNNode join = TestHelper.getFirstGateway(d, Gateway.GatewayType.PARALLEL, false);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, join, joinTemplate)));
    }

    @Test
    void testOR() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String splitTemplate =  "<inclusiveGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "<outgoing>outgoingID_1</outgoing>" +
                                "</inclusiveGateway>";
        BPMNNode split = TestHelper.getFirstGateway(d, Gateway.GatewayType.INCLUSIVE, true);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, split, splitTemplate)));

        String joinTemplate =   "<inclusiveGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<incoming>incomingID_1</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "</inclusiveGateway>";
        BPMNNode join = TestHelper.getFirstGateway(d, Gateway.GatewayType.INCLUSIVE, false);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, join, joinTemplate)));
    }

    @Test
    void testEventBasedGateway() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/Diagram_Basic_Elements_Connected.bpmn");
        String bpmnExport = TestHelper.exportFromDiagram(d).replaceAll("\n", "");

        String splitTemplate =  "<eventBasedGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "<outgoing>outgoingID_1</outgoing>" +
                                "</eventBasedGateway>";
        BPMNNode split = TestHelper.getFirstGateway(d, Gateway.GatewayType.EVENTBASED, true);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, split, splitTemplate)));

        String joinTemplate =   "<eventBasedGateway id=\"nodeID\">" +
                                "<incoming>incomingID_0</incoming>" +
                                "<incoming>incomingID_1</incoming>" +
                                "<outgoing>outgoingID_0</outgoing>" +
                                "</eventBasedGateway>";
        BPMNNode join = TestHelper.getFirstGateway(d, Gateway.GatewayType.EVENTBASED, false);
        assertTrue(bpmnExport.contains(TestHelper.getNodeExport(d, join, joinTemplate)));
    }
}