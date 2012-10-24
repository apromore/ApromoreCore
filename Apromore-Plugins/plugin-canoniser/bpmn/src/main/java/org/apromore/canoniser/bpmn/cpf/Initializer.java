package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;

// Local packages
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.*;

/**
 * This class is a clunky way of doing the work that <code>super</code> calls normally would in the constructors of the CPF elements.
 * The CPF extension classes in {@link org.apromore.canoniser.bpmn.cpf} can't inherit from one another since they each must extend
 * from the corresponding classes in {@link org.apromore.cpf}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Initializer {

    final CpfCanonicalProcessType  cpf;
    final IdFactory                cpfIdFactory             = new IdFactory();;
    final BpmnDefinitions          definitions;
    final Map<TFlowNode, TLane>    laneMap                  = new HashMap<TFlowNode, TLane>();
    final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap = new HashMap<TFlowNode, NodeType>();

    /**
     * Sole constructor.
     *
     * @param newCpf  the instance being constructed
     * @param newDefinitions  the BPMN instance that <code>newCpf</code> will correspond to
     */
    public Initializer(final CpfCanonicalProcessType newCpf, final BpmnDefinitions newDefinitions) {
        cpf                      = newCpf;
        definitions              = newDefinitions;
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

    void populateActivity(final WorkType work, final TActivity activity) {
        populateFlowElement(work, activity);

        BigInteger completionQuantity = activity.getCompletionQuantity();
        List<TDataInputAssociation> dias = activity.getDataInputAssociation();
        List<TDataOutputAssociation> doas = activity.getDataOutputAssociation();
        Object defaultRef = activity.getDefault();
        TInputOutputSpecification ioSpec = activity.getIoSpecification();
        JAXBElement<? extends TLoopCharacteristics> loopChars = activity.getLoopCharacteristics();
        List<TProperty> props = activity.getProperty();
        List<JAXBElement<? extends TResourceRole>> resRoles = activity.getResourceRole();
        BigInteger startQuantity = activity.getStartQuantity();
        Boolean isForCompensation = activity.isIsForCompensation();
    }

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
