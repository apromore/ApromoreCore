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

package org.apromore.service.loganimation.impl;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import java.util.*;
import org.apromore.service.loganimation.replay.AnimationLog;
import org.apromore.service.loganimation.replay.ReplayTrace;
import org.apromore.service.loganimation.replay.TraceNode;
import org.eclipse.collections.impl.list.mutable.FastList;

import javax.xml.bind.JAXBException;


public class ProcessSelection {

    public static final String DELETE = "_DELETE_+";
    private int id = 0;
    private final Definitions bpmnDefinition;

    private List<FlowElement> lstImplicitSequenceFlows;
    private List<FlowElement> lstImplicitGateWays;
    private Process firstProcess;
    private Definitions bpmnDefinitionReduced;
    private final Definitions originalBpmnDefinitions;

    public ProcessSelection(String bpmn) throws JAXBException, AnimationException {
        this.originalBpmnDefinitions = BPMN2DiagramConverter.parseBPMN(bpmn, getClass().getClassLoader());
        this.bpmnDefinition = BPMN2DiagramConverter.parseBPMN(bpmn, getClass().getClassLoader());
        this.selectProcess();
    }

    public Process getFirstProcess() {
        return firstProcess;
    }

    public Definitions getReducedBpmnDefinitions() {
        return bpmnDefinitionReduced;
    }

    public Definitions getOriginalBpmnDefintions() {
        return originalBpmnDefinitions;
    }

    private void selectProcess() throws AnimationException {
        this.bpmnDefinitionReduced = new Definitions();
        bpmnDefinitionReduced.getDiagram().addAll(this.bpmnDefinition.getDiagram());
        selectPool();
        if (firstProcess == null) {
            throw new AnimationException("The diagram contains no elements. A replay is hence not possible.");
        }
        bpmnDefinitionReduced.getRootElement().add(firstProcess);
        removeUnsupportedElements();
        addImplicitGateways();
    }

    /**
     * The method selectPool() iterates through the base elements of the diagram and selects the first element that
     * 1. is a process (pool), and
     * 2. contains a start and an end event.
     * The selected process is saved in the property firstProcess.
     */
    private void selectPool() {
        firstProcess = null;
        //Select first pool with start and end event
        List<Process> processes = new ArrayList<>();
        for (BaseElement element : this.bpmnDefinition.getRootElement()) {
            if (element instanceof Process) {
                processes.add((Process) element);
            }
        }
        for (Process process : processes) {
            boolean containsStartEvent = false;
            boolean containsEndEvent = false;
            boolean isConnected = true;
            for (FlowElement flowElement : process.getFlowElement()) {
                isConnected = isConnected && checkConnectedness(flowElement);
                if(flowElement instanceof StartEvent)
                    containsStartEvent = true;
                else if (flowElement instanceof EndEvent)
                    containsEndEvent = true;
            }
            if (containsStartEvent && containsEndEvent && isConnected) {
                firstProcess = process;
                break;
            }
        }
    }

    private boolean checkConnectedness(FlowElement flowElement)
    {
        if (flowElement instanceof StartEvent) {
            if(flowElement.getOutgoing().isEmpty()) {
                return false;
            }
        } else if (flowElement instanceof EndEvent) {
            if(flowElement.getIncoming().isEmpty()) {
                return false;
            }
        } else if ((flowElement instanceof Activity || flowElement instanceof Event || flowElement instanceof Gateway) &&
            (flowElement.getOutgoing().isEmpty() || flowElement.getIncoming().isEmpty())) {
            return false;
        }
        return true;
    }

    /**
     * The method removeUnsupportedElements() removes any flow elements from the diagram
     * that are not supported in the replay.
     * For instance data stores, text annotations or data objects flows are removed.
     * Additionally, any association flows are removed from the incoming
     * or outgoing arcs of each retained flow element.
     */
    private void removeUnsupportedElements() {
        Iterator<FlowElement> i = firstProcess.getFlowElement().iterator();
        while (i.hasNext()) {
            FlowElement flowElement = i.next();
            if (flowElement instanceof SequenceFlow) {
                //Do nothing
            } else if (flowElement instanceof Event || flowElement instanceof Activity
                    || flowElement instanceof Gateway) {
                removeUnsupportedEdges(flowElement);
            } else {
                i.remove();
            }
        }
    }

