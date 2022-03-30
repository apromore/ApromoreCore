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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.springframework.util.CollectionUtils;

public class ModelChecker {

    private boolean allowPools;
    private Map<Integer, List<String>> errorMap = new HashMap<>();

    public static final ModelChecker MODEL_CHECKER_RESTRICTED = new ModelChecker(false);
    public static final ModelChecker MODEL_CHECKER_RELAXED = new ModelChecker(true);

    private ModelChecker(boolean allowPools) {
        this.allowPools = allowPools;
    }

    /**
     * Finds errors in a model.
     *
     * @param bpmnDiagram the model to find errors in.
     * @return a object containing the errors in the model.
     */
    public ModelCheckResult checkModel(BPMNDiagram bpmnDiagram) {
        errorMap.clear();

        if (isModelEmpty(bpmnDiagram)) {
            //empty model - no nodes or edges
            addError(ModelCheckResult.EMPTY_MODEL_CODE, "DIAGRAM");
        }

        if (!allowPools && !CollectionUtils.isEmpty(bpmnDiagram.getPools())) {
            addError(ModelCheckResult.POOLS_NOT_SUPPORTED_CODE, "DIAGRAM");
        }

        List<BPMNNode> sourceNodes = new ArrayList<>();
        List<BPMNNode> targetNodes = new ArrayList<>();

        for (Flow flow : bpmnDiagram.getFlows()) {
            if ((flow.getSource() == null || flow.getTarget() == null)) {
                if (!errorMap.containsKey(ModelCheckResult.DISCONNECTED_ARC_CODE)) {
                    //disconnected - a flow source or target is missing
                    addError(ModelCheckResult.DISCONNECTED_ARC_CODE, flow.getSource().getLabel());
                }
            } else if (flow.getSource().equals(flow.getTarget())) {
                addError(ModelCheckResult.SELF_LOOP_CODE, flow.getSource().getLabel());
            }
            sourceNodes.add(flow.getSource());
            targetNodes.add(flow.getTarget());
        }

        for (Activity activity : bpmnDiagram.getActivities()) {
            checkActivity(activity, sourceNodes, targetNodes);
        }

        for (Event event : bpmnDiagram.getEvents()) {
            checkEvent(event, sourceNodes, targetNodes);
        }

        for (Gateway gateway : bpmnDiagram.getGateways()) {
            checkGateway(gateway, sourceNodes, targetNodes);
        }

        return new ModelCheckResult(errorMap);
    }

    /**
     * Check if the model is empty.
     *
     * @param bpmnDiagram the model being checked.
     * @return true if the model has no nodes or edges.
     */
    private boolean isModelEmpty(BPMNDiagram bpmnDiagram) {
        return CollectionUtils.isEmpty(bpmnDiagram.getNodes()) && CollectionUtils.isEmpty(bpmnDiagram.getEdges());
    }

    /**
     * Finds errors in an activity.
     *
     * @param activity    the activity to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     */
    private void checkActivity(Activity activity, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        if (Collections.frequency(sourceNodes, activity) > 1) {
            // > 1 outgoing arc - activity appears more than once as a flow source
            addError(ModelCheckResult.TASK_MULTIPLE_OUTGOING_ARCS_CODE, activity.getLabel());
        }

        if (Collections.frequency(targetNodes, activity) > 1) {
            // > 1 incoming arc - activity appears more than once as a flow target
            addError(ModelCheckResult.TASK_MULTIPLE_INCOMING_ARCS_CODE, activity.getLabel());
        }

        if (!sourceNodes.contains(activity) || !targetNodes.contains(activity)) {
            // missing incoming or outgoing arc - activity does not appear as a flow source or target
            addError(ModelCheckResult.TASK_MISSING_ARCS_CODE, activity.getLabel());
        }
    }

    /**
     * Finds errors in an event.
     *
     * @param event       the event to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     */
    private void checkEvent(Event event, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        if (Event.EventType.START.equals(event.getEventType())) {
            checkStartEvent(event, sourceNodes, targetNodes);
        } else if (Event.EventType.END.equals(event.getEventType())) {
            checkEndEvent(event, sourceNodes, targetNodes);
        }
    }

    /**
     * Finds errors in a start event.
     *
     * @param event       the start event to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     */
    private void checkStartEvent(Event event, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        if (Collections.frequency(sourceNodes, event) > 1) {
            // > 1 outgoing arc - start event appears more than once as a flow source
            addError(ModelCheckResult.START_MULTIPLE_OUTGOING_ARCS_CODE, "START_EVENT");
        } else if (!sourceNodes.contains(event)) {
            // No outgoing arc - start event does not appear as a flow source
            addError(ModelCheckResult.START_NO_OUTGOING_ARC_CODE, "START_EVENT");
        }

        if (targetNodes.contains(event)) {
            // Any number of incoming arcs - start event appears as a flow target
            addError(ModelCheckResult.START_INCOMING_ARCS_CODE, "START_EVENT");
        }
    }

    /**
     * Finds errors in an end event.
     *
     * @param event       the end event to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     */
    private void checkEndEvent(Event event, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        if (Collections.frequency(targetNodes, event) > 1) {
            // > 1 incoming arc - end event appears more than once as a flow target
            addError(ModelCheckResult.END_MULTIPLE_INCOMING_ARCS_CODE, "END_EVENT");
        } else if (!targetNodes.contains(event)) {
            // No incoming arc - end event does not appear as a flow target
            addError(ModelCheckResult.END_NO_INCOMING_ARC_CODE, "END_EVENT");
        }

        if (sourceNodes.contains(event)) {
            // Any number of outgoing arcs - end event appears as a flow source
            addError(ModelCheckResult.END_OUTGOING_ARCS_CODE, "END_EVENT");
        }
    }

    /**
     * Finds errors in a gateway.
     *
     * @param gateway     the gateway to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     */
    private void checkGateway(Gateway gateway, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        if (!sourceNodes.contains(gateway) || !targetNodes.contains(gateway)) {
            // missing incoming or outgoing arc - gateway does not appear as a flow source or target
            addError(ModelCheckResult.GATE_MISSING_ARCS_CODE, gateway.getId().toString());
        }
    }

    /**
     * Add an error to the error map.
     *
     * @param errorCode the error code.
     * @param element   the name or id of the element with an error.
     */
    private void addError(int errorCode, String element) {
        List<String> elementList = errorMap.getOrDefault(errorCode, new ArrayList<>());
        elementList.add(element);
        errorMap.put(errorCode, elementList);
    }
}
