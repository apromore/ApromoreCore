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
     * Translate a CPF {@link NodeType} into a BPMN {@link TFlowNode}.
     *
     * @param node  a CPF node
     * @param initializer  BPMN document construction state
     * @return a {@link TFlowElement} instance, wrapped in a {@link JAXBElement}
     * @throws CanoniserException if <var>node</var> isn't an event or a task
     */
    private static JAXBElement<? extends TFlowNode> createFlowNode(final CpfNodeType node, final Initializer initializer) throws CanoniserException {

        if (node instanceof EventType) {
            // Count the incoming and outgoing edges to determine whether this is a start, end, or intermediate event
            CpfNodeType cpfNode = (CpfNodeType) node;
            if (cpfNode.getIncomingEdges().size() == 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // Assuming a StartEvent here, but could be TBoundaryEvent too
                TStartEvent event = new TStartEvent();
                initializer.populateBaseElement(event, node);
                return initializer.getFactory().createStartEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() == 0) {
                TEndEvent event = new TEndEvent();
                initializer.populateBaseElement(event, node);
                return initializer.getFactory().createEndEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // Assuming all intermediate events are ThrowEvents
                TIntermediateThrowEvent event = new TIntermediateThrowEvent();
                initializer.populateBaseElement(event, node);
                return initializer.getFactory().createIntermediateThrowEvent(event);
            } else {
                throw new CanoniserException("Event \"" + node.getId() + "\" has no edges");
            }
        } else if (node instanceof RoutingType) {
            if (node instanceof ANDJoinType || node instanceof ANDSplitType) {
                TParallelGateway gateway = new TParallelGateway();
                initializer.populateGateway(gateway, node);
                return initializer.getFactory().createParallelGateway(gateway);
            } else if (node instanceof ORJoinType || node instanceof ORSplitType) {
                TInclusiveGateway gateway = new TInclusiveGateway();
                initializer.populateGateway(gateway, node);
                return initializer.getFactory().createInclusiveGateway(gateway);
            } else if (node instanceof XORJoinType || node instanceof XORSplitType) {
                TExclusiveGateway gateway = new TExclusiveGateway();
                initializer.populateGateway(gateway, node);
                return initializer.getFactory().createExclusiveGateway(gateway);
            } else {
                throw new CanoniserException("Routing \"" + node.getId() + " is not a supported type");
            }
        } else if (node instanceof TaskType) {
            CpfTaskType that = (CpfTaskType) node;

            if (that.getCalledElement() != null) {  // This CPF Task is a BPMN CallActivity
                return initializer.getFactory().createCallActivity(new BpmnCallActivity(that, initializer));
            } else if (that.getSubnetId() != null) {  // This CPF Task is a BPMN SubProcess
                return initializer.getFactory().createSubProcess(new BpmnSubProcess(that, initializer));
            } else {  // This CPF Task is a BPMN Task
                return initializer.getFactory().createTask(new BpmnTask(that, initializer));
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
    // TODO - EITHER - make this an instance method of ProcessWrapper, replacing the "process" parameter
    //      - OR     - make this an instance method of Initializer, replacing the "initializer" parameter
    public static void populateProcess(final ProcessWrapper process,
                                       final NetType net,
                                       final Initializer initializer) throws CanoniserException {

        // Add the CPF ResourceType lattice as a BPMN Lane hierarchy
        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : initializer.getResourceTypes()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().isEmpty()) {
                 laneSet.getLane().add(new BpmnLane(cpfResourceType, initializer));
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            process.getLaneSet().add(laneSet);
        }

        // Add the CPF Edges as BPMN SequenceFlows
        for (EdgeType edge : net.getEdge()) {
            TSequenceFlow sequenceFlow = new BpmnSequenceFlow((CpfEdgeType) edge, initializer);
            process.getFlowElement().add(initializer.getFactory().createSequenceFlow(sequenceFlow));
        }

        // Add the CPF Objects as BPMN DataObjects
        for (ObjectType object : net.getObject()) {
            TDataObject dataObject = new BpmnDataObject((CpfObjectType) object, initializer);
            process.getFlowElement().add(initializer.getFactory().createDataObject(dataObject));
        }

        // Add the CPF Nodes as BPMN FlowNodes
        for (NodeType node : net.getNode()) {
            JAXBElement<? extends TFlowNode> flowNode = createFlowNode((CpfNodeType) node, initializer);
            process.getFlowElement().add(flowNode);

            if (node instanceof WorkType) {

                // Populate the lane flowNodeRefs
                for (ResourceTypeRefType resourceTypeRef : ((WorkType) node).getResourceTypeRef()) {
                    TLane lane = (TLane) initializer.findElement(resourceTypeRef.getResourceTypeId());
                    JAXBElement<Object> jeo = (JAXBElement) flowNode;
                    lane.getFlowNodeRef().add((JAXBElement) flowNode);
                }
            }
        }
    }
}
