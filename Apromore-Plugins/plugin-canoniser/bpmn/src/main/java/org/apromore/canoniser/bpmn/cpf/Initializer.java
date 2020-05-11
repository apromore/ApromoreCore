/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

// Local packages
import com.processconfiguration.Configurable;
import org.apromore.canoniser.bpmn.AbstractInitializer;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.bpmn.BpmnObjectFactory;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TComplexGateway;
import org.omg.spec.bpmn._20100524.model.TDataInputAssociation;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TInputOutputSpecification;
import org.omg.spec.bpmn._20100524.model.TRootElement;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

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

    static final Logger LOGGER = Logger.getLogger(Initializer.class.getCanonicalName());

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
        populateExtensionElements(
            baseElement,
            (Attributed) edge,
            null,
            new UnaryFunction<Element>() { public void run(Element element) { ExtensionUtils.addToExtensions(element, edge, BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS); }}
        );
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
        populateExtensionElements(
            baseElement,
            (Attributed) node,
            new Runnable() { public void run() { node.setConfigurable(true); }},
            new UnaryFunction<Element>() { public void run(Element element) { ExtensionUtils.addToExtensions(element, node, BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS); }}
        );
    } 

    /**
     * Read the extension elements from a BPMN element, and use them to populate the corresponding CPF element.
     *
     * Some extension elements have explicit representations in CPF (currently, pc:configuration in BPMN becomes
     * the configurable property of CPF nodes/objects/resourceTypes).  The remainder are serialized as raw XML
     * so that they can be round-tripped without being interpreted.
     *
     * @param baseElement      the BPMN element which holds the extension elements
     * @param cpfElement       the CPF element to populate
     * @param setConfigurable  if the CPF element has a <var>configurable</var> property, pass a closure which
     *                         sets the <var>configurable</var> property true, otherwise pass <code>null</code>
     */
    void populateExtensionElements(final TBaseElement     baseElement,
                                   final Attributed       cpfElement,
                                   Runnable               setConfigurable,
                                   UnaryFunction<Element> addToExtensions) {
        
        // Skip out early if there aren't any BPMN extension elements
        if (baseElement.getExtensionElements() == null) { return; }

        // Iterate through the BPMN extension elements and remove any that have specific CPF representations
        List l = new ArrayList<>(baseElement.getExtensionElements().getAny());
        Iterator i = l.iterator(); 
        while (i.hasNext()) {
            Object o = i.next();

            // BPMN pc:configurable corresponds to the CPF configurable property
            if (setConfigurable != null && o instanceof Configurable) {
                setConfigurable.run();
                i.remove();
            }
            else if (o instanceof Element) {
                addToExtensions.run((Element) o);
                i.remove();
            }
        }

        // Shovel the remaining uninterpreted XML into a CPF attribute element
        try {
            Element element = ExtensionUtils.marshalFragment(EXTENSION_ELEMENTS,
                                                             baseElement.getExtensionElements(),
                                                             TExtensionElements.class,
                                                             BPMN_CPF_NS,
                                                             BpmnDefinitions.newContext());
            for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                org.w3c.dom.Node n = element.getChildNodes().item(j);
                if (n instanceof Element) {
                    addToExtensions.run((Element) n);
                }
            }
        }
        catch (CanoniserException e) {
            LOGGER.log(Level.WARNING, "Unable to marshal BPMN extension elements to CPF", e);
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

        // Handle ioSpecification
        TInputOutputSpecification ioSpec = activity.getIoSpecification();
        if (ioSpec != null) {
            TypeAttribute attribute = new TypeAttribute();
            attribute.setName("bpmn:ioSpecification");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Marshaller marshaller = BpmnDefinitions.newContext().createMarshaller();
                marshaller.setProperty("jaxb.fragment", true);
                BpmnObjectFactory factory = new BpmnObjectFactory();
                marshaller.marshal(factory.createIoSpecification(ioSpec), baos);
            } catch (JAXBException e) {
		e.printStackTrace();
                throw new CanoniserException("Failed to parse bpmn:ioSpecification " + ioSpec.getId(), e);
            }
            attribute.setValue(baos.toString());
            work.getAttribute().add(attribute);
        }

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
        populateExtensionElements(
            baseElement,
            (Attributed) object,
            new Runnable() { public void run() { object.setConfigurable(true); }},
            new UnaryFunction<Element>() { public void run(Element element) { ExtensionUtils.addToExtensions(element, object, BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS); }}
        );
    }

    void populateFlowElement(final ObjectType object, final TFlowElement flowElement) throws CanoniserException {
        populateBaseElement(object, flowElement);

        // An oddity of CPF is that no two Objects belonging to the same Net may have the same name
        String name = flowElement.getName();
        if (name == null) { name = ""; }  // Also, names are mandatory
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
        populateExtensionElements(
            baseElement,
            (Attributed) objectRef,
            null,
            new UnaryFunction<Element>() { public void run(Element element) { ExtensionUtils.addToExtensions(element, objectRef, BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS); }}
        );
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
        populateExtensionElements(
            baseElement,
            (Attributed) resourceType,
            new Runnable() { public void run() { resourceType.setConfigurable(true); }},
            new UnaryFunction<Element>() { public void run(Element element) { ExtensionUtils.addToExtensions(element, resourceType, BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS); }}
        );
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
}
