/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.*;
import org.omg.spec.bpmn._20100524.model.*;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;

/**
 * CPF 1.0 net with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfNetType extends NetType implements Attributed, ExtensionConstants {

    private enum EventTypeEnum {MESSAGE, NONE, TIMER};

    /** In CPF, no two Objects within the same Net may have the same name, so we have to keep track of the names that have occurred. */
    private final Set<String> objectNameSet = new HashSet<String>();  // TODO - use diamond operator

    /** No-arg constructor. */
    public CpfNetType() { }

    /**
     * Add a net to the CPF document, corresponding to a given BPMN process.
     *
     * @param process  the BPMN process to translate into a net
     * @param parent  if this is a subnet, the parent net; if this is a root net, <code>null</code>
     * @param initializer  BPMN document construction state
     * @param cpf  the CPF root element, used to store attributes
     * @throws CanoniserException  if the net (and its subnets) can't be created and added
     */
    public CpfNetType(final ProcessWrapper       process,
                      final NetType              parent,
                      final Initializer          initializer,
                      final CanonicalProcessType cpf) throws CanoniserException {

        final CpfNetType net = this;  // inner classes are easier to understand if there's an alternative to CpfNetType.this

        net.setId(initializer.newId(process.getId()));
        net.setName(requiredName(process.getName()));
        if (parent == null) {
            initializer.addRootId(net.getId());
        }
        initializer.addNet(net);

        // Generate resource types for each pool and lane
        for (JAXBElement<? extends TRootElement> rootElement2 : initializer.getBpmnRootElements()) {
            if (rootElement2.getValue() instanceof TCollaboration) {
                for (TParticipant participant : ((TCollaboration) rootElement2.getValue()).getParticipant()) {
                    if (participant.getProcessRef() != null && process.getId().equals(participant.getProcessRef().getLocalPart())) {
                        addPools(participant, process.getLaneSet(), initializer);
                    }
                }
            }
        }

        for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
            flowElement.getValue().accept(new BaseVisitor() {

                @Override public void visit(final TAdHocSubProcess adHocSubProcess) {
                    try {
                        net.getNode().add(new CpfTaskType(adHocSubProcess, initializer, net));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TBoundaryEvent boundaryEvent) {
                    try {
                        switch (eventType(boundaryEvent)) {
                        case MESSAGE:   net.getNode().add(new CpfMessageType(boundaryEvent, initializer));   break;
                        case NONE:      net.getNode().add(new CpfEventTypeImpl(boundaryEvent, initializer)); break;
                        case TIMER:     net.getNode().add(new CpfTimerType(boundaryEvent, initializer));     break;
                        }
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TBusinessRuleTask businessRuleTask) {
                    try {
                        net.getNode().add(new CpfTaskType(businessRuleTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TCallActivity callActivity) {
                    try {
                        net.getNode().add(new CpfTaskType(callActivity, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TCallChoreography that) {
                    unimplemented(that);
                }

                @Override public void visit(final TChoreographyTask that) {
                    unimplemented(that);
                }

                @Override public void visit(final TComplexGateway complexGateway) {
                    //unimplemented(complexGateway);
                    /* TODO - figure out how ComplexGateway and EventBasedGateway are to be distinguished in CPF */

                    try {
                        RoutingType routing = new CpfStateType();

                        initializer.populateDefaultingGateway(routing, complexGateway, complexGateway.getDefault());

                        net.getNode().add(routing);

                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TDataObject dataObject) {
                    try {
                        net.getObject().add(new CpfObjectTypeImpl(dataObject, net, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TDataObjectReference dataObjectReference) {
                    try {
                        new CpfObjectRefType(dataObjectReference, initializer);
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TDataStoreReference dataStoreReference) {
                    try {
                        net.getObject().add(new CpfObjectTypeImpl(dataStoreReference, net, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TEndEvent endEvent) {
                    try {
                        switch (eventType(endEvent)) {
                        case MESSAGE:   net.getNode().add(new CpfMessageType(endEvent, initializer));   break;
                        case NONE:      net.getNode().add(new CpfEventTypeImpl(endEvent, initializer)); break;
                        case TIMER:
                            initializer.warn("BPMN event " + endEvent.getId() + " is a timer end, not legal BPMN");
                            net.getNode().add(new CpfTimerType(endEvent, initializer));
                            break;
                        default: throw new CanoniserException(eventType(endEvent) + " end event " + endEvent.getId() + " unsupported");
                        }
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TEventBasedGateway eventBasedGateway) {
                    try {
                        RoutingType routing = new CpfStateType();

                        initializer.populateFlowNode(routing, eventBasedGateway);

                        net.getNode().add(routing);

                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TExclusiveGateway exclusiveGateway) {
                    try {
                        RoutingType routing;

                        switch (exclusiveGateway.getGatewayDirection()) {
                            case CONVERGING: routing = new CpfXORJoinType(); break;
                            case DIVERGING:  routing = new CpfXORSplitType();  break;
                            case UNSPECIFIED:
                               if (exclusiveGateway.getIncoming().size() > 1 && exclusiveGateway.getOutgoing().size() == 1) {
                                   routing = new CpfXORJoinType();
                                   break;
                               } else if (exclusiveGateway.getIncoming().size() == 1 && exclusiveGateway.getOutgoing().size() > 1) {
                                   routing = new CpfXORSplitType();
                                   break;
                               } else if (exclusiveGateway.getIncoming().size() == 1 && exclusiveGateway.getOutgoing().size() == 1) {
                                   // Arbitrarily assume a split -- can't tell from either the explicit direction or incoming/outgoing counts
                                   routing = new CpfXORSplitType();
                                   break;
                               } else {
                                   System.out.println("[Warning] Found a detached gateway while parsing: " + exclusiveGateway.getId() );
                                   routing = new CpfXORSplitType();
                                   break;
                               }
                            default:
                                throw new RuntimeException(
                                    new CanoniserException("Gateway " + exclusiveGateway.getId() +
                                                           " unimplemented gateway direction " + exclusiveGateway.getGatewayDirection())
                                );  // TODO - remove wrapper hack
                        }
                        assert routing != null;

                        initializer.populateDefaultingGateway(routing, exclusiveGateway, exclusiveGateway.getDefault());

                        net.getNode().add(routing);

                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TImplicitThrowEvent that) {
                    unimplemented(that);
                }

                @Override public void visit(final TInclusiveGateway inclusiveGateway) {
                    try {
                        RoutingType routing;

                        switch (inclusiveGateway.getGatewayDirection()) {
                            case CONVERGING: routing = new CpfORJoinType(); break;
                            case DIVERGING:  routing = new CpfORSplitType();  break;
                            default:
                                throw new RuntimeException(
                                    new CanoniserException("Gateway " + inclusiveGateway.getId() +
                                                           " unimplemented gateway direction " + inclusiveGateway.getGatewayDirection())
                                );  // TODO - remove wrapper hack
                        }
                        assert routing != null;

                        initializer.populateDefaultingGateway(routing, inclusiveGateway, inclusiveGateway.getDefault());

                        net.getNode().add(routing);

                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TIntermediateCatchEvent intermediateCatchEvent) {
                    try {
                        switch (eventType(intermediateCatchEvent)) {
                        case MESSAGE:   net.getNode().add(new CpfMessageType(intermediateCatchEvent, initializer));   break;
                        case NONE:      net.getNode().add(new CpfEventTypeImpl(intermediateCatchEvent, initializer)); break;
                        case TIMER:     net.getNode().add(new CpfTimerType(intermediateCatchEvent, initializer));     break;
                        default: throw new CanoniserException(eventType(intermediateCatchEvent) + " intermediate catch event " +
                                                              intermediateCatchEvent.getId() + " unsupported");
                        }
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TIntermediateThrowEvent intermediateThrowEvent) {
                    try {
                        switch (eventType(intermediateThrowEvent)) {
                        case MESSAGE:   net.getNode().add(new CpfMessageType(intermediateThrowEvent, initializer));   break;
                        case NONE:      net.getNode().add(new CpfEventTypeImpl(intermediateThrowEvent, initializer)); break;
                        case TIMER:     net.getNode().add(new CpfTimerType(intermediateThrowEvent, initializer));     break;
                        default: throw new CanoniserException(eventType(intermediateThrowEvent) + " intermediate throw event " +
                                                              intermediateThrowEvent.getId() + " unsupported");
                        }
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TManualTask manualTask) {
                    try {
                        net.getNode().add(new CpfTaskType(manualTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TParallelGateway parallelGateway) {
                    try {
                       RoutingType routing;

                        switch (parallelGateway.getGatewayDirection()) {
                            case CONVERGING: routing = new CpfANDJoinType(); break;
                            case DIVERGING:  routing = new CpfANDSplitType();  break;
                            default:
                                throw new RuntimeException(
                                    new CanoniserException("Gateway " + parallelGateway.getId() +
                                                           " unimplemented gateway direction " + parallelGateway.getGatewayDirection())
                                );  // TODO - remove wrapper hack
                        }
                        assert routing != null;

                        initializer.populateFlowNode(routing, parallelGateway);

                        net.getNode().add(routing);

                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TReceiveTask receiveTask) {
                    try {
                        net.getNode().add(new CpfTaskType(receiveTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TScriptTask scriptTask) {
                    try {
                        net.getNode().add(new CpfTaskType(scriptTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TSendTask sendTask) {
                    try {
                        net.getNode().add(new CpfTaskType(sendTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TSequenceFlow sequenceFlow) {
                    try {
                        net.getEdge().add(new CpfEdgeType(sequenceFlow, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TServiceTask serviceTask) {
                    try {
                        net.getNode().add(new CpfTaskType(serviceTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TStartEvent startEvent) {
                    try {
                        switch (eventType(startEvent)) {
                        case MESSAGE:   net.getNode().add(new CpfMessageType(startEvent, initializer));   break;
                        case NONE:      net.getNode().add(new CpfEventTypeImpl(startEvent, initializer)); break;
                        case TIMER:     net.getNode().add(new CpfTimerType(startEvent, initializer));     break;
                        default: throw new CanoniserException(eventType(startEvent) + " start event " + startEvent.getId() + " unsupported");
                        }
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TSubChoreography that) { unimplemented(that); }

                @Override public void visit(final TSubProcess subProcess) {
                    try {
                        net.getNode().add(new CpfTaskType(subProcess, initializer, net));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TTask task) {
                    try {
                        net.getNode().add(new CpfTaskType(task, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TUserTask userTask) {
                    try {
                        net.getNode().add(new CpfTaskType(userTask, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                // Internal methods

                /**
                 * Classify BPMN events by trigger type (message, timer, etc).
                 *
                 * @param a BPMN Catch Event
                 * @return the special type of the specified event
                 * @throws CanoniserException if the trigger type isn't supported
                 */
                private EventTypeEnum eventType(final TCatchEvent event) throws CanoniserException {
                    return eventType(event.getEventDefinition(), event.getEventDefinitionRef());
                }

                /**
                 * Classify BPMN events by trigger type (message, timer, etc).
                 *
                 * @param a BPMN Throw Event
                 * @return the special type of the specified event
                 * @throws CanoniserException if the trigger type isn't supported
                 */
                private EventTypeEnum eventType(final TThrowEvent event) throws CanoniserException {
                    return eventType(event.getEventDefinition(), event.getEventDefinitionRef());
                }
                    
                private EventTypeEnum eventType(final List<JAXBElement<? extends TEventDefinition>> eventDefinitionList,
                                                final List<QName> eventDefinitionRefList) throws CanoniserException {

                    if (eventDefinitionRefList.size() > 0) {
                        throw new CanoniserException("BPMN EventDefinition references not supported");
                    }

                    for (Iterator<JAXBElement<? extends TEventDefinition>> i = eventDefinitionList.iterator(); i.hasNext();) {
                        TEventDefinition eventDef = i.next().getValue();
                        if (eventDef instanceof TMessageEventDefinition) {
                            while (i.hasNext()) {
                                if (i.next().getValue() instanceof TTimerEventDefinition) {
                                    throw new CanoniserException("Message event is also a timer event");
                                }
                            }
                            return EventTypeEnum.MESSAGE;
                        }
                        if (eventDef instanceof TTimerEventDefinition) {
                            while (i.hasNext()) {
                                if (i.next().getValue() instanceof TMessageEventDefinition) {
                                    throw new CanoniserException("Timer event is also a message event");
                                }
                            }
                            return EventTypeEnum.TIMER;
                        }
                    }

                    return EventTypeEnum.NONE;
                }

                private void unimplemented(final Object o) {
                    throw new RuntimeException(new CanoniserException("Unimplemented BPMN element: " + o));
                }
            });
        }

        // TODO - probably need to move BPMN artifacts to ANF
        for (JAXBElement<? extends TArtifact> artifact : process.getArtifact()) {
            artifact.getValue().accept(new BaseVisitor() {

                @Override public void visit(final TAssociation association) {
                    try {
                        TBaseElement source = initializer.findBpmnElement(association.getSourceRef());
                        TBaseElement target = initializer.findBpmnElement(association.getTargetRef());

                        if (association.getAssociationDirection().equals(TAssociationDirection.ONE) &&
                            source instanceof TStartEvent &&
                            target instanceof TDataObject) {

                            initializer.warn("Detected a spurious Association from ch9_loan");
                            WorkType   work   = (WorkType)   initializer.findElement(source);
                            ObjectType object = (ObjectType) initializer.findElement(target);
                        }
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TGroup group) {
                    // TODO
                }

                @Override public void visit(final TTextAnnotation textAnnotation) {
                    // TODO
                }
            });
        }

        // Convert BPMN extension elements to CPF attributes
        if (cpf != null) {
            initializer.populateExtensionElements(
                process.getBaseElement(),
                this,
                null,  // CPF Net instances don't have a "configurable" property
                new UnaryFunction<Element>() { public void run(Element element){ ExtensionUtils.addToExtensions(element, cpf, BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS); }}
            );
        }
    }

    /** @return the names of all {@link ObjectType} nodes belonging to this instance */
    Set<String> getObjectNames() {
        return objectNameSet;
    }

    /**
     * Each lane set in a process corresponds to a pool; for each such pool, create a CPF resource type.
     *
     * @param participant  the BPMN participant corresponding to the pool
     * @param laneSet      the BPMN lane set of the process referenced by the <var>participant</var>
     * @param cpf  the CPF document to populate
     * @param cpfIdFactory  generator of identifiers for pools and lanes
     * @param CanoniserException
     */
    private static void addPools(final TParticipant   participant,
                                 final List<TLaneSet> laneSets,
                                 final Initializer    initializer) throws CanoniserException {

        for (TLaneSet laneSet : laneSets) {

            // Create a pool
            ResourceTypeType poolResourceType = new CpfResourceTypeTypeImpl(participant, initializer);

            // Create the lanes within the pool
            poolResourceType.getSpecializationIds().addAll(addLanes(laneSet, initializer));

            initializer.addResourceType(poolResourceType);
        }
    }

    /**
     * Recursively add resource types to this CPF corresponding to BPMN lanes.
     *
     * This is recursive, since a lane may itself contain a child lane set.
     *
     * @param laneSet  BPMN lane set to add, never <code>null</code>
     * @param cpf  the CPF document to populate
     * @param cpfIdFactory  generator of identifiers for pools and lanes
     * @return the CPF ids of all the added lanes (but not their sublanes)
     */
    private static Set<String> addLanes(final TLaneSet              laneSet,
                                        final Initializer           initializer) {

        Set<String> specializationIds = new HashSet<String>();  // TODO - diamond operator

        for (final TLane lane : laneSet.getLane()) {
            ResourceTypeType laneResourceType = new CpfResourceTypeTypeImpl();

            // Add the resource type to the CPF model
            laneResourceType.setId(initializer.newId(lane.getId()));
            laneResourceType.setName(requiredName(lane.getName()));
            specializationIds.add(laneResourceType.getId());
            initializer.addResourceType(laneResourceType);

            // Populate laneMap so we'll know later on which lane each element belongs to
            List list = lane.getFlowNodeRef();
            for (Object object : list) {
                JAXBElement je = (JAXBElement) object;
                Object value = je.getValue();
                if (value instanceof TFlowNode) {
                    final TFlowNode flowNode = (TFlowNode) value;

                    initializer.defer(new Initialization() {
                        public void initialize() throws CanoniserException {
                            CpfNodeType cpfNode = (CpfNodeType) initializer.findElement(flowNode);
                            if (cpfNode instanceof WorkType) {
                                ((WorkType) cpfNode).getResourceTypeRef().add(new CpfResourceTypeRefType(lane, initializer));
                            }
                        }
                    });
                } else {
                    String s = value == null                 ? "null" :
                               value instanceof TBaseElement ? ((TBaseElement) value).getId() : value.toString();
                    initializer.warn("Lane " + lane.getId() + " contains " + s + ", which is not a flow node");
                }
            }

            // recurse on any child lane sets
            if (lane.getChildLaneSet() != null) {
                laneResourceType.getSpecializationIds().addAll(addLanes(lane.getChildLaneSet(), initializer));
            }
        }

        return specializationIds;
    }
}
