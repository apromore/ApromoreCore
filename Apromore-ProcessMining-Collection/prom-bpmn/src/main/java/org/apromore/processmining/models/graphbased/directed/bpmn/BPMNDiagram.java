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

import java.util.Collection;
import java.util.Set;

import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.CallActivity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataAssociation;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.DataObject;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.apromore.processmining.plugins.bpmn.BpmnAssociation.AssociationDirection;
import org.eclipse.collections.api.map.MutableMap;

/**
 * <b>BPMNDiagram</b> is a graph that provides support for BPMN specification
 * @author Anna Kalenkova
 * @author Bruce Nguyen
 *     - 20 Oct 2021: Add getNextId and addNextId, capability for BPMNDiagram to either create random IDs for elements
 *     or reuse existing IDs (e.g. from .bpmn files). Random IDs are needed when new BPMNDiagram is created from code.
 *     Reuse existing IDs is when the BPMNDiagram is created from importing .bpmn files or transferred over network.
 */
public interface BPMNDiagram extends DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> {

    /**
     * Generate the next ID used in creating diagram elements
     * This method will return any existing IDs added via {@link #setNextId} or it will return a random ID.
     * @param idPrefix prefix to provide some semantics for the ID
     * @return ID used for assigning to new element.
     */
    String getNextId(String idPrefix);

    /**
     *     This method allows this diagram to reuse an existing id, e.g. in case of importing from BPMN files
     *     with element IDs already stored in the file.<br>
     *     Call {@link #getNextId} after this method will return the id added here.
     * @param id
     */
    void setNextId(String id);

    @Override
    String getLabel();

    BPMNEdge<? extends BPMNNode, ? extends BPMNNode> addEdge(BPMNNode source, BPMNNode target,
                                                             BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);

    BPMNNode addNode(BPMNNode node);

    //Activities
    Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed);

    Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed, SubProcess parentSubProcess);

    Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                         boolean bCollapsed, Swimlane parentSwimlane);

    Activity removeActivity(Activity activity);

    Collection<Activity> getActivities();

    Collection<Activity> getActivities(Swimlane pool);

    //callActivities
    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                                 boolean bCollapsed);

    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                                 boolean bCollapsed, SubProcess parentSubProcess);

    CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
                                 boolean bCollapsed, Swimlane parentSwimlane);

    CallActivity removeCallActivity(CallActivity activity);

    Collection<CallActivity> getCallActivities();

    Collection<CallActivity> getCallActivities(Swimlane pool);

    //SubProcesses
    SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                             boolean bMultiinstance, boolean bCollapsed);

    SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                             boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess);

    SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                             boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane);

    SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                             boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent);

    SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                             boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent, SubProcess parentSubProcess);

    SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                             boolean bMultiinstance, boolean bCollapsed, boolean bTriggeredByEvent, Swimlane parentSwimlane);


    Activity removeSubProcess(SubProcess subprocess);

    Collection<SubProcess> getSubProcesses();

    Collection<SubProcess> getSubProcesses(Swimlane pool);

    //Events
    @Deprecated
    Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                   Activity exceptionFor);

    @Deprecated
    Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                   SubProcess parentSubProcess, Activity exceptionFor);

    @Deprecated
    Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                   Swimlane parentSwimlane, Activity exceptionFor);

    Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                   boolean isInterrupting, Activity exceptionFor);

    Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                   SubProcess parentSubProcess, boolean isInterrupting, Activity exceptionFor);

    Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                   Swimlane parentSwimlane, boolean isInterrupting, Activity exceptionFor);

    Event removeEvent(Event event);

    Collection<Event> getEvents();

    Collection<Event> getEvents(Swimlane pool);

    //Gateways
    Gateway addGateway(String label, GatewayType gatewayType);

    Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess);

    Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane);

    Gateway removeGateway(Gateway gateway);

    Collection<Gateway> getGateways();

    Collection<Gateway> getGateways(Swimlane pool);

    //Data objects
    DataObject addDataObject(String label);

    DataObject removeDataObject(DataObject dataObject);

    Collection<DataObject> getDataObjects();

    //Artifacts
    TextAnnotation addTextAnnotation(String label);

    TextAnnotation removeTextAnnotation(TextAnnotation textAnnotation);

    Collection<TextAnnotation> getTextAnnotations();

    Collection<TextAnnotation> getTextAnnotations(Swimlane pool);

    Association addAssociation(BPMNNode source, BPMNNode target, AssociationDirection direction);

    Collection<Association> getAssociations();

    Collection<Association> getAssociations(Swimlane pool);

    //Flows
    Flow addFlow(BPMNNode source, BPMNNode target, String label);

    @Deprecated
    Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

    @Deprecated
    Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

    Collection<Flow> getFlows();

    Collection<Flow> getFlows(Swimlane pool);

    Collection<Flow> getFlows(SubProcess subProcess);

    //MessageFlows
    MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, String label);

    MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label);

    MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label);

    Set<MessageFlow> getMessageFlows();

    //DataAssociatons
    DataAssociation addDataAssociation(BPMNNode source, BPMNNode target, String label);

    Collection<DataAssociation> getDataAssociations();

    //TextAnnotations
    TextAnnotation addTextAnnotations(TextAnnotation textAnnotation);

    Collection<TextAnnotation> getTextannotations();

    Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent);

    Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent, SwimlaneType type);

    Swimlane removeSwimlane(Swimlane swimlane);

    Collection<Swimlane> getSwimlanes();

    Collection<Swimlane> getPools();

    Collection<Swimlane> getLanes(ContainingDirectedGraphNode parent);

    boolean checkSimpleEquality(BPMNDiagram other);

    boolean checkSimpleEqualityWithMapping(BPMNDiagram other, MutableMap<BPMNNode, BPMNNode> nodeMapping,
                                           MutableMap<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edgeMapping);

    BPMNDiagram getSubProcessDiagram(SubProcess subProcess);
}
