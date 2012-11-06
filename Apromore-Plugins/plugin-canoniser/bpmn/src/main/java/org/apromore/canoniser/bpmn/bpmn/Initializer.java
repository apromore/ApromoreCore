package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Local classes
import org.apromore.canoniser.bpmn.AbstractInitializer;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.Attributed;
import org.apromore.canoniser.bpmn.cpf.CpfEdgeType;
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.bpmn.cpf.CpfNodeType;
import org.apromore.canoniser.bpmn.cpf.CpfObjectType;
import org.apromore.canoniser.bpmn.cpf.CpfObjectRefType;
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.canoniser.bpmn.cpf.ExtensionConstants;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.anf.AnnotationType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TAuditing;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TComplexGateway;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TGatewayDirection;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TMonitoring;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.dd._20100524.di.DiagramElement;

/**
 * Global state of BPMn document construction used within {@link #BpmnDefinition(CanonicalProcessType)}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class Initializer extends AbstractInitializer implements ExtensionConstants {

    // CPF document root
    private final CanonicalProcessType cpf;

    // Generates all identifiers scoped to the BPMN document
    private final IdFactory bpmnIdFactory = new IdFactory();

    // Used to wrap BPMN elements in JAXBElements
    private final BpmnObjectFactory factory = new BpmnObjectFactory();

    // Map from CPF @cpfId node identifiers to BPMN elements
    private final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();

    private final String targetNamespace;

    /**
     * Sole constructor.
     *
     * @param newBpmnIdFactory;
     * @param newIdMap;
     */
    Initializer(final CanonicalProcessType newCpf, final String newTargetNamespace) {
        cpf             = newCpf;
        targetNamespace = newTargetNamespace;
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    public CpfNetType findNet(final String id) throws CanoniserException {

        for (final NetType net : cpf.getNet()) {
            if (id.equals(net.getId())) {
                return (CpfNetType) net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }

    /**
     * @param cpfId  a CPF identifier
     * @return the BPMN element corresponding to the identified CPF element
     */
    public TBaseElement findElement(final String cpfId) {
        return idMap.get(cpfId);
    }

    /** @return shared {@link BpmnObjectFactory} instance */
    BpmnObjectFactory getFactory() {
        return factory;
    }

    /** @return the CPF ResourceTypes */
    List<ResourceTypeType> getResourceTypes() {
        return cpf.getResourceType();
    }

    /**
     * @return the target namespace of the BPMN document under construction
     */
    String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * @param id  requested identifier (typically the identifier of the corresponding CPF element); may be <code>null</code>
     * @return an indentifier unique within the BPMN document
     */
    String newId(final String id) {
        return bpmnIdFactory.newId(id);
    }

    //
    // Pseudo-superclass initialization methods
    //

    // ...for CpfEdgeType

    void populateBaseElement(final TBaseElement baseElement, final CpfEdgeType cpfEdge) {

        // Handle @id attribute
        baseElement.setId(newId(cpfEdge.getId()));
        idMap.put(cpfEdge.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfEdge);
    }

    void populateFlowElement(final TFlowElement flowElement, final CpfEdgeType cpfEdge) {
        populateBaseElement(flowElement, cpfEdge);

        // TODO - handle @name attribute as an extension
        String name = flowElement.getName();

        // TODO - handle the following attributes, which are in the standard but not used in practice
        TAuditing auditing = flowElement.getAuditing();
        List<QName> categoryValueRef = flowElement.getCategoryValueRef();
        TMonitoring monitoring = flowElement.getMonitoring();
    }

    // ...for CpfNetType

    /**
     * Initialize a BPMN element, based on the CPF net it corresponds to.
     * Only BPMN Processes ever corresponds to a CPF Net, but we're only interested in their base properties here.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TProcess baseElement, final CpfNetType cpfNet) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNet.getId()));
        idMap.put(cpfNet.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfNet);
    };

    /**
     * Initialize the content of a wrapped BPMN {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the wrapped {@link TProcess} or {@link TSubProcess} to be populated
     * @param net  the CPF net which the <code>process</code> corresponds to
     * @throws CanoniserException if anything goes wrong
     */
    void populateProcess(final ProcessWrapper process, final CpfNetType net) throws CanoniserException {
        ProcessWrapper.populateProcess(process, net, this);
    }

    // ...for CpfNodeType

    /**
     * Initialize a BPMN element, based on the CPF node it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfNodeType cpfNode) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNode.getId()));
        idMap.put(cpfNode.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfNode);
    };

    void populateFlowElement(final TFlowElement flowElement, final CpfNodeType cpfNode) {
        populateBaseElement(flowElement, cpfNode);

        // Handle @name
        flowElement.setName(cpfNode.getName());
    }

    void populateFlowNode(final TFlowNode flowNode, final CpfNodeType cpfNode) throws CanoniserException {
        populateFlowElement(flowNode, cpfNode);

        // TODO - handle incoming and outgoing

        // Some flow nodes may have a default sequence flow
        int defaultEdgeCount = 0;
        for (final EdgeType edge : cpfNode.getOutgoingEdges()) {
            if (edge.isDefault()) {

               // No element should have more than one default edge
               if (++defaultEdgeCount > 1) {
                   throw new CanoniserException("CPF node " + cpfNode.getId() + " has " + defaultEdgeCount + " default edges");
                }

                // The edge will be flagged as default after all elements have been created (it might not exist yet)
                defer(new Initialization() {
                    public void initialize() throws CanoniserException {
                        assert edge.getSourceId() != null;

                        TBaseElement  defaultingElement = idMap.get(edge.getSourceId());
                        TSequenceFlow defaultFlow       = (TSequenceFlow) idMap.get(edge.getId());

                        assert defaultingElement != null : "Could not find BPMN element corresponding to source of default CPF edge " + edge.getId();
                        assert defaultFlow       != null : "Could not find BPMN flow corresponding to default CPF edge " + edge.getId();

                        if (defaultingElement instanceof TActivity) {
                            ((TActivity) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TComplexGateway) {
                            ((TComplexGateway) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TExclusiveGateway) {
                            ((TExclusiveGateway) defaultingElement).setDefault(defaultFlow);
                        } else if (defaultingElement instanceof TInclusiveGateway) {
                            ((TInclusiveGateway) defaultingElement).setDefault(defaultFlow);
                        } else {
                            throw new CanoniserException("Could not set default sequence flow for " + defaultingElement.getId());
                        }
                    }
                });
            }
        }
    }

    void populateActivity(final TActivity activity, final CpfNodeType cpfNode) throws CanoniserException {
        populateFlowNode(activity, cpfNode);

        // Create data associations
        assert cpfNode instanceof WorkType;  // TODO - add a CpfWorkType interface matching WorkType
        for (ObjectRefType objectRef : ((WorkType) cpfNode).getObjectRef()) {
            CpfObjectRefType cpfObjectRef = (CpfObjectRefType) objectRef;

            switch (cpfObjectRef.getType()) {
            case INPUT:  activity.getDataInputAssociation().add(new BpmnDataInputAssociation(cpfObjectRef, activity, this));   break;
            case OUTPUT: activity.getDataOutputAssociation().add(new BpmnDataOutputAssociation(cpfObjectRef, activity, this)); break;
            default:     assert false : "CPF ObjectRef " + cpfObjectRef.getId() + " has unsupported type " + cpfObjectRef.getType();
            }
        }
    }

    void populateGateway(final TGateway gateway, final CpfNodeType cpfNode) throws CanoniserException {
        assert cpfNode instanceof RoutingType : "Tried to populate " + cpfNode.getId() + " as if it was a gateway";
        populateFlowNode(gateway, cpfNode);

        // Handle gatewayDirection
        int ins  = cpfNode.getIncomingEdges().size();
        int outs = cpfNode.getOutgoingEdges().size();
        if (ins > 1 && outs <= 1) {
            gateway.setGatewayDirection(TGatewayDirection.CONVERGING);
        } else if (ins <= 1 && outs > 1) {
            gateway.setGatewayDirection(TGatewayDirection.DIVERGING);
        } else if (ins > 1 && outs > 1) {
            gateway.setGatewayDirection(TGatewayDirection.MIXED);
        } else {
            gateway.setGatewayDirection(TGatewayDirection.UNSPECIFIED);
        }
    }

    // ...for CpfObjectType

    /**
     * Initialize a BPMN element, based on the CPF object it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfObjectType cpfObject) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfObject.getId()));
        idMap.put(cpfObject.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfObject);
    };

    /**
     * Initialize a BPMN flow element, based on the CPF object it corresponds to.
     *
     * @param flowElement  the BPMN flow element to set
     * @param cpfNode  the CPF object which <code>flowElement</code> corresponds to
     */
    void populateFlowElement(final TFlowElement flowElement, final CpfObjectType cpfObject) {
        populateBaseElement(flowElement, cpfObject);

        // Handle @name attribute
        flowElement.setName(cpfObject.getName());

        // TODO - handle the following attributes, which are in the standard but not used in practice
        flowElement.setAuditing(null);
        flowElement.getCategoryValueRef();  // .add((QName) ...);
        flowElement.setMonitoring(null);
    };

    // ...for ObjectRefType

    /**
     * Initialize a BPMN element, based on the CPF ObjectRef it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param objectRef  the CPF ObjectRef which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfObjectRefType cpfObjectRef) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfObjectRef.getId()));
        idMap.put(cpfObjectRef.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfObjectRef);
    };

    // ...for CpfResourceTypeType

    /**
     * Initialize a BPMN element, based on the CPF resource it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfResourceType  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfResourceTypeType cpfResourceType) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
        idMap.put(cpfResourceType.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfResourceType);
    };

    // ...for ANF AnnotationType

    /**
     * @param graphics  the ANF annotation
     */
    void populateDiagramElement(final DiagramElement diagramElement, final AnnotationType annotation) {

        // Handle @id attribute
        diagramElement.setId(newId(annotation.getId()));
    }

    // Internal methods

    /**
     * Translate a CPF attribute list to the BPMN BaseElement's extensionElements.
     *
     * @param attributes  the attribute list of the source CPF element
     * @param baseElement  the destination BPMN element
     */
    private void populateBaseElementExtensionElements(final TBaseElement baseElement, final Attributed cpfElement) {

        final String name = BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS;

        List<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
        for (TypeAttribute attr : cpfElement.getAttribute()) {
            if (name.equals(attr.getName())) {
                attributes.add(attr);
            }
        }

        if (attributes.size() > 0) {
            TExtensionElements extensionElements = baseElement.getExtensionElements();
            if (extensionElements == null) {
                extensionElements = factory.createTExtensionElements();
            }
            assert extensionElements != null;

            for (TypeAttribute attribute : attributes) {
                Element element = (Element) attribute.getAny();
                NodeList nodes = element.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    extensionElements.getAny().add(nodes.item(i));
                }
            }

            baseElement.setExtensionElements(extensionElements);
        }
    }
}
