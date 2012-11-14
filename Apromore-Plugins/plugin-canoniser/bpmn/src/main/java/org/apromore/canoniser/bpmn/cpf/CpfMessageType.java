package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.DirectionEnum.INCOMING;
import static org.apromore.cpf.DirectionEnum.OUTGOING;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;

/**
 * CPF 1.0 message event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfMessageType extends MessageType implements CpfEventType {

    /** Second superclass. */
    private final CpfEventTypeImpl super2 = new CpfEventTypeImpl();

    // Constructors

    /** No-arg constructor. */
    public CpfMessageType() { }

    /**
     * Construct a CPF Message corresponding to a BPMN Boundary Event.
     *
     * @param endEvent  a BPMN Boundary Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfMessageType(final TBoundaryEvent boundaryEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, boundaryEvent, initializer);
    }

    /**
     * Construct a CPF Message corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfMessageType(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, endEvent, initializer);

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
        super2.construct(this, intermediateThrowEvent, initializer);

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
        super2.construct(this, startEvent, initializer);

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

    /** {@inheritDoc} */
    public boolean isCompensation() {
        return super2.isCompensation();
    }

    /** {@inheritDoc} */
    public QName getCompensationActivityRef() {
        return super2.getCompensationActivityRef();
    }

    /** {@inheritDoc} */
    public void setCompensationActivityRef(final QName value) {
        super2.setCompensationActivityRef(value);
    }

    /** {@inheritDoc} */
    public boolean isError() {
        return super2.isError();
    }

    /** {@inheritDoc} */
    public QName getErrorRef() {
        return super2.getErrorRef();
    }

    /** {@inheritDoc} */
    public void setErrorRef(final QName value) {
        super2.setErrorRef(value);
    }

    /** {@inheritDoc} */
    public boolean isSignal() {
        return super2.isSignal();
    }

    /** {@inheritDoc} */
    public QName getSignalRef() {
        return super2.getSignalRef();
    }

    /** {@inheritDoc} */
    public void setSignalRef(final QName value) {
        super2.setSignalRef(value);
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        return CpfEventTypeImpl.toBpmn(this, initializer);
    }
}
