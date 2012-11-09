package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.TimerType;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;

/**
 * CPF 1.0 timer event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfTimerType extends TimerType implements CpfEventType {

    /** Second superclass. */
    private final CpfEventType super2;

    // Constructors

    /** No-arg constructor. */
    public CpfTimerType() {
        super2 = new CpfEventTypeImpl();
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        super2 = new CpfEventTypeImpl(endEvent, initializer);
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Intermediate Throw Event.
     *
     * @param endEvent  a BPMN Intermediate Throw Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TIntermediateThrowEvent intermediateThrowEvent, final Initializer initializer) throws CanoniserException {
        super2 = new CpfEventTypeImpl(intermediateThrowEvent, initializer);
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Start Event.
     *
     * @param startEvent  a BPMN Start Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TStartEvent startEvent, final Initializer initializer) throws CanoniserException {
        super2 = new CpfEventTypeImpl(startEvent, initializer);
    }

    // Second superclass methods

    /** {@inheritDoc} */
    public Set<EdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<EdgeType> getOutgoingEdges() {
        return super2.getOutgoingEdges();
    }
}
