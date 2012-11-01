package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

// Local packages
import org.apromore.canoniser.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.*;

/**
 * This class is a clunky way of doing the work that <code>super</code> calls normally would in the constructors of the CPF elements.
 * The CPF extension classes in {@link org.apromore.canoniser.bpmn.cpf} can't inherit from one another since they each must extend
 * from the corresponding classes in {@link org.apromore.cpf}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Initializer implements ExtensionConstants {

    /** The instance executing the {@link CpfCanonicalProcessType#(BpmnDefinitions)} constructor with this {@link Initializer}. */
    private final CpfCanonicalProcessType  cpf;

    /** Generator of unique CPF identifiers for this document. */
    private final IdFactory cpfIdFactory = new IdFactory();

    /** The BPMN document which the constructed CPF document corresponds to. */
    private final BpmnDefinitions definitions;

    /** Which lane each BPMN flow node is assigned to. */
    private final Map<TFlowNode, TLane> laneMap = new HashMap<TFlowNode, TLane>();

    /** Which CPF element each BPMN element corresponds to. */
    private final Map<TBaseElement, Object> bpmnElementToCpfElementMap = new HashMap<TBaseElement, Object>();

    /** Map from CPF identifiers to CPF elements.  Note that CPF lacks a root class, hence the use of {@link Object}. */
    private final Map<String, Object> elementMap;

    /**
     * Map from BPMN sequence flows to the CPF identifier of the element for which the corresponding Edge should be default.
     *
     * In BPMN 2.0 the element types with a "default" attribute comprise {@link TActivity}, {@link TComplexGateway},
     * {@link TExclusiveGateway} and {@link TInclusiveGateway}.
     */
    private final Map<TSequenceFlow, String> defaultSequenceFlowMap = new HashMap<TSequenceFlow, String>();

    /**
     * Sole constructor.
     *
     * @param newCpf  the instance being constructed
     * @param newDefinitions  the BPMN instance that <code>newCpf</code> will correspond to
     * @param newElementMap  the constructing instance's <code>elementMap</code> field
     */
    public Initializer(final CpfCanonicalProcessType newCpf,
                       final BpmnDefinitions         newDefinitions,
                       final Map<String, Object>     newElementMap) {
        cpf         = newCpf;
        definitions = newDefinitions;
        elementMap  = newElementMap;
    }

    /**
     * Called once the BPMN document has been traversed, to fill in assorted deferred initializations.
     * <ul>
     * <li>Take the {@link #laneMap} populated by {@link #addLaneSet} and use it to populate the CPF nodes' {@link NodeType#resourceTypeRef}s.</li>
     * </ul>
     *
     * @throws CanoniserException  if any member of a BPMN lane lacks a CPF counterpart
     */
    void close() throws CanoniserException {

        // For each BPMN Lane flowNodeRef, add a CPF ResourceTypeRef to the corresponding CPD element
        for (Map.Entry<TFlowNode, TLane> entry : laneMap.entrySet()) {
            if (!bpmnElementToCpfElementMap.containsKey(entry.getKey())) {
                throw new CanoniserException("Lane " + entry.getValue().getId() + " contains " +
                                             entry.getKey().getId() + " which is not present");
            }
            NodeType node = (NodeType) bpmnElementToCpfElementMap.get(entry.getKey());  // get the CPF node corresponding to the BPMN flow node
            if (node instanceof WorkType) {
                ((WorkType) node).getResourceTypeRef().add(new CpfResourceTypeRefType(entry.getValue(), this));
            }
        }

        // Verify that all default edges have been dealt with
        if (!defaultSequenceFlowMap.isEmpty()) {
            throw new CanoniserException("Not all default edges were marked: " + defaultSequenceFlowMap);
        }
    }

    /** @param net  new Net to be added to the top level of the CPF document */
    void addNet(final NetType net) {
        cpf.getNet().add(net);
    }

    /** @param resourceType  new ResourceType to be added to the top level of the CPF document */
    void addResourceType(final ResourceTypeType resourceType) {
        cpf.getResourceType().add(resourceType);
    }

    /** @param rootId  new element for the CPF document's rootIds list */
    void addRootId(final String rootId) {
        cpf.getRootIds().add(rootId);
    }

    /** @return the root elements of the BPMN document */
    List<JAXBElement<? extends TRootElement>> getBpmnRootElements() {
        return definitions.getRootElement();
    }

    /**
     * @param id  a BPMN identifier, which will be forced into the BPMN document's target namespace if it has no prefix
     * @return the BPMN element with the given <code>id</code>, or <code>null</code> if no such element exists
     * @throws CanoniserException if the <code>id</code> isn't local to this document
     */
    TBaseElement findBpmnElement(final QName id) throws CanoniserException {

        // Make sure the id is valid for dereferencing within the local namespace
        if (!"".equals(id.getPrefix()) && !definitions.getTargetNamespace().equals(id.getNamespaceURI())) {
            throw new CanoniserException(id + " with prefix \"" + id.getPrefix() + "\" is not in local namespace " + definitions.getTargetNamespace());
        }

        return findBpmnElement(id.getLocalPart());
    }

    /**
     * @param id  a BPMN identifier
     * @return the BPMN element with the given <code>id</code>, or <code>null</code> if no such element exists
     *
     * @see {@link #getElement} for CPF elements
     */
    TBaseElement findBpmnElement(final String id) {
        return definitions.findElementById(id);
    }

    /** @return the target namespace of the BPMN document */
    /*
    String getBpmnTargetNamespace() {
        return definitions.getTargetNamespace();
    }
    */

    /**
     * @param id  a CPF identifier
     * @return the CPF element bearing the given identifier
     *
     * @see {@link #getElement} for BPMN elements
     */
    Object findElement(final String id) {
        return elementMap.get(id);
    }

    /**
     * @param bpmnElement  a BPMN element
     * @return the corresponding CPF element
     */
    Object findElement(final TBaseElement bpmnElement) {
        return bpmnElementToCpfElementMap.get(bpmnElement);
    }

    /**
     * @param id  a requested identifier (typically the ID of the corresponding BPMN element); may be <code>null</code>
     * @return an identifier unique within the CPF document
     */
    String newId(final String id) {
        return cpfIdFactory.newId(id);
    }

    /**
     * Record a flowNodeRef in a BPMN Lane.
     *
     * @param flowNode  the referenced node
     * @param lane  the containing lane
     */
    void recordLaneNode(final TFlowNode flowNode, final TLane lane) {
        laneMap.put(flowNode, lane);
    }

    // Edge supertype handlers

    void populateBaseElement(final EdgeType edge, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, edge);

        // Handle @id
        edge.setId(cpfIdFactory.newId(baseElement.getId()));
        edge.setOriginalID(baseElement.getId());
        elementMap.put(edge.getId(), edge);

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), edge);
        }
    }

    void populateFlowElement(final EdgeType edge, final TFlowElement flowElement) throws CanoniserException {
        populateBaseElement(edge, flowElement);
    }

    void populateSequenceFlow(final EdgeType edge, final TSequenceFlow sequenceFlow) throws CanoniserException {
        populateFlowElement(edge, sequenceFlow);

        // handle source
        NodeType source = (NodeType) bpmnElementToCpfElementMap.get(sequenceFlow.getSourceRef());
        edge.setSourceId(source.getId());
        ((CpfNodeType) source).getOutgoingEdges().add(edge);

        // handle target
        NodeType target = (NodeType) bpmnElementToCpfElementMap.get(sequenceFlow.getTargetRef());
        edge.setTargetId(target.getId());
        ((CpfNodeType) target).getIncomingEdges().add(edge);

        // handle default
        if (defaultSequenceFlowMap.containsKey(sequenceFlow)) {
            // Sanity checks that the source actually is the kind of element that has defaults
            String cpfId = defaultSequenceFlowMap.get(sequenceFlow);
            Object cpfElement = findElement(cpfId);
            assert sequenceFlow.getSourceRef() instanceof TActivity         ||
                   sequenceFlow.getSourceRef() instanceof TComplexGateway   ||
                   sequenceFlow.getSourceRef() instanceof TExclusiveGateway ||
                   sequenceFlow.getSourceRef() instanceof TInclusiveGateway :
                "Source of default BPMN flow " + sequenceFlow.getId() + " is " + sequenceFlow.getSourceRef().getId() + " which is not a defaulting type";
            assert cpfElement instanceof RoutingType || cpfElement instanceof TaskType :
                "Source of default CPF edge " + edge.getId() + " is " + cpfElement + " which is not a defaulting type";

            // Mark that this is a default flow, and remove it from the "to do" map
            edge.setDefault(true);
            defaultSequenceFlowMap.remove(sequenceFlow);
        }
    }

    // Node supertype handlers

    void populateBaseElement(final NodeType node, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, node);

        // Handle @id
        node.setId(cpfIdFactory.newId(baseElement.getId()));
        node.setOriginalID(baseElement.getId());
        elementMap.put(node.getId(), node);

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), node);
        }
    }

    void populateFlowElement(final NodeType node, final TFlowElement flowElement) throws CanoniserException {
        populateBaseElement(node, flowElement);
        node.setName(flowElement.getName());
    }

    // Routing supertype handler

    void populateFlowNode(final RoutingType routing, final TFlowNode flowNode) throws CanoniserException {
        populateFlowElement(routing, flowNode);
    }

    /**
     * Pretend there's a superclass for the 3 kinds of BPMN gateway with default flows, and that this is its constructor code.
     *
     * @param routing  the CPF Routing under construction
     * @param gateway  a BPMN {@link TComplexGateway}, {@link TExclusiveGateway}, or {@link TInclusiveGateway}
     * @param bpmnDefault  the default attribute of <code>gateway</code>
     */
    void populateDefaultingGateway(final RoutingType routing, final TGateway gateway, final TSequenceFlow bpmnDefault) throws CanoniserException {
        assert gateway instanceof TComplexGateway   ||
               gateway instanceof TExclusiveGateway ||
               gateway instanceof TInclusiveGateway : "Gateway " + gateway.getId() + " can't have a default flow";

        populateFlowNode(routing, gateway);

        // If the gateway has a default flow, record that fact
        if (bpmnDefault != null) {
            defaultSequenceFlowMap.put(bpmnDefault, routing.getId());
        }
    }

    // Work supertype handler

    void populateActivity(final WorkType work, final TActivity activity) throws CanoniserException {
        populateFlowNode(work, activity);

        // Handle dataInputAssociation
        for (TDataInputAssociation dia : activity.getDataInputAssociation()) {
        }

        // Handle dataOutputAssociation
        for (TDataOutputAssociation doa : activity.getDataOutputAssociation()) {
        }

        // Handle default
        if (activity.getDefault() != null) {
            defaultSequenceFlowMap.put(activity.getDefault(), work.getId());
        }

        TInputOutputSpecification ioSpec = activity.getIoSpecification();

        /*
        if (activity.getCompletionQuantity() != null) {
            throw new CanoniserException("BPMN completion quantity on " + activity.getId() + " not supported");
        }

        if (activity.getLoopCharacteristics() != null) {
            throw new CanoniserException("BPMN loop characteristics on " + activity.getId() + " not supported");
        }

        if (!activity.getProperty().isEmpty()) {
            throw new CanoniserException("BPMN properties on " + activity.getId() + " not supported");
        }

        if (!activity.getResourceRole().isEmpty()) {
            throw new CanoniserException("BPMN resource roles on " + activity.getId() + " not supported");
        }

        if (activity.getStartQuantity() != null) {
            throw new CanoniserException("BPMN start quantity on " + activity.getId() + " not supported");
        }

        if (activity.isIsForCompensation()) {
            throw new CanoniserException("BPMN compensation on " + activity.getId() + " not supported");
        }
        */
    }

    void populateFlowNode(final WorkType work, final TFlowNode flowNode) throws CanoniserException {
        populateFlowElement(work, flowNode);
    }

    // Object supertype handlers

    void populateBaseElement(final ObjectType object, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, object);

        // Handle @id
        object.setId(cpfIdFactory.newId(baseElement.getId()));
        elementMap.put(object.getId(), object);

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), object);
        }
    }

    void populateFlowElement(final ObjectType object, final TFlowElement flowElement) throws CanoniserException {
        populateBaseElement(object, flowElement);

        // An oddity of CPF is that no two Objects belonging to the same Net may have the same name
        String name = flowElement.getName();
        while (((CpfObjectType) object).getNet().getObjectNames().contains(name)) {
            name = name + "'";
        }
        object.setName(name);
        ((CpfObjectType) object).getNet().getObjectNames().add(name);

        // For the sake of round-tripping, if we've changed the name to avoid clashes, record the original name as an extension attribute
        if (!name.equals(flowElement.getName())) {
            ((CpfObjectType) object).setOriginalName(flowElement.getName());
        }
    }

    // ObjectRef supertype handlers

    void populateBaseElement(final ObjectRefType objectRef, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, objectRef);

        // Handle @id
        objectRef.setId(cpfIdFactory.newId(baseElement.getId()));
        elementMap.put(objectRef.getId(), objectRef);

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), objectRef);
        }
    }

    // ResourceType supertype handlers

    void populateBaseElement(final ResourceTypeType resourceType, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, resourceType);

        // Handle @id
        resourceType.setId(cpfIdFactory.newId(baseElement.getId()));
        resourceType.setOriginalID(baseElement.getId());

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), resourceType);
        }
    }

    // Internal methods

    /**
     * @param baseElement  a BPMN element with a <code>extensionElements</code> subelement
     * @return the <code>extensionElements</code> of the BPMN element
     * @throws CanoniserException  if the conversion cannot be performed
     */
    private Element extensionElements(final TBaseElement baseElement) throws CanoniserException {
        return ExtensionUtils.marshalFragment(EXTENSION_ELEMENTS,
                                              baseElement.getExtensionElements(),
                                              TExtensionElements.class,
                                              BPMN_CPF_NS,
                                              BpmnDefinitions.BPMN_CONTEXT);
    }
}
