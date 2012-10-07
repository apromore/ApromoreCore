package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.AnnotationType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.exception.PluginException;
import org.apromore.plugin.impl.DefaultPluginResult;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.PropertyType;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TArtifact;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TLaneSet;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.di.DiagramElement;

/**
 * Canoniser for Business Process Model and Notation (BPMN) 2.0.
 *
 * @see <a href="http://www.bpmn.org">Object Management Group BPMN site</a>
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class BPMN20Canoniser implements Canoniser {

    /** CPF schema version. */
    public static final String CPF_VERSION = "1.0";

    // Methods implementing Canoniser

    /** {@inheritDoc} */
    @Override
    public String getNativeType() {
        return "BPMN 2.0";
    }

    /** {@inheritDoc} */
    @Override
    public PluginResult canonise(final InputStream                bpmnInput,
                         final List<AnnotationsType>      annotationFormat,
                         final List<CanonicalProcessType> canonicalFormat,
                         final PluginRequest request) throws CanoniserException {

        try {
            CanoniserDefinitions definitions = JAXBContext.newInstance(BpmnObjectFactory.class,
                                                                       org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                                       org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                                       org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                                       org.omg.spec.dd._20100524.di.ObjectFactory.class)
                                                          .createUnmarshaller()
                                                          .unmarshal(new StreamSource(bpmnInput), CanoniserDefinitions.class)
                                                          .getValue();  // discard the JAXBElement wrapper
            CanoniserResult result = canonise(definitions);
            for (int i = 0; i < result.size(); i++) {
                annotationFormat.add(result.getAnf(i));
                canonicalFormat.add(result.getCpf(i));
            }
            return new DefaultPluginResult();
        } catch (Exception e) {
            throw new CanoniserException("Could not canonise to BPMN stream", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PluginResult deCanonise(final CanonicalProcessType canonicalFormat,
                           final AnnotationsType      annotationFormat,
                           final OutputStream         bpmnOutput,
                           final PluginRequest request) throws CanoniserException {

        try {
            Marshaller marshaller = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                            org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                            org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                            org.omg.spec.dd._20100524.di.ObjectFactory.class)
                                               .createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new CanoniserDefinitions(canonicalFormat, annotationFormat), bpmnOutput);
            
            return new DefaultPluginResult();
        } catch (Exception e) {
            throw new CanoniserException("Could not decanonise from BPMN stream", e);
        }
    }

    // Methods implementing PropertyAwarePlugin (superinterface of Canoniser)

    /** {@inheritDoc} */
    @Override
    public Set<PropertyType<?>> getAvailableProperties() {
        // TODO please inherit from DefaultAbstractCanoniser
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public Set<PropertyType<?>> getMandatoryProperties() {
     // TODO please inherit from DefaultAbstractCanoniser
        return Collections.emptySet();
    }

    // Methods implementing Plugin (superinterface of PropertyAwarePlugin, superinterface of Canoniser)

    /** {@inheritDoc} */
    @Override
    public String getName() {
        // TODO please inherit from DefaultAbstractCanoniser and use .config file
        return BPMN20Canoniser.class.getCanonicalName();
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "1.0";
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return Canoniser.class.getCanonicalName();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Implements only the descriptive subclass of BPMN 2.0.";
    }

    @Override
    public String getAuthor() {
        // TODO Auto-generated method stub
        return null;
    }

    // Implementation of canonisation

    /**
     * Convert this BPMN document into an equivalent collection of CPF and ANF documents.
     *
     * @param definitions  the BPMN document to translate
     * @throws CanoniserException  if the translation can't be performed
     * @return a result containing CPF and ANF documents equivalent to this BPMN
     */
    public static CanoniserResult canonise(final CanoniserDefinitions definitions) throws CanoniserException {

        // Generate identifiers for @uri scoped across all generated CPF and ANF documents
        final IdFactory linkUriFactory = new IdFactory();

        // This instance will be populated and returned at the end of this method
        final CanoniserResult result = new CanoniserResult();

        // Map BPMN flow nodes to the CPF lanes containing them
        final Map<TFlowNode, TLane> laneMap = new HashMap<TFlowNode, TLane>();

        // Map BPMN flow nodes to CPF nodes
        final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap = new HashMap<TFlowNode, NodeType>();

        // Traverse processes
        for (JAXBElement<? extends TRootElement> rootElement : definitions.getRootElement()) {
            if (rootElement.getValue() instanceof TProcess) {
                TProcess process = (TProcess) rootElement.getValue();

                // Create this process and its subprocesses
                CanonicalProcessType cpf = new CanonicalProcessType();
                IdFactory cpfIdFactory = new IdFactory();  // Generate identifiers scoped to this single CPF document
                cpf.setName(requiredName(definitions.getName()));
                cpf.setVersion(CPF_VERSION);
                addNet(cpf, cpfIdFactory, new ProcessWrapper(process), laneMap, bpmnFlowNodeToCpfNodeMap, null, definitions);

                // For each diagram in the BPMN, generate an ANF for this CPF
                List<AnnotationsType> anfs = annotate(definitions);

                // Link the ANF to the CPF so that @cpfId attributes are meaningful
                String linkUri = linkUriFactory.newId(null);
                cpf.setUri(linkUri);
                for (AnnotationsType anf : anfs) {
                    anf.setUri(linkUri);
                    result.put(cpf, anf);
                }
            }
        }

        // Dummy return value
        return result;
    }

    /**
     * Add a net to the CPF document, corresponding to a given BPMN process.
     *
     * @param cpf  the CPF document to populate
     * @param cpfIdFactory  generator for CPF identifiers
     * @param process  the BPMN process to translate into a net
     * @param laneMap  which BPMN nodes belong in which lanes?
     * @param bpmnFlowNodeToCpfNodeMap  which BPMN nodes correspond to which CPF nodes?
     * @param parent  if this is a subnet, the parent net; if this is a root net, <code>null</code>
     * @param definitions  the BPMN document
     * @return the new CPF net corresponding to the <code>process</code>
     * @throws CanoniserException  if the net (and its subnets) can't be created and added
     */
    private static NetType addNet(final CanonicalProcessType cpf,
                                  final IdFactory cpfIdFactory,
                                  final ProcessWrapper process,
                                  final Map<TFlowNode, TLane> laneMap,
                                  final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap,
                                  final NetType parent,
                                  final CanoniserDefinitions definitions) throws CanoniserException {

        final NetType net = new NetType();
        net.setId(cpfIdFactory.newId(process.getId()));
        if (parent == null) {
            cpf.getRootIds().add(net.getId());
        }
        cpf.getNet().add(net);

        // Generate resource types for each pool and lane
        for (JAXBElement<? extends TRootElement> rootElement2 : definitions.getRootElement()) {
            if (rootElement2.getValue() instanceof TCollaboration) {
                for (TParticipant participant : ((TCollaboration) rootElement2.getValue()).getParticipant()) {
                    if (process.getId().equals(participant.getProcessRef().getLocalPart())) {
                        addPools(participant, process.getLaneSet(), cpf, cpfIdFactory, laneMap);
                    }
                }
            }
        }

        for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElement()) {
            flowElement.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                @Override
                public void visit(final TDataObject dataObject) {
                    ObjectType object = new ObjectType();

                    object.setConfigurable(false);  // BPMN doesn't have an obvious equivalent

                    if (dataObject.isIsCollection()) {
                        // TODO - represent using some sort of extension element
                    }

                    populateFlowElement(object, dataObject);

                    net.getObject().add(object);
                }

                @Override
                public void visit(final TEndEvent endEvent) {
                    EventType event = new EventType();
                    populateFlowNode(event, endEvent);

                    net.getNode().add(event);
                }

                @Override
                public void visit(final TExclusiveGateway exclusiveGateway) {
                    RoutingType routing;

                    switch (exclusiveGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new XORJoinType(); break;
                        case DIVERGING:  routing = new XORSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Unimplemented gateway direction " + exclusiveGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    populateFlowElement(routing, exclusiveGateway);

                    net.getNode().add(routing);
                }

                @Override
                public void visit(final TInclusiveGateway inclusiveGateway) {
                    RoutingType routing;

                    switch (inclusiveGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new ORJoinType(); break;
                        case DIVERGING:  routing = new ORSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Unimplemented gateway direction " + inclusiveGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    populateFlowElement(routing, inclusiveGateway);

                    net.getNode().add(routing);
                }

                @Override
                public void visit(final TParallelGateway parallelGateway) {
                    RoutingType routing;

                    switch (parallelGateway.getGatewayDirection()) {
                        case CONVERGING: routing = new ANDJoinType(); break;
                        case DIVERGING:  routing = new ANDSplitType();  break;
                        default:
                            throw new RuntimeException(
                                new CanoniserException("Unimplemented gateway direction " + parallelGateway.getGatewayDirection())
                            );  // TODO - remove wrapper hack
                    }
                    assert routing != null;

                    populateFlowElement(routing, parallelGateway);

                    net.getNode().add(routing);
                }

                @Override
                public void visit(final TSequenceFlow sequenceFlow) {
                    EdgeType edge = new EdgeType();
                    populateFlowElement(edge, sequenceFlow);

                    if (sequenceFlow.getConditionExpression() != null) {

                        // We don't handle multiple conditions
                        if (sequenceFlow.getConditionExpression().getContent().size() != 1) {
                            throw new RuntimeException(
                                                       new CanoniserException("BPMN sequence flow " + sequenceFlow.getId() + " has " +
                                                                              sequenceFlow.getConditionExpression().getContent().size() +
                                                                              " conditions, which the canoniser doesn't implement")
                                                       );  // TODO - remove wrapper hack
                        }

                        ConditionExpressionType conditionExpr = new ConditionExpressionType();
                        conditionExpr.setExpression(sequenceFlow.getConditionExpression().getContent().get(0).toString());
                        edge.setConditionExpr(conditionExpr);
                    }
                    edge.setSourceId(((TFlowNode) sequenceFlow.getSourceRef()).getId());  // TODO - process through cpfIdFactory
                    edge.setTargetId(((TFlowNode) sequenceFlow.getTargetRef()).getId());  // TODO - process through cpfIdFactory

                    net.getEdge().add(edge);
                }

                @Override
                public void visit(final TStartEvent startEvent) {
                    EventType event = new EventType();
                    populateFlowNode(event, startEvent);

                    net.getNode().add(event);
                }

                @Override
                public void visit(final TSubProcess subprocess) {

                    // Add the CPF child net
                    NetType subnet;
                    try {
                        subnet = addNet(cpf, cpfIdFactory, new ProcessWrapper(subprocess), laneMap, bpmnFlowNodeToCpfNodeMap, net, definitions);
                    } catch (CanoniserException e) {
                        throw new RuntimeException("Couldn't create CPF Net for BPMN SubProcess " + subprocess.getId(), e);
                        // TODO - remove wrapper hack
                    }

                    // Add the CPF Task to the parent Net
                    TaskType cpfTask = new TaskType();
                    populateFlowNode(cpfTask, subprocess);
                    cpfTask.setSubnetId(subnet.getId());
                    net.getNode().add(cpfTask);
                }

                @Override
                public void visit(final TTask bpmnTask) {
                    TaskType cpfTask = new TaskType();
                    populateFlowNode(cpfTask, bpmnTask);

                    net.getNode().add(cpfTask);
                }

                // Edge supertype handlers

                private void populateBaseElement(final EdgeType edge, final TBaseElement baseElement) {
                    edge.setId(cpfIdFactory.newId(baseElement.getId()));
                    edge.setOriginalID(baseElement.getId());
                }

                private void populateFlowElement(final EdgeType edge, final TFlowElement flowElement) {
                    populateBaseElement(edge, flowElement);
                }

                // Node supertype handlers

                private void populateBaseElement(final NodeType node, final TBaseElement baseElement) {
                    node.setId(cpfIdFactory.newId(baseElement.getId()));
                    node.setOriginalID(baseElement.getId());
                }

                private void populateFlowElement(final NodeType node, final TFlowElement flowElement) {
                    populateBaseElement(node, flowElement);
                    node.setName(flowElement.getName());
                }

                // Work supertype handler

                private void populateFlowNode(final WorkType work, final TFlowNode flowNode) {
                    populateFlowElement(work, flowNode);
                    bpmnFlowNodeToCpfNodeMap.put(flowNode, work);
                }

                // Object supertype handlers

                private void populateBaseElement(final ObjectType object, final TBaseElement baseElement) {
                    object.setId(cpfIdFactory.newId(baseElement.getId()));
                }

                private void populateFlowElement(final ObjectType object, final TFlowElement flowElement) {
                    populateBaseElement(object, flowElement);
                    object.setName(flowElement.getName());
                }

                // ResourceType supertype handlers

                private void populateBaseElement(final ResourceTypeType resourceType, final TBaseElement baseElement) {
                    resourceType.setId(cpfIdFactory.newId(baseElement.getId()));
                    resourceType.setOriginalID(baseElement.getId());
                }
            });
        }

        unwindLaneMap(cpfIdFactory, laneMap, bpmnFlowNodeToCpfNodeMap);

        return net;
    }

    /**
     * Each lane set in a process corresponds to a pool; for each such pool, create a CPF resource type.
     *
     * @param participant  the BPMN participant corresponding to the pool
     * @param laneSet      the BPMN lanet set of the process referenced by the <var>participant</var>
     * @param cpf  the CPF document to populate
     * @param cpfIdFactory  generator of identifiers for pools and lanes
     */
    private static void addPools(final TParticipant          participant,
                                 final List<TLaneSet>        laneSets,
                                 final CanonicalProcessType  cpf,
                                 final IdFactory             cpfIdFactory,
                                 final Map<TFlowNode, TLane> laneMap) {

        for (TLaneSet laneSet : laneSets) {

            // Create a pool
            ResourceTypeType poolResourceType = new ResourceTypeType();
            poolResourceType.setId(cpfIdFactory.newId(participant.getId()));
            poolResourceType.setName(requiredName(participant.getName()));
            cpf.getResourceType().add(poolResourceType);

            // Create the lanes within the pool
            poolResourceType.getSpecializationIds().addAll(
                                                           addLanes(laneSet, cpf, cpfIdFactory, laneMap)
                                                           );
        }
    }

    /**
     * Recursively add resource types to this CPF corresponding to BPMN lanes.
     *
     * This is recursive, since a lane may itself contain a child lane set.
     *
     * @param laneSet  BPMN lane set to add, never <code>null</code>
     * @param cpf  the CPF document to populate
     * @param cpfIdFactory  generator of identifiers for pools and lanes
     * @return the CPF ids of all the added lanes (but not their sublanes)
     */
    private static Set<String> addLanes(final TLaneSet              laneSet,
                                        final CanonicalProcessType  cpf,
                                        final IdFactory             cpfIdFactory,
                                        final Map<TFlowNode, TLane> laneMap) {

        Set<String> specializationIds = new HashSet<String>();  // TODO - diamond operator

        for (TLane lane : laneSet.getLane()) {
            ResourceTypeType laneResourceType = new ResourceTypeType();

            // Add the resource type to the CPF model
            laneResourceType.setId(cpfIdFactory.newId(lane.getId()));
            laneResourceType.setName(requiredName(lane.getName()));
            specializationIds.add(laneResourceType.getId());
            cpf.getResourceType().add(laneResourceType);

            // Populate laneMap so we'll know later on which lane each element belongs to
            List list = lane.getFlowNodeRef();
            for (Object object : list) {
                JAXBElement je = (JAXBElement) object;
                Object value = je.getValue();
                TFlowNode flowNode = (TFlowNode) value;
                laneMap.put(flowNode, lane);
            }

            // recurse on any child lane sets
            if (lane.getChildLaneSet() != null) {
                laneResourceType.getSpecializationIds().addAll(
                                                               addLanes(lane.getChildLaneSet(), cpf, cpfIdFactory, laneMap)
                                                               );
            }
        }

        return specializationIds;
    }

    /**
     * Take the {@link #laneMap} populated by {@link #addLaneSet} and use it to populate the CPF nodes' {@link NodeType#resourceTypeRef}s.
     *
     * @param cpfIdFactory  generator for {@link ResourceTypeRefType#id}s
     * @throws CanoniserException  if the {@link #laneMap} contains a lane mapping to a node that doesn't exist
     */
    private static void unwindLaneMap(final IdFactory cpfIdFactory,
                                      final Map<TFlowNode, TLane> laneMap,
                                      final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap) throws CanoniserException {

        for (Map.Entry<TFlowNode, TLane> entry : laneMap.entrySet()) {
            if (!bpmnFlowNodeToCpfNodeMap.containsKey(entry.getKey())) {
                throw new CanoniserException("Lane " + entry.getValue().getId() + " contains " +
                                             entry.getKey().getId() + " which is not present");
            }
            NodeType node = bpmnFlowNodeToCpfNodeMap.get(entry.getKey());  // get the CPF node corresponding to the BPMN flow node
            if (node instanceof WorkType) {
                ResourceTypeRefType resourceTypeRef = new ResourceTypeRefType();

                resourceTypeRef.setId(cpfIdFactory.newId(null));
                //resourceTypeRef.setOptional(false);  // redundant, since false is the default
                resourceTypeRef.setQualifier(null);
                resourceTypeRef.setResourceTypeId(entry.getValue().getId());

                ((WorkType) node).getResourceTypeRef().add(resourceTypeRef);
            }
        }
    }

    /**
     * Traverse the BPMN diagram elements, converting them into ANF documents.
     *
     * @param definitions  a BPMN document
     * @return an ANF document
     */
    private static List<AnnotationsType> annotate(final CanoniserDefinitions definitions) {

        final List<AnnotationsType> anfs = new ArrayList<AnnotationsType>();

        for (BPMNDiagram diagram : definitions.getBPMNDiagram()) {
            //logger.info("Annotating a diagram " + ((Plane) diagram.getBPMNPlane()).getDiagramElement());

            final AnnotationsType anf = new AnnotationsType();

            for (JAXBElement<? extends DiagramElement> element : diagram.getBPMNPlane().getDiagramElement()) {

                // Generator for identifiers scoped to this ANF document
                final IdFactory anfIdFactory = new IdFactory();

                //logger.info("Annotating an element " + element);
                element.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                    @Override
                    public void visit(final BPMNEdge edge) {
                        //logger.info("Annotating an edge");
                        AnnotationType annotation = new AnnotationType();
                        annotation.setId(anfIdFactory.newId(edge.getId()));
                        annotation.setCpfId(edge.getBpmnElement().toString());  // TODO - process through cpfIdFactory instead
                        anf.getAnnotation().add(annotation);
                    }
                    @Override
                    public void visit(final BPMNShape shape) {
                        //logger.info("Annotating a shape");
                        AnnotationType annotation = new AnnotationType();
                        annotation.setId(anfIdFactory.newId(shape.getId()));
                        annotation.setCpfId(shape.getBpmnElement().toString());  // TODO - process through cpfIdFactory instead
                        anf.getAnnotation().add(annotation);
                    }
                });
            }

            anfs.add(anf);
        }

        return anfs;
    }

    /**
     * This method centralizes the policy of filling in absent names with a zero-length
     * string in cases where CPF requires a name which is optional in BPMN.
     *
     * @param name  a name which might be absent in the source language
     * @return <var>name</var> if present, otherwise <code>""</code> (the zero-length string).
     */
    static String requiredName(final String name) {
        return (name == null ? "" : name);
    }

    /**
     * Wrapper to provide a common interface to both {@link TProcess} and {@link TSubProcess}.
     */
    static class ProcessWrapper {
        private final String id;
        private final List<JAXBElement<? extends TArtifact>> artifact;
        private final List<JAXBElement<? extends TFlowElement>> flowElement;
        private final List<TLaneSet> laneSet;

        /** @param process  wrapped instance */
        ProcessWrapper(final TProcess process) {
            id = process.getId();
            artifact = process.getArtifact();
            flowElement = process.getFlowElement();
            laneSet = process.getLaneSet();
        }

        /** @param subprocess  wrapped instance */
        ProcessWrapper(final TSubProcess subprocess) {
            id = "subprocess";
            artifact = subprocess.getArtifact();
            flowElement = subprocess.getFlowElement();
            laneSet = subprocess.getLaneSet();
        }

        String getId() { return id; }
        List<JAXBElement<? extends TArtifact>> getArtifact() { return artifact; }
        List<JAXBElement<? extends TFlowElement>> getFlowElement() { return flowElement; }
        List<TLaneSet> getLaneSet() { return laneSet; }
    }

    @Override
    public Set<PropertyType<?>> getOptionalProperties() {
        // TODO please inherit from DefaultAbstractCanoniser
        return null;
    }

    @Override
    public PluginResult createInitialNativeFormat(OutputStream nativeOutput, String processName, String processVersion, String processAuthor,
            Date processCreated, PluginRequest request) {
        // TODO please implement
        return null;
    }

    @Override
    public CanoniserMetadataResult readMetaData(InputStream nativeInput, PluginRequest request) {
        // TODO please implement
        return null;
    }

}
