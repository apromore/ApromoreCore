package org.apromore.canoniser.bpmn;

// Java 2 Standard packges
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.AnnotationType;
import org.apromore.anf.DocumentationType;
//import org.apromore.anf.FillType;
//import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
//import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
//import org.apromore.anf.SizeType;
import org.apromore.canoniser.bpmn.cpf.CpfNodeType;
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
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
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TArtifact;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TEvent;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExpression;
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
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;
import org.omg.spec.dd._20100524.di.DiagramElement;
//import org.omg.spec.dd._20100524.di.Plane;

/**
 * BPMN 2.0 object model with canonisation methods.
 * <p>
 * To canonise a BPMN document, unmarshal the XML into an object of this class, and invoke the {@link #canonise} method.
 * The resulting {@link CanoniserResult} represents a list of CPF/ANF pairs.
 * Because a BPMN document may describe a collection of processes (for example, in a collaboration) the resulting
 * {@link CanoniserResult} may contain several {@link CanonicalProcessType} instances.
 * <p>
 * To decanonise a canonical model into BPMN, invoke the constructor {@link #CanoniserDefinitions(CanonicalProcessType, AnnotationsType)}.
 * Only individual canonical models may be decanonised; there is no facility for generating a BPMN document containing
 * multiple top-level processes.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @version 0.4
 * @since 0.3
 */
@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL", name = "definitions")
public class CanoniserDefinitions extends TDefinitions {

    /**
     * Logger.  Named after the class.
     */
    @XmlTransient
    private final Logger logger = Logger.getLogger(CanoniserDefinitions.class.getCanonicalName());

    /**
     * Apromore URI.
     */
    public static final String APROMORE_URI = "http://apromore.org";

    /**
     * Apromore version.
     */
    public static final String APROMORE_VERSION = "0.4";

    /**
     * Namespace of the document root element.
     *
     * Chosen arbitrarily to match Signavio.
     */
    public static final String TARGET_NS = "http://www.signavio.com/bpmn20";

    /**
     * BPMN 2.0 namespace.
     */
    public static final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    /**
     * CPF schema version.
     */
    public static final String CPF_VERSION = "1.0";

    /**
     * XPath expression language URI.
     */
    public static final String XPATH_URI = "http://www.w3.org/1999/XPath";

    /**
     * XML Schema datatype language URI.
     */
    public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * No-op constructor.
     *
     * Required for JUnit to work.
     */
    public CanoniserDefinitions() { }

