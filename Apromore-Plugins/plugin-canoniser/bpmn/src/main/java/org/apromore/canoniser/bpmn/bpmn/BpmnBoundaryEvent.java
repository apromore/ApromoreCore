package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;

/**
 * BPMN Start Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnBoundaryEvent extends TBoundaryEvent {

    /** No-arg constructor. */
    public BpmnBoundaryEvent() { }

    /**
     * Construct a BPMN Boundary Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param attachedId  the CPF id of the task this boundary event is attached to
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the boundary event can't be constructed
     */
    public BpmnBoundaryEvent(final CpfEventType cpfEvent, final String attachedId, final Initializer initializer) throws CanoniserException {
        initializer.populateEvent(this, cpfEvent);

        setCancelActivity(cpfEvent.isInterrupting());

        initializer.defer(new Initialization() {
            public void initialize() {
                setAttachedToRef(new QName(initializer.getTargetNamespace(), initializer.findElement(attachedId).getId()));
            }
        });
    }
}
