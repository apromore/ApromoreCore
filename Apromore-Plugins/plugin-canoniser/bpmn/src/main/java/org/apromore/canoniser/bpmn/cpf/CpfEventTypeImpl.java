package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.bpmn.BpmnEndEvent;
import org.apromore.canoniser.bpmn.bpmn.BpmnIntermediateThrowEvent;
import org.apromore.canoniser.bpmn.bpmn.BpmnStartEvent;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.utils.ExtensionUtils;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.WorkType;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;
import org.omg.spec.bpmn._20100524.model.TCatchEvent;
import org.omg.spec.bpmn._20100524.model.TCompensateEventDefinition;
import org.omg.spec.bpmn._20100524.model.TEvent;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TErrorEventDefinition;
import org.omg.spec.bpmn._20100524.model.TEventDefinition;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TSignalEventDefinition;
import org.omg.spec.bpmn._20100524.model.TTerminateEventDefinition;
import org.omg.spec.bpmn._20100524.model.TThrowEvent;

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
    void construct(final CpfEventType this2, final TCatchEvent event, final Initializer initializer) throws CanoniserException {
        initializer.populateFlowNode((WorkType) this2, event);
        construct(event.getEventDefinition(), initializer);
    }

    void construct(final CpfEventType this2, final TThrowEvent event, final Initializer initializer) throws CanoniserException {
        initializer.populateFlowNode((WorkType) this2, event);
        construct(event.getEventDefinition(), initializer);
    }

    private void construct(final List<JAXBElement<? extends TEventDefinition>> eventDefinitionList, final Initializer initializer) throws CanoniserException {
        for (JAXBElement<? extends TEventDefinition> ted : eventDefinitionList) {

            // Handle BPMN compensate event
            if (ted.getValue() instanceof TCompensateEventDefinition) {
                setCompensationActivityRef(((TCompensateEventDefinition) ted.getValue()).getActivityRef());
            }

            // Handle BPMN error event
            if (ted.getValue() instanceof TErrorEventDefinition) {
                setErrorRef(((TErrorEventDefinition) ted.getValue()).getErrorRef());
            }

            // Handle BPMN signal event
            if (ted.getValue() instanceof TSignalEventDefinition) {
                setSignalRef(((TSignalEventDefinition) ted.getValue()).getSignalRef());
            }

            // Handle BPMN terminate event
            if (ted.getValue() instanceof TTerminateEventDefinition) {

                // Later we'll know who this event's containing subprocess is
                initializer.defer(new Initialization() {
                    public void initialize() throws CanoniserException {
                        CpfNetType parent = initializer.findParent(CpfEventTypeImpl.this);
                        if (parent == null) {
                            throw new CanoniserException("CPF event " + getId() + " for BPMN Terminate event has no parent Net");
                        }

                        // A BPMN End Terminate event aborts its containing process, but CPF can only terminate nodes and edges
                        // We approximate this by exhaustively cancelling the contents fo the CPF Net

                        // Cancel all the nodes
                        for (NodeType node : parent.getNode()) {
                            final CancellationRefType cancellationRef = new CancellationRefType();
                            cancellationRef.setRefId(node.getId());
                            getCancelNodeId().add(cancellationRef);
                        }

                        // Cancel all the edges
                        for (EdgeType edge : parent.getEdge()) {
                            final CancellationRefType cancellationRef = new CancellationRefType();
                            cancellationRef.setRefId(edge.getId());
                            getCancelEdgeId().add(cancellationRef);
                        }
                    }
                });
            }
        }
    }

    /**
     * Construct a CPF Event corresponding to a BPMN Boundary Event.
     *
     * @param endEvent  a BPMN Boundary Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfEventTypeImpl(final TBoundaryEvent boundaryEvent, final Initializer initializer) throws CanoniserException {
        construct(this, boundaryEvent, initializer);
    }

    /**
     * Construct a CPF Event corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfEventTypeImpl(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        construct(this, endEvent, initializer);
    }
    /**
     * Construct a CPF Event corresponding to a BPMN Intermediate Throw Event.
     *
     * @param intermediateThrowEvent  a BPMN Intermediate Throw Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfEventTypeImpl(final TIntermediateThrowEvent intermediateThrowEvent, final Initializer initializer) throws CanoniserException {
        construct(this, intermediateThrowEvent, initializer);
    }

    /**
     * Construct a CPF Event corresponding to a BPMN Start Event.
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

    /** {@inheritDoc} */
    public boolean isCompensation() {
        return ExtensionUtils.hasExtension(getAttribute(), COMPENSATION);
    }

    /** {@inheritDoc} */
    public QName getCompensationActivityRef() {
        String s = ExtensionUtils.getString(getAttribute(), COMPENSATION);
        return s == null ? null : QName.valueOf(s);
    }

    /** {@inheritDoc} */
    public void setCompensationActivityRef(final QName value) {
        if (value == null) {
            ExtensionUtils.flagExtension(getAttribute(), COMPENSATION, true);
        } else {
            ExtensionUtils.setString(getAttribute(), COMPENSATION, value.toString());
        }
    }

    /** {@inheritDoc} */
    public boolean isError() {
        return ExtensionUtils.hasExtension(getAttribute(), ERROR);
    }

    /** {@inheritDoc} */
    public QName getErrorRef() {
        String s = ExtensionUtils.getString(getAttribute(), ERROR);
        return s == null ? null : QName.valueOf(s);
    }

    /** {@inheritDoc} */
    public void setErrorRef(final QName value) {
        if (value == null) {
            ExtensionUtils.flagExtension(getAttribute(), ERROR, true);
        } else {
            ExtensionUtils.setString(getAttribute(), ERROR, value.toString());
        }
    }

    /** {@inheritDoc} */
    public boolean isSignal() {
        return ExtensionUtils.hasExtension(getAttribute(), SIGNAL);
    }

    /** {@inheritDoc} */
    public QName getSignalRef() {
        String s = ExtensionUtils.getString(getAttribute(), SIGNAL);
        return s == null ? null : QName.valueOf(s);
    }

    /** {@inheritDoc} */
    public void setSignalRef(final QName value) {
        if (value == null) {
            ExtensionUtils.flagExtension(getAttribute(), SIGNAL, true);
        } else {
            ExtensionUtils.setString(getAttribute(), SIGNAL, value.toString());
        }
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        return toBpmn(this, initializer);
    }
    static <T extends CpfEventType> JAXBElement<? extends TFlowNode> toBpmn(final T event, final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        if (event.getIncomingEdges().size() == 0 && event.getOutgoingEdges().size() > 0) {
            // Assuming a StartEvent here, but could be TBoundaryEvent too
            return initializer.getFactory().createStartEvent(new BpmnStartEvent(event, initializer));
        } else if (event.getIncomingEdges().size() > 0 && event.getOutgoingEdges().size() == 0) {
            return initializer.getFactory().createEndEvent(new BpmnEndEvent(event, initializer));
        } else if (event.getIncomingEdges().size() > 0 && event.getOutgoingEdges().size() > 0) {
            // Assuming all intermediate events are ThrowEvents
            return initializer.getFactory().createIntermediateThrowEvent(new BpmnIntermediateThrowEvent(event, initializer));
        } else {
            throw new CanoniserException("Event \"" + event.getId() + "\" has no edges");
        }
    }
}