    /**
     * Construct a BPMN model from a canonical form.
     *
     * @param cpf  a canonical process model
     * @param anf  annotations for the canonical process model
     * @throws CanoniserException if unable to generate BPMN from the given CPF and ANF arguments
     */
    public CanoniserDefinitions(final CanonicalProcessType cpf, final AnnotationsType anf) throws CanoniserException {

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

        // We can get by without an ANF parameter, but we definitely need a CPF
        if (cpf == null) {
            throw new CanoniserException("Cannot create BPMN from null CPF");
        }

        // Set attributes of the document root
        setExporter(APROMORE_URI);
        setExporterVersion(APROMORE_VERSION);
        setExpressionLanguage(XPATH_URI);
        setId(null);
        setName(cpf.getName());
        setTargetNamespace(TARGET_NS);
        setTypeLanguage(XSD_URI);

        /* TODO - add as extension attributes
        String author = cpf.getAuthor();
        String creationDate = cpf.getCreationDate();
        String modificationDate = cpf.getModificationDate();
        */

        // Assume there will be pools, all of which belong to a single collaboration
        TCollaboration collaboration = factory.createTCollaboration();
        getRootElement().add(factory.createCollaboration(collaboration));

        // Translate CPF Nets as BPMN Processes
        for (final NetType net : cpf.getNet()) {

            // Add the BPMN Process element
            final TProcess process = new TProcess();
            process.setId(bpmnIdFactory.newId(net.getId()));
            getRootElement().add(factory.createProcess(process));

            // Add the BPMN Participant element
            TParticipant participant = new TParticipant();
            participant.setId(bpmnIdFactory.newId("participant"));
            participant.setName(process.getName());  // TODO - use an extension element for pool name if it exists
            participant.setProcessRef(new QName(BPMN_NS, process.getId()));
            collaboration.getParticipant().add(participant);

            // Add the CPF ResourceType lattice as a BPMN Lane hierarchy
            TLaneSet laneSet = new TLaneSet();
            for (ResourceTypeType resourceType : cpf.getResourceType()) {
                CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
                if (cpfResourceType.getGeneralizationRefs().isEmpty()) {
                     TLane lane = new TLane();
                     lane.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
                     idMap.put(cpfResourceType.getId(), lane);
                     addChildLanes(lane, cpf.getResourceType(), bpmnIdFactory, idMap);
                     laneSet.getLane().add(lane);
                }
            }
            if (!laneSet.getLane().isEmpty()) {
                process.getLaneSet().add(laneSet);
            }

            // Add the CPF Edges as BPMN SequenceFlows
            for (EdgeType edge : net.getEdge()) {
                TSequenceFlow sequenceFlow = createSequenceFlow(edge, bpmnIdFactory, idMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
                edgeMap.put(edge.getId(), sequenceFlow);
                process.getFlowElement().add(factory.createSequenceFlow(sequenceFlow));
            }

            // Add the CPF Nodes as BPMN FlowNodes
            for (NodeType node : net.getNode()) {
                JAXBElement<? extends TFlowNode> flowNode = createFlowNode(node, bpmnIdFactory, idMap, factory);
                process.getFlowElement().add(flowNode);

                // Fill any BPMN @sourceRef or @targetRef attributes referencing this node
                if (flowWithoutSourceRefMap.containsKey(node.getId())) {
                    flowWithoutSourceRefMap.get(node.getId()).setSourceRef((TFlowNode) idMap.get(node.getId()));
                    flowWithoutSourceRefMap.remove(node.getId());
                }
                if (flowWithoutTargetRefMap.containsKey(node.getId())) {
                    flowWithoutTargetRefMap.get(node.getId()).setTargetRef((TFlowNode) idMap.get(node.getId()));
                    flowWithoutTargetRefMap.remove(node.getId());
                }

                // Populate the lane flowNodeRefs
                if (node instanceof WorkType) {
                    for (ResourceTypeRefType resourceTypeRef : ((WorkType) node).getResourceTypeRef()) {
                        TLane lane = (TLane) idMap.get(resourceTypeRef.getResourceTypeId());
                        JAXBElement<Object> jeo = (JAXBElement) flowNode;
                        lane.getFlowNodeRef().add((JAXBElement) flowNode);
                    }
                }
            }
        }

        // Make sure all the deferred fields did eventually get filled in
        if (!flowWithoutSourceRefMap.isEmpty()) {
            throw new CanoniserException("Missing source references: " + flowWithoutSourceRefMap.keySet());
        }
        if (!flowWithoutTargetRefMap.isEmpty()) {
            throw new CanoniserException("Missing target references: " + flowWithoutTargetRefMap.keySet());
        }

        // Translate any ANF annotations into a BPMNDI diagram element
        if (anf != null) {
            getBPMNDiagram().add(createBpmnDiagram(anf, bpmnIdFactory, idMap, edgeMap));
        }
    }

    /**
     * Workaround for incorrect marshalling of {@link TLane#getFlowNodeRef} by JAXB.
     *
     * A flow node reference on a lane ought to be serialized as
     * <pre>
     * <lane>
     *   <flowNodeRef>id-123</flowNodeRef>
     * </lane>
     * </pre>
     * but instead they end up serialized as
     * <pre>
     * <lane>
     *   <task id="id-123"/>
     * </lane>
     * </pre>
     * This method applies an XSLT transform to correct things.
     *
     * @param definitions  the buggy JAXB document
     * @throws JAXBException if <var>definitions</var> can't be marshalled to XML or unmarshalled back
     * @throws TransformerException  if the XSLT transformation fails
     * @return corrected JAXB document
     */
    public static CanoniserDefinitions correctFlowNodeRefs(CanoniserDefinitions definitions, /*org.apromore.cpf.ObjectFactory*/ BpmnObjectFactory factory)
        throws JAXBException, TransformerException {

        JAXBContext context = JAXBContext.newInstance(factory.getClass(),
                                                      org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                      org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.di.ObjectFactory.class);

        // Marshal the BPMN into a DOM tree
        DOMResult intermediateResult = new DOMResult();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(factory.createDefinitions(definitions), intermediateResult);

        // Apply the XSLT transformation, generating a new DOM tree
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(ClassLoader.getSystemResourceAsStream("xsd/fix-flowNodeRef.xsl")));
        DOMSource finalSource = new DOMSource(intermediateResult.getNode());
        DOMResult finalResult = new DOMResult();
        transformer.transform(finalSource, finalResult);

        // Unmarshal back to JAXB
        Object def2 = context.createUnmarshaller().unmarshal(finalResult.getNode());
        return ((JAXBElement<CanoniserDefinitions>) def2).getValue();
    }

