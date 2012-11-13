package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TStartEvent;

/**
 * BPMN Start Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnStartEvent extends TStartEvent {

    /** No-arg constructor. */
    public BpmnStartEvent() { }

    /**
     * Construct a BPMN Start Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the start event can't be constructed
     */
    public BpmnStartEvent(final CpfEventType cpfEvent, final Initializer initializer) throws CanoniserException {
        initializer.populateEvent(this, cpfEvent);
    }
}
