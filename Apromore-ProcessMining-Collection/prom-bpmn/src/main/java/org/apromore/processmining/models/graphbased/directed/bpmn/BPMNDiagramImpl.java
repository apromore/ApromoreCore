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

import java.util.*;

import javax.swing.SwingConstants;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
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
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.Tuples;

// objects of this type should be represented in the framework by the
// BPMNDiagram interface.
//@SubstitutionType(substitutedType = BPMNDiagram.class)
public class BPMNDiagramImpl extends AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>
    implements BPMNDiagram {

    protected final Set<Event> events;
    protected final List<Activity> activities;
    protected final Set<SubProcess> subprocesses;
    protected final List<Gateway> gateways;
    protected final Set<DataObject> dataObjects;
    protected final Set<TextAnnotation> textAnnotations;
    protected final List<Flow> flows;
    protected final Set<MessageFlow> messageFlows;
    protected final Set<DataAssociation> dataAssociations;
    protected final Set<Association> associations;
    protected final List<Swimlane> swimlanes;
    protected final Set<CallActivity> callActivities;

    private LocalIDGenerator idGenerator = new LocalIDGenerator();
    private String nextId = "";

    public BPMNDiagramImpl(String label) {
        super();
        events = new LinkedHashSet<Event>();
        activities = new ArrayList<Activity>();
        subprocesses = new LinkedHashSet<SubProcess>();
        gateways = new ArrayList<Gateway>();
        dataObjects = new LinkedHashSet<DataObject>();
        textAnnotations = new LinkedHashSet<TextAnnotation>();
        flows = new ArrayList<Flow>();
        messageFlows = new LinkedHashSet<MessageFlow>();
        dataAssociations = new LinkedHashSet<DataAssociation>();
        associations = new LinkedHashSet<Association>();
        swimlanes = new ArrayList<Swimlane>();
        callActivities = new LinkedHashSet<CallActivity>();
        getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
        getAttributeMap().put(AttributeMap.LABEL, label);
    }

    @Override
    public String getNextId(String idPrefix) {
        String id = nextId.isEmpty() ? idGenerator.nextId(idPrefix) : nextId;
        nextId = "";
        return id;
    }

    @Override
    public void setNextId(String id) {
        nextId = id;
    }

    @Override
    protected BPMNDiagramImpl getEmptyClone() {
        return new BPMNDiagramImpl(getLabel());
    }

    @Override
    protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
        DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph) {
        BPMNDiagram bpmndiagram = (BPMNDiagram) graph;
        HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

        boolean newSwimlanes = true;
        while (newSwimlanes) {
            newSwimlanes = false;
            for (Swimlane s : bpmndiagram.getSwimlanes()) {
                // If swimlane has not been added yet
                if (!mapping.containsKey(s)) {
                    newSwimlanes = true;
                    Swimlane parentSwimlane = s.getParentSwimlane();
                    // If there is no parent or parent has been added, add swimlane
                    if (parentSwimlane == null) {
                        mapping.put(s, addSwimlane(s.getLabel(), parentSwimlane, s.getSwimlaneType()));
                    } else if (mapping.containsKey(parentSwimlane)) {
                        mapping.put(s,
                            addSwimlane(s.getLabel(), (Swimlane) mapping.get(parentSwimlane),
                                s.getSwimlaneType()));
                    }
                }
            }
        }
        boolean newSubprocesses = true;
        while (newSubprocesses) {
            newSubprocesses = false;
            for (SubProcess s : bpmndiagram.getSubProcesses()) {
                // If subprocess has not been added yet
                if (!mapping.containsKey(s)) {
                    newSubprocesses = true;
                    if (s.getParentSubProcess() != null) {
                        if (mapping.containsKey(s.getParentSubProcess())) {
                            mapping.put(
                                s,
                                addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
                                    s.isBMultiinstance(), s.isBCollapsed(),
                                    (SubProcess) mapping.get(s.getParentSubProcess())));
                        }
                    } else if (s.getParentSwimlane() != null) {
                        if (mapping.containsKey(s.getParentSwimlane())) {
                            mapping.put(
                                s,
                                addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
                                    s.isBMultiinstance(), s.isBCollapsed(),
                                    (Swimlane) mapping.get(s.getParentSwimlane())));
                        }
                    } else

                        mapping.put(
                            s,
                            addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
                                s.isBMultiinstance(), s.isBCollapsed()));
                }
            }
        }
        for (Activity a : bpmndiagram.getActivities()) {
            if (a.getParentSubProcess() != null) {
                if (mapping.containsKey(a.getParentSubProcess())) {
                    mapping.put(
                        a,
                        addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
                            a.isBMultiinstance(), a.isBCollapsed(),
                            (SubProcess) mapping.get(a.getParentSubProcess())));
                }
            } else if (a.getParentSwimlane() != null) {
                if (mapping.containsKey(a.getParentSwimlane())) {
                    mapping.put(
                        a,
                        addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
                            a.isBMultiinstance(), a.isBCollapsed(),
                            (Swimlane) mapping.get(a.getParentSwimlane())));
                }
            } else
                mapping.put(
                    a,
                    addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
                        a.isBMultiinstance(), a.isBCollapsed()));
        }

        for (CallActivity a : bpmndiagram.getCallActivities()) {
            if (a.getParentSubProcess() != null) {
                if (mapping.containsKey(a.getParentSubProcess())) {
                    mapping.put(
                        a,
                        addCallActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
                            a.isBMultiinstance(), a.isBCollapsed(),
                            (SubProcess) mapping.get(a.getParentSubProcess())));
                }
            } else if (a.getParentSwimlane() != null) {
                if (mapping.containsKey(a.getParentSwimlane())) {
                    mapping.put(
                        a,
                        addCallActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
                            a.isBMultiinstance(), a.isBCollapsed(),
                            (Swimlane) mapping.get(a.getParentSwimlane())));
                }
            } else
                mapping.put(
                    a,
                    addCallActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
                        a.isBMultiinstance(), a.isBCollapsed()));
        }

        for (Event e : bpmndiagram.getEvents()) {
            if (e.getParentSubProcess() != null) {
                if (mapping.containsKey(e.getParentSubProcess())) {
                    mapping.put(
                        e,
                        addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
                            (SubProcess) mapping.get(e.getParentSubProcess()), e.getBoundingNode()));
                }
            } else if (e.getParentSwimlane() != null) {
                if (mapping.containsKey(e.getParentSwimlane())) {
                    mapping.put(
                        e,
                        addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
                            (Swimlane) mapping.get(e.getParentSwimlane()), e.getBoundingNode()));
                }
            } else
                mapping.put(
                    e,
                    addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
                        e.getBoundingNode()));
        }
        for (Gateway g : bpmndiagram.getGateways()) {
            if (g.getParentSubProcess() != null) {
                if (mapping.containsKey(g.getParentSubProcess())) {
                    mapping.put(
                        g,
                        addGateway(g.getLabel(), g.getGatewayType(),
                            (SubProcess) mapping.get(g.getParentSubProcess())));
                }
            } else if (g.getParentSwimlane() != null) {
                if (mapping.containsKey(g.getParentSwimlane())) {
                    mapping.put(g,
                        addGateway(g.getLabel(), g.getGatewayType(), (Swimlane) mapping.get(g.getParentSwimlane())));
                }
            } else
                mapping.put(g, addGateway(g.getLabel(), g.getGatewayType()));
        }

        for (DataObject d : bpmndiagram.getDataObjects()) {
            mapping.put(d, addDataObject(d.getLabel()));
        }

        for (Flow f : bpmndiagram.getFlows()) {
            mapping.put(f, addFlow((BPMNNode) mapping.get(f.getSource()),
                (BPMNNode) mapping.get(f.getTarget()), f.getLabel()));
        }
        for (MessageFlow f : bpmndiagram.getMessageFlows()) {
            mapping.put(f, addMessageFlow((BPMNNode) mapping.get(f.getSource()),
                (BPMNNode) mapping.get(f.getTarget()), f.getLabel()));
        }
        for (DataAssociation a : bpmndiagram.getDataAssociations()) {
            mapping.put(a, addDataAssociation((BPMNNode) mapping.get(a.getSource()),
                (BPMNNode) mapping.get(a.getTarget()), a.getLabel()));
        }

        getAttributeMap().clear();
        AttributeMap map = bpmndiagram.getAttributeMap();
        for (String key : map.keySet()) {
            getAttributeMap().put(key, map.get(key));
        }
        return mapping;
    }

    @Override
    public BPMNDiagram getSubProcessDiagram(SubProcess subProcess) {
        BPMNDiagram subProcessDiagram = new BPMNDiagramImpl("");

        BPMNDiagram topDiagram = (BPMNDiagram) subProcess.getGraph();
        Map<BPMNNode, BPMNNode> nodeMap = new HashMap<>();

        //Nodes
        for (ContainableDirectedGraphElement subProcessChild : subProcess.getChildren()) {
            if (subProcessChild instanceof BPMNNode) {
                BPMNNode node = ((BPMNNode) subProcessChild);
                nodeMap.put(node, subProcessDiagram.addNode(node.copy()));
            }
        }

        //Edges
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : topDiagram.getEdges()) {
            BPMNNode source = edge.getSource();
            BPMNNode target = edge.getTarget();

            if (nodeMap.containsKey(source) || nodeMap.containsKey(target)) {
                BPMNNode newSource = nodeMap.get(source);
                BPMNNode newTarget = nodeMap.get(target);

                if (newSource == null) {
                    newSource = subProcessDiagram.addNode(source.copy());
                    nodeMap.put(source, newSource);
                }

                if (newTarget == null) {
                    newTarget = subProcessDiagram.addNode(target.copy());
                    nodeMap.put(target, newTarget);
                }

                subProcessDiagram.addEdge(newSource, newTarget, edge);
            }
        }
        return subProcessDiagram;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void removeEdge(DirectedGraphEdge edge) {
        if (edge instanceof Flow) {
            flows.remove(edge);
        } else if (edge instanceof MessageFlow) {
            messageFlows.remove(edge);
        } else if (edge instanceof DataAssociation) {
            dataAssociations.remove(edge);
        } else {
            assert (false);
        }
        graphElementRemoved(edge);
    }

    @Override
    public Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> getEdges() {
        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
        edges.addAll(flows);
        edges.addAll(messageFlows);
        edges.addAll(dataAssociations);
        edges.addAll(associations);
        return edges;
    }

    @Override
    public Set<BPMNNode> getNodes() {
        Set<BPMNNode> nodes = new HashSet<BPMNNode>();
        nodes.addAll(activities);
        nodes.addAll(subprocesses);
        nodes.addAll(events);
        nodes.addAll(gateways);
        nodes.addAll(dataObjects);
        nodes.addAll(swimlanes);
        nodes.addAll(textAnnotations);
        nodes.addAll(callActivities);
        return nodes;
    }

    @Override
    public void removeNode(DirectedGraphNode node) {
        if (node instanceof Activity) {
            removeActivity((Activity) node);
        } else if (node instanceof SubProcess) {
            removeSubProcess((SubProcess) node);
        } else if (node instanceof Swimlane) {
            removeSwimlane((Swimlane) node);
        } else if (node instanceof Event) {
            removeEvent((Event) node);
        } else if (node instanceof Gateway) {
            removeGateway((Gateway) node);
        } else if (node instanceof DataObject) {
            removeDataObject((DataObject) node);
        } else {
            assert (false);
        }
    }

    @Override
    public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                                boolean bMultiinstance, boolean bCollapsed) {
        Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
        activities.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                                boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane) {
        Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
            parentSwimlane);
        activities.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                                boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess) {
        Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
            parentSubProcess);
        activities.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                                        boolean bMultiinstance, boolean bCollapsed) {
        CallActivity a = new CallActivity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
        callActivities.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                                        boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane) {
        CallActivity a = new CallActivity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
            parentSwimlane);
        callActivities.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public CallActivity addCallActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
                                        boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess) {
        CallActivity a = new CallActivity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
            parentSubProcess);
        callActivities.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
                                    boolean multiinstance, boolean collapsed) {
        SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed);
        subprocesses.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
                                    boolean multiinstance, boolean collapsed, SubProcess parentSubProcess) {
        SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
            parentSubProcess);
        subprocesses.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
                                    boolean multiinstance, boolean collapsed, Swimlane parentSwimlane) {
        SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
            parentSwimlane);
        subprocesses.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
                                    boolean multiinstance, boolean collapsed, boolean triggeredByEvent) {
        SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
            triggeredByEvent);
        subprocesses.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
                                    boolean multiinstance, boolean collapsed, boolean triggeredByEvent, SubProcess parentSubProcess) {
        SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
            triggeredByEvent, parentSubProcess);
        subprocesses.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
                                    boolean multiinstance, boolean collapsed, boolean triggeredByEvent, Swimlane parentSwimlane) {
        SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
            triggeredByEvent, parentSwimlane);
        subprocesses.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    @Deprecated
    public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                          Activity exceptionFor) {
        Event e = new Event(this, label, eventType, eventTrigger, eventUse, exceptionFor);
        if(exceptionFor != null) {
            SubProcess parentSubProcess = exceptionFor.getParentSubProcess();
            if(parentSubProcess != null) {
                e.setParentSubprocess(parentSubProcess);
            } else {
                Swimlane parentSwimlane = exceptionFor.getParentSwimlane();
                e.setParentSwimlane(parentSwimlane);
            }
        }
        events.add(e);
        graphElementAdded(e);
        return e;
    }

    @Override
    @Deprecated
    public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                          SubProcess parentSubProcess, Activity exceptionFor) {
        Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSubProcess, exceptionFor);
        events.add(e);
        graphElementAdded(e);
        return e;
    }

    @Override
    @Deprecated
    public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                          Swimlane parentSwimlane, Activity exceptionFor) {
        Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSwimlane, exceptionFor);
        events.add(e);
        graphElementAdded(e);
        return e;
    }

    @Override
    public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                          boolean isInterrupting, Activity exceptionFor) {
        Event e = new Event(this, label, eventType, eventTrigger, eventUse, isInterrupting, exceptionFor);
        if(exceptionFor != null) {
            SubProcess parentSubProcess = exceptionFor.getParentSubProcess();
            if(parentSubProcess != null) {
                e.setParentSubprocess(parentSubProcess);
            } else {
                Swimlane parentSwimlane = exceptionFor.getParentSwimlane();
                e.setParentSwimlane(parentSwimlane);
            }
        }
        events.add(e);
        graphElementAdded(e);
        return e;
    }

    @Override
    public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                          SubProcess parentSubProcess, boolean isInterrupting, Activity exceptionFor) {
        Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSubProcess, isInterrupting,
            exceptionFor);
        events.add(e);
        graphElementAdded(e);
        return e;
    }

    @Override
    public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
                          Swimlane parentSwimlane, boolean isInterrupting, Activity exceptionFor) {
        Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSwimlane, isInterrupting,
            exceptionFor);
        events.add(e);
        graphElementAdded(e);
        return e;
    }

    @Override
    public DataObject addDataObject(String label) {
        DataObject d = new DataObject(this, label);
        dataObjects.add(d);
        graphElementAdded(d);
        return d;
    }

    @Override
    public TextAnnotation addTextAnnotation(String label) {
        TextAnnotation t = new TextAnnotation(this, label);
        textAnnotations.add(t);
        graphElementAdded(t);
        return t;
    }

    @Override
    @Deprecated
    public Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label) {
        Flow f = new Flow(source, target, parent, label);
        flows.add(f);
        graphElementAdded(f);
        return f;
    }

    @Override
    @Deprecated
    public Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label) {
        Flow f = new Flow(source, target, parent, label);
        flows.add(f);
        graphElementAdded(f);
        return f;
    }

    @Override
    public Flow addFlow(BPMNNode source, BPMNNode target, String label) {
        Flow f = new Flow(source, target, label);
        flows.add(f);
        graphElementAdded(f);
        return f;
    }

    @Override
    public MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label) {
        MessageFlow f = new MessageFlow(source, target, parent, label);
        messageFlows.add(f);
        graphElementAdded(f);
        return f;
    }

    @Override
    public MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label) {
        MessageFlow f = new MessageFlow(source, target, parent, label);
        messageFlows.add(f);
        graphElementAdded(f);
        return f;
    }

    @Override
    public MessageFlow addMessageFlow(BPMNNode source, BPMNNode target, String label) {
        MessageFlow f = new MessageFlow(source, target, label);
        messageFlows.add(f);
        graphElementAdded(f);
        return f;
    }

    @Override
    public DataAssociation addDataAssociation(BPMNNode source, BPMNNode target, String label) {
        DataAssociation d = new DataAssociation(source, target, label);
        dataAssociations.add(d);
        graphElementAdded(d);
        return d;
    }

    @Override
    public Association addAssociation(BPMNNode source, BPMNNode target, AssociationDirection direction) {
        Association a = new Association(source, target, direction);
        associations.add(a);
        graphElementAdded(a);
        return a;
    }

    @Override
    public Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess) {
        Gateway g = new Gateway(this, label, gatewayType, parentSubProcess);
        gateways.add(g);
        graphElementAdded(g);
        return g;
    }

    @Override
    public Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane) {
        Gateway g = new Gateway(this, label, gatewayType, parentSwimlane);
        gateways.add(g);
        graphElementAdded(g);
        return g;
    }

    @Override
    public Gateway addGateway(String label, GatewayType gatewayType) {
        Gateway g = new Gateway(this, label, gatewayType);
        gateways.add(g);
        graphElementAdded(g);
        return g;
    }

    @Override
    public Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent) {
        Swimlane s;
        if(parent instanceof Swimlane) {
            s = new Swimlane(this, label, (Swimlane)parent);
        } else if (parent instanceof SubProcess) {
            s = new Swimlane(this, label, (SubProcess)parent);
        } else {
            s = new Swimlane(this, label);
        }
        swimlanes.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public Swimlane addSwimlane(String label, ContainingDirectedGraphNode parent, SwimlaneType type) {
        Swimlane s;
        if(parent == null) {
            s = new Swimlane(this, label, type);
        } else if(parent instanceof Swimlane) {
            s = new Swimlane(this, label, (Swimlane)parent, type);
        } else if (parent instanceof SubProcess) {
            s = new Swimlane(this, label, (SubProcess)parent, type);
        } else {
            s = new Swimlane(this, label, type);
        }
        swimlanes.add(s);
        graphElementAdded(s);
        return s;
    }

    @Override
    public BPMNEdge<? extends BPMNNode, ? extends BPMNNode> addEdge(
        BPMNNode source, BPMNNode target, BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
        BPMNEdge<BPMNNode, BPMNNode> addedEdge = null;

        if (edge instanceof Flow) {
            addedEdge = addFlow(source, target, edge.getLabel());
        } else if (edge instanceof Association) {
            addedEdge = addAssociation(source, target, ((Association) edge).getDirection());
        } else if (edge instanceof MessageFlow) {
            addedEdge = addMessageFlow(source, target, edge.getLabel());
        } else if (edge instanceof DataAssociation) {
            addedEdge = addDataAssociation(source, target, edge.getLabel());
        }

        return addedEdge;
    }

    @Override
    public BPMNNode addNode(BPMNNode node) {
        node.setGraph(this);

        if (node instanceof SubProcess) {
            subprocesses.add((SubProcess) node);
        } else if (node instanceof Swimlane) {
            swimlanes.add((Swimlane) node);
        } else if (node instanceof Gateway) {
            gateways.add((Gateway)node);
        } else if (node instanceof DataObject) {
            dataObjects.add((DataObject) node);
        } else if (node instanceof TextAnnotation) {
            textAnnotations.add((TextAnnotation) node);
        } else if (node instanceof Event) {
            events.add((Event) node);
        } else if (node instanceof CallActivity) {
            callActivities.add((CallActivity) node);
        } else if (node instanceof Activity) {
            activities.add((Activity) node);
        }
        else {
            throw new IllegalArgumentException("Unsupported node type for " + node.getLabel()
                + ", id=" + node.getId());
        }

        graphElementAdded(node);
        return node;
    }

    @Override
    public Collection<Activity> getActivities() {
        return activities;
    }

    @Override
    public Collection<CallActivity> getCallActivities() {
        return callActivities;
    }

    @Override
    public Collection<Activity> getActivities(Swimlane pool) {
        List<Activity> activitiesFromPool = new ArrayList<Activity>();
        for (Activity activity : activities) {
            if (activity.getParentSubProcess() == null) {
                if ((pool != null) && (pool.equals(activity.getParentPool()))) {
                    activitiesFromPool.add(activity);
                } else if (pool == null) {
                    if (activity.getParentPool() == null) {
                        activitiesFromPool.add(activity);
                    }
                }
            }
        }
        return activitiesFromPool;
    }

    @Override
    public Collection<CallActivity> getCallActivities(Swimlane pool) {
        Set<CallActivity> activitiesFromPool = new HashSet<CallActivity>();
        for (CallActivity activity : callActivities) {
            if (activity.getParentSubProcess() == null) {
                if ((pool != null) && (pool.equals(activity.getParentPool()))) {
                    activitiesFromPool.add(activity);
                } else if (pool == null) {
                    if (activity.getParentPool() == null) {
                        activitiesFromPool.add(activity);
                    }
                }
            }
        }
        return activitiesFromPool;
    }

    @Override
    public Collection<SubProcess> getSubProcesses() {
        return subprocesses;
    }

    @Override
    public Collection<SubProcess> getSubProcesses(Swimlane pool) {
        Set<SubProcess> subProcessesFromPool = new HashSet<SubProcess>();
        for (SubProcess subProcess : subprocesses) {
            if ((pool != null) && (pool.equals(subProcess.getParentPool()))) {
                subProcessesFromPool.add(subProcess);
            } else if (pool == null) {
                if (subProcess.getParentPool() == null) {
                    subProcessesFromPool.add(subProcess);
                }
            }
        }
        return subProcessesFromPool;
    }

    @Override
    public Collection<Event> getEvents() {
        return events;
    }

    @Override
    public Collection<Event> getEvents(Swimlane pool) {
        Set<Event> eventsFromPool = new HashSet<Event>();
        for (Event event : events) {
            if (event.getParentSubProcess() == null) {
                if ((pool != null) && (pool.equals(event.getParentPool()))) {
                    eventsFromPool.add(event);
                } else if (pool == null) {
                    if (event.getParentPool() == null) {
                        eventsFromPool.add(event);
                    }
                }
            }
        }
        return eventsFromPool;
    }

    @Override
    public Collection<DataObject> getDataObjects() {
        return dataObjects;
    }

    @Override
    public Collection<TextAnnotation> getTextAnnotations() {
        return textAnnotations;
    }

    @Override
    public Collection<TextAnnotation> getTextAnnotations(Swimlane pool) {
        Set<TextAnnotation> textAnnotationsFromPool = new HashSet<TextAnnotation>();
        for (TextAnnotation textAnnotation : textAnnotations) {
            if (textAnnotation.getParentSubProcess() == null) {
                if ((pool != null) && (pool.equals(textAnnotation.getParentPool()))) {
                    textAnnotationsFromPool.add(textAnnotation);
                } else if (pool == null) {
                    if (textAnnotation.getParentPool() == null) {
                        textAnnotationsFromPool.add(textAnnotation);
                    }
                }
            }
        }
        return textAnnotationsFromPool;
    }

    @Override
    public Collection<Flow> getFlows() {
        return Collections.unmodifiableCollection(flows);
    }

    @Override
    public Collection<Flow> getFlows(Swimlane pool) {
        List<Flow> flowsFromPool = new ArrayList<Flow>();
        for (Flow flow : flows) {
            BPMNNode source = flow.getSource();
            BPMNNode target = flow.getTarget();
            if (source.getAncestorSubProcess() == null) {
                if ((source.getParentPool() == pool) && (target.getParentPool() == pool)) {
                    flowsFromPool.add(flow);
                }
            }
        }
        return Collections.unmodifiableCollection(flowsFromPool);
    }

    @Override
    public Collection<Flow> getFlows(SubProcess subProcess) {
        List<Flow> flowsFromSubProcess = new ArrayList<Flow>();
        for (Flow flow : flows) {
            BPMNNode source = flow.getSource();
            BPMNNode target = flow.getTarget();
            if ((source.getAncestorSubProcess() == subProcess)
                && (target.getAncestorSubProcess() == subProcess)) {
                flowsFromSubProcess.add(flow);
            }
        }
        return Collections.unmodifiableCollection(flowsFromSubProcess);
    }


    @Override
    public Set<MessageFlow> getMessageFlows() {
        return Collections.unmodifiableSet(messageFlows);
    }

    @Override
    public Collection<Gateway> getGateways() {
        return gateways;
    }

    @Override
    public Collection<Gateway> getGateways(Swimlane pool) {
        List<Gateway> gatewaysFromPool = new ArrayList<Gateway>();
        for (Gateway gateway : gateways) {
            if (gateway.getParentSubProcess() == null) {
                if ((pool != null) && (pool.equals(gateway.getParentPool()))) {
                    gatewaysFromPool.add(gateway);
                } else if (pool == null) {
                    if (gateway.getParentPool() == null) {
                        gatewaysFromPool.add(gateway);
                    }
                }
            }
        }
        return gatewaysFromPool;
    }

    @Override
    public Activity removeActivity(Activity activity) {
        removeSurroundingEdges(activity);
        return removeNodeFromCollection(activities, activity);
    }

    @Override
    public CallActivity removeCallActivity(CallActivity activity) {
        removeSurroundingEdges(activity);
        return removeNodeFromCollection(callActivities, activity);
    }

    @Override
    public Activity removeSubProcess(SubProcess subprocess) {
        //TODO: it is probably necessary to remove all nodes that are contained in the subprocess as well 
        removeSurroundingEdges(subprocess);
        return removeNodeFromCollection(subprocesses, subprocess);
    }

    @Override
    public Event removeEvent(Event event) {
        removeSurroundingEdges(event);
        return removeNodeFromCollection(events, event);
    }

    @Override
    public Gateway removeGateway(Gateway gateway) {
        removeSurroundingEdges(gateway);
        return removeNodeFromCollection(gateways, gateway);
    }

    @Override
    public DataObject removeDataObject(DataObject dataObject) {
        removeSurroundingEdges(dataObject);
        return removeNodeFromCollection(dataObjects, dataObject);
    }

    @Override
    public Swimlane removeSwimlane(Swimlane swimlane) {
        removeSurroundingEdges(swimlane);
        return removeNodeFromCollection(swimlanes, swimlane);
    }

    @Override
    public TextAnnotation removeTextAnnotation(TextAnnotation textAnnotation) {
        removeSurroundingEdges(textAnnotation);
        return removeNodeFromCollection(textAnnotations, textAnnotation);
    }

    @Override
    public Collection<Swimlane> getSwimlanes() {
        return swimlanes;
    }

    @Override
    public Collection<Swimlane> getPools() {
        Collection<Swimlane> result = new HashSet<Swimlane>();
        for (Swimlane swimlane : swimlanes) {
            if (swimlane.getSwimlaneType() == SwimlaneType.POOL) {
                result.add(swimlane);
            }
        }
        return result;
    }

    @Override
    public Collection<Swimlane> getLanes(ContainingDirectedGraphNode parent) {
        List<Swimlane> lanes = new ArrayList<Swimlane>();
        for (Swimlane lane : swimlanes) {
            if (SwimlaneType.LANE.equals(lane.getSwimlaneType())) {
                if ((parent != null) && (parent.equals(lane.getParent()))) {
                    lanes.add(lane);
                } else if (parent == null) {
                    if (lane.getParent() == null) {
                        lanes.add(lane);
                    }
                }
            }
        }
        return lanes;
    }

    @Override
    public Collection<DataAssociation> getDataAssociations() {
        return dataAssociations;
    }

    @Override
    public Collection<Association> getAssociations() {
        return associations;
    }

    @Override
    public Collection<Association> getAssociations(Swimlane pool) {
        Set<Association> associationsFromPool = new HashSet<Association>();
        for (Association association : associations) {
            if ((association.getTarget().getParentSubProcess() == null)
                && (association.getSource().getParentSubProcess() == null)) {
                if ((pool != null) && (pool.equals(association.getTarget().getParentSubProcess())
                    &&(pool.equals((association.getSource().getParentSubProcess()))))) {
                    associationsFromPool.add(association);
                } else if (pool == null) {
                    if  ((association.getTarget().getParentPool() == null)
                        || (association.getSource().getParentPool() == null)) {
                        associationsFromPool.add(association);
                    }
                }
            }
        }
        return associationsFromPool;
    }

    //TextAnnotations
    @Override
    public TextAnnotation addTextAnnotations(TextAnnotation textAnnotation) {
        textAnnotations.add(textAnnotation);
        graphElementAdded(textAnnotation);
        return textAnnotation;
    }

    @Override
    public Collection<TextAnnotation> getTextannotations() {
        return textAnnotations;
    }

    @Override
    public boolean checkSimpleEquality(BPMNDiagram d2) {
        return this.checkSimpleEqualityWithMapping(d2, null, null);
    }

    // Assume the BPMN diagrams are simple with the following elements:
    // - has only start/end events, activities, gateways and sequence flows.
    // - one start event, one end event
    // - the activities are simple, no multi-instance sign, no self-loop sign.
    // Return: true if they are equivalent and can be mapped, false otherwise.
    @Override
    public boolean checkSimpleEqualityWithMapping(BPMNDiagram d2, MutableMap<BPMNNode,BPMNNode> nodeMapping,
                                                  MutableMap<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edgeMapping) {
        BPMNDiagram d1 = this;
        Set<Event> starts1 = new HashSet<>();
        Set<Event> starts2 = new HashSet<>();
        Set<Event> ends1 = new HashSet<>();
        Set<Event> ends2 = new HashSet<>();
        Map<String,BPMNNode> acts1 = new HashMap<>();
        Map<String,BPMNNode> acts2 = new HashMap<>();
        Set<Pair<BPMNNode, BPMNNode>> edges1 = new HashSet<>();
        Set<Pair<BPMNNode, BPMNNode>> edges2 = new HashSet<>();
        boolean hasGateway = false;
        boolean hasOthers = false;

        if (nodeMapping == null) {
            nodeMapping = Maps.mutable.empty();
        }
        if (edgeMapping == null) {
            edgeMapping = Maps.mutable.empty();
        }

        // Quick check first
        if (d1.getNodes().size() != d2.getNodes().size() || d1.getEdges().size() != d2.getEdges().size()) {
            return false;
        }

        // Collect activity, start event, and end event nodes.
        for (BPMNNode node : d1.getNodes()) {
            if (node instanceof Activity) {
                acts1.put(node.getLabel(), node);
            }
            else if (node instanceof Event && ((Event)node).getEventType() == EventType.START) {
                starts1.add((Event)node);
            }
            else if (node instanceof Event && ((Event)node).getEventType() == EventType.END) {
                ends1.add((Event)node);
            }
            else if (node instanceof Gateway) {
                hasGateway = true;
            }
            else {
                hasOthers = true;
            }
        }
        for (BPMNNode node : d2.getNodes()) {
            if (node instanceof Activity) {
                acts2.put(node.getLabel(), node);
            }
            else if (node instanceof Event && ((Event)node).getEventType() == EventType.START) {
                starts2.add((Event)node);
            }
            else if (node instanceof Event && ((Event)node).getEventType() == EventType.END) {
                ends2.add((Event)node);
            }
            else if (node instanceof Gateway) {
                hasGateway = true;
            }
            else {
                hasOthers = true;
            }
        }

        // Quick check on the simple elements
        if (hasOthers) {
            return false;
        }
        else if (starts1.size() != starts2.size() || starts1.size() != 1 || starts2.size() != 1) {
            return false;
        }
        else if (ends1.size() != ends2.size() || ends1.size() != 1 || ends2.size() != 1) {
            return false;
        }
        else if (acts1.size() != acts2.size()) {
            return false;
        }
        else if (edges1.size() != edges2.size()) {
            return false;
        }

        // Collect edges: they are directed edges between simple elements
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : d1.getEdges()) {
            edges1.add(Tuples.pair((BPMNNode)e.getSource(), (BPMNNode)e.getTarget()));
        }
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : d2.getEdges()) {
            edges2.add(Tuples.pair((BPMNNode)e.getSource(), (BPMNNode)e.getTarget()));
        }

        // Map start and end events;
        nodeMapping.put(starts1.iterator().next(), starts2.iterator().next());
        nodeMapping.put(ends1.iterator().next(), ends2.iterator().next());

        // Map activity nodes: must check labels
        if (acts1.size() != acts2.size()) {
            return false;
        }
        else {
            for (String name1: acts1.keySet()) {
                if (!acts2.containsKey(name1)) {
                    return false;
                }
                else {
                    nodeMapping.put(acts1.get(name1), acts2.get(name1));
                }
            }
        }

        // Map gateways: consider nested gateways
        if (hasGateway) {
            Set<BPMNNode> tobeChecked = new HashSet<>(nodeMapping.keySet());
            while (nodeMapping.keySet().size() < d1.getNodes().size()) {
                Set<BPMNNode> newMappingNodes = new HashSet<>();
                for (BPMNNode node: tobeChecked) {
                    BPMNNode mapNode = nodeMapping.get(node);

                    BPMNNode unmapNode = getSingleUnmappedInputNode(d1, node, nodeMapping.keySet());
                    if (unmapNode != null) {
                        BPMNNode otherUnmapNode = getSingleUnmappedInputNode(d2, mapNode, nodeMapping.values());
                        if (otherUnmapNode != null && isSameType(unmapNode, otherUnmapNode)) {
                            newMappingNodes.add(unmapNode);
                            nodeMapping.put(unmapNode, otherUnmapNode);
                        }
                        else {
                            return false;
                        }
                    }

                    unmapNode = getSingleUnmappedOutputNode(d1, node, nodeMapping.keySet());
                    if (unmapNode != null) {
                        BPMNNode otherUnmapNode = getSingleUnmappedOutputNode(d2, mapNode, nodeMapping.values());
                        if (otherUnmapNode != null && isSameType(unmapNode, otherUnmapNode)) {
                            newMappingNodes.add(unmapNode);
                            nodeMapping.put(unmapNode, otherUnmapNode);
                        }
                        else {
                            return false;
                        }
                    }
                }

                tobeChecked = newMappingNodes;

                // Has checked all nodes but there are still some nodes cannot be mapped between two diagrams
                if (tobeChecked.isEmpty() && nodeMapping.keySet().size() < d1.getNodes().size()) {
                    return false;
                }
            }
        }

        // Check edges once all simple nodes have been mapped without any differences.
        for (Pair<BPMNNode, BPMNNode> pair : edges1) {
            BPMNNode mapSource = nodeMapping.get(pair.getOne());
            BPMNNode mapTarget = nodeMapping.get(pair.getTwo());
            if (!edges2.contains(Tuples.pair(mapSource, mapTarget))) {
                return false;
            }
            else {
                edgeMapping.put(d1.getEdges(pair.getOne(), pair.getTwo()).iterator().next(),
                    d2.getEdges(mapSource, mapTarget).iterator().next());
            }
        }

        return true;
    }

    // Return the single input node of the <node> in the diagram <d> that is not in <mappedNodes>
    // If not found, return null
    private BPMNNode getSingleUnmappedInputNode(BPMNDiagram d, BPMNNode node, Collection<BPMNNode> mappedNodes) {
        Set<BPMNNode> unmap = new HashSet<>();
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : d.getInEdges(node)) {
            if (!mappedNodes.contains(e.getSource())) {
                unmap.add(e.getSource());
            }
        }
        if (unmap.size() == 1) {
            return unmap.iterator().next();
        }
        else {
            return null;
        }
    }

    // Return the single output node of the <node> in the diagram <d> that is not in <mappedNodes>
    // If not found, return null
    private BPMNNode getSingleUnmappedOutputNode(BPMNDiagram d, BPMNNode node, Collection<BPMNNode> mappedNodes) {
        Set<BPMNNode> unmap = new HashSet<>();
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : d.getOutEdges(node)) {
            if (!mappedNodes.contains(e.getTarget())) {
                unmap.add(e.getTarget());
            }
        }
        if (unmap.size() == 1) {
            return unmap.iterator().next();
        }
        else {
            return null;
        }
    }

    private boolean isSameType(BPMNNode node1, BPMNNode node2) {
        if (node1 instanceof Activity && node2 instanceof Activity) {
            return true;
        }
        else if (node1 instanceof Event && node2 instanceof Event) {
            return true;
        }
        else if (node1 instanceof Gateway && node2 instanceof Gateway) {
            return ((Gateway)node1).getGatewayType() == ((Gateway)node2).getGatewayType();
        }
        else {
            return false;
        }
    }
}