    private void removeUnsupportedEdges(FlowElement flowElement) {
        Iterator<Edge> it = flowElement.getIncoming().iterator();
        while (it.hasNext()) {
            Edge e = it.next();
            if (!(e instanceof SequenceFlow)) {
                it.remove();
            }
        }
        it = flowElement.getOutgoing().iterator();
        while (it.hasNext()) {
            Edge e = it.next();
            if (!(e instanceof SequenceFlow)) {
                it.remove();
            }
        }
    }


    /**
     * The method addImplicitGateways() adds flow elements to a diagram to explicitly model implicit gateways.
     * For any non-gateway flow element with multiple outgoing arcs an and-split is added
     * and a sequence flow is added from flow element to the gateway.
     * The source of all outgoing arcs of the flow node are then set to the added and-split.
     * For any non-gateway flow element with multiple incoming arcs a xor-join is added
     * and a sequence flow is added from the gateway to the flow element.
     * The target of all incoming arcs of the flow node are then set to the added xor-join.
     */
    private void addImplicitGateways() {
        lstImplicitGateWays = new FastList<>();
        lstImplicitSequenceFlows = new FastList<>();
        for (FlowElement flowElement : firstProcess.getFlowElement()) {
            if ((flowElement instanceof Gateway)) {
                continue;
            }
            if (flowElement.getIncoming().size() > 1) {
                //addImplicitXORJoin
                SequenceFlow sequenceflow = new SequenceFlow();
                sequenceflow.setId(DELETE + id++);
                lstImplicitSequenceFlows.add(sequenceflow);
                ExclusiveGateway xor = new ExclusiveGateway();
                xor.setId(DELETE + id++);
                lstImplicitGateWays.add(xor);
                sequenceflow.setSourceRef(xor);
                sequenceflow.setTargetRef(flowElement);
                xor.setGatewayDirection(GatewayDirection.CONVERGING);
                xor.getOutgoing().add(sequenceflow);
                for (Edge incomingEdge : flowElement.getIncoming()) {
                    SequenceFlow incoming = (SequenceFlow) incomingEdge;
                    incoming.setTargetRef(xor);
                    xor.getIncoming().add(incoming);
                }
                flowElement.getIncoming().clear();
                flowElement.getIncoming().add(sequenceflow);
            }
            if (flowElement.getOutgoing().size() > 1) {
                //addImplicitANDSplit
                SequenceFlow sequenceflow = new SequenceFlow();
                sequenceflow.setId(DELETE + id++);
                lstImplicitSequenceFlows.add(sequenceflow);
                ParallelGateway and = new ParallelGateway();
                and.setId(DELETE + id++);
                lstImplicitGateWays.add(and);
                sequenceflow.setSourceRef(flowElement);
                sequenceflow.setTargetRef(and);
                and.setGatewayDirection(GatewayDirection.DIVERGING);
                and.getIncoming().add(sequenceflow);
                for (Edge outgoingEdge : flowElement.getOutgoing()) {
                    SequenceFlow outgoing = (SequenceFlow) outgoingEdge;
                    outgoing.setSourceRef(and);
                    and.getOutgoing().add(outgoing);
                }
                flowElement.getOutgoing().clear();
                flowElement.getOutgoing().add(sequenceflow);
            }
        }
        firstProcess.getFlowElement().addAll(lstImplicitGateWays);
        firstProcess.getFlowElement().addAll(lstImplicitSequenceFlows);
    }

