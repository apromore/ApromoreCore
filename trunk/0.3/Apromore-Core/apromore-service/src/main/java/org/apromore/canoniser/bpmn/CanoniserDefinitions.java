package org.apromore.canoniser.bpmn;

// Java 2 Standard packges
import java.util.HashMap;
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
import org.apromore.anf.FillType;
import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.anf.SizeType;
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
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.Definitions;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
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
import org.omg.spec.dd._20100524.di.Plane;

/**
 * BPMN 2.0 object model with canonisation methods.
 *
 * This also supports extensions to BPMN for configurable models.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.3
 */
@XmlRootElement(namespace="http://www.omg.org/spec/BPMN/20100524/MODEL",name="definitions")
public class CanoniserDefinitions extends Definitions {

    /**
     * Logger.  Named after the class.
     */
    private static final Logger logger = Logger.getLogger(CanoniserDefinitions.class.getCanonicalName());

    /**
     * The dummy value for the <code>uri</code> attributes of the paired CPF and ANF structures.
     *
     * Although the name strongly suggests this is a URI, the specification (and the XML schema) allows general strings.
     *
     * @see <a href="http://apromore.org/wp-content/uploads/2010/12/cpf.gif">Canonical Process Format UML diagram</a>
     */
    private static final String DUMMY_URI = "dummy-model-uri";

    /**
     * Canonical process model equivalent to this BPMN model.
     *
     * This is lazily initialized by the {@link #canonise} method.
     */
    @XmlTransient
    private final CanonicalProcessType cpf = new CanonicalProcessType();

    /**
     * Canonical anotations equivalent to this BPMN model.
     *
     * This is lazily initialized by the {@link #canonise} method.
     */
    @XmlTransient
    private final AnnotationsType anf = new AnnotationsType();

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
    public CanoniserDefinitions() {}

