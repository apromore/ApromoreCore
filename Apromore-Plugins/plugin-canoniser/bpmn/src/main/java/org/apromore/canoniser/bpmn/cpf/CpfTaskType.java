/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.bpmn.BpmnCallActivity;
import org.apromore.canoniser.bpmn.bpmn.BpmnSubProcess;
import org.apromore.canoniser.bpmn.bpmn.BpmnTask;
import org.apromore.canoniser.bpmn.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TCallActivity;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.bpmn._20100524.model.TTask;

/**
 * CPF 1.0 task with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfTaskType extends TaskType implements CpfWorkType {

    /**
     * We can indicate a BPMN Subprocess by creating a link from a CPF Task to a CPF Net,
     * but because the net won't contain any nodes it'll be invalid CPF.
     */
    public static boolean CREATE_PLACEHOLDER_CPF_NET_FOR_EMPTY_BPMN_SUBPROCESS = false;

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating a BPMN CallActivity's called element. */
    public static final String CALLED_ELEMENT = "calledElement";

    /** {@link TypeAttribute#name} indicating a BPMN event-triggered subprocess. */
    public static final String TRIGGERED_BY_EVENT = "bpmnTriggeredByEvent";

    // Internal state

    /** Attached boundary events. */
    private Set<CpfEventType> boundaryEvents = new HashSet<CpfEventType>();  // TODO - diamond operator

    /** Incoming edges. */
    private Set<CpfEdgeType> incomingEdges = new HashSet<CpfEdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<CpfEdgeType> outgoingEdges = new HashSet<CpfEdgeType>();  // TODO - diamond operator

    // Constructors

    /** No-arg constructor. */
    public CpfTaskType() { }

    /**
     * Construct a CPF Task corresponding to a BPMN Call Activity.
     *
     * @param callActivity  a BPMN Call Activity
     * @param initializer  global construction state
     * @throws CanoniserException if the task can't be constructed
     */
    public CpfTaskType(final TCallActivity callActivity, final Initializer initializer) throws CanoniserException {
        initializer.populateActivity(this, callActivity);
        if (false) {
            // The called element is a process or global task within this same BPMN document
            setSubnetId(callActivity.getId());  // TODO - process through CpfIdFactory
        } else {
            // The called element is NOT a process or global task within this same BPMN document
            setCalledElement(new QName("dummy"));
        }
    }

    /**
     * Construct a CPF Task corresponding to a BPMN SubProcess.
     *
     * @param subProcess  a BPMN SubProcess
     * @param initializer  global construction state
     * @param net  parent net
     * @throws CanoniserException if the task's subnet can't be constructed
     */
    public CpfTaskType(final TSubProcess subProcess,
                       final Initializer initializer,
                       final NetType     net) throws CanoniserException {

        if (CREATE_PLACEHOLDER_CPF_NET_FOR_EMPTY_BPMN_SUBPROCESS || !subProcess.getFlowElement().isEmpty()) {
            // Add the CPF child net
            NetType subnet = new CpfNetType(new ProcessWrapper(subProcess, initializer.newId("subprocess")),
                                            net,
                                            initializer,
                                            null);
            assert subnet != null;

            setSubnetId(subnet.getId());
        }

        // Add the CPF Task to the parent Net
        initializer.populateActivity(this, subProcess);
    }

    /**
     * Construct a CPF Task corresponding to a BPMN Task.
     *
     * @param task  a BPMN Task
     * @param initializer  global construction state
     * @throws CanoniserException if the task can't be constructed
     */
    public CpfTaskType(final TTask task, final Initializer initializer) throws CanoniserException {
        initializer.populateActivity(this, task);
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        if (getCalledElement() != null) {  // This CPF Task is a BPMN CallActivity
            return initializer.getFactory().createCallActivity(new BpmnCallActivity(this, initializer));
        } else if (getSubnetId() != null) {  // This CPF Task is a BPMN SubProcess
            return initializer.getFactory().createSubProcess(new BpmnSubProcess(this, initializer));
        } else {  // This CPF Task is a BPMN Task
            return initializer.getFactory().createTask(new BpmnTask(this, initializer));
        }
    }

    // Accessor methods

    /** @return every edge which has this node as its target */
    public Set<CpfEventType> getBoundaryEvents() {
        return boundaryEvents;
    }

    /** @return every edge which has this node as its target */
    public Set<CpfEdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /** @return every edge which has this node as its source */
    public Set<CpfEdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }

    // Accessors for CPF extension attributes

    /** @return the identifier of the called element of a BPMN Call Activity */
    public QName getCalledElement() {
        String s = ExtensionUtils.getString(getAttribute(), CALLED_ELEMENT);
        return s == null ? null : QName.valueOf(s);
    }

    /** @param id  The identifier of the called element, or <code>null</code> to clear the property */
    public void setCalledElement(final QName id) {
        ExtensionUtils.setString(getAttribute(), CALLED_ELEMENT, id == null ? null : id.toString());
    }

    /** @return whether this task has any attribute named {@link #TRIGGERED_BY_EVENT}. */
    public boolean isTriggeredByEvent() {
        return ExtensionUtils.hasExtension(getAttribute(), TRIGGERED_BY_EVENT);
    }

    /** @param value  whether this CPF task corresponds to a BPMN event-triggered subprocess */
    public void setTriggeredByEvent(final Boolean value) {
        ExtensionUtils.flagExtension(getAttribute(), TRIGGERED_BY_EVENT, value);
    }
}
