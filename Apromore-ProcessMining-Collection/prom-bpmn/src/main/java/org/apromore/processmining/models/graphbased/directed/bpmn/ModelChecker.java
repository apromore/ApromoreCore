/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelChecker {
    private static final String DISCONNECTED_ARC_MSG = "The model has disconnected arcs";
    private static final String EMPTY_MODEL_MSG = "The model is empty";
    private static final String POOLS_NOT_SUPPORTED_MSG = "There are pools in the model. Pools are not yet supported";
    private static final String SELF_LOOP_MSG_FORMAT = "The element %s has a self-loop";
    private static final String TASK_MULTIPLE_OUTGOING_ARCS_MSG_FORMAT = "The task %s has more than one outgoing arc";
    private static final String TASK_MULTIPLE_INCOMING_ARCS_MSG_FORMAT = "The task %s has more than one incoming arc";
    private static final String TASK_MISSING_ARCS_MSG_FORMAT = "The task %s has missing incoming or outgoing arcs";
    private static final String GATE_MISSING_ARCS_MSG_FORMAT = "The gateway %s has missing incoming or outgoing arcs";
    private static final String START_MULTIPLE_OUTGOING_ARCS_MSG = "The Start Event has more than one outgoing arc";
    private static final String START_NO_OUTGOING_ARC_MSG = "The Start Event has a missing outgoing arc";
    private static final String START_INCOMING_ARCS_MSG = "The Start Event has incoming arc(s)";
    private static final String END_MULTIPLE_INCOMING_ARCS_MSG = "The End Event has more than one incoming arc";
    private static final String END_NO_INCOMING_ARC_MSG = "The End Event has a missing incoming arc";
    private static final String END_OUTGOING_ARCS_MSG = "The End Event has outgoing arc(s)";


    /**
     * Finds errors in a model.
     * @param bpmnDiagram the model to find errors in.
     * @param allowPools true if pools are supported.
     * @return a object containing the errors in the model.
     */
    public ModelCheckResult checkModel(BPMNDiagram bpmnDiagram, boolean allowPools) {
        List<String> errors = new ArrayList<>();

        if (CollectionUtils.isEmpty(bpmnDiagram.getNodes()) && CollectionUtils.isEmpty(bpmnDiagram.getEdges())) {
            errors.add(EMPTY_MODEL_MSG); //empty model - no nodes or edges
        }

        if (!allowPools && !CollectionUtils.isEmpty(bpmnDiagram.getPools())) {
            errors.add(POOLS_NOT_SUPPORTED_MSG);
        }

        List<BPMNNode> sourceNodes = new ArrayList<>();
        List<BPMNNode> targetNodes = new ArrayList<>();

        for (Flow flow : bpmnDiagram.getFlows()) {
            if ((flow.getSource() == null || flow.getTarget() == null) && !errors.contains(DISCONNECTED_ARC_MSG)) {
                errors.add(DISCONNECTED_ARC_MSG); //disconnected - a flow source or target is missing
            } else if (flow.getSource().equals(flow.getTarget())) {
                errors.add(String.format(SELF_LOOP_MSG_FORMAT, flow.getSource().getLabel()));
            }
            sourceNodes.add(flow.getSource());
            targetNodes.add(flow.getTarget());
        }

        for (Activity activity : bpmnDiagram.getActivities()) {
            errors.addAll(checkActivity(activity, sourceNodes, targetNodes));
        }

        for (Event event : bpmnDiagram.getEvents()) {
            if (Event.EventType.START.equals(event.getEventType())) {
                errors.addAll(checkStartEvent(event, sourceNodes, targetNodes));
            } else if (Event.EventType.END.equals(event.getEventType())) {
                errors.addAll(checkEndEvent(event, sourceNodes, targetNodes));
            }
        }

        for (Gateway gateway : bpmnDiagram.getGateways()) {
            if (!sourceNodes.contains(gateway) || !targetNodes.contains(gateway)) {
                // missing incoming or outgoing arc - gateway does not appear as a flow source or target
                errors.add(String.format(GATE_MISSING_ARCS_MSG_FORMAT, gateway.getId()));
            }
        }

        return new ModelCheckResult(errors);
    }

    /**
     * Finds errors in an activity.
     * @param activity the activity to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     * @return A list of errors in the activity.
     */
    private List<String> checkActivity(Activity activity, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        List<String> errors = new ArrayList<>();
        if (Collections.frequency(sourceNodes, activity) > 1) {
            // > 1 outgoing arc - activity appears more than once as a flow source
            errors.add(String.format(TASK_MULTIPLE_OUTGOING_ARCS_MSG_FORMAT, activity.getLabel()));
        }

        if (Collections.frequency(targetNodes, activity) > 1) {
            // > 1 incoming arc - activity appears more than once as a flow target
            errors.add(String.format(TASK_MULTIPLE_INCOMING_ARCS_MSG_FORMAT, activity.getLabel()));
        }

        if (!sourceNodes.contains(activity) || !targetNodes.contains(activity)) {
            // missing incoming or outgoing arc - activity does not appear as a flow source or target
            errors.add(String.format(TASK_MISSING_ARCS_MSG_FORMAT, activity.getLabel()));
        }
        return errors;
    }

    /**
     * Finds errors in a start event.
     * @param event the start event to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     * @return A list of errors in the activity.
     */
    private List<String> checkStartEvent(Event event, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        List<String> errors = new ArrayList<>();
        if (Collections.frequency(sourceNodes, event) > 1) {
            // > 1 outgoing arc - start event appears more than once as a flow source
            errors.add(START_MULTIPLE_OUTGOING_ARCS_MSG);
        } else if (!sourceNodes.contains(event)) {
            // No outgoing arc - start event does not appear as a flow source
            errors.add(START_NO_OUTGOING_ARC_MSG);
        }

        if (targetNodes.contains(event)) {
            // Any number of incoming arcs - start event appears as a flow target
            errors.add(START_INCOMING_ARCS_MSG);
        }
        return errors;
    }

    /**
     * Finds errors in an end event.
     * @param event the end event to find errors in.
     * @param sourceNodes a list of nodes in the model which have outgoing edges.
     * @param targetNodes a list of nodes in the model which have incoming edges.
     * @return A list of errors in the activity.
     */
    private List<String> checkEndEvent(Event event, List<BPMNNode> sourceNodes, List<BPMNNode> targetNodes) {
        List<String> errors = new ArrayList<>();
        if (Collections.frequency(targetNodes, event) > 1) {
            // > 1 incoming arc - end event appears more than once as a flow target
            errors.add(END_MULTIPLE_INCOMING_ARCS_MSG);
        } else if (!targetNodes.contains(event)) {
            // No incoming arc - end event does not appear as a flow target
            errors.add(END_NO_INCOMING_ARC_MSG);
        }

        if (sourceNodes.contains(event)) {
            // Any number of outgoing arcs - end event appears as a flow source
            errors.add(END_OUTGOING_ARCS_MSG);
        }
        return errors;
    }
}
