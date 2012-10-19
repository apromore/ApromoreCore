package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Map;

// Local packages
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TLane;

/**
 * This class is a clunky way of doing the work that <code>super</code> calls normally would in the constructors of the CPF elements.
 * The CPF extension classes in {@link org.apromore.canoniser.bpmn.cpf} can't inherit from one another since they each must extend
 * from the corresponding classes in {@link org.apromore.cpf}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Initializer {

    final CpfCanonicalProcessType  cpf;
    final IdFactory                cpfIdFactory;
    final BpmnDefinitions          definitions;
    final Map<TFlowNode, TLane>    laneMap;
    final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap;

    /**
     * Sole constructor.
     *
     * @param newCpfIdFactory  generator for CPF identifier
     * @param newBpmnFlowNodeToCpfNodeMap  mapping from BPMN to CPF graph vertices
     */
    public Initializer(final CpfCanonicalProcessType  newCpf,
                       final IdFactory                newCpfIdFactory,
                       final BpmnDefinitions          newDefinitions,
                       final Map<TFlowNode, TLane>    newLaneMap,
                       final Map<TFlowNode, NodeType> newBpmnFlowNodeToCpfNodeMap) {

        cpf                      = newCpf;
        cpfIdFactory             = newCpfIdFactory;
        definitions              = newDefinitions;
        laneMap                  = newLaneMap;
        bpmnFlowNodeToCpfNodeMap = newBpmnFlowNodeToCpfNodeMap;
    }

    // Edge supertype handlers

    void populateBaseElement(final EdgeType edge, final TBaseElement baseElement) {
        edge.setId(cpfIdFactory.newId(baseElement.getId()));
        edge.setOriginalID(baseElement.getId());
    }

    void populateFlowElement(final EdgeType edge, final TFlowElement flowElement) {
        populateBaseElement(edge, flowElement);
    }

    // Node supertype handlers

    void populateBaseElement(final NodeType node, final TBaseElement baseElement) {
        node.setId(cpfIdFactory.newId(baseElement.getId()));
        node.setOriginalID(baseElement.getId());
    }

    void populateFlowElement(final NodeType node, final TFlowElement flowElement) {
        populateBaseElement(node, flowElement);
        node.setName(flowElement.getName());
    }

    // Work supertype handler

    void populateFlowNode(final WorkType work, final TFlowNode flowNode) {
        populateFlowElement(work, flowNode);
        bpmnFlowNodeToCpfNodeMap.put(flowNode, work);
    }

    // Object supertype handlers

    void populateBaseElement(final ObjectType object, final TBaseElement baseElement) {
        object.setId(cpfIdFactory.newId(baseElement.getId()));
    }

    void populateFlowElement(final ObjectType object, final TFlowElement flowElement) {
        populateBaseElement(object, flowElement);
        object.setName(flowElement.getName());
    }

    // ResourceType supertype handlers

    void populateBaseElement(final ResourceTypeType resourceType, final TBaseElement baseElement) {
        resourceType.setId(cpfIdFactory.newId(baseElement.getId()));
        resourceType.setOriginalID(baseElement.getId());
    }
}
