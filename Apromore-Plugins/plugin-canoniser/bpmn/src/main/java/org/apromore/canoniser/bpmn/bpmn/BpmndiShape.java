package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages

import javax.xml.namespace.QName;

import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataObjectReference;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;
import org.omg.spec.bpmn._20100524.model.TEvent;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.dd._20100524.dc.Bounds;

// Local packages

/**
 * BPMNDI Shape element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmndiShape extends BPMNShape {

    private static final double SCALE = 1;

    /** No-arg constructor. */
    public BpmndiShape() { }

    /**
     * Construct a BPMNDI Shape corresponding to an ANF Graphics annotation.
     * @param graphics  an ANF graphics annotation, never <code>null</code>
     * @param initializer  BPMN document construction state
     * @throws CanoniserException if the shape can't be constructed
     */
    public BpmndiShape(final GraphicsType graphics, final Initializer initializer) throws CanoniserException {
        this(graphics, initializer, false);
    }

    /**
     * Construct a BPMNDI Shape corresponding to an ANF Graphics annotation.
     * @param graphics  an ANF graphics annotation, never <code>null</code>
     * @param initializer  BPMN document construction state
     * @param fromEPML to determine is we are converting from EPML    // TODO: Needs to be removed.
     * @throws CanoniserException if the shape can't be constructed
     */
    public BpmndiShape(final GraphicsType graphics, final Initializer initializer, final boolean fromEPML) throws CanoniserException {
        initializer.populateDiagramElement(this, graphics);

        // Validate the graphics parameter: requires a bounding box, defined by a top-left position and a size (width and height)
        if (graphics.getPosition().size() != 1) {
            throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF shape " +
                    graphics.getCpfId() + " should have just one origin position");
        }
        if (graphics.getSize() == null) {
            throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF shape " +
                    graphics.getCpfId() + " should specify a size");
        }

        // Validate the cpfId: must reference a BPMN element that has a bounding box
        TBaseElement bpmnElement = initializer.findElement(graphics.getCpfId());
        if (bpmnElement instanceof TProcess) {
            java.util.logging.Logger.getAnonymousLogger().warning(
                    "ANF graphics " + graphics.getId() + " references CPF element " + graphics.getCpfId() + " which corresponds to a BPMN process " +
                            bpmnElement.getId()
            );
        } else if (!(bpmnElement instanceof TDataObject          ||
                bpmnElement instanceof TDataObjectReference ||
                bpmnElement instanceof TDataStoreReference  ||
                bpmnElement instanceof TFlowNode            ||
                bpmnElement instanceof TLane                ||
                bpmnElement instanceof TParticipant         ||
                bpmnElement instanceof TProcess)) {  // TODO - decide whether TProcess really is legitimate to have a BPMNShape

            throw new CanoniserException(graphics.getCpfId() + " isn't a BPMN element with a Shape");
        }

        // Handle @bpmnElement
        setBpmnElement(new QName(initializer.getTargetNamespace(),
                initializer.findElement(graphics.getCpfId()).getId()));

        // Handle @isExpanded
        if (bpmnElement instanceof TSubProcess) {
            setIsExpanded(true);  // TODO - this should dereference graphics.getCpfId() to determine a proper value
        }

        // add the ANF position and size as a BPMNDI bounds
        Bounds bounds = new Bounds();
        if (fromEPML) {
            if (bpmnElement instanceof TEvent || bpmnElement instanceof TActivity) {
                bounds.setHeight(30.0);
                bounds.setWidth(30.0);
                bounds.setX(graphics.getPosition().get(0).getX().doubleValue() + (graphics.getSize().getWidth().doubleValue() - 30.0) / 2);
                bounds.setY(graphics.getPosition().get(0).getY().doubleValue() + (graphics.getSize().getHeight().doubleValue() - 30.0) / 2);
            } else if (bpmnElement instanceof TGateway) {
                bounds.setHeight(40.0);
                bounds.setWidth(40.0);
                bounds.setX(graphics.getPosition().get(0).getX().doubleValue() + (graphics.getSize().getWidth().doubleValue() - 40.0) / 2);
                bounds.setY(graphics.getPosition().get(0).getY().doubleValue() + (graphics.getSize().getHeight().doubleValue() - 40.0) / 2);
            }
        } else {
            bounds.setHeight(SCALE * graphics.getSize().getHeight().doubleValue());
            bounds.setWidth(SCALE * graphics.getSize().getWidth().doubleValue());
            bounds.setX(SCALE * graphics.getPosition().get(0).getX().doubleValue());
            bounds.setY(SCALE * graphics.getPosition().get(0).getY().doubleValue());
        }
        setBounds(bounds);
    }
}
