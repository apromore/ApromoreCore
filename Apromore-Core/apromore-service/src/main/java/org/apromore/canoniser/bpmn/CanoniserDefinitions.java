package org.apromore.canoniser.bpmn;

// Java 2 Standard packges
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

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
import org.apromore.exception.CanoniserException;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
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
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.Definitions;
import org.omg.spec.bpmn._20100524.model.Lane;
import org.omg.spec.bpmn._20100524.model.LaneSet;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;
import org.omg.spec.dd._20100524.di.DiagramElement;
//import org.omg.spec.dd._20100524.di.Plane;

/**
 * BPMN 2.0 object model with canonisation methods.
 *
 * Apromore's canonical format (CPF) describes an individual process.
 * The annotation format (ANF) is paired with a specific CPF document and currently describes diagram layout.
 * A BPMN document describes a collection of processes which may all feature in a single diagram.
 * Consequently one BPMN document may correspond to several CPF documents, and a single diagram element within
 * a BPMN document may correspond to several ANF documents.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @version 0.4
 * @since 0.3
 */
@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL", name = "definitions")
public class CanoniserDefinitions extends Definitions {

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
     * BPMN 2.0 namespace
     */
    public static final String BPMN_NS = "http://www.signavio.com/bpmn20";

    /**
     * CPF schema version.
     */
    public static final String CPF_VERSION = "0.5";

    /**
     * XPath expression language URI.
     */
    public static final String XPATH_URI = "http://www.w3.org/1999/XPath";

