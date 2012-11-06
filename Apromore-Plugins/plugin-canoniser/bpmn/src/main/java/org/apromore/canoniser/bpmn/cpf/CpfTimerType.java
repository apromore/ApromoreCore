package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.TimerType;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;

/**
 * CPF 1.0 timer event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfTimerType extends TimerType implements CpfEventType {

    /** Incoming edges. */
    private Set<EdgeType> incomingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<EdgeType> outgoingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    // Constructors

    /** No-arg constructor. */
    public CpfTimerType() { }

    /**
     * Construct a CPF Timer corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        initializer.populateFlowNode(this, endEvent);
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Start Event.
     *
     * @param startEvent  a BPMN Start Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TStartEvent startEvent, final Initializer initializer) throws CanoniserException {
        initializer.populateFlowNode(this, startEvent);
    }

    // Accessor methods

    /**
     * @return every edge which has this node as its target
     */
    public Set<EdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * @return every edge which has this node as its source
     */
    public Set<EdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }
}
