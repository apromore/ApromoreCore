package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.BaseVisitor;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;

/**
 * BPMNDI Diagram element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/DI", name = "BPMNDiagram")
public class BpmndiDiagram extends BPMNDiagram {

    /** Logger.  Named after the class. */
    private final Logger logger = Logger.getLogger(BpmndiDiagram.class.getCanonicalName());

    /** No-arg constructor. */
    public BpmndiDiagram() {
        super();
    }

    /**
     * Construct a BPMNDI Diagram corresponding to an ANF AnnotationsType.
     *
     * @param anf  an ANF model, never <code>null</code>
     * @param initializer  BPMN document construction state
     */
    public BpmndiDiagram(final AnnotationsType anf, final Initializer initializer) {

        final BpmndiObjectFactory bpmndiObjectFactory = new BpmndiObjectFactory();

        // Create BPMNDiagram
        final BPMNDiagram bpmnDiagram = this;
        bpmnDiagram.setId(initializer.newId("diagram"));
        bpmnDiagram.setName(anf.getName());

        // Create BPMNPlane
        final BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnPlane.setId(initializer.newId("plane"));
        assert bpmnDiagram.getBPMNPlane() == null;
        bpmnDiagram.setBPMNPlane(bpmnPlane);

        // Populate the BPMNPlane with elements for each CPF Annotation
        for (final AnnotationType annotation : anf.getAnnotation()) {
            //logger.info("Annotation id=" + annotation.getId() + " cpfId=" + annotation.getCpfId());
            annotation.accept(new BaseVisitor() {
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

                    if (initializer.getElement(annotation.getCpfId()) instanceof TFlowNode ||
                        initializer.getElement(annotation.getCpfId()) instanceof TLane) {
                        BPMNShape shape = new BPMNShape();
                        shape.setId(initializer.newId(annotation.getId()));
                        shape.setBpmnElement(initializer.getElement(annotation.getCpfId()).getId());

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

                        bpmnPlane.getDiagramElement().add(bpmndiObjectFactory.createBPMNShape(shape));

                    } else if (initializer.getElement(annotation.getCpfId()) instanceof BpmnSequenceFlow) {
                        BPMNEdge edge = new BPMNEdge();
                        edge.setId(initializer.newId(annotation.getId()));
                        edge.setBpmnElement(initializer.getElement(annotation.getCpfId()).getId());

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

                        bpmnPlane.getDiagramElement().add(bpmndiObjectFactory.createBPMNEdge(edge));
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
    }
}
