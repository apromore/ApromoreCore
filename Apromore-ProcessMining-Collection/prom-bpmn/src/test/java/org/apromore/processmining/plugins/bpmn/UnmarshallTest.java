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

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.LocalIDGenerator;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UnmarshallTest {
    private LocalIDGenerator idGenerator = new LocalIDGenerator();

    @Test
    void test_Reading_Diagram_IDs_Retained() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/all_elements.bpmn");
        assertElementIDsRetained_all_elements_diagram(d);
    }

    @Test
    void test_Reading_Diagram_IDs_Retained_After_Export() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/all_elements.bpmn");
        String exportedValue = TestHelper.exportFromDiagram(d);

        // Read the exported representation into diagram again and verify
        BPMNDiagram d2 = BPMNDiagramFactory.newDiagramFromProcessText(exportedValue);

        assertElementIDsRetained_all_elements_diagram(d2);
    }

    private void assertElementIDsRetained_all_elements_diagram(BPMNDiagram d) {
        assertEquals("StartEvent_116eqyz",
                d.getEvents().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("start"))
                        .findFirst().get().getId().toString());
        assertEquals("Event_0i8y03o",
                d.getEvents().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("end"))
                        .findFirst().get().getId().toString());
        assertEquals("Event_0tdjz3g",
                d.getEvents().stream()
                        .filter(a -> a.getEventType() == Event.EventType.INTERMEDIATE && a.getEventTrigger() == Event.EventTrigger.MESSAGE &&
                                a.getEventUse() == Event.EventUse.THROW)
                        .findFirst().get().getId().toString());
        assertEquals("Event_04axsqr",
                d.getEvents().stream()
                        .filter(a -> a.getEventType() == Event.EventType.INTERMEDIATE && a.getEventTrigger() == Event.EventTrigger.MESSAGE &&
                                a.getEventUse() == Event.EventUse.CATCH)
                        .findFirst().get().getId().toString());
        assertEquals("Event_0hrhc69",
                d.getEvents().stream()
                        .filter(a -> a.getEventType() == Event.EventType.INTERMEDIATE && a.getEventTrigger() == Event.EventTrigger.TIMER)
                        .findFirst().get().getId().toString());

        assertEquals("Activity_02qx3vl",
                d.getActivities().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("A"))
                        .findFirst().get().getId().toString());
        assertEquals("Gateway_0yo1qnp",
                d.getGateways().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("XOR"))
                        .findFirst().get().getId().toString());
        assertEquals("Gateway_0r81uxr",
                d.getGateways().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("AND"))
                        .findFirst().get().getId().toString());
        assertEquals("Gateway_16ji8bn",
                d.getGateways().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("OR"))
                        .findFirst().get().getId().toString());
        assertEquals("Gateway_1hxnw66",
                d.getGateways().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("EventBased"))
                        .findFirst().get().getId().toString());

        assertEquals("Flow_00unnid",
                d.getFlows().stream()
                        .filter(a -> a.getSource().getLabel() != null && a.getSource().getLabel().equals("start"))
                        .findFirst().get().getEdgeID().toString());
        assertEquals("Flow_109p5mt",
                d.getFlows().stream()
                        .filter(a -> a.getSource().getLabel() != null && a.getSource().getLabel().equals("A"))
                        .findFirst().get().getEdgeID().toString());
        assertEquals("Flow_102s47r",
                d.getFlows().stream()
                        .filter(a -> a.getSource().getLabel() != null && a.getSource().getLabel().equals("XOR") &&
                                a.getTarget().getLabel() != null && a.getTarget().getLabel().equals("AND"))
                        .findFirst().get().getEdgeID().toString());
        assertEquals("Flow_1aqyksb",
                d.getMessageFlows().stream()
                        .filter(a -> a.getSource().getLabel() != null && a.getSource().getLabel().equals("A"))
                        .findFirst().get().getEdgeID().toString());

        assertEquals("Activity_11lixbi",
                d.getSubProcesses().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("SubProcess"))
                        .findFirst().get().getId().toString());

        assertEquals("DataObjectReference_0y5i7vl",
                d.getDataObjects().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("document"))
                        .findFirst().get().getId().toString());

        assertEquals("Participant_1ykcmqv",
                d.getPools().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("O1"))
                        .findFirst().get().getId().toString());

        assertEquals("Lane_03yko6d",
                d.getSwimlanes().stream()
                        .filter(a -> a.getLabel() != null && a.getLabel().equals("R1"))
                        .findFirst().get().getId().toString());
    }

    @Test
    void test_Creating_Diagram_Random_IDs_Created() throws Exception {
        BPMNDiagram d = BPMNDiagramFactory.newBPMNDiagram("");

        Event startEvent = d.addEvent("Start", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
        assertValidID(startEvent.getId().toString());

        Event intermediate = d.addEvent("Throw", Event.EventType.INTERMEDIATE, Event.EventTrigger.NONE, Event.EventUse.THROW, false, null);
        assertValidID(intermediate.getId().toString());

        Activity a = d.addActivity("", false, false, false, false, false);
        assertValidID(a.getId().toString());

        Flow flow = d.addFlow(startEvent, a, "");
        assertValidID(flow.getEdgeID().toString());

        DataObject dataObject = d.addDataObject("");
        assertValidID(dataObject.getId().toString());

        DataAssociation dataAsoc = d.addDataAssociation(a, dataObject, "");
        assertValidID(dataAsoc.getEdgeID().toString());

        Swimlane lane = d.addSwimlane("O1", null, SwimlaneType.POOL);
        assertValidID(lane.getId().toString());

        Swimlane subLane = d.addSwimlane("R1", lane, SwimlaneType.LANE);
        assertValidID(subLane.getId().toString());

        MessageFlow msgFlow = d.addMessageFlow(a, lane, "");
        assertValidID(msgFlow.getEdgeID().toString());
    }

    private boolean assertValidID(String id) {
        return idGenerator.isValidId(id);
    }

    @Test
    void test_Reading_InvalidDiagram() throws Exception {
        try {
            BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/d1_invalid.bpmn");
            fail("Expected exception but no exceptions were thrown from reading an invalid diagram");
        }
        catch (Exception ex) {
            //
        }

        try {
            BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/d1_valid.bpmn");
        }
        catch (Exception ex) {
            fail("Expected no exception but exceptions were thrown from reading a valid diagram");
        }
    }
}
