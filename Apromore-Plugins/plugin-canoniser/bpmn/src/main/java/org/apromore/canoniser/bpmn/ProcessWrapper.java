package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.cpf.*;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.*;
import org.omg.spec.bpmn._20100524.model.*;

/**
 * This class fakes the common superclass that {@link TProcess} and {@link TSubProcess} should've had.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class ProcessWrapper {

    private final String id;
    private final List<JAXBElement<? extends TArtifact>> artifact;
    private final List<JAXBElement<? extends TFlowElement>> flowElement;
    private final List<TLaneSet> laneSet;

    // Constructors

    /**
     * Wrap a {@link TProcess}.
     *
     * @param process  wrapped instance
     */
    public ProcessWrapper(final TProcess process) {
        id          = process.getId();
        artifact    = process.getArtifact();
        flowElement = process.getFlowElement();
        laneSet     = process.getLaneSet();
    }

    /**
     * Wrap a {@link TSubProcess}.
     *
     * @param subprocess  wrapped instance
     * @param processId  identifier to be used for the implicit process within the subprocess
     */
    public ProcessWrapper(final TSubProcess subprocess, final String processId) {
        id          = processId;
        artifact    = subprocess.getArtifact();
        flowElement = subprocess.getFlowElement();
        laneSet     = subprocess.getLaneSet();
    }

    // Accessor methods

    /** @return <code>id</code> property */
    public String getId() { return id; }

    /** @return <code>artifact</code> property */
    public List<JAXBElement<? extends TArtifact>> getArtifact() { return artifact; }

    /** @return <code>flowElement</code> property */
    public List<JAXBElement<? extends TFlowElement>> getFlowElement() { return flowElement; }

    /** @return <code>laneSet</code> property */
    public List<TLaneSet> getLaneSet() { return laneSet; }

    // Constructor methods used by subclasses

   /**
     * Recursively populate a BPMN {@link TLane}'s child lanes.
     *
     * TODO - circular resource type chains cause non-termination!  Need to check for and prevent this.
     */
    private static void addChildLanes(final TLane parentLane,
                               final List<ResourceTypeType> resourceTypeList,
                               final IdFactory bpmnIdFactory,
                               final Map<String, TBaseElement> idMap) {

        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : resourceTypeList) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().contains(parentLane.getId())) {
                TLane childLane = new TLane();
                childLane.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
                idMap.put(cpfResourceType.getId(), childLane);
                addChildLanes(childLane, resourceTypeList, bpmnIdFactory, idMap);
                laneSet.getLane().add(childLane);
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            parentLane.setChildLaneSet(laneSet);
        }
    }

    /**
     * Translate a CPF {@link NodeType} into a BPMN {@link TFlowNode}.
     *
     * @param node  a CPF node
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param factory  the created object will have come from this factory
     * @return a {@link TFlowElement} instance, wrapped in a {@link JAXBElement}
     * @throws CanoniserException if <var>node</var> isn't an event or a task
     */
    private static JAXBElement<? extends TFlowNode> createFlowNode(final NodeType node,
                                                            final CanonicalProcessType cpf,
                                                            final IdFactory bpmnIdFactory,
                                                            final Map<String, TBaseElement> idMap,
                                                            final BpmnObjectFactory factory,
                                                            final Map<String, TSequenceFlow> edgeMap,
                                                            final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                                                            final Map<String, TSequenceFlow> flowWithoutTargetRefMap) throws CanoniserException {

        if (node instanceof EventType) {
            // Count the incoming and outgoing edges to determine whether this is a start, end, or intermediate event
            CpfNodeType cpfNode = (CpfNodeType) node;
            if (cpfNode.getIncomingEdges().size() == 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // Assuming a StartEvent here, but could be TBoundaryEvent too
                TStartEvent event = new TStartEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createStartEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() == 0) {
                TEndEvent event = new TEndEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createEndEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // Assuming all intermediate events are ThrowEvents
                TIntermediateThrowEvent event = new TIntermediateThrowEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createIntermediateThrowEvent(event);
            } else {
                throw new CanoniserException("Event \"" + node.getId() + "\" has no edges");
            }
        } else if (node instanceof TaskType) {
            CpfTaskType that = (CpfTaskType) node;

            if (that.getCalledElement() != null) {  // This CPF Task is a BPMN CallActivity
                return factory.createCallActivity(new BpmnCallActivity(that, bpmnIdFactory, idMap));
            } else if (that.getSubnetId() != null) {  // This CPF Task is a BPMN SubProcess
                return factory.createSubProcess(new BpmnSubProcess(that, cpf, factory, bpmnIdFactory, idMap, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap));
            } else {  // This CPF Task is a BPMN Task
                return factory.createTask(new BpmnTask(that, bpmnIdFactory, idMap));
            }
        } else {
            throw new CanoniserException("Node " + node.getId() + " type not supported: " + node.getClass().getCanonicalName());
        }
    }

    /**
     * Add the lanes, nodes and so forth to a {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the {@link TProcess} or {@link TSubProcess} to be populated
     */
    public static void populateProcess(final ProcessWrapper process,
                                       final NetType net,
                                       final CanonicalProcessType cpf,
                                       final BpmnObjectFactory factory,
                                       final IdFactory bpmnIdFactory,
                                       final Map<String, TBaseElement> idMap,
                                       final Map<String, TSequenceFlow> edgeMap,
                                       final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                                       final Map<String, TSequenceFlow> flowWithoutTargetRefMap) throws CanoniserException {

        // Add the CPF ResourceType lattice as a BPMN Lane hierarchy
        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : cpf.getResourceType()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().isEmpty()) {
                 TLane lane = new TLane();
                 lane.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
                 idMap.put(cpfResourceType.getId(), lane);
                 addChildLanes(lane, cpf.getResourceType(), bpmnIdFactory, idMap);
                 laneSet.getLane().add(lane);
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            process.getLaneSet().add(laneSet);
        }

        // Add the CPF Edges as BPMN SequenceFlows
        for (EdgeType edge : net.getEdge()) {
            TSequenceFlow sequenceFlow = new BpmnSequenceFlow(edge, bpmnIdFactory, idMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
            edgeMap.put(edge.getId(), sequenceFlow);
            process.getFlowElement().add(factory.createSequenceFlow(sequenceFlow));
        }

        // Add the CPF Nodes as BPMN FlowNodes
        for (NodeType node : net.getNode()) {
            JAXBElement<? extends TFlowNode> flowNode =
                createFlowNode(node, cpf, bpmnIdFactory, idMap, factory, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
            process.getFlowElement().add(flowNode);

            // Fill any BPMN @sourceRef or @targetRef attributes referencing this node
            if (flowWithoutSourceRefMap.containsKey(node.getId())) {
                flowWithoutSourceRefMap.get(node.getId()).setSourceRef((TFlowNode) idMap.get(node.getId()));
                flowWithoutSourceRefMap.remove(node.getId());
           }
            if (flowWithoutTargetRefMap.containsKey(node.getId())) {
                flowWithoutTargetRefMap.get(node.getId()).setTargetRef((TFlowNode) idMap.get(node.getId()));
                flowWithoutTargetRefMap.remove(node.getId());
            }

            // Populate the lane flowNodeRefs
            if (node instanceof WorkType) {
                for (ResourceTypeRefType resourceTypeRef : ((WorkType) node).getResourceTypeRef()) {
                    TLane lane = (TLane) idMap.get(resourceTypeRef.getResourceTypeId());
                    JAXBElement<Object> jeo = (JAXBElement) flowNode;
                    lane.getFlowNodeRef().add((JAXBElement) flowNode);
                }
            }
        }
    }
}
