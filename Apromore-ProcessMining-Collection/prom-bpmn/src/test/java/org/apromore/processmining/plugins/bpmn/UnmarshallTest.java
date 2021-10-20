package org.apromore.processmining.plugins.bpmn;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnImportPlugin;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class UnmarshallTest {
    @Test
    public void test_Reading_Diagram_IDs_Retained() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/all_elements.bpmn");
        assertElementIDsRetained_all_elements_diagram(d);
    }

    @Test
    public void test_Reading_Diagram_IDs_Retained_After_Export() throws Exception {
        BPMNDiagram d = TestHelper.readBPMNDiagram("src/test/data/all_elements.bpmn");
        String exportedValue = TestHelper.exportFromDiagram(d);

        // Read the exported representation into diagram again and verify
        BpmnImportPlugin bpmnImport = new BpmnImportPlugin();
        BPMNDiagram d2 = bpmnImport.importFromStreamToDiagram(new ByteArrayInputStream(exportedValue.getBytes()),
                                            "all_elements_diagram");

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
}
