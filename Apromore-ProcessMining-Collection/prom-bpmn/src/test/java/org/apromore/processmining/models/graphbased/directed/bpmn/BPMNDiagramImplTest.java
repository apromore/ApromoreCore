/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.processmining.models.graphbased.directed.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.apromore.processmining.plugins.bpmn.BpmnAssociation;
import org.junit.jupiter.api.Test;

public class BPMNDiagramImplTest {

    @Test
    void testCloneSubProcessContents() {
        BPMNDiagram oldBpmnDiagram = new BPMNDiagramImpl("Old model");
        SubProcess subProcess = oldBpmnDiagram.addSubProcess("subProcess", false, false, false, false, false);

        //Add nodes to the subprocess
        Activity activity = oldBpmnDiagram.addActivity("activity", false, false, false, false, false, subProcess);
        CallActivity callActivity = oldBpmnDiagram.addCallActivity("callActivity", false, false, false, false, false, subProcess);
        Event event = oldBpmnDiagram.addEvent("event", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, subProcess, false, null);
        Gateway gateway = oldBpmnDiagram.addGateway("gateway", Gateway.GatewayType.INCLUSIVE, subProcess);
        SubProcess innerSubProcess = oldBpmnDiagram.addSubProcess("inner subProcess", false, false, false, false, false, subProcess);
        oldBpmnDiagram.addSwimlane("swimlane", subProcess, SwimlaneType.POOL);

        //Must connect these to be associated with subProcess
        DataObject dataObject = oldBpmnDiagram.addDataObject("data object");
        TextAnnotation textAnnotation = oldBpmnDiagram.addTextAnnotation("text annotation");

        //Add one of each edge to the subProcess
        oldBpmnDiagram.addAssociation(activity, textAnnotation, BpmnAssociation.AssociationDirection.NONE);
        oldBpmnDiagram.addDataAssociation(callActivity, dataObject, "data association");
        oldBpmnDiagram.addFlow(event, gateway, "flow");
        oldBpmnDiagram.addMessageFlow(gateway, innerSubProcess, "message flow");

        BPMNDiagram newBpmnDiagram = new BPMNDiagramImpl("New model");
        assertTrue(newBpmnDiagram.getNodes().isEmpty());
        assertTrue(newBpmnDiagram.getEdges().isEmpty());

        newBpmnDiagram.cloneSubProcessContents(subProcess);
        assertEquals(1, newBpmnDiagram.getActivities().size());
        assertEquals(1, newBpmnDiagram.getCallActivities().size());
        assertEquals(1, newBpmnDiagram.getEvents().size());
        assertEquals(1, newBpmnDiagram.getGateways().size());
        assertEquals(1, newBpmnDiagram.getSubProcesses().size());
        assertEquals(1, newBpmnDiagram.getSwimlanes().size());

        assertEquals(1, newBpmnDiagram.getDataObjects().size());
        assertEquals(1, newBpmnDiagram.getTextAnnotations().size());

        assertEquals(1, newBpmnDiagram.getAssociations().size());
        assertEquals(1, newBpmnDiagram.getDataAssociations().size());
        assertEquals(1, newBpmnDiagram.getFlows().size());
        assertEquals(1, newBpmnDiagram.getMessageFlows().size());
    }
}
