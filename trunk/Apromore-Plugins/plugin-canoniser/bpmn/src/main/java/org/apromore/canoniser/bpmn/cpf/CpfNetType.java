package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.*;
import org.omg.spec.bpmn._20100524.model.*;

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
     * @param cpfIdFactory  generator for CPF identifiers
     * @param process  the BPMN process to translate into a net
     * @param laneMap  which BPMN nodes belong in which lanes?
     * @param bpmnFlowNodeToCpfNodeMap  which BPMN nodes correspond to which CPF nodes?
     * @param parent  if this is a subnet, the parent net; if this is a root net, <code>null</code>
     * @param definitions  the BPMN document
     * @throws CanoniserException  if the net (and its subnets) can't be created and added
     */
    public CpfNetType(final CanonicalProcessType cpf,
                      final IdFactory cpfIdFactory,
                      final ProcessWrapper process,
                      final Map<TFlowNode, TLane> laneMap,
                      final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap,
                      final NetType parent,
                      final BpmnDefinitions definitions) throws CanoniserException {

        super();

        final NetType net = this;  // needed so that the inner classes can reference this instance

        net.setId(cpfIdFactory.newId(process.getId()));
        if (parent == null) {
            cpf.getRootIds().add(net.getId());
        }
        cpf.getNet().add(net);

        // Generate resource types for each pool and lane
        for (JAXBElement<? extends TRootElement> rootElement2 : definitions.getRootElement()) {
            if (rootElement2.getValue() instanceof TCollaboration) {
                for (TParticipant participant : ((TCollaboration) rootElement2.getValue()).getParticipant()) {
                    if (process.getId().equals(participant.getProcessRef().getLocalPart())) {
                        addPools(participant, process.getLaneSet(), cpf, cpfIdFactory, laneMap);
                    }
                }
            }
        }

        for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
            flowElement.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                @Override
                public void visit(final TCallActivity callActivity) {
                    CpfTaskType task = new CpfTaskType();
                    populateFlowNode(task, callActivity);

                    if (false) {
                        // The called element is a process or global task within this same BPMN document
                        task.setSubnetId(callActivity.getId());
                    } else {
                        // The called element is NOT a process or global task within this same BPMN document
                        task.setCalledElement(new QName("dummy"));
                    }

                    net.getNode().add(task);
                }

                @Override
                public void visit(final TDataObject dataObject) {
                    ObjectType object = new ObjectType();

                    object.setConfigurable(false);  // BPMN doesn't have an obvious equivalent

                    if (dataObject.isIsCollection()) {
                        // TODO - represent using some sort of extension element
                    }

                    populateFlowElement(object, dataObject);

                    net.getObject().add(object);
                }

                @Override
                public void visit(final TEndEvent endEvent) {
                    EventType event = new EventType();
                    populateFlowNode(event, endEvent);

                    net.getNode().add(event);
                }

                @Override
                public void visit(final TExclusiveGateway exclusiveGateway) {
                    RoutingType routing;

                    switch (exclusiveGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new XORJoinType(); break;
                        case DIVERGING:  routing = new XORSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Unimplemented gateway direction " + exclusiveGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    populateFlowElement(routing, exclusiveGateway);

                    net.getNode().add(routing);
                }

                @Override
                public void visit(final TInclusiveGateway inclusiveGateway) {
                    RoutingType routing;

                    switch (inclusiveGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new ORJoinType(); break;
                        case DIVERGING:  routing = new ORSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Unimplemented gateway direction " + inclusiveGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    populateFlowElement(routing, inclusiveGateway);

                    net.getNode().add(routing);
                }

                @Override
                public void visit(final TParallelGateway parallelGateway) {
                    RoutingType routing;

                    switch (parallelGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new ANDJoinType(); break;
                        case DIVERGING:  routing = new ANDSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Unimplemented gateway direction " + parallelGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    populateFlowElement(routing, parallelGateway);

                    net.getNode().add(routing);
                }

                @Override
                public void visit(final TSequenceFlow sequenceFlow) {
                    EdgeType edge = new EdgeType();
                    populateFlowElement(edge, sequenceFlow);

                    if (sequenceFlow.getConditionExpression() != null) {

                        // We don't handle multiple conditions
                        if (sequenceFlow.getConditionExpression().getContent().size() != 1) {
                            throw new RuntimeException(
                                                       new CanoniserException("BPMN sequence flow " + sequenceFlow.getId() + " has " +
                                                                              sequenceFlow.getConditionExpression().getContent().size() +
                                                                              " conditions, which the canoniser doesn't implement")
                                                       );  // TODO - remove wrapper hack
                        }

                        ConditionExpressionType conditionExpr = new ConditionExpressionType();
                        conditionExpr.setExpression(sequenceFlow.getConditionExpression().getContent().get(0).toString());
                        edge.setConditionExpr(conditionExpr);
                    }
                    edge.setSourceId(((TFlowNode) sequenceFlow.getSourceRef()).getId());  // TODO - process through cpfIdFactory
                    edge.setTargetId(((TFlowNode) sequenceFlow.getTargetRef()).getId());  // TODO - process through cpfIdFactory

                    net.getEdge().add(edge);
                }

                @Override
                public void visit(final TStartEvent startEvent) {
                    EventType event = new EventType();
                    populateFlowNode(event, startEvent);

                    net.getNode().add(event);
                }

                @Override
                public void visit(final TSubProcess subprocess) {

                    // Add the CPF child net
                    NetType subnet;
                    try {
                        subnet = new CpfNetType(cpf,
                                                cpfIdFactory,
                                                new ProcessWrapper(subprocess, cpfIdFactory.newId("subprocess")),
                                                laneMap,
                                                bpmnFlowNodeToCpfNodeMap,
                                                net,
                                                definitions);
                    } catch (CanoniserException e) {
                        throw new RuntimeException("Couldn't create CPF Net for BPMN SubProcess " + subprocess.getId(), e);
                        // TODO - remove wrapper hack
                    }
                    assert subnet != null;

                    // Add the CPF Task to the parent Net
                    TaskType cpfTask = new TaskType();
                    populateFlowNode(cpfTask, subprocess);
                    cpfTask.setSubnetId(subnet.getId());
                    net.getNode().add(cpfTask);
                }

                @Override
                public void visit(final TTask bpmnTask) {
                    TaskType cpfTask = new TaskType();
                    populateFlowNode(cpfTask, bpmnTask);

                    net.getNode().add(cpfTask);
                }

                // Edge supertype handlers

                private void populateBaseElement(final EdgeType edge, final TBaseElement baseElement) {
                    edge.setId(cpfIdFactory.newId(baseElement.getId()));
                    edge.setOriginalID(baseElement.getId());
                }

                private void populateFlowElement(final EdgeType edge, final TFlowElement flowElement) {
                    populateBaseElement(edge, flowElement);
                }

                // Node supertype handlers

                private void populateBaseElement(final NodeType node, final TBaseElement baseElement) {
                    node.setId(cpfIdFactory.newId(baseElement.getId()));
                    node.setOriginalID(baseElement.getId());
                }

                private void populateFlowElement(final NodeType node, final TFlowElement flowElement) {
                    populateBaseElement(node, flowElement);
                    node.setName(flowElement.getName());
                }

                // Work supertype handler

                private void populateFlowNode(final WorkType work, final TFlowNode flowNode) {
                    populateFlowElement(work, flowNode);
                    bpmnFlowNodeToCpfNodeMap.put(flowNode, work);
                }

                // Object supertype handlers

                private void populateBaseElement(final ObjectType object, final TBaseElement baseElement) {
                    object.setId(cpfIdFactory.newId(baseElement.getId()));
                }

                private void populateFlowElement(final ObjectType object, final TFlowElement flowElement) {
                    populateBaseElement(object, flowElement);
                    object.setName(flowElement.getName());
                }

                // ResourceType supertype handlers

                private void populateBaseElement(final ResourceTypeType resourceType, final TBaseElement baseElement) {
                    resourceType.setId(cpfIdFactory.newId(baseElement.getId()));
                    resourceType.setOriginalID(baseElement.getId());
                }
            });
        }

        unwindLaneMap(cpfIdFactory, laneMap, bpmnFlowNodeToCpfNodeMap);
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
                                 final CanonicalProcessType  cpf,
                                 final IdFactory             cpfIdFactory,
                                 final Map<TFlowNode, TLane> laneMap) {

        for (TLaneSet laneSet : laneSets) {

            // Create a pool
            ResourceTypeType poolResourceType = new ResourceTypeType();
            poolResourceType.setId(cpfIdFactory.newId(participant.getId()));
            poolResourceType.setName(requiredName(participant.getName()));
            cpf.getResourceType().add(poolResourceType);

            // Create the lanes within the pool
            poolResourceType.getSpecializationIds().addAll(addLanes(laneSet, cpf, cpfIdFactory, laneMap));
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
                                        final CanonicalProcessType  cpf,
                                        final IdFactory             cpfIdFactory,
                                        final Map<TFlowNode, TLane> laneMap) {

        Set<String> specializationIds = new HashSet<String>();  // TODO - diamond operator

        for (TLane lane : laneSet.getLane()) {
            ResourceTypeType laneResourceType = new ResourceTypeType();

            // Add the resource type to the CPF model
            laneResourceType.setId(cpfIdFactory.newId(lane.getId()));
            laneResourceType.setName(requiredName(lane.getName()));
            specializationIds.add(laneResourceType.getId());
            cpf.getResourceType().add(laneResourceType);

            // Populate laneMap so we'll know later on which lane each element belongs to
            List list = lane.getFlowNodeRef();
            for (Object object : list) {
                JAXBElement je = (JAXBElement) object;
                Object value = je.getValue();
                TFlowNode flowNode = (TFlowNode) value;
                laneMap.put(flowNode, lane);
            }

            // recurse on any child lane sets
            if (lane.getChildLaneSet() != null) {
                laneResourceType.getSpecializationIds().addAll(addLanes(lane.getChildLaneSet(), cpf, cpfIdFactory, laneMap));
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
    private static void unwindLaneMap(final IdFactory cpfIdFactory,
                                      final Map<TFlowNode, TLane> laneMap,
                                      final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap) throws CanoniserException {

        for (Map.Entry<TFlowNode, TLane> entry : laneMap.entrySet()) {
            if (!bpmnFlowNodeToCpfNodeMap.containsKey(entry.getKey())) {
                throw new CanoniserException("Lane " + entry.getValue().getId() + " contains " +
                                             entry.getKey().getId() + " which is not present");
            }
            NodeType node = bpmnFlowNodeToCpfNodeMap.get(entry.getKey());  // get the CPF node corresponding to the BPMN flow node
            if (node instanceof WorkType) {
                ResourceTypeRefType resourceTypeRef = new ResourceTypeRefType();

                resourceTypeRef.setId(cpfIdFactory.newId(null));
                //resourceTypeRef.setOptional(false);  // redundant, since false is the default
                resourceTypeRef.setQualifier(null);
                resourceTypeRef.setResourceTypeId(entry.getValue().getId());

                ((WorkType) node).getResourceTypeRef().add(resourceTypeRef);
            }
        }
    }
}
