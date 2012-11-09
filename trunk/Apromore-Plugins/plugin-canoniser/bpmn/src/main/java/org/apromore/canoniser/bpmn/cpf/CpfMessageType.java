package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.DirectionEnum.INCOMING;
import static org.apromore.cpf.DirectionEnum.OUTGOING;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;

/**
 * CPF 1.0 message event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfMessageType extends MessageType implements CpfEventType {

    /** Second superclass. */
    private final CpfEventType super2;

    // Constructors

    /** No-arg constructor. */
    public CpfMessageType() {
        super2 = new CpfEventTypeImpl();
    }

    /**
     * Construct a CPF Message corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfMessageType(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        super2 = new CpfEventTypeImpl(endEvent, initializer);
        CpfEventTypeImpl.construct(this, endEvent, initializer);

        setDirection(OUTGOING);
    }

    /**
     * Construct a CPF Message corresponding to a BPMN Intermediate Throw Event.
     *
     * @param endEvent  a BPMN Intermediate Throw Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfMessageType(final TIntermediateThrowEvent intermediateThrowEvent, final Initializer initializer) throws CanoniserException {
        super2 = new CpfEventTypeImpl(intermediateThrowEvent, initializer);
        CpfEventTypeImpl.construct(this, intermediateThrowEvent, initializer);

        setDirection(OUTGOING);
    }

    /**
     * Construct a CPF Message corresponding to a BPMN Start Event.
     *
     * @param startEvent  a BPMN Start Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfMessageType(final TStartEvent startEvent, final Initializer initializer) throws CanoniserException {
        super2 = new CpfEventTypeImpl(startEvent, initializer);
        CpfEventTypeImpl.construct(this, startEvent, initializer);

        setDirection(INCOMING);
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
