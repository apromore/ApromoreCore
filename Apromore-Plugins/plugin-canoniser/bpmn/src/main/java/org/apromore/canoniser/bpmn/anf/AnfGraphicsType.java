package org.apromore.canoniser.bpmn.anf;

// Local packages
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.bpmn.IdFactory;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.dd._20100524.dc.Point;

/**
 * ANF 0.3 annotation element.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AnfGraphicsType extends GraphicsType {

    /** No-arg constructor. */
    public AnfGraphicsType() {
        super();
    }

    /**
     * Construct a graphics annotation for a BPMNDI Edge.
     *
     * @param edge  a BPMNDI Edge
     * @param anfIdFactory  generator for identifiers
     */
    public AnfGraphicsType(final BPMNEdge edge, final IdFactory anfIdFactory) {
        setId(anfIdFactory.newId(edge.getId()));
        setCpfId(edge.getBpmnElement().getLocalPart());  // TODO - process through cpfIdFactory instead

        // Each waypoint becomes a position
        for (Point waypoint : edge.getWaypoint()) {
            getPosition().add(new AnfPositionType(waypoint));
        }
    }

    /**
     * Construct an annotation for a BPMNDI Shape.
     *
     * @param shape  a BPMNDI Shape
     * @param anfIdFactory  generator for identifiers
     */
    public AnfGraphicsType(final BPMNShape shape, final IdFactory anfIdFactory) {
        setId(anfIdFactory.newId(shape.getId()));
        setCpfId(shape.getBpmnElement().getLocalPart());  // TODO - process through cpfIdFactory instead

        // The bounds become a position and size
        getPosition().add(new AnfPositionType(shape.getBounds()));
        setSize(new AnfSizeType(shape.getBounds()));
    }
}
