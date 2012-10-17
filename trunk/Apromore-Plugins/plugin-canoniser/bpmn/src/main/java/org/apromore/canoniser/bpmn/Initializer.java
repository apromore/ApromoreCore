package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.Map;

// Local classes
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * Global state of BPMn document construction used within {@link #BpmnDefinition(CanonicalProcessType)}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class Initializer {

    // CPF document root
    final CanonicalProcessType cpf;

    // Generates all identifiers scoped to the BPMN document
    final IdFactory bpmnIdFactory = new IdFactory();

    // Used to wrap BPMN elements in JAXBElements
    final BpmnObjectFactory factory = new BpmnObjectFactory();

    // Map from CPF @cpfId node identifiers to BPMN ids
    final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();

    // Map from CPF @cpfId edge identifiers to BPMN ids
    final Map<String, TSequenceFlow> edgeMap = new HashMap<String, TSequenceFlow>();

    // Records the CPF cpfIds of BPMN sequence flows which need their @sourceRef populated
    final Map<String, TSequenceFlow> flowWithoutSourceRefMap = new HashMap<String, TSequenceFlow>();

    // Records the CPF cpfIds of BPMN sequence flows which need their @targetRef populated
    final Map<String, TSequenceFlow> flowWithoutTargetRefMap = new HashMap<String, TSequenceFlow>();

    /**
     * Sole constructor.
     *
     * @param newBpmnIdFactory;
     * @param newIdMap;
     */
    Initializer(final CanonicalProcessType newCpf) {
        cpf = newCpf;
    }

    /**
     * Initialize a BPMN element, based on the CPF node it corresponds to. 
     *
     * @param baseElement  the BPMN element e set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final NodeType cpfNode) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNode.getId()));
        idMap.put(cpfNode.getId(), baseElement);
    };

    /**
     * Initialize a BPMN element, based on the CPF resource it corresponds to. 
     *
     * @param baseElement  the BPMN element e set
     * @param cpfResourceType  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final ResourceTypeType cpfResourceType) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
        idMap.put(cpfResourceType.getId(), baseElement);
    };

    /**
     * Called at the end of {@link BpmnDefinitions#(CanonicalProcessType, AnotationsType)}.
     *
     * @throws CanoniserException if any undone tasks still remain for the BPMN document construction
     */
    void close() throws CanoniserException {
        // Make sure all the deferred fields did eventually get filled in
        if (!flowWithoutSourceRefMap.isEmpty()) {
            throw new CanoniserException("Missing source references: " + flowWithoutSourceRefMap.keySet());
        }
        if (!flowWithoutTargetRefMap.isEmpty()) {
            throw new CanoniserException("Missing target references: " + flowWithoutTargetRefMap.keySet());
        }
    }
}