    /**
     * XML Schema datatype language URI.
     */
    public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * Canonical process models equivalent to those in this BPMN model.
     *
     * The ordering of the list corresponds to the order of the processes in the BPMN document.
     *
     * This is lazily initialized by the {@link #canonise} method.
     */
    @XmlTransient
    private final List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>(1);

    /**
     * Canonical annotations equivalent to this BPMN model.
     *
     * This is lazily initialized by the {@link #canonise} method.
     */
    @XmlTransient
    private final List<AnnotationsType> anfList = new ArrayList<AnnotationsType>(1);

    /**
     * Whether or not the {@link #cpf} and {@link #anf} fields have been initialized.
     */
    @XmlTransient
    private boolean canonised = false;

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

        // Map from CPF @cpfId node identifiers to BPMN ids
        final Map<String, TFlowNode> idMap = new HashMap<String, TFlowNode>();

        // Map from CPF @cpfId edge identifiers to BPMN ids
        final Map<String, TSequenceFlow> edgeMap = new HashMap<String, TSequenceFlow>();

        // Records the CPF cpfIds of BPMN sequence flows which need their @sourceRef populated
        final Map<String, TSequenceFlow> flowWithoutSourceRefMap = new HashMap<String, TSequenceFlow>();

        // Records the CPF cpfIds of BPMN sequence flows which need their @targetRef populated
        final Map<String, TSequenceFlow> flowWithoutTargetRefMap = new HashMap<String, TSequenceFlow>();

        // Set attributes of the document root
        setExporter(APROMORE_URI);
        setExporterVersion(APROMORE_VERSION);
        setExpressionLanguage(XPATH_URI);
        setId(null);
        setName(cpf.getName());
        setTargetNamespace(BPMN_NS);
        setTypeLanguage(XSD_URI);

        // Process components
        if (cpf != null) {
            final CanoniserObjectFactory factory = new CanoniserObjectFactory();

            for (TypeAttribute attribute : cpf.getAttribute()) {
                //logger.info("CanonicalProcess attribute typeRef=" + attribute.getTypeRef() + " value=" + attribute.getValue());
            }

            String author = cpf.getAuthor();
            String creationDate = cpf.getCreationDate();
            String modificationDate = cpf.getModificationDate();
            String cpfName = cpf.getName();

            for (NetType net : cpf.getNet()) {
                //logger.info("Net id=" + net.getId() + " originalID=" + net.getOriginalID());

                // Translate this CPF net to a BPMN process
                final TProcess process = new TProcess();
                process.setId("process-" + net.getId());
                getRootElements().add(factory.createProcess(process));

                for (TypeAttribute attribute : net.getAttribute()) {
                    //logger.info("  attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                }

                for (EdgeType edge : net.getEdge()) {
                    /*
                    logger.info("  Edge " + edge.getId());

                    for (TypeAttribute attribute : edge.getAttribute()) {
                        logger.info("     attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                    }
                    */

                    TSequenceFlow sequenceFlow = new TSequenceFlow();
                    sequenceFlow.setId(bpmnIdFactory.newId("flow-" + edge.getId()));
                    edgeMap.put(edge.getId(), sequenceFlow);

                    // Deal with @sourceId
                    if (idMap.containsKey(edge.getSourceId())) {
                        sequenceFlow.setSourceRef(idMap.get(edge.getSourceId()));
                    } else {
                        assert !flowWithoutSourceRefMap.containsKey(sequenceFlow);
                        flowWithoutSourceRefMap.put(edge.getSourceId(), sequenceFlow);
                    }

                    // Deal with @targetId
                    if (idMap.containsKey(edge.getTargetId())) {
                        sequenceFlow.setTargetRef(idMap.get(edge.getTargetId()));
                    } else {
                        assert !flowWithoutTargetRefMap.containsKey(sequenceFlow);
                        flowWithoutTargetRefMap.put(edge.getTargetId(), sequenceFlow);
                    }

                    process.getFlowElements().add(factory.createSequenceFlow(sequenceFlow));
                }

                for (final NodeType node : net.getNode()) {
                    //logger.info("  Node " + node.getId());

                    for (TypeAttribute attribute : node.getAttribute()) {
                        //logger.info("     attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                    }

                    node.accept(new org.apromore.cpf.BaseVisitor() {
                        @Override public void visit(final EventType that) {
                            //logger.info("     Event");

                            TStartEvent event = new TStartEvent();
                            event.setId(bpmnIdFactory.newId("event-" + node.getId()));
                            idMap.put(node.getId(), event);
                            process.getFlowElements().add(factory.createStartEvent(event));
                        }

                        @Override public void visit(final TaskType that) {
                            //logger.info("     Task subnetId=" + ((TaskType) node).getSubnetId());

                            TTask task = new TTask();
                            task.setId(bpmnIdFactory.newId("task-" + node.getId()));
                            idMap.put(node.getId(), task);
                            process.getFlowElements().add(factory.createTask(task));
                        }
                    });

                    // Fill any BPMN @sourceRef or @targetRef attributes referencing this node
                    if (flowWithoutSourceRefMap.containsKey(node.getId())) {
                        flowWithoutSourceRefMap.get(node.getId()).setSourceRef(idMap.get(node.getId()));
                        flowWithoutSourceRefMap.remove(node.getId());
                    }
                    if (flowWithoutTargetRefMap.containsKey(node.getId())) {
                        flowWithoutTargetRefMap.get(node.getId()).setTargetRef(idMap.get(node.getId()));
                        flowWithoutTargetRefMap.remove(node.getId());
                    }
                }
            }

            for (ResourceTypeType resource : cpf.getResourceType()) {
                logger.info("Resource id=" + resource.getId() +
                            " name=" + resource.getName() +
                            " isConfigurable=" + resource.isConfigurable() +
                            " originalID=" + resource.getOriginalID());

                for (TypeAttribute attribute : resource.getAttribute()) {
                    logger.info("  attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                }

                for (String id : resource.getSpecializationIds()) {
                    logger.info("  specialization ID=" + id);
                }
            }

            String rootId = cpf.getRootId();
            String cpfUri = cpf.getUri();
            String version = cpf.getVersion();
        }

        // Make sure all the deferred fields did eventually get filled in
        if (!flowWithoutSourceRefMap.isEmpty()) {
            throw new CanoniserException("Missing source references: " + flowWithoutSourceRefMap.keySet());
        }
        if (!flowWithoutTargetRefMap.isEmpty()) {
            throw new CanoniserException("Missing target references: " + flowWithoutTargetRefMap.keySet());
        }

        // Translate any ANF annotations into a BPMN diagram
        if (anf != null) {
            final org.omg.spec.bpmn._20100524.di.ObjectFactory diObjectFactory = new org.omg.spec.bpmn._20100524.di.ObjectFactory();

            // Create BPMNDiagram
            final BPMNDiagram bpmnDiagram = new BPMNDiagram();
            bpmnDiagram.setId(bpmnIdFactory.newId("diagram"));
            getBPMNDiagrams().add(bpmnDiagram);

            // Create BPMNPlane
            final BPMNPlane bpmnPlane = new BPMNPlane();
            bpmnPlane.setId(bpmnIdFactory.newId("plane"));
            assert bpmnDiagram.getBPMNPlane() == null;
            bpmnDiagram.setBPMNPlane(bpmnPlane);

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
                                throw new RuntimeException(  // TODO - remove this wrapper hack
                                    new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                        annotation.getCpfId() + " should have just one origin position")
                                );
                            }
                            if (graphics.getSize() == null) {
                                throw new RuntimeException(  // TODO - remove this wrapper hack
                                    new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                        annotation.getCpfId() + " should specify a size")
                                );
                            }

                            // add the ANF position and size as a BPMNDI bounds
                            Bounds bounds = new Bounds();
                            bounds.setHeight(graphics.getSize().getHeight().doubleValue());
                            bounds.setWidth(graphics.getSize().getWidth().doubleValue());
                            bounds.setX(graphics.getPosition().get(0).getX().doubleValue());
                            bounds.setY(graphics.getPosition().get(0).getY().doubleValue());
                            shape.setBounds(bounds);

                            bpmnPlane.getDiagramElements().add(diObjectFactory.createBPMNShape(shape));

                        } else if (edgeMap.containsKey(annotation.getCpfId())) {
                            BPMNEdge edge = new BPMNEdge();
                            edge.setId(bpmnIdFactory.newId(annotation.getId()));
                            edge.setBpmnElement(edgeMap.get(annotation.getCpfId()).getId());

                            // an edge requires two or more waypoints
                            if (graphics.getPosition().size() < 2) {
                                throw new RuntimeException(  // TODO - remove this wrapper hack
                                    new CanoniserException("Annotation " + annotation.getId() + " for edge " +
                                        annotation.getCpfId() + " should have at least two positions")
                                );
                            }

                            // add each ANF position as a BPMNDI waypoint
                            for (PositionType position : graphics.getPosition()) {
                                Point point = new Point();
                                point.setX(position.getX().doubleValue());
                                point.setY(position.getY().doubleValue());
                                edge.getWaypoints().add(point);
                            }

                            bpmnPlane.getDiagramElements().add(diObjectFactory.createBPMNEdge(edge));
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
            String anfName = anf.getName();
            String anfUri = anf.getUri();
        }
    }

    /**
     * The canonical annotations corresponding to this BPMN.
     *
     * This is lazily initialized the first time it is accessed.
     * Identifiers in this structure reference the corresponding {@link CanonicalProcessType}
     * returned by {@link #getCPF}.
     *
     * @return canonical annotations corresponding to this BPMN
     * @throws CanoniserException unless this BPMN has only a single process
     */
    public AnnotationsType getANF() throws CanoniserException {

        // Lazy initialization
        if (!canonised) {
            canonise();
            canonised = true;
        }
        assert canonised;

        // Until multiple ANF output is supported, raise an exception if this BPMN document doesn't correspond to a single ANF
        if (anfList.size() != 1) {
            throw new CanoniserException("There are " + anfList.size() + " ANF documents");
        }

        return anfList.get(0);
    }

    /**
     * The canonical process model corresponding to this BPMN.
     *
     * This is lazily initialized the first time it is accessed.
     *
     * @return canonical process models corresponding to this BPMN
     * @throws CanoniserException unless this BPMN has only a single process
     */
    public CanonicalProcessType getCPF() throws CanoniserException {

        // Lazy initialization
        if (!canonised) {
            canonise();
            canonised = true;
        }
        assert canonised;

        // Until multiple CPF output is supported, raise an exception if this BPMN document doesn't correspond to a single CPF
        if (cpfList.size() != 1) {
            throw new CanoniserException("There are " + cpfList.size() + " CPF documents");
        }

        return cpfList.get(0);
    }

    /**
     * Convert this BPMN document into an equivalent collection of CPF and ANF documents.
     *
     * @return a result containing CPF and ANF documents equivalent to this BPMN
     */
    public CanoniserResult canonise() {

        // Generate identifiers for @uri scoped across all generated CPF and ANF documents
        final IdFactory linkUriFactory = new IdFactory();

        // Map BPMN flow nodes to CPF nodes
        final Map<TFlowNode, NodeType> bpmnFlowNodeToCpfNodeMap = new HashMap<>();

        // Map BPMN flow nodes to the CPF lanes containing them
        final Map<TFlowNode, Lane> laneMap = new HashMap<>();

        // Traverse processes
        for (JAXBElement<? extends TRootElement> rootElement : getRootElements()) {
            rootElement.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                @Override
                public void visit(final TProcess process) {

                    // Generate identifiers scoped to this single CPF document
                    final IdFactory cpfIdFactory = new IdFactory();

                    final CanonicalProcessType cpf = new CanonicalProcessType();
                      
                    // Top-level attributes
                    cpf.setName(requiredName(getName()));
                    cpf.setVersion(CPF_VERSION);

                    final NetType net = new NetType();
                    net.setId(cpfIdFactory.newId(process.getId()));
                    cpf.setRootId(net.getId());
                    cpf.getNet().add(net);

                    for (LaneSet laneSet : process.getLaneSets()) {
                        addLaneSet(laneSet, process.getName(), cpf, cpfIdFactory);
                    }

                    for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElements()) {
                        flowElement.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                            @Override
                            public void visit(final TDataObject dataObject) {
                                ObjectType object = new ObjectType();

                                object.setConfigurable(false);  // BPMN doesn't have an obvious equivalent

                                if (dataObject.isIsCollection()) {
                                    TypeAttribute attribute = new TypeAttribute();
                                    attribute.setTypeRef("bpmn.isCollection");
                                    attribute.setValue("true");
                                    object.getAttribute().add(attribute);
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
                                    throw new RuntimeException(  // TODO - remove wrapper hack
                                        new CanoniserException("Unimplemented gateway direction " + exclusiveGateway.getGatewayDirection())
                                    );
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
                                    throw new RuntimeException(  // TODO - remove wrapper hack
                                        new CanoniserException("Unimplemented gateway direction " + inclusiveGateway.getGatewayDirection())
                                    );
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
                                    throw new RuntimeException(  // TODO - remove wrapper hack
                                        new CanoniserException("Unimplemented gateway direction " + parallelGateway.getGatewayDirection())
                                    );
                                }
                                assert routing != null;

                                populateFlowElement(routing, parallelGateway);

                                net.getNode().add(routing);
                            }

                            @Override
                            public void visit(final TStartEvent startEvent) {
                                EventType event = new EventType();
                                populateFlowNode(event, startEvent);

                                net.getNode().add(event);
                            }

                            @Override
                            public void visit(final TSequenceFlow sequenceFlow) {
                                EdgeType edge = new EdgeType();
                                populateFlowElement(edge, sequenceFlow);

                                if (sequenceFlow.getConditionExpression() != null) {
                                    edge.setCondition(sequenceFlow.getConditionExpression().getContent().get(0).toString());
                                    // TODO - handle non-singleton expressions
                                }
                                edge.setSourceId(((TFlowNode) sequenceFlow.getSourceRef()).getId());  // TODO - process through cpfIdFactory
                                edge.setTargetId(((TFlowNode) sequenceFlow.getTargetRef()).getId());  // TODO - process through cpfIdFactory

                                net.getEdge().add(edge);
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

                    // For each diagram in the BPMN, generate an ANF for this CPF
                    List<AnnotationsType> anfs = annotate();

                    // Assign resource types to nodes
                    unwindLaneMap(cpfIdFactory);

                    // Link the ANF to the CPF so that @cpfId attributes are meaningful
                    String linkUri = linkUriFactory.newId(null);
                    cpf.setUri(linkUri);
                    for (AnnotationsType anf : anfs) {
                        anf.setUri(linkUri);
                    }

                    // Populate the output lists; these are singletons currently, but eventually should be lists
                    cpfList.add(cpf);
                    anfList.addAll(anfs);
                }

                /**
                 * Recursively add resource types to this CPF corresponding to BPMN swimlanes.
                 *
                 * This is recursive, since a lane may itself contain a child lane set.
                 *
                 * @param laneSet  BPMN lane set to add, never <code>null</code>
                 * @param parentName  the name attribute of the parent element ({@link TProcess} or a {@link TLane}), possibly <code>null</code>
                 * @param cpf  the CPF document to populate
                 * @param cpfIdFactory  generator of identifiers for pools and lanes
                 */
                private void addLaneSet(final LaneSet laneSet, final String parentName, final CanonicalProcessType cpf, final IdFactory cpfIdFactory) {
                    ResourceTypeType poolResourceType = new ResourceTypeType();

                    poolResourceType.setId(cpfIdFactory.newId(laneSet.getId()));

                    // In BPMN lane sets have their own distinct name attribute, but we ignore this and use the parent name instead
                    poolResourceType.setName(requiredName(parentName));

                    for (Lane lane : laneSet.getLanes()) {
                        ResourceTypeType laneResourceType = new ResourceTypeType();

                        for (JAXBElement<Object> object : lane.getFlowNodeRefs()) {
                            TFlowNode flowNode = (TFlowNode) object.getValue();
                            logger.info("Lane " + flowNode.getId());
                            laneMap.put(flowNode, lane);
                        }

                        laneResourceType.setId(cpfIdFactory.newId(lane.getId()));
                        laneResourceType.setName(requiredName(lane.getName()));
                        poolResourceType.getSpecializationIds().add(laneResourceType.getId());

                        cpf.getResourceType().add(laneResourceType);

                        // recurse on any child lane sets
                        if (lane.getChildLaneSet() != null) {
                            addLaneSet(lane.getChildLaneSet(), lane.getName(), cpf, cpfIdFactory);
                        }
                    }

                    cpf.getResourceType().add(poolResourceType);
                }

                /**
                 * Take the {@link #laneMap} populated by {@link #addLaneSet} and use it to populate the CPF nodes' {@link NodeType#resourceTypeRef}s.
                 *
                 * @param cpfIdFactory  generator for {@link ResourceTypeRefType#id}s
                 * @throws CanoniserException  if the {@link #laneMap} contains a lane mapping to a node that doesn't exist
                 */
                private void unwindLaneMap(final IdFactory cpfIdFactory) {

                    for (Map.Entry<TFlowNode, Lane> entry : laneMap.entrySet()) {
                        if (!bpmnFlowNodeToCpfNodeMap.containsKey(entry.getKey())) {
                            throw new RuntimeException(  // TODO - remove the wrapper hack
                                new CanoniserException("Lane " + entry.getValue().getId() + " contains " + entry.getKey().getId() + " which is not present")
                            );
                        }
                        NodeType node = bpmnFlowNodeToCpfNodeMap.get(entry.getKey());  // get the CPF node corresponding to the BPMN flow node
                        if (node instanceof WorkType) {
                            ResourceTypeRefType resourceTypeRef = new ResourceTypeRefType();

                            resourceTypeRef.setId(cpfIdFactory.newId(null));
                            resourceTypeRef.setOptional(false);  // redundant, since false is the default
                            resourceTypeRef.setQualifier(null);
                            resourceTypeRef.setResourceTypeId(entry.getValue().getId());

                            ((WorkType) node).getResourceTypeRef().add(resourceTypeRef);
                        }
                    }
                }
            });
        }

        // Dummy return value
        return null;
    }

    /**
     * Traverse the BPMN diagram elements, converting them into ANF documents.
     *
     * @return an ANF document
     */
    private List<AnnotationsType> annotate() {

        final List<AnnotationsType> anfs = new ArrayList<AnnotationsType>();

        for (BPMNDiagram diagram : getBPMNDiagrams()) {
            //logger.info("Annotating a diagram " + ((Plane) diagram.getBPMNPlane()).getDiagramElements());

            final AnnotationsType anf = new AnnotationsType();

            for (JAXBElement<? extends DiagramElement> element : diagram.getBPMNPlane().getDiagramElements()) {

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
