package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

// Local packages
import org.apromore.canoniser.bpmn.AbstractInitializer;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.*;

/**
 * This class is a clunky way of doing the work that <code>super</code> calls normally would in the constructors of the CPF elements.
 * The CPF extension classes in {@link org.apromore.canoniser.bpmn.cpf} can't inherit from one another since they each must extend
 * from the corresponding classes in {@link org.apromore.cpf}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class Initializer extends AbstractInitializer implements ExtensionConstants {

    /**
     * Whether or not to record the original ID attribute.
     *
     * The persistence layer of Apromore uses originalID for its own internal IDs, so generally no point in setting it.
     */
    static final boolean RECORD_ORIGINAL_ID = false;

    /** The instance executing the {@link CpfCanonicalProcessType#(BpmnDefinitions)} constructor with this {@link Initializer}. */
    private final CpfCanonicalProcessType  cpf;

    /** Generator of unique CPF identifiers for this document. */
    private final IdFactory cpfIdFactory = new IdFactory();

    /** The BPMN document which the constructed CPF document corresponds to. */
    private final BpmnDefinitions definitions;

    /** Which CPF element each BPMN element corresponds to. */
    private final Map<TBaseElement, Object> bpmnElementToCpfElementMap = new HashMap<TBaseElement, Object>();

    /** Map from CPF identifiers to CPF elements.  Note that CPF lacks a root class, hence the use of {@link Object}. */
    private final Map<String, Object> elementMap;

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

    /** @param net  new Net to be added to the top level of the CPF document */
    void addNet(final CpfNetType net) {
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

        assert id != null : "Null QName passed";
        assert definitions != null : "Null BPMN definitions while seeking " + id;
        assert definitions.getTargetNamespace() != null : "Null target namespace while seeking " + id;

        // Make sure the id is valid for dereferencing within the local namespace
        if (!"".equals(id.getPrefix()) && !definitions.getTargetNamespace().equals(id.getNamespaceURI())) {
            throw new CanoniserException(id + " with prefix \"" + id.getPrefix() + "\" is not in local namespace " +
                                         definitions.getTargetNamespace());
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
     * @param node  a CPF node
     * @return the CPF Net which the <code>node</code> belongs to
     */
    public CpfNetType findParent(final CpfNodeType node) {
        for (NetType net : cpf.getNet()) {
            if (net.getNode().contains(node)) {
                return (CpfNetType) net;
            }
        }

        // Didn't find a parent
        return null;
    }

    /**
     * @param id  a requested identifier (typically the ID of the corresponding BPMN element); may be <code>null</code>
     * @return an identifier unique within the CPF document
     */
    String newId(final String id) {
        return cpfIdFactory.newId(id);
    }

    // Edge supertype handlers

    void populateBaseElement(final EdgeType edge, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, edge);

        // Handle @id
        edge.setId(cpfIdFactory.newId(baseElement.getId()));
        elementMap.put(edge.getId(), edge);

        if (RECORD_ORIGINAL_ID) {
            edge.setOriginalID(baseElement.getId());
        }

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), edge);
        }
    }

    void populateFlowElement(final EdgeType edge, final TFlowElement flowElement) throws CanoniserException {
        populateBaseElement(edge, flowElement);
    }

    // Node supertype handlers

    void populateBaseElement(final NodeType node, final TBaseElement baseElement) throws CanoniserException {
        bpmnElementToCpfElementMap.put(baseElement, node);

        // Handle @id
        node.setId(cpfIdFactory.newId(baseElement.getId()));
        elementMap.put(node.getId(), node);

        if (RECORD_ORIGINAL_ID) {
            node.setOriginalID(baseElement.getId());
        }

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
        deferDefault(bpmnDefault);
    }

    // Work supertype handler

    void populateActivity(final WorkType work, final TActivity activity) throws CanoniserException {
        populateFlowNode(work, activity);

        // Handle dataInputAssociation
        for (TDataInputAssociation dataInputAssociation : activity.getDataInputAssociation()) {
            work.getObjectRef().add(new CpfObjectRefType(dataInputAssociation, activity, this));
        }

        // Handle dataOutputAssociation
        for (TDataOutputAssociation dataOutputAssociation : activity.getDataOutputAssociation()) {
            work.getObjectRef().add(new CpfObjectRefType(dataOutputAssociation, activity, this));
        }

        // Handle default
        deferDefault(activity.getDefault());

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

        if (RECORD_ORIGINAL_ID) {
            resourceType.setOriginalID(baseElement.getId());
        }

        // Handle BPMN extension elements
        if (baseElement.getExtensionElements() != null) {
            ExtensionUtils.addToExtensions(extensionElements(baseElement), resourceType);
        }
    }

    // Internal methods

    /** @param a BPMN sequence flow whose corresponding edge needs to be marked as a default flow */
    private void deferDefault(final TSequenceFlow defaultFlow) {
        if (defaultFlow != null) {
            defer(new Initialization() {
                public void initialize() {
                    CpfEdgeType edge = (CpfEdgeType) findElement(defaultFlow);
                    edge.setDefault(true);
                }
            });
        }
    }

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
                                              BpmnDefinitions.newContext()/*BPMN_CONTEXT*/);
    }
}