    /**
     * Construct a BPMN model from a canonical form.
     *
     * @param cpf  a canonical process model
     * @param anf  annotations for the canonical process model
     * @throws CanoniserException
     */
    public CanoniserDefinitions(CanonicalProcessType cpf, AnnotationsType anf) throws CanoniserException {

        // Maps from CPF @cpfId identifiers to BPMN ids
        final Map<String, TFlowNode> idMap = new HashMap<String, TFlowNode>();
        final Map<String, TSequenceFlow> edgeMap = new HashMap<String, TSequenceFlow>();

        // Records the CPF cpfIds of  BPMN sequence flows which need their @sourceRef|@targetRef populated
        final Map<String, TSequenceFlow> flowWithoutSourceRefMap = new HashMap<String, TSequenceFlow>();
        final Map<String, TSequenceFlow> flowWithoutTargetRefMap = new HashMap<String, TSequenceFlow>();

        // Set attributes of the document root
        setExporter("http://apromore.org");
        setExporterVersion("0.3");
        setExpressionLanguage("http://www.w3.org/1999/XPath");
        setId(null);
        setName(cpf.getName());
        setTargetNamespace("http://www.signavio.com/bpmn20");
        setTypeLanguage("http://www.w3.org/2001/XMLSchema");

        // Process components
        if (cpf != null) {
            final CanoniserObjectFactory factory = new CanoniserObjectFactory();

            for (TypeAttribute attribute : cpf.getAttribute()) {
                logger.info("CanonicalProcess attribute typeRef=" + attribute.getTypeRef() + " value=" + attribute.getValue());
            }

            String author = cpf.getAuthor();
            String creationDate = cpf.getCreationDate();
            String modificationDate = cpf.getModificationDate();
            String cpfName = cpf.getName();

            for (NetType net : cpf.getNet()) {
                logger.info("Net id=" + net.getId() + " originalID=" + net.getOriginalID());

                // Translate this CPF net to a BPMN process
                final TProcess process = new TProcess();
                process.setId("process-" + net.getId());
                getRootElements().add(factory.createProcess(process));

                for (TypeAttribute attribute : net.getAttribute()) {
                    logger.info("  attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                }

                for (EdgeType edge : net.getEdge()) {
                    logger.info("  Edge " + edge.getId());

                    for (TypeAttribute attribute : edge.getAttribute()) {
                        logger.info("     attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                    }

                    TSequenceFlow sequenceFlow = new TSequenceFlow();
                    sequenceFlow.setId("flow-" + edge.getId());
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
                    logger.info("  Node " + node.getId());

                    for (TypeAttribute attribute : node.getAttribute()) {
                        logger.info("     attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
                    }

                    node.accept(new org.apromore.cpf.BaseVisitor() {
                        @Override public void visit(final EventType that) {
                            logger.info("     Event"); 

                            TStartEvent event = new TStartEvent();
                            event.setId("event-" + node.getId());
                            idMap.put(node.getId(), event);
                            process.getFlowElements().add(factory.createStartEvent(event));
                        }

                        @Override public void visit(final TaskType that) {
                            logger.info("     Task subnetId=" + ((TaskType) node).getSubnetId()); 

                            TTask task = new TTask();
                            task.setId("task-" + node.getId());
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

            // Beware that in CPF 0.6 objects will become children of net rather than of canonicalProcess
            for (ObjectType object : cpf.getObject()) {
                logger.info("Object id=" + object.getId() + " name=" + object.getName() + " isConfigurable=" + object.isConfigurable());
                for (TypeAttribute attribute : object.getAttribute()) {
                    logger.info("  attribute " + attribute.getTypeRef() + "=" + attribute.getValue());
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
            bpmnDiagram.setId("diagram");
            getBPMNDiagrams().add(bpmnDiagram);

            // Create BPMNPlane
            final BPMNPlane bpmnPlane = new BPMNPlane();
            bpmnPlane.setId("plane");
            assert bpmnDiagram.getBPMNPlane() == null;
            bpmnDiagram.setBPMNPlane(bpmnPlane);

            for (final AnnotationType annotation : anf.getAnnotation()) {
                logger.info("Annotation id=" + annotation.getId() + " cpfId=" + annotation.getCpfId());
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
                            shape.setId(annotation.getId());
                            shape.setBpmnElement(idMap.get(annotation.getCpfId()).getId());

                            /* TODO - generated visit() methods need to throw CanoniserException
                            // a shape requires a bounding box, defined by a top-left position and a size (width and height)
                            if (graphics.getPosition().size() != 1) {
                                throw new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                    annotation.getCpfId() + " should have just one origin position");
                            }
                            if (graphics.getSize() == null) {
                                throw new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                    annotation.getCpfId() + " should specify a size");
                            }
                            */

                            // add the ANF position and size as a BPMNDI bounds
                            Bounds bounds = new Bounds();
                            bounds.setHeight(graphics.getSize().getHeight().doubleValue());
                            bounds.setWidth(graphics.getSize().getWidth().doubleValue());
                            bounds.setX(graphics.getPosition().get(0).getX().doubleValue());
                            bounds.setY(graphics.getPosition().get(0).getY().doubleValue());
                            shape.setBounds(bounds);

                            bpmnPlane.getDiagramElements().add(diObjectFactory.createBPMNShape(shape));

                        } else if(edgeMap.containsKey(annotation.getCpfId())) {
                            BPMNEdge edge = new BPMNEdge();
                            edge.setId(annotation.getId());
                            edge.setBpmnElement(edgeMap.get(annotation.getCpfId()).getId());

                            /* TODO - generated visit() methods need to throw CanoniserException
                            // an edge requires two or more waypoints
                            if (graphics.getPosition().size() < 2) {
                                throw new CanoniserException("Annotation " + annotation.getId() + " for edge " +
                                    annotation.getCpfId() + " should have at least two positions");
                            }
                            */

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
     */
    public AnnotationsType getANF() {

        // Lazy initialization
        if (!canonised) {
            canonise(DUMMY_URI);
            canonised = true;
        }
        assert canonised;

        return anf;
    }

    /**
     * The canonical process model corresponding to this BPMN.
     *
     * This is lazily initialized the first time it is accessed.
     *
     * @return canonical process model corresponding to this BPMN
     */
    public CanonicalProcessType getCPF() {

        // Lazy initialization
        if (!canonised) {
            canonise(DUMMY_URI);
            canonised = true;
        }
        assert canonised;

        return cpf;
    }

    /**
     * Initialize {@link #anf} and {@link #cpf}.
     *
     * @param uri  the shared top-level <code>uri</code>  attribute of the CPF and ANF structures
     */
    private void canonise(String uri) {

        // Top-level attributes
        cpf.setName(getName() == null ? "" : getName());  // @name is required by the CPF XML schema

        anf.setUri(uri);
        cpf.setUri(uri);

        cpf.setVersion("0.5");

        // Traverse diagram
        logger.info("Traversing diagrams");
        for (BPMNDiagram diagram : getBPMNDiagrams()) {
            logger.info("Annotating a diagram " + ((Plane) diagram.getBPMNPlane()).getDiagramElements());
            for (JAXBElement<? extends DiagramElement> element : diagram.getBPMNPlane().getDiagramElements()) {
                logger.info("Annotating an element " + element);
                element.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                    @Override
                    public void visit(final BPMNEdge edge) {
                        logger.info("Annotating an edge");
                        AnnotationType annotation = new AnnotationType();
                        annotation.setId(edge.getId());
                        annotation.setCpfId(edge.getBpmnElement().toString());
                        anf.getAnnotation().add(annotation);
                    }
                    @Override
                    public void visit(final BPMNShape shape) {
                        logger.info("Annotating a shape");
                        AnnotationType annotation = new AnnotationType();
                        annotation.setId(shape.getId());
                        annotation.setCpfId(shape.getBpmnElement().toString());
                        anf.getAnnotation().add(annotation);
                    }
                });
            }
        }

        // Traverse processes
        logger.info("Traversing processes");
        for (JAXBElement<? extends TRootElement> rootElement : getRootElements()) {
            rootElement.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                @Override
                public void visit(final TProcess process) {
                    final NetType net = new NetType();
                    net.setId(process.getId());
                    cpf.setRootId(process.getId());
                    cpf.getNet().add(net);

                    for (JAXBElement<? extends TFlowElement> flowElement : process.getFlowElements()) {
                        flowElement.getValue().accept(new org.omg.spec.bpmn._20100524.model.BaseVisitor() {
                            @Override
                            public void visit(final TEndEvent endEvent) {
                                EventType event = new EventType();
                                populateFlowElement(event, endEvent);

                                net.getNode().add(event);
                            }

                            @Override
                            public void visit(final TExclusiveGateway exclusiveGateway) {
                                RoutingType routing;

                                switch (exclusiveGateway.getGatewayDirection()) {
                                case CONVERGING: routing = new XORJoinType(); break;
                                case DIVERGING:  routing = new XORSplitType();  break;
                                default:
                                    throw new RuntimeException("Unimplemented gateway direction " + exclusiveGateway.getGatewayDirection());  // TODO - handle this properly
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
                                    throw new RuntimeException("Unimplemented gateway direction " + inclusiveGateway.getGatewayDirection());  // TODO - handle this properly
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
                                    throw new RuntimeException("Unimplemented gateway direction " + parallelGateway.getGatewayDirection());  // TODO - handle this properly
                                }
                                assert routing != null;

                                populateFlowElement(routing, parallelGateway);

                                net.getNode().add(routing);
                            }

                            @Override
                            public void visit(final TStartEvent startEvent) {
                                EventType event = new EventType();
                                populateFlowElement(event, startEvent);

                                net.getNode().add(event);
                            }

                            @Override
                            public void visit(final TSequenceFlow sequenceFlow) {
                                EdgeType edge = new EdgeType();
                                populateFlowElement(edge, sequenceFlow);

                                if (sequenceFlow.getConditionExpression() != null) {
                                    edge.setCondition(sequenceFlow.getConditionExpression().getContent().get(0).toString());  // TODO - handle non-singleton expressions
                                }
                                edge.setSourceId(((TFlowNode) sequenceFlow.getSourceRef()).getId());
                                edge.setTargetId(((TFlowNode) sequenceFlow.getTargetRef()).getId());

                                net.getEdge().add(edge);
                            }

                            @Override
                            public void visit(final TTask bpmnTask) {
                                TaskType cpfTask = new TaskType();
                                populateFlowElement(cpfTask, bpmnTask);

                                net.getNode().add(cpfTask);
                            }

                            // Edge supertype handlers

                            private void populateBaseElement(final EdgeType edge, final TBaseElement baseElement) {
                                edge.setId(baseElement.getId());
                            }

                            private void populateFlowElement(final EdgeType edge, final TFlowElement flowElement) {
                                populateBaseElement(edge, flowElement);
                            }

                            // Node supertype handlers

                            private void populateBaseElement(final NodeType node, final TBaseElement baseElement) {
                                node.setId(baseElement.getId());
                            }

                            private void populateFlowElement(final NodeType node, final TFlowElement flowElement) {
                                populateBaseElement(node, flowElement);
                                node.setName(flowElement.getName());
                            }
                        });
                    }
                }
            });
        }
    };
}
