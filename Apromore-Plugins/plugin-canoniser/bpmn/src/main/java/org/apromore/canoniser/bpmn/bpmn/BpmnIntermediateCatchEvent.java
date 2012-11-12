package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TIntermediateCatchEvent;

/**
 * BPMN Intermediate Catch Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnIntermediateCatchEvent extends TIntermediateCatchEvent {

    /** No-arg constructor. */
    public BpmnIntermediateCatchEvent() { }

    /**
     * Construct a BPMN Intermediate Catch Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the data object can't be constructed
     */
    public BpmnIntermediateCatchEvent(final CpfEventType cpfEvent, final Initializer initializer) throws CanoniserException {
        initializer.populateEvent(this, cpfEvent);
    }
}
