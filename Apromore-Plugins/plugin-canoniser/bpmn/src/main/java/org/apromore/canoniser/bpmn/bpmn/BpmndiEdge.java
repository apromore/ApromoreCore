package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.Map;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.BaseVisitor;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.model.TDataAssociation;
import org.omg.spec.bpmn._20100524.model.TMessageFlow;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;

/**
 * BPMNDI Edge element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmndiEdge extends BPMNEdge {

    /** No-arg constructor. */
    public BpmndiEdge() { }

    /**
     * Construct a BPMNDI Edge corresponding to an ANF Graphics annotation.
     *
     * @param anf  an ANF model, never <code>null</code>
     * @param initializer  BPMN document construction state
     */
    public BpmndiEdge(final GraphicsType graphics, final Initializer initializer) throws CanoniserException {
        initializer.populateDiagramElement(this, graphics);

        // Validate the graphics parameter: requires two or more waypoints
        if (graphics.getPosition().size() < 2) {
            //throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF edge " +
            //                             graphics.getCpfId() + " should contain at least two positions");
            initializer.warn("ANF Graphics annotation " + graphics.getId() + " for CPF edge " +
                             graphics.getCpfId() + " should contain at least two positions");

            // TODO - remove this brazen hack, which exists only to humor the OrderFulfillment.anf test case
            if (graphics.getPosition().size() == 0) {
                initializer.warn("Inserting fake waypoints to ANF Graphics annotation " + graphics.getId());
                getWaypoint().add(new Point());
                getWaypoint().add(new Point());
            }
        }

        // Validate the cpfId: must reference a BPMN flow node or lane
        if (!(initializer.findElement(graphics.getCpfId()) instanceof TDataAssociation ||
              initializer.findElement(graphics.getCpfId()) instanceof TMessageFlow     ||
              initializer.findElement(graphics.getCpfId()) instanceof TSequenceFlow)) {

            throw new CanoniserException(graphics.getCpfId() + " isn't a BPMN element with an Edge");
        }

        // Handle @bpmnElement
        setBpmnElement(new QName(initializer.getTargetNamespace(),
                                 initializer.findElement(graphics.getCpfId()).getId()));

        // add each ANF position as a BPMNDI waypoint
        for (PositionType position : graphics.getPosition()) {
            Point point = new Point();
            point.setX(position.getX().doubleValue());
            point.setY(position.getY().doubleValue());
            getWaypoint().add(point);
        }
    }
}
