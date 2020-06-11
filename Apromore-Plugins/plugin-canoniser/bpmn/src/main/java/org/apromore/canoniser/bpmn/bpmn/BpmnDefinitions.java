/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packges

import com.processconfiguration.Variants;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.bpmn.Constants;
import org.apromore.canoniser.bpmn.JAXBConstants;
import org.apromore.canoniser.bpmn.cpf.CpfCanonicalProcessType;
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TComplexGateway;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEventBasedGateway;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TGatewayDirection;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.w3c.dom.Element;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// Local packages

/**
 * BPMN 2.0 object model with canonisation methods.
 * <p>
 * To canonise a BPMN document, unmarshal the XML into an object of this class, and pass it to the constructor of
 * {@link CpfCanonicalProcessType}.
 * <p>
 * To decanonise a canonical model into BPMN, invoke the constructor.
 * Only individual canonical models may be decanonised; there is no facility for generating a BPMN document containing
 * multiple top-level processes.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL", name = "definitions")
public class BpmnDefinitions extends TDefinitions implements Constants, JAXBConstants {

    /** Mapping from IDs to {@link TBaseElement}s within this document. */
    @XmlTransient
    private final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();  // TODO - use diamond operator

    /** JAXB context for BPMN. */
    //public static final JAXBContext BPMN_CONTEXT = newContext();

    /** XML schema for BPMN 2.0. */
    //private static final Schema BPMN_SCHEMA = getBpmnSchema();

    /**
     * The two halves of the XSLT transformation, occuring before and after the magic token "genid:TARGET".
     *
     * This is an ugly and desperate approach to the nigh-impossibility to modifying xmlns declarations via JAXP or XSLT.
     */
    private final String[] fixNamespacesXslt = getFixNamespacesXslt();

    /** Initialize {@link #fixNamespacesXslt}. */
    private String[] getFixNamespacesXslt() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(
                new StreamSource(getClass().getClassLoader().getResourceAsStream("xsd/fix-namespaces.xsl")),
                new StreamResult(baos)
            );
            String[] s = baos.toString().split("genid:TARGET");
            if (s.length != 2) {
                throw new RuntimeException("genid:TARGET does not occur exactly once within fix-namespaces.xsl");
            }
            return s;
            
        } catch (TransformerException e) {
            throw new RuntimeException("Couldn't parse fix-namespaces.xslt", e);
        }
    }

    /** @return BPMN 2.0 XML schema */
    private static Schema getBpmnSchema() {
        try {
            final ClassLoader loader = BpmnDefinitions.class.getClassLoader();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new JarLSResourceResolver());
            return schemaFactory.newSchema(new StreamSource(loader.getResourceAsStream(BPMN_XSD)));
        } catch (SAXException e) {
            throw new RuntimeException("Couldn't parse BPMN XML schema", e);
        }
    }

    /**
     * No-arg constructor.
     *
     * Required for JUnit to work.
     */
    public BpmnDefinitions() { }

    /**
     * Construct a BPMN model from a canonical model.
     * In other words, de-canonise a CPF/ANF model into a BPMN one.
     *
     * @param cpf  a canonical process model
     * @param anf  annotations for the canonical process model
     * @throws CanoniserException if unable to generate BPMN from the given CPF and ANF arguments
     */
    private BpmnDefinitions(final CpfCanonicalProcessType cpf, final AnnotationsType anf) throws CanoniserException {
        // We can get by without an ANF parameter, but we definitely need a CPF
        if (cpf == null) {
            throw new CanoniserException("Cannot create BPMN from null CPF");
        }

        Initializer initializer = new Initializer(this, cpf, "http://www.apromore.org/bpmn/" + UUID.randomUUID() + "#");

        // Set attributes of the document root
        setExporter(APROMORE_URI);
        setExporterVersion(APROMORE_VERSION);
        setExpressionLanguage(CPFSchema.EXPRESSION_LANGUAGE_XPATH);  // This is the default, so specifying it is redundant
        setId(null);
        setName(cpf.getName());
        setTargetNamespace(initializer.getTargetNamespace());
        setTypeLanguage(CPFSchema.TYPE_LANGUAGE_XSD);  // This is the default, so specifying it is redundant

        /* TODO - add as extension attributes
        String author = cpf.getAuthor();
        String creationDate = cpf.getCreationDate();
        String modificationDate = cpf.getModificationDate();
        */

        // Assume there will be pools, all of which belong to a single collaboration
        TCollaboration collaboration = initializer.getFactory().createTCollaboration();
        JAXBElement<TCollaboration> wrapperCollaboration = initializer.getFactory().createCollaboration(collaboration);

        // Workaround for the Apromore-core's failure to generate the CPF rootIds attribute
        final List<String> rootIds = cpf.getRootIds();
        final List<String> subnetIds = new ArrayList<String>();
        if (rootIds.size() == 0) {
            cpf.accept(new org.apromore.cpf.TraversingVisitor(new org.apromore.cpf.DepthFirstTraverserImpl(), new org.apromore.cpf.BaseVisitor() {
                @Override public void visit(final NetType net) { rootIds.add(net.getId()); }
                @Override public void visit(final TaskType task) { subnetIds.add(task.getSubnetId()); }  // null check not required
            }));
            rootIds.removeAll(subnetIds);
            initializer.warn("Using reconstructed root net list: " + rootIds);
        }

        // Translate CPF Nets as BPMN Processes TODO: Fix
        for (final NetType net : cpf.getNet()) {
            // Only root elements are decanonised here; subnets are dealt with by recursion
            if (!rootIds.contains(net.getId())) {
                continue;
            }

            getRootElement().add(initializer.getFactory().createProcess(new BpmnProcess((CpfNetType) net, initializer, collaboration)));

            /*
            // If we haven't added the collaboration yet and this process is a pool, add the collaboration
            if (!getRootElement().contains(wrapperCollaboration)) {
                getRootElement().add(wrapperCollaboration);
            }
            */
        }

        // pc:variants occurring on the document element of the CPF need to be moved to one of the root elements of the BPMN
        for (org.apromore.cpf.TypeAttribute attribute: cpf.getAttribute()) {
            outer: switch (attribute.getName()) {
            case "bpmn_cpf/extensions":
                for (JAXBElement<? extends TRootElement> jaxbRoot: getRootElement()) {
                    TRootElement root = jaxbRoot.getValue();
                    TExtensionElements extensionElements = root.getExtensionElements();
                    if (extensionElements == null) {
                        extensionElements = new BpmnObjectFactory().createTExtensionElements();
                        root.setExtensionElements(extensionElements);
                    }
                    assert extensionElements != null;
                    assert extensionElements == root.getExtensionElements();

                    extensionElements.getAny().add(attribute.getAny());

                    break outer;
                }
                throw new CanoniserException("Couldn't find a root element to place the BPMN variants attribute");

            case "BranchID":
            case "BranchName":
            case "RootFragmentId":
            case "InitialFormat":
            case "IntialFormat":
            case "PMVID":
            case "ProcessName":
            case "VersionNumber":
                // recognized, but ignored
                break;

            default:
                throw new CanoniserException("Unsupported extension attribute in CPF: " + attribute.getName() + " value=" + attribute.getValue() + " any=" + attribute.getAny());
            }
        }

        // Make sure all the deferred fields did eventually get filled in
        initializer.close();

        // Translate any ANF annotations into a BPMNDI diagram element
        if (anf != null) {
            getBPMNDiagram().add(new BpmndiDiagram(anf, initializer));
        }
    }



    /**
     * Construct a BPMN model from a canonical model.
     * In other words, de-canonise a CPF/ANF model into a BPMN one.
     *
     * @param cpf  a canonical process model
     * @param anf  annotations for the canonical process model
     * @return a new BPMN process model corresponding to <code>cpf</code>
     * @throws CanoniserException if unable to generate BPMN from the given CPF and ANF arguments
     */
    public static BpmnDefinitions newInstance(final CpfCanonicalProcessType cpf, final AnnotationsType anf) throws CanoniserException {

        // Use JAXB to construct the de-canonised BPMN document
        BpmnDefinitions definitions = new BpmnDefinitions(cpf, anf);

        // Use XSLT to work around JAXB's inability to correctly generate IDREFS like bpmn:flowNodeRefs
        try {
            return definitions.correctFlowNodeRefs(definitions, new BpmnObjectFactory());
        } catch (JAXBException | TransformerException e) {
            throw new CanoniserException("Unable to correct JAXB-misgenerated bpmn:flowNodeRef elements", e);
        }
    }

    /**
     * Construct an instance from a stream.
     *
     * @param in  a BPMN-formatted stream
     * @param validate  whether to perform schema validation
     * @throws JAXBException if the stream can't be parsed as BPMN
     * @return the parsed instance
     */
    @SuppressWarnings("unchecked")
    public static BpmnDefinitions newInstance(final InputStream in, final Boolean validate) throws JAXBException {
        assert in != null;
        Unmarshaller unmarshaller = /*BPMN_CONTEXT*/ newContext().createUnmarshaller();
        BpmnIDResolver resolver = new BpmnIDResolver();
        BpmnUnmarshallerListener listener = new BpmnUnmarshallerListener(resolver);
        unmarshaller.setListener(listener);
        unmarshaller.setProperty(ID_RESOLVER, resolver);
        unmarshaller.setProperty(OBJECT_FACTORY, new Object[]{new BpmnObjectFactory(), new BpmndiObjectFactory()});
        if (validate) {
            unmarshaller.setSchema(getBpmnSchema() /*BPMN_SCHEMA*/);
        }
        BpmnDefinitions result = ((JAXBElement<BpmnDefinitions>) unmarshaller.unmarshal(new StreamSource(in))).getValue();
        result.idMap.putAll(listener.getIdMap());
        return result;
    }

    /**
     * @return a context containing the various XML namespaces comprising BPMN 2.0.
     */
    public static JAXBContext newContext() {
        try {
            return JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                           org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                           org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                           org.omg.spec.dd._20100524.di.ObjectFactory.class,
                                           com.processconfiguration.ObjectFactory.class,
                                           com.signavio.ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create JAXB context for BPMN", e);
        }
    }

    /**
     * Look up a {@link TBaseElement} by ID.
     *
     * @param id  the ID of the element
     * @return the unique element identified by <code>id</code>, or <code>null</code> if the document contains no such element
     */
    public TBaseElement findElementById(final String id) {
        return idMap.get(id);
    }

    /**
     * Serialize this instance to a steam.
     *
     * Default deserialization is pretty-printed but not schema-validated.
     *
     * @param out  the stream for writing
     * @param validate  whether to perform schema validation
     * @throws JAXBException if the steam can't be written to
     */
    public void marshal(final OutputStream out, final Boolean validate) throws JAXBException {
      try {
        // Create an empty DOM
        DOMResult intermediateResult = new DOMResult();

        // Marshal from JAXB to DOM
        Marshaller marshaller = /*BPMN_CONTEXT*/ newContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (validate) {
            marshaller.setSchema(getBpmnSchema() /*BPMN_SCHEMA*/);
        }
        marshaller.marshal(new BpmnObjectFactory().createDefinitions(this), intermediateResult);

        // Apply the XSLT transformation, from DOM to the output stream
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(
            new StreamSource(new java.io.StringBufferInputStream(fixNamespacesXslt[0] + getTargetNamespace() + fixNamespacesXslt[1]))
        );
        DOMSource finalSource = new DOMSource(intermediateResult.getNode());
        StreamResult finalResult = new StreamResult(out);
        transformer.transform(finalSource, finalResult);
      } catch (TransformerException e) { throw new JAXBException("Dodgy wrapped exception", e); }  // TODO - create transformer elsewhere
    }

    /**
     * Workaround for incorrect marshalling of {@link TLane#getFlowNodeRef} by JAXB.
     *
     * A flow node reference on a lane ought to be serialized as
     * <pre>
     * &lt;lane&gt;
     *   &lt;flowNodeRef&gt;id-123&lt;/flowNodeRef&gt;
     * &lt;/lane&gt;
     * </pre>
     * but instead they end up serialized as
     * <pre>
     * &lt;lane&gt;
     *   &lt;task id="id-123"/&gt;
     * &lt;/lane&gt;
     * </pre>
     * This method applies an XSLT transform to correct things.
     *
     * @param definitions  the buggy JAXB document
     * @param factory  source of elements for the result document
     * @throws JAXBException if <code>definitions</code> can't be marshalled to XML or unmarshalled back
     * @throws TransformerException  if the XSLT transformation fails
     * @return corrected JAXB document
     */
    // TODO - change the return type and the factory parameter to be Definitions and ObjectFactory, and move to bpmn-schema
    public BpmnDefinitions correctFlowNodeRefs(final BpmnDefinitions definitions,
                                               final BpmnObjectFactory factory) throws JAXBException, TransformerException {

        JAXBContext context = JAXBContext.newInstance(factory.getClass(),
                                                      com.processconfiguration.ObjectFactory.class,
                                                      org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                      org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.di.ObjectFactory.class,
                                                      com.signavio.ObjectFactory.class);

        // Marshal the BPMN into a DOM tree
        DOMResult intermediateResult = new DOMResult();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(factory.createDefinitions(definitions), intermediateResult);

        // Apply the XSLT transformation, generating a new DOM tree
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(
            new StreamSource(getClass().getClassLoader().getResourceAsStream("xsd/fix-flowNodeRef.xsl"))
        );
        DOMSource finalSource = new DOMSource(intermediateResult.getNode());
        DOMResult finalResult = new DOMResult();
        transformer.transform(finalSource, finalResult);

        // Unmarshal back to JAXB
        Object def2 = context.createUnmarshaller().unmarshal(finalResult.getNode());
        return ((JAXBElement<BpmnDefinitions>) def2).getValue();
    }

    /**
     * Replace every instance of multiple incoming sequence flows to an activity with an exclusive data-based gateway,
     * every instance of multiple outgoing sequence flows from an activity with a parallel gateway, and every mixed gateway
     * as a converging gateway flowing to a diverging gateway.
     *
     * This is required as pre-processing for canonisation, since CPF Work elements must have no more than one incoming edge and
     * no more than one outgoing edge.
     *
     * @throws CanoniserException if the rewriting can't be performed
     */
    public void rewriteImplicitGatewaysExplicitly() throws CanoniserException {
        for (JAXBElement<? extends TRootElement> rootElement : getRootElement()) {
            if (rootElement.getValue() instanceof TProcess) {
                TProcess process = (TProcess) rootElement.getValue();
                rewriteFlowElements(process.getFlowElement());
            }
        }
    }

    private void rewriteFlowElements(List<JAXBElement<? extends TFlowElement>> jfel) throws CanoniserException {
        for (JAXBElement<? extends TFlowElement> flowElement : new ArrayList<JAXBElement<? extends TFlowElement>>(jfel)) {
            if (flowElement.getValue() instanceof TFlowNode && !(flowElement.getValue() instanceof TGateway)) {
                TFlowNode flowNode = (TFlowNode) flowElement.getValue();

                if (flowNode.getIncoming().size() > 1) {

                    // Create a converging XOR gateway
                    BpmnObjectFactory factory = new BpmnObjectFactory();
                    TExclusiveGateway xor = factory.createTExclusiveGateway();
                    xor.setId(flowNode.getId() + "_implicit_join");
                    xor.setGatewayDirection(TGatewayDirection.CONVERGING);
                    jfel.add(factory.createExclusiveGateway(xor));

                    // Retarget the incoming flows to the XOR gateway
                    for (QName incoming: flowNode.getIncoming()) {
                        BpmnSequenceFlow incomingFlow = (BpmnSequenceFlow) findElement(incoming);
                        incomingFlow.setTargetRef(xor);
                    }

                    xor.getIncoming().addAll(flowNode.getIncoming());
                    flowNode.getIncoming().clear();

                    // Create a flow from the XOR gateway to the flow node
                    BpmnSequenceFlow flow = factory.createTSequenceFlow();
                    flow.setId(flowNode.getId() + "_implicit_join_edge");  // TODO - should use IdFactory to ward against clashes
                    flow.setSourceRef(xor);
                    flow.setTargetRef(flowNode);
                    jfel.add(factory.createSequenceFlow(flow));

                    QName flowQName = new QName(targetNamespace, flow.getId());
                    flowNode.getIncoming().add(flowQName);
                    xor.getOutgoing().add(flowQName);
                }

                if (flowNode.getOutgoing().size() > 1) {

                    // Create a diverging AND gateway
                    BpmnObjectFactory factory = new BpmnObjectFactory();
                    TParallelGateway and = factory.createTParallelGateway();
                    and.setId(flowNode.getId() + "_implicit_split");
                    and.setGatewayDirection(TGatewayDirection.DIVERGING);
                    jfel.add(factory.createParallelGateway(and));

                    // Change the source of the outgoing flows to be from the AND gateway
                    for (QName outgoing: flowNode.getOutgoing()) {
                        BpmnSequenceFlow outgoingFlow = (BpmnSequenceFlow) findElement(outgoing);
                        outgoingFlow.setSourceRef(and);
                    }

                    and.getOutgoing().addAll(flowNode.getOutgoing());
                    flowNode.getOutgoing().clear();

                    // Create a flow to the AND gateway from the flow node
                    BpmnSequenceFlow flow = factory.createTSequenceFlow();
                    flow.setId(flowNode.getId() + "_implicit_split_edge");  // TODO - should use IdFactory to ward against clashes
                    flow.setSourceRef(flowNode);
                    flow.setTargetRef(and);
                    jfel.add(factory.createSequenceFlow(flow));

                    QName flowQName = new QName(targetNamespace, flow.getId());
                    flowNode.getOutgoing().add(flowQName);
                    and.getIncoming().add(flowQName);
                }

                // Recursively rewrite any subprocesses
                if (flowNode instanceof TSubProcess) {
                    rewriteFlowElements(((TSubProcess) flowNode).getFlowElement());
                }
            }

            // Rewrite mixed gateways
            if (flowElement.getValue() instanceof TGateway) {
                TGateway gateway = (TGateway) flowElement.getValue();

                if (gateway.getIncoming().size() > 1 && gateway.getOutgoing().size() > 1) {

                    // Create a diverging XOR gateway
                    BpmnObjectFactory factory = new BpmnObjectFactory();
                    TGateway newGateway;
                    if (gateway instanceof TComplexGateway) {
                        newGateway = factory.createTComplexGateway();
                        jfel.add(factory.createComplexGateway((TComplexGateway) newGateway));
                    } else if (gateway instanceof TEventBasedGateway) {
                        newGateway = factory.createTEventBasedGateway();
                        jfel.add(factory.createEventBasedGateway((TEventBasedGateway) newGateway));
                    } else if (gateway instanceof TExclusiveGateway) {
                        newGateway = factory.createTExclusiveGateway();
                        jfel.add(factory.createExclusiveGateway((TExclusiveGateway) newGateway));
                    } else if (gateway instanceof TInclusiveGateway) {
                        newGateway = factory.createTInclusiveGateway();
                        jfel.add(factory.createInclusiveGateway((TInclusiveGateway) newGateway));
                    } else if (gateway instanceof TParallelGateway) {
                        newGateway = factory.createTParallelGateway();
                        jfel.add(factory.createParallelGateway((TParallelGateway) newGateway));
                    } else {
                        throw new CanoniserException("Mixed gateway " + gateway.getId() + " of type " + gateway.getClass() + " unsupported");
                    }
                    assert newGateway != null;
                    newGateway.setId(gateway.getId() + "_mixed_split");
                    newGateway.setGatewayDirection(TGatewayDirection.DIVERGING);

                    // Re-source the outgoing flows from the diverging gateway
                    for (QName outgoing: gateway.getOutgoing()) {
                        BpmnSequenceFlow outgoingFlow = (BpmnSequenceFlow) findElement(outgoing);
                        outgoingFlow.setSourceRef(newGateway);
                    }
                    gateway.setGatewayDirection(TGatewayDirection.CONVERGING);
                    gateway.getOutgoing().clear();

                    // Create a flow from the original (now converging) gateway to the new diverging gateway
                    BpmnSequenceFlow flow = factory.createTSequenceFlow();
                    flow.setId(gateway.getId() + "_mixed_edge");
                    flow.setSourceRef(gateway);
                    flow.setTargetRef(newGateway);
                    jfel.add(factory.createSequenceFlow(flow));

                    QName flowQName = new QName(targetNamespace, flow.getId());
                    newGateway.getIncoming().add(flowQName);
                    gateway.getOutgoing().add(flowQName);
                }
            }
        }
    }

    /**
     * @param id  the name of a BPMN element
     * @throws CanoniserException if <code>id</code> isn't a BPMN element from the current document
     */
    public TBaseElement findElement(final QName id) throws CanoniserException {
        if (!targetNamespace.equals(id.getNamespaceURI())) {
            if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(id.getNamespaceURI())) {
                java.util.logging.Logger.getAnonymousLogger().warning(
                    id + " is a QName reference to the BPMN namespace rather than the document's target namespace.  " +
                    "Treating as {" + targetNamespace + "}" + id.getLocalPart() + " instead.  " +
                    "Please fix the original document.");
            } else {
                throw new CanoniserException(id + " is not local to this BPMN model");
            }
        }

        return idMap.get(id.getLocalPart());
    }

    /**
     * @param element  a BPMN element with an ID already present in the model
     * @throws CanoniserException if <code>element</code> doesn't have an ID that's already mapped
     */
    public void updateElement(final TBaseElement element) throws CanoniserException {
        TBaseElement old = idMap.put(element.getId(), element);
        if (old == null) {
            throw new CanoniserException(element.getId() + " can't be updated because it doesn't already exist");
        }
        assert old.getId().equals(element.getId());
    }
}
