package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TEvent;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;

/**
 * CPF 1.0 event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfEventTypeImpl extends EventType implements CpfEventType {

    /** Incoming edges. */
    private Set<EdgeType> incomingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<EdgeType> outgoingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    // Constructors

    /** No-arg constructor. */
    public CpfEventTypeImpl() { }

    /**
     * Fake constructor for the secondary superclass, to be called at the beginning of every actual constructor of classes which are
     * faking multiple inheritance.
     *
     * @param this2  the instance under construction
     * @param event  a BPMN Event <code>this2</code> corresponds to
     * @param initializer  global construction state 
     * @throws CanoniserException if construction fails
     */
    void construct(final CpfEventType this2, final TEvent event, final Initializer initializer) throws CanoniserException {
        initializer.populateFlowNode((WorkType) this2, event);
    }

    /**
     * Construct a CPF Task corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfEventTypeImpl(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        construct(this, endEvent, initializer);
    }
    /**
     * Construct a CPF Task corresponding to a BPMN Intermediate Throw Event.
     *
     * @param intermediateThrowEvent  a BPMN Intermediate Throw Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfEventTypeImpl(final TIntermediateThrowEvent intermediateThrowEvent, final Initializer initializer) throws CanoniserException {
        construct(this, intermediateThrowEvent, initializer);
    }

    /**
     * Construct a CPF Task corresponding to a BPMN Start Event.
     *
     * @param startEvent  a BPMN Start Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfEventTypeImpl(final TStartEvent startEvent, final Initializer initializer) throws CanoniserException {
        construct(this, startEvent, initializer);
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
