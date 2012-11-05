package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TEndEvent;

/**
 * BPMN End Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnEndEvent extends TEndEvent {

    /** No-arg constructor. */
    public BpmnEndEvent() { }

    /**
     * Construct a BPMN End Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the data object can't be constructed
     */
    public BpmnEndEvent(final CpfEventType cpfEvent, final Initializer initializer) throws CanoniserException {

        initializer.populateFlowNode(this, cpfEvent);
    }
}
