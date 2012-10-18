package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.List;
import javax.xml.bind.JAXBElement;

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
    private static void addChildLanes(final TLane parentLane, final Initializer initializer) {

        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : initializer.cpf.getResourceType()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().contains(parentLane.getId())) {
                TLane childLane = new TLane();
                initializer.populateBaseElement(childLane, cpfResourceType);
                addChildLanes(childLane, initializer);
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
     * @param initializer  BPMN document construction state
     * @return a {@link TFlowElement} instance, wrapped in a {@link JAXBElement}
     * @throws CanoniserException if <var>node</var> isn't an event or a task
     */
    private static JAXBElement<? extends TFlowNode> createFlowNode(final NodeType node, final Initializer initializer) throws CanoniserException {

        if (node instanceof EventType) {
            // Count the incoming and outgoing edges to determine whether this is a start, end, or intermediate event
            CpfNodeType cpfNode = (CpfNodeType) node;
            if (cpfNode.getIncomingEdges().size() == 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // Assuming a StartEvent here, but could be TBoundaryEvent too
                TStartEvent event = new TStartEvent();
                initializer.populateBaseElement(event, node);
                return initializer.factory.createStartEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() == 0) {
                TEndEvent event = new TEndEvent();
                initializer.populateBaseElement(event, node);
                return initializer.factory.createEndEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // Assuming all intermediate events are ThrowEvents
                TIntermediateThrowEvent event = new TIntermediateThrowEvent();
                initializer.populateBaseElement(event, node);
                return initializer.factory.createIntermediateThrowEvent(event);
            } else {
                throw new CanoniserException("Event \"" + node.getId() + "\" has no edges");
            }
        } else if (node instanceof TaskType) {
            CpfTaskType that = (CpfTaskType) node;

            if (that.getCalledElement() != null) {  // This CPF Task is a BPMN CallActivity
                return initializer.factory.createCallActivity(new BpmnCallActivity(that, initializer));
            } else if (that.getSubnetId() != null) {  // This CPF Task is a BPMN SubProcess
                return initializer.factory.createSubProcess(new BpmnSubProcess(that, initializer));
            } else {  // This CPF Task is a BPMN Task
                return initializer.factory.createTask(new BpmnTask(that, initializer));
            }
        } else {
            throw new CanoniserException("Node " + node.getId() + " type not supported: " + node.getClass().getCanonicalName());
        }
    }

    /**
     * Add the lanes, nodes and so forth to a {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the {@link TProcess} or {@link TSubProcess} to be populated
     * @param net  the CPF net which the <code>process</code> corresponds to
     * @param initializer  BPMN document construction state
     * @throws CanoniserException if the child elements can't be added
     */
    // TODO - make this an instance method of ProcessWrapper, replacing the "process" parameter
    public static void populateProcess(final ProcessWrapper process,
                                       final NetType net,
                                       final Initializer initializer) throws CanoniserException {

        // Add the CPF ResourceType lattice as a BPMN Lane hierarchy
        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : initializer.cpf.getResourceType()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().isEmpty()) {
                 TLane lane = new TLane();
                 initializer.populateBaseElement(lane, cpfResourceType);
                 addChildLanes(lane, initializer);
                 laneSet.getLane().add(lane);
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            process.getLaneSet().add(laneSet);
        }

        // Add the CPF Edges as BPMN SequenceFlows
        for (EdgeType edge : net.getEdge()) {
            TSequenceFlow sequenceFlow = new BpmnSequenceFlow(edge, initializer);
            initializer.edgeMap.put(edge.getId(), sequenceFlow);
            process.getFlowElement().add(initializer.factory.createSequenceFlow(sequenceFlow));
        }

        // Add the CPF Nodes as BPMN FlowNodes
        for (NodeType node : net.getNode()) {
            JAXBElement<? extends TFlowNode> flowNode = createFlowNode(node, initializer);
            process.getFlowElement().add(flowNode);

            // Fill any BPMN @sourceRef or @targetRef attributes referencing this node
            if (initializer.flowWithoutSourceRefMap.containsKey(node.getId())) {
                initializer.flowWithoutSourceRefMap.get(node.getId()).setSourceRef((TFlowNode) initializer.idMap.get(node.getId()));
                initializer.flowWithoutSourceRefMap.remove(node.getId());
           }
            if (initializer.flowWithoutTargetRefMap.containsKey(node.getId())) {
                initializer.flowWithoutTargetRefMap.get(node.getId()).setTargetRef((TFlowNode) initializer.idMap.get(node.getId()));
                initializer.flowWithoutTargetRefMap.remove(node.getId());
            }

            // Populate the lane flowNodeRefs
            if (node instanceof WorkType) {
                for (ResourceTypeRefType resourceTypeRef : ((WorkType) node).getResourceTypeRef()) {
                    TLane lane = (TLane) initializer.idMap.get(resourceTypeRef.getResourceTypeId());
                    JAXBElement<Object> jeo = (JAXBElement) flowNode;
                    lane.getFlowNodeRef().add((JAXBElement) flowNode);
                }
            }
        }
    }
}
