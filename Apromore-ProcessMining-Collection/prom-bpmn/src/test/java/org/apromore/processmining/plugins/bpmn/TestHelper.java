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
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnImportPlugin;

import java.io.File;
import java.io.FileInputStream;

public class TestHelper {
    public static BPMNDiagram readBPMNDiagram(String fullFilePath) throws Exception {
        BpmnImportPlugin bpmnImport = new BpmnImportPlugin();
        return bpmnImport.importFromStreamToDiagram(new FileInputStream(new File(fullFilePath)), fullFilePath);
    }

    public static String exportFromDiagram(BPMNDiagram d) {
        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(d); // recreate layout
        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);
        String model = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">" +
                definitions.exportElements() +
                "</definitions>";
        return model;
    }

    public static BPMNNode getFirstActivity(BPMNDiagram d, String label) {
        return d.getNodes().stream()
                .filter(n -> n instanceof Activity && n.getLabel().equals(label))
                .findFirst()
                .get();
    }

    public static BPMNNode getFirstEvent(BPMNDiagram d, Event.EventType eventType) {
        return d.getNodes().stream()
                .filter(n -> n instanceof Event && ((Event)n).getEventType() == eventType)
                .findFirst()
                .get();
    }

    public static BPMNNode getFirstGateway(BPMNDiagram d, Gateway.GatewayType gatewayType, boolean isSplit) {
        return d.getNodes().stream()
                .filter(n -> n instanceof Gateway &&
                        ((Gateway)n).getGatewayType() == gatewayType &&
                        ((isSplit && d.getInEdges(n).size() == 1) || !isSplit && (d.getOutEdges(n).size() == 1)))
                .findFirst()
                .get();
    }

    public static String getNodeExport(BPMNDiagram d, BPMNNode node, String template) {
        String[] export = {template.replace("nodeID", node.getId().toString())};

        int[] index = {0};
        d.getInEdges(node).stream()
                .sorted((o1, o2) -> o1.getEdgeID().toString().compareTo(o2.getEdgeID().toString()))
                .forEach(e -> export[0] = export[0].replace("incomingID_" + index[0]++, e.getEdgeID().toString()));

        index[0] = 0;
        d.getOutEdges(node).stream()
                .sorted((o1, o2) -> o1.getEdgeID().toString().compareTo(o2.getEdgeID().toString()))
                .forEach(e -> export[0] = export[0].replace("outgoingID_" + index[0]++, e.getEdgeID().toString()));

        return export[0];
    }
}