    /**
     * The method removeImplicitFlowElements() removes all flow elements
     * that represent implicit gateways after the replay.
     * This is done to again properly represent the diagram and create proper replay traces.
     */
    private void removeImplicitFlowElements() {
        for (FlowElement gateway : this.lstImplicitGateWays) {
            if (gateway instanceof ParallelGateway) {
                SequenceFlow in = (SequenceFlow) gateway.getIncoming().get(0);
                FlowElement inSource = in.getSourceRef();
                for (Edge outgoingEdge : gateway.getOutgoing()) {
                    SequenceFlow outgoing = (SequenceFlow) outgoingEdge;
                    outgoing.setSourceRef(inSource);
                    inSource.getOutgoing().add(outgoing);
                }
                inSource.getOutgoing().remove(in);
            } else {
                SequenceFlow out = (SequenceFlow) gateway.getOutgoing().get(0);
                FlowElement outTarget = out.getTargetRef();
                for (Edge incomingEdge : gateway.getIncoming()) {
                    SequenceFlow incoming = (SequenceFlow) incomingEdge;
                    incoming.setTargetRef(outTarget);
                    outTarget.getIncoming().add(incoming);
                }
            }
        }
        firstProcess.getFlowElement().removeAll(lstImplicitGateWays);
        firstProcess.getFlowElement().removeAll(lstImplicitSequenceFlows);

    }

    /**
     * The method removeImplicitElementsFrom(AnimationLog animationLog) removes all Trace nodes
     * from the replay traces of a given AnimationLog
     * that represent flow nodes in the diagram that represent implicit gateways.
     * The flow nodes to be removed contain a special tag DELETE in their id.
     * If a xor-join is removed this way, its incoming sequence flow
     * can simply be re-routed to its successor trace node.
     * The outgoing sequence flow of the xor-join can then safely be removed since it was also artificially inserted.
     * For removing an and-split, all outgoing sequence flows need to start from its predecessor trace node.
     * The incoming sequence flow of the and-split can again be safely deleted.
     * Finally, once all implicit elements have been removed from the replay trace,
     * the timing of the replay needs to be re-calculated
     * since the removal of sequence flows influences the timing calculation.
     */
    public AnimationLog removeImplicitElementsFrom(AnimationLog animationLog) {
        this.removeImplicitFlowElements();
        for (ReplayTrace replayTrace : animationLog.getTraces()) {
            removeImplicitElementsFrom(replayTrace);
        }
        return animationLog;
    }

    private void removeImplicitElementsFrom(ReplayTrace replayTrace) {
        Set<TraceNode> toBeRemoved = new HashSet<>();
        for(TraceNode traceNode : replayTrace.getTimeOrderedReplayedNodes()) {
            TraceNode succeedingNode = getNext(traceNode);
            if (!traceNode.getModelNode().getId().contains(DELETE) || succeedingNode==null) {
                continue;
            }
            toBeRemoved.add(traceNode);
            SequenceFlow incoming = (SequenceFlow) traceNode.getIncoming().get(0);
            TraceNode precedingNode = (TraceNode) incoming.getSourceRef();
            if (traceNode.getModelNode() instanceof ExclusiveGateway) {
                SequenceFlow outgoing = (SequenceFlow) traceNode.getOutgoing().get(0);
                traceNode.getOutgoing().remove(outgoing);
                incoming.setTargetRef(succeedingNode);
                succeedingNode.getIncoming().add(incoming);
                succeedingNode.getIncoming().remove(outgoing);
                replayTrace.removeSequenceFlow(outgoing);
            } else if (traceNode.getModelNode() instanceof ParallelGateway) {
                for (Edge out : traceNode.getOutgoing()) {
                    SequenceFlow outgoing = (SequenceFlow) out;
                    precedingNode.getOutgoing().add(outgoing);
                    outgoing.setSourceRef(precedingNode);
                }
                precedingNode.getOutgoing().remove(incoming);
                replayTrace.removeSequenceFlow(incoming);
            }
        }
        toBeRemoved.forEach(replayTrace::removeNode);
        replayTrace.calcTiming();
    }

    private TraceNode getNext(TraceNode node) {
        return !node.getOutgoingSequenceFlows().isEmpty()
                ? (TraceNode) node.getOutgoingSequenceFlows().get(0).getTargetRef()
                : null;
    }
}
