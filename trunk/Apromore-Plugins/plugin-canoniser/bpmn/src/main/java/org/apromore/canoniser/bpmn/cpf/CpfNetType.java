package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
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
public class CpfNetType extends NetType {

    /** No-arg constructor. */
    public CpfNetType() {
        super();
    }

    /**
     * Add a net to the CPF document, corresponding to a given BPMN process.
     *
     * @param cpf  the CPF document to populate
     * @param process  the BPMN process to translate into a net
     * @param parent  if this is a subnet, the parent net; if this is a root net, <code>null</code>
     * @param definitions  the BPMN document
     * @throws CanoniserException  if the net (and its subnets) can't be created and added
     */
    public CpfNetType(final ProcessWrapper process,
                      final NetType parent,
                      final Initializer initializer) throws CanoniserException {

        super();

        final NetType net = this;  // needed so that the inner classes can reference this instance

        net.setId(initializer.cpfIdFactory.newId(process.getId()));
        if (parent == null) {
            initializer.cpf.getRootIds().add(net.getId());
        }
        initializer.cpf.getNet().add(net);

        // Generate resource types for each pool and lane
        for (JAXBElement<? extends TRootElement> rootElement2 : initializer.definitions.getRootElement()) {
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
                    net.getNode().add(new CpfTaskType(businessRuleTask, initializer));
                }

                @Override public void visit(final TCallActivity callActivity) {
                    net.getNode().add(new CpfTaskType(callActivity, initializer));
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
                    net.getObject().add(new CpfObjectType(dataObject, initializer));
                }

                @Override public void visit(final TDataObjectReference dataObjectReference) {
                    unimplemented(dataObjectReference);
                }

                @Override public void visit(final TDataStoreReference dataStoreReference) {
                    unimplemented(dataStoreReference);
                }

                @Override public void visit(final TEndEvent endEvent) {
                    net.getNode().add(new CpfEventType(endEvent, initializer));
                }

                @Override public void visit(final TEventBasedGateway that) {
                    unimplemented(that);
                }

                @Override public void visit(final TExclusiveGateway exclusiveGateway) {
                    RoutingType routing;

                    switch (exclusiveGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new XORJoinType(); break;
                        case DIVERGING:  routing = new XORSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Gateway " + exclusiveGateway.getId() +
                                                       " unimplemented gateway direction " + exclusiveGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    initializer.populateFlowElement(routing, exclusiveGateway);

                    net.getNode().add(routing);
                }

                @Override public void visit(final TImplicitThrowEvent that) {
                    unimplemented(that);
                }

                @Override public void visit(final TInclusiveGateway inclusiveGateway) {
                    RoutingType routing;

                    switch (inclusiveGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new ORJoinType(); break;
                        case DIVERGING:  routing = new ORSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Gateway " + inclusiveGateway.getId() +
                                                       " unimplemented gateway direction " + inclusiveGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    initializer.populateFlowElement(routing, inclusiveGateway);

                    net.getNode().add(routing);
                }

                @Override public void visit(final TIntermediateCatchEvent that) {
                    unimplemented(that);
                }

                @Override public void visit(final TIntermediateThrowEvent that) {
                    unimplemented(that);
                }

                @Override public void visit(final TManualTask manualTask) {
                    net.getNode().add(new CpfTaskType(manualTask, initializer));
                }

                @Override public void visit(final TParallelGateway parallelGateway) {
                    RoutingType routing;

                    switch (parallelGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new ANDJoinType(); break;
                        case DIVERGING:  routing = new ANDSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Gateway " + parallelGateway.getId() +
                                                       " unimplemented gateway direction " + parallelGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    initializer.populateFlowElement(routing, parallelGateway);

                    net.getNode().add(routing);
                }

                @Override public void visit(final TReceiveTask receiveTask) {
                    net.getNode().add(new CpfTaskType(receiveTask, initializer));
                }

                @Override public void visit(final TScriptTask scriptTask) {
                    net.getNode().add(new CpfTaskType(scriptTask, initializer));
                }

                @Override public void visit(final TSendTask sendTask) {
                    net.getNode().add(new CpfTaskType(sendTask, initializer));
                }

                @Override public void visit(final TSequenceFlow sequenceFlow) {
                    try {
                        net.getEdge().add(new CpfEdgeType(sequenceFlow, initializer));
                    } catch (CanoniserException e) {
                        throw new RuntimeException(e);  // TODO - remove wrapper hack
                    }
                }

                @Override public void visit(final TServiceTask serviceTask) {
                    net.getNode().add(new CpfTaskType(serviceTask, initializer));
                }

                @Override public void visit(final TStartEvent startEvent) {
                    net.getNode().add(new CpfEventType(startEvent, initializer));
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
                    net.getNode().add(new CpfTaskType(task, initializer));
                }

                @Override public void visit(final TUserTask userTask) {
                    net.getNode().add(new CpfTaskType(userTask, initializer));
                }

                // Internal methods

                private void unimplemented(Object o) {
                    throw new RuntimeException(new CanoniserException("Unimplemented BPMN element: " + o));
                }
            });
        }

        unwindLaneMap(initializer);

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
            ResourceTypeType poolResourceType = new ResourceTypeType();
            poolResourceType.setId(initializer.cpfIdFactory.newId(participant.getId()));
            poolResourceType.setName(requiredName(participant.getName()));
            initializer.cpf.getResourceType().add(poolResourceType);

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
            ResourceTypeType laneResourceType = new ResourceTypeType();

            // Add the resource type to the CPF model
            laneResourceType.setId(initializer.cpfIdFactory.newId(lane.getId()));
            laneResourceType.setName(requiredName(lane.getName()));
            specializationIds.add(laneResourceType.getId());
            initializer.cpf.getResourceType().add(laneResourceType);

            // Populate laneMap so we'll know later on which lane each element belongs to
            List list = lane.getFlowNodeRef();
            for (Object object : list) {
                JAXBElement je = (JAXBElement) object;
                Object value = je.getValue();
                TFlowNode flowNode = (TFlowNode) value;
                initializer.laneMap.put(flowNode, lane);
            }

            // recurse on any child lane sets
            if (lane.getChildLaneSet() != null) {
                laneResourceType.getSpecializationIds().addAll(addLanes(lane.getChildLaneSet(), initializer));
            }
        }

        return specializationIds;
    }

    /**
     * Take the {@link #laneMap} populated by {@link #addLaneSet} and use it to populate the CPF nodes' {@link NodeType#resourceTypeRef}s.
     *
     * @param cpfIdFactory  generator for {@link ResourceTypeRefType#id}s
     * @throws CanoniserException  if the {@link #laneMap} contains a lane mapping to a node that doesn't exist
     */
    private static void unwindLaneMap(final Initializer initializer) throws CanoniserException {

        for (Map.Entry<TFlowNode, TLane> entry : initializer.laneMap.entrySet()) {
            if (!initializer.bpmnFlowNodeToCpfNodeMap.containsKey(entry.getKey())) {
                throw new CanoniserException("Lane " + entry.getValue().getId() + " contains " +
                                             entry.getKey().getId() + " which is not present");
            }
            NodeType node = initializer.bpmnFlowNodeToCpfNodeMap.get(entry.getKey());  // get the CPF node corresponding to the BPMN flow node
            if (node instanceof WorkType) {
                ((WorkType) node).getResourceTypeRef().add(new CpfResourceTypeRefType(entry.getValue(), initializer));
            }
        }
    }
}
