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

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.apromore.processmining.plugins.bpmn.BpmnAssociation;
import org.junit.jupiter.api.Test;

class BPMNDiagramImplTest {

    @Test
    void testGetSubProcessDiagram() {
        BPMNDiagram oldBpmnDiagram = new BPMNDiagramImpl("Old model");
        SubProcess subProcess = oldBpmnDiagram.addSubProcess("subProcess", false, false, false, false, false);

        //Add nodes to the subprocess
        Activity activity = oldBpmnDiagram.addActivity("activity", false, false, false, false, false, subProcess);
        CallActivity callActivity = oldBpmnDiagram.addCallActivity("callActivity", false, false, false, false, false, subProcess);
        Event event = oldBpmnDiagram.addEvent("event", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, subProcess, false, null);
        Gateway gateway = oldBpmnDiagram.addGateway("gateway", Gateway.GatewayType.INCLUSIVE, subProcess);
        SubProcess innerSubProcess = oldBpmnDiagram.addSubProcess("inner subProcess", false, false, false, false, false, subProcess);
        Swimlane swimlane = oldBpmnDiagram.addSwimlane("swimlane", subProcess, SwimlaneType.POOL);

        //Must connect these to be associated with subProcess
        DataObject dataObject = oldBpmnDiagram.addDataObject("data object");
        TextAnnotation textAnnotation = oldBpmnDiagram.addTextAnnotation("text annotation");

        //Add one of each edge to the subProcess
        oldBpmnDiagram.addAssociation(textAnnotation, activity, BpmnAssociation.AssociationDirection.NONE);
        oldBpmnDiagram.addDataAssociation(callActivity, dataObject, "data association");
        oldBpmnDiagram.addFlow(event, gateway, "flow");
        oldBpmnDiagram.addMessageFlow(gateway, innerSubProcess, "message flow");

        //Non-subprocess nodes
        oldBpmnDiagram.addDataObject("disconnected data object");
        oldBpmnDiagram.addActivity("non-subprocess activity", false, false, false, false, false);

        //Check that the correct number of elements has been added to the new diagram. Node ids should remain the same.
        BPMNDiagram newBpmnDiagram = oldBpmnDiagram.getSubProcessDiagram(subProcess);
        assertEquals(1, newBpmnDiagram.getActivities().size());
        Activity newDiagramActivity = newBpmnDiagram.getActivities().stream().findFirst().orElse(null);
        assertEquals(activity.getId(), newDiagramActivity.getId());

        assertEquals(1, newBpmnDiagram.getCallActivities().size());
        CallActivity newDiagramCallActivity = newBpmnDiagram.getCallActivities().stream().findFirst().orElse(null);
        assertEquals(callActivity.getId(), newDiagramCallActivity.getId());

        assertEquals(1, newBpmnDiagram.getEvents().size());
        Event newDiagramEvent = newBpmnDiagram.getEvents().stream().findFirst().orElse(null);
        assertEquals(event.getId(), newDiagramEvent.getId());

        assertEquals(1, newBpmnDiagram.getGateways().size());
        Gateway newDiagramGateway = newBpmnDiagram.getGateways().stream().findFirst().orElse(null);
        assertEquals(gateway.getId(), newDiagramGateway.getId());

        assertEquals(1, newBpmnDiagram.getSubProcesses().size());
        SubProcess newDiagramSubprocess = newBpmnDiagram.getSubProcesses().stream().findFirst().orElse(null);
        assertEquals(innerSubProcess.getId(), newDiagramSubprocess.getId());

        assertEquals(1, newBpmnDiagram.getSwimlanes().size());
        Swimlane newDiagramSwimlane = newBpmnDiagram.getSwimlanes().stream().findFirst().orElse(null);
        assertEquals(swimlane.getId(), newDiagramSwimlane.getId());

        assertEquals(1, newBpmnDiagram.getDataObjects().size());
        DataObject newDiagramDataObject = newBpmnDiagram.getDataObjects().stream().findFirst().orElse(null);
        assertEquals(dataObject.getId(), newDiagramDataObject.getId());

        assertEquals(1, newBpmnDiagram.getTextAnnotations().size());
        TextAnnotation newDiagramTextAnnotation = newBpmnDiagram.getTextAnnotations().stream().findFirst().orElse(null);
        assertEquals(textAnnotation.getId(), newDiagramTextAnnotation.getId());

        assertEquals(1, newBpmnDiagram.getAssociations().size());
        assertEquals(1, newBpmnDiagram.getDataAssociations().size());
        assertEquals(1, newBpmnDiagram.getFlows().size());
        assertEquals(1, newBpmnDiagram.getMessageFlows().size());
    }
}
