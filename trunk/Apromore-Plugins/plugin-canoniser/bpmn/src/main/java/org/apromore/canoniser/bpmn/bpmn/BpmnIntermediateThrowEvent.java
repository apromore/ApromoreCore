package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;

/**
 * BPMN Intermediate Throw Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnIntermediateThrowEvent extends TIntermediateThrowEvent {

    /** No-arg constructor. */
    public BpmnIntermediateThrowEvent() { }

    /**
     * Construct a BPMN Intermediate Throw Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the throw event can't be constructed
     */
    public BpmnIntermediateThrowEvent(final CpfEventType cpfEvent, final Initializer initializer) throws CanoniserException {
        initializer.populateEvent(this, cpfEvent);
    }
}