    /**
     * Recursively populate a BPMN {@link TLane}'s child lanes.
     *
     * TODO - circular resource type chains cause non-termination!  Need to check for and prevent this.
     */
    private void addChildLanes(TLane parentLane,
                               List<ResourceTypeType> resourceTypeList,
                               IdFactory bpmnIdFactory,
                               Map<String, TBaseElement> idMap) {

        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : resourceTypeList) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().contains(parentLane.getId())) {
                TLane childLane = new TLane();
                childLane.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
                idMap.put(cpfResourceType.getId(), childLane);
                addChildLanes(childLane, resourceTypeList, bpmnIdFactory, idMap);
                laneSet.getLane().add(childLane);
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            parentLane.setChildLaneSet(laneSet);
        }
    }

    /**
     * Translate a CPF {@link NodeType} into a BPMN {@link TFlowNode}.
     *
     * @param node  a CPF node
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param factory  the created object will have come from this factory
     * @return a {@link TFlowElement} instance, wrapped in a {@link JAXBElement}
     * @throws CanoniserException if <var>node</var> isn't an event or a task
     */
    private JAXBElement<? extends TFlowNode> createFlowNode(final NodeType node,
                                                            final IdFactory bpmnIdFactory,
                                                            final Map<String, TBaseElement> idMap,
                                                            final BpmnObjectFactory factory) throws CanoniserException {

        if (node instanceof EventType) {
            // Count the incoming and outgoing edges to determine whether this is a start, end, or intermediate event
            CpfNodeType cpfNode = (CpfNodeType) node;
            if (cpfNode.getIncomingEdges().size() == 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // assuming a StartEvent here, but could be TBoundaryEvent too
                TStartEvent event = new TStartEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createStartEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() == 0) {
                TEndEvent event = new TEndEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createEndEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() > 0) {
                throw new CanoniserException("Intermediate event \"" + node.getId() + "\" not supported");
            } else {
                throw new CanoniserException("Event \"" + node.getId() + "\" has no edges");
            }
        } else if (node instanceof TaskType) {
            TaskType that = (TaskType) node;

            // TODO - implement subprocesses
            if (that.getSubnetId() != null) {
                throw new CanoniserException("Subprocesses not supported");
            }

            TTask task = new TTask();
            task.setId(bpmnIdFactory.newId(node.getId()));
            idMap.put(node.getId(), task);
            return factory.createTask(task);
        } else {
            throw new CanoniserException("Node " + node.getId() + " type not supported: " + node.getClass().getCanonicalName());
        }

        /*
                    private void populateWork(final TFlowNode flowNode, final WorkType work) {
                        for (ResourceTypeRefType resourceTypeRef : work.getResourceTypeRef()) {
                            logger.info(work.getId() + " should be in lane " + resourceTypeRef.getResourceTypeId() + " with processRef " + net.getId());
                        }
                    }
        */
    }

    /**
     * Translate a CPF {@link EdgeType} into a BPMN {@link TSequenceFlow}.
     *
     * @param edge  a CPF edge
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param flowWithoutSourceRefMap  deferred source nodes
     * @param flowWithoutTargetRefMap  deferred target nodes
     * @return a BPMN sequence flow
     */
    private TSequenceFlow createSequenceFlow(final EdgeType edge,
                                             final IdFactory bpmnIdFactory,
                                             final Map<String, TBaseElement> idMap,
                                             final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                                             final Map<String, TSequenceFlow> flowWithoutTargetRefMap) {

        TSequenceFlow sequenceFlow = new TSequenceFlow();
        sequenceFlow.setId(bpmnIdFactory.newId(edge.getId()));

        // Deal with @conditionExpression
        if (edge.getConditionExpr() != null) {
            TExpression expression = new TExpression();
            expression.getContent().add(edge.getConditionExpr().getExpression());
            sequenceFlow.setConditionExpression(expression);
        }

        // Deal with @sourceId
        if (idMap.containsKey(edge.getSourceId())) {
            sequenceFlow.setSourceRef((TFlowNode) idMap.get(edge.getSourceId()));
        } else {
            assert !flowWithoutSourceRefMap.containsKey(sequenceFlow);
            flowWithoutSourceRefMap.put(edge.getSourceId(), sequenceFlow);
        }

        // Deal with @targetId
        if (idMap.containsKey(edge.getTargetId())) {
            sequenceFlow.setTargetRef((TFlowNode) idMap.get(edge.getTargetId()));
        } else {
            assert !flowWithoutTargetRefMap.containsKey(sequenceFlow);
            flowWithoutTargetRefMap.put(edge.getTargetId(), sequenceFlow);
        }

        return sequenceFlow;
    }

    /**
     * Translate an ANF annotation document into a BPMNDI Diagram element
     *
     * @param anf  an ANF model, never <code>null</code>
     * @param bpmnIdFactory  generator for IDs unique within the diagram's intended BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param edgeMap  map from CPF @cpfId edge identifiers to BPMN ids
     * @return a BPMNDI Diagram element
     */
    private BPMNDiagram createBpmnDiagram(final AnnotationsType anf,
                                          final IdFactory bpmnIdFactory,
                                          final Map<String, TBaseElement> idMap,
                                          final Map<String, TSequenceFlow> edgeMap) {

        final org.omg.spec.bpmn._20100524.di.ObjectFactory diObjectFactory = new org.omg.spec.bpmn._20100524.di.ObjectFactory();

        // Create BPMNDiagram
        final BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId(bpmnIdFactory.newId("diagram"));
        bpmnDiagram.setName(anf.getName());

        // Create BPMNPlane
        final BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnPlane.setId(bpmnIdFactory.newId("plane"));
        assert bpmnDiagram.getBPMNPlane() == null;
        bpmnDiagram.setBPMNPlane(bpmnPlane);

        // Populate the BPMNPlane with elements for each CPF Annotation
        for (final AnnotationType annotation : anf.getAnnotation()) {
            //logger.info("Annotation id=" + annotation.getId() + " cpfId=" + annotation.getCpfId());
            annotation.accept(new org.apromore.anf.BaseVisitor() {
                @Override public void visit(final DocumentationType that) {
                    logger.info("  Documentation");
                }

                @Override public void visit(final GraphicsType that) {
                    GraphicsType graphics = (GraphicsType) annotation;
                    /*
                    logger.info("  Graphics");

                    FillType fill = graphics.getFill();
                    if (fill != null) {
                        logger.info("  Fill color=" + fill.getColor() +
                                    " gradientColor=" + fill.getGradientColor() +
                                    " gradientRotation=" + fill.getGradientRotation() +
                                    " image=" + fill.getImage() +
                                    " transparency=" + fill.getTransparency());
                    };

                    FontType font = graphics.getFont();
                    if (font != null) {
                        logger.info("  Font color=" + font.getColor() +
                                    " decoration=" + font.getDecoration() +
                                    " family=" + font.getFamily() +
                                    " horizontalAlign=" + font.getHorizontalAlign() +
                                    " rotation=" + font.getRotation() +
                                    " size=" + font.getSize() +
                                    " style=" + font.getStyle() +
                                    " transparency=" + font.getTransparency() +
                                    " verticalAlign= " + font.getVerticalAlign() +
                                    " weight=" + font.getWeight() +
                                    " xPosition=" + font.getXPosition() +
                                    " yPosition=" + font.getYPosition());
                    };

                    LineType line = graphics.getLine();
                    if (line != null) {
                        logger.info("  Line color=" + line.getColor() +
                                    " gradientColor=" + line.getGradientColor() +
                                    " gradientRotation=" + line.getGradientRotation() +
                                    " shape=" + line.getShape() +
                                    " style=" + line.getStyle() +
                                    " transparency=" + line.getTransparency() +
                                    " width=" + line.getWidth());
                    };

                    for (PositionType position : graphics.getPosition()) {
                        logger.info("  Position (" + position.getX() + ", " + position.getY() + ")");
                    }

                    SizeType size = graphics.getSize();
                    if (size != null) {
                        logger.info("  Size " + size.getWidth() + " x " + size.getHeight());
                    };
                    */

                    if (idMap.containsKey(annotation.getCpfId())) {
                        BPMNShape shape = new BPMNShape();
                        shape.setId(bpmnIdFactory.newId(annotation.getId()));
                        shape.setBpmnElement(idMap.get(annotation.getCpfId()).getId());

                        // a shape requires a bounding box, defined by a top-left position and a size (width and height)
                        if (graphics.getPosition().size() != 1) {
                            throw new RuntimeException(
                                new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                    annotation.getCpfId() + " should have just one origin position")
                            );  // TODO - remove this wrapper hack
                        }
                        if (graphics.getSize() == null) {
                            throw new RuntimeException(
                                new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                    annotation.getCpfId() + " should specify a size")
                            );  // TODO - remove this wrapper hack
                        }

                        // add the ANF position and size as a BPMNDI bounds
                        Bounds bounds = new Bounds();
                        bounds.setHeight(graphics.getSize().getHeight().doubleValue());
                        bounds.setWidth(graphics.getSize().getWidth().doubleValue());
                        bounds.setX(graphics.getPosition().get(0).getX().doubleValue());
                        bounds.setY(graphics.getPosition().get(0).getY().doubleValue());
                        shape.setBounds(bounds);

                        bpmnPlane.getDiagramElement().add(diObjectFactory.createBPMNShape(shape));

                    } else if (edgeMap.containsKey(annotation.getCpfId())) {
                        BPMNEdge edge = new BPMNEdge();
                        edge.setId(bpmnIdFactory.newId(annotation.getId()));
                        edge.setBpmnElement(edgeMap.get(annotation.getCpfId()).getId());

                        // an edge requires two or more waypoints
                        if (graphics.getPosition().size() < 2) {
                            throw new RuntimeException(
                                new CanoniserException("Annotation " + annotation.getId() + " for edge " +
                                    annotation.getCpfId() + " should have at least two positions")
                            );  // TODO - remove this wrapper hack
                        }

                        // add each ANF position as a BPMNDI waypoint
                        for (PositionType position : graphics.getPosition()) {
                            Point point = new Point();
                            point.setX(position.getX().doubleValue());
                            point.setY(position.getY().doubleValue());
                            edge.getWaypoint().add(point);
                        }

                        bpmnPlane.getDiagramElement().add(diObjectFactory.createBPMNEdge(edge));
                    } else {
                        throw new RuntimeException(
                            new CanoniserException("CpfId \"" + annotation.getCpfId() + "\" in ANF document not found in CPF document")
                        );  // TODO - remove this wrapper hack
                    }
                }

                @Override public void visit(final SimulationType that) {
                    logger.info("  Simulation");
                }
            });

            for (Map.Entry<QName, String> entry : annotation.getOtherAttributes().entrySet()) {
                logger.info("  Annotation attribute " + entry.getKey() + "=" + entry.getValue());
            }
        }

        return bpmnDiagram;
    }

    //
    // Decanonization
    //

    /**
     * Convert this BPMN document into an equivalent collection of CPF and ANF documents.
     *
     * @throws CanoniserException  if the translation can't be performed
     * @return a result containing CPF and ANF documents equivalent to this BPMN
     */
    public CanoniserResult canonise() throws CanoniserException {

        // Generate identifiers for @uri scoped across all generated CPF and ANF documents
        final IdFactory linkUriFactory = new IdFactory();

        // This instance will be populated and returned at the end of this method
        final CanoniserResult result = new CanoniserResult();

        // Map BPMN flow nodes to the CPF lanes containing them
        final Map<TFlowNode, TLane> laneMap = new HashMap<TFlowNode, TLane>();

        // Map BPMN flow nodes to CPF nodes
        final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap = new HashMap<TFlowNode, NodeType>();

        // Traverse processes
        for (JAXBElement<? extends TRootElement> rootElement : getRootElement()) {
            if (rootElement.getValue() instanceof TProcess) {
                TProcess process = (TProcess) rootElement.getValue();

                // Create this process and its subprocesses
                CanonicalProcessType cpf = new CanonicalProcessType();
                IdFactory cpfIdFactory = new IdFactory();  // Generate identifiers scoped to this single CPF document
                cpf.setName(requiredName(getName()));
                cpf.setVersion(CPF_VERSION);
                addNet(cpf, cpfIdFactory, new ProcessWrapper(process), laneMap, bpmnFlowNodeToCpfNodeMap);

                // For each diagram in the BPMN, generate an ANF for this CPF
                List<AnnotationsType> anfs = annotate();

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
     * Wrapper to provide a common interface to both {@link TProcess} and {@link TSubProcess}.
     */
    static class ProcessWrapper {
        private final String id;
        private final List<JAXBElement<? extends TArtifact>> artifact;
        private final List<JAXBElement<? extends TFlowElement>> flowElement;
        private final List<TLaneSet> laneSet;

        /** @param process  wrapped instance */
        ProcessWrapper(TProcess process) {
            id = process.getId();
            artifact = process.getArtifact();
            flowElement = process.getFlowElement();
            laneSet = process.getLaneSet();
        }

        /** @param subprocess  wrapped instance */
        ProcessWrapper(TSubProcess subprocess) {
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

    /**
     * Add a net to the CPF document, corresponding to a given BPMN process.
     *
     * @param process  the BPMN process to translate into a net
     * @return the new CPF net corresponding to the <var>process</var>
     */
    public NetType addNet(final CanonicalProcessType cpf,
                          final IdFactory cpfIdFactory,
                          final ProcessWrapper process,
                          final Map<TFlowNode, TLane> laneMap,
                          final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap) throws CanoniserException {

        final NetType net = new NetType();
        net.setId(cpfIdFactory.newId(process.getId()));
        cpf.getRootIds().add(net.getId());
        cpf.getNet().add(net);

        // Generate resource types for each pool and lane
        for (JAXBElement<? extends TRootElement> rootElement2 : getRootElement()) {
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
                        subnet = addNet(cpf, cpfIdFactory, new ProcessWrapper(subprocess), laneMap, bpmnFlowNodeToCpfNodeMap);
                    } catch (CanoniserException e) {
                        throw new RuntimeException("Couldn't create CPF Net for BPMN SubProcess " + subprocess.getId(), e);  // TODO - remove wrapper hack
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
    private void addPools(final TParticipant          participant,
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
    private Set<String> addLanes(final TLaneSet              laneSet,
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
    private void unwindLaneMap(final IdFactory cpfIdFactory,
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
     * @return an ANF document
     */
    private List<AnnotationsType> annotate() {

        final List<AnnotationsType> anfs = new ArrayList<AnnotationsType>();

        for (BPMNDiagram diagram : getBPMNDiagram()) {
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
}
