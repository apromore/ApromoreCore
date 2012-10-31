package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.*;
import org.omg.spec.bpmn._20100524.model.*;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;

/**
 * CPF 1.0 net with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfNetType extends NetType implements Attributed {

    /** In CPF, no two Objects within the same Net may have the same name, so we have to keep track of the names that have occurred. */
    private final Set<String> objectNameSet = new HashSet<String>();  // TODO - use diamond operator

    /** No-arg constructor. */
    public CpfNetType() {
        super();
    }

    /**
     * Add a net to the CPF document, corresponding to a given BPMN process.
     *
     * @param process  the BPMN process to translate into a net
     * @param parent  if this is a subnet, the parent net; if this is a root net, <code>null</code>
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the net (and its subnets) can't be created and added
     */
    public CpfNetType(final ProcessWrapper process,
                      final NetType parent,
                      final Initializer initializer) throws CanoniserException {

        super();

        final CpfNetType net = this;  // needed so that the inner classes can reference this instance

        net.setId(initializer.newId(process.getId()));
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

                @Override public void visit(final TBoundaryEvent that) {
                    unimplemented(that);
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

                @Override public void visit(final TComplexGateway that) {
                    unimplemented(that);
                }

                @Override public void visit(final TDataObject dataObject) {
                    try {
                        net.getObject().add(new CpfObjectType(dataObject, net, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TDataObjectReference dataObjectReference) {
                    unimplemented(dataObjectReference);
                }

                @Override public void visit(final TDataStoreReference dataStoreReference) {
                    try {
                        net.getObject().add(new CpfObjectType(dataStoreReference, net, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TEndEvent endEvent) {
                    try {
                        net.getNode().add(new CpfEventType(endEvent, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TEventBasedGateway that) {
                    unimplemented(that);
                }

                @Override public void visit(final TExclusiveGateway exclusiveGateway) {
                    try {
                        RoutingType routing;

                        switch (exclusiveGateway.getGatewayDirection()) {
                            case CONVERGING: routing = new CpfXORJoinType(); break;
                            case DIVERGING:  routing = new CpfXORSplitType();  break;
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

                @Override public void visit(final TIntermediateCatchEvent that) {
                    unimplemented(that);
                }

                @Override public void visit(final TIntermediateThrowEvent that) {
                    unimplemented(that);
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
                        net.getNode().add(new CpfEventType(startEvent, initializer));
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

                private void unimplemented(final Object o) {
                    throw new RuntimeException(new CanoniserException("Unimplemented BPMN element: " + o));
                }
            });
        }

        // TODO - probably need to move BPMN artifacts to ANF
        for (JAXBElement<? extends TArtifact> artifact : process.getArtifact()) {
            artifact.getValue().accept(new BaseVisitor() {

                @Override public void visit(final TAssociation association) {
                    // TODO
                }

                @Override public void visit(final TGroup group) {
                    // TODO
                }

                @Override public void visit(final TTextAnnotation textAnnotation) {
                    // TODO
                }
            });
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
     * @param laneSet      the BPMN lanet set of the process referenced by the <var>participant</var>
     * @param cpf  the CPF document to populate
     * @param cpfIdFactory  generator of identifiers for pools and lanes
     */
    private static void addPools(final TParticipant          participant,
                                 final List<TLaneSet>        laneSets,
                                 final Initializer           initializer) {

        for (TLaneSet laneSet : laneSets) {

            // Create a pool
            ResourceTypeType poolResourceType = new CpfResourceTypeType();
            poolResourceType.setId(initializer.newId(participant.getId()));
            poolResourceType.setName(requiredName(participant.getName()));
            initializer.addResourceType(poolResourceType);

            // Create the lanes within the pool
            poolResourceType.getSpecializationIds().addAll(addLanes(laneSet, initializer));
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

        for (TLane lane : laneSet.getLane()) {
            ResourceTypeType laneResourceType = new CpfResourceTypeType();

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
                    TFlowNode flowNode = (TFlowNode) value;
                    initializer.recordLaneNode(flowNode, lane);
                } else {
                    String s = value instanceof TBaseElement ? ((TBaseElement) value).getId() : value.toString();
                    Logger.getAnonymousLogger().fine("Lane " + lane.getId() + " contains " + s + ", which is not a flow node");
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
