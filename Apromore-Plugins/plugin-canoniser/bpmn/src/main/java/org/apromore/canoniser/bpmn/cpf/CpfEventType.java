package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.namespace.QName;

/**
 * CPF 1.0 event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface CpfEventType extends CpfWorkType {

    // Constants

    /** {@link TypeAttribute#name} indicating that a BPMN Event is for compensation and its associated compensating activity reference. */
    String COMPENSATION = "compensation";

    /** {@link TypeAttribute#name} indicating that a BPMN Event is an error and its associated error reference. */
    String ERROR = "error";

    /** {@link TypeAttribute#name} indicating that a BPMN Event is a signal and its associated signal reference. */
    String SIGNAL = "signal";

    // Accessors

    /** @return whether {@link #setCompensationActivityRef} has been called on this instance */
    boolean isCompensation();

    /** @return the value assigned by {@link #setCompensationActivityRef}, which may be <code>null</code> */
    QName getCompensationActivityRef();

    /** @param compensationActivityRef  the BPMN compensation reference, or <code>null</code> to mark a compensation event without a reference */
    void setCompensationActivityRef(QName compensationActivityRef);

    /** @return whether {@link #setErrorRef} has been called on this instance */
    boolean isError();

    /** @return the value assigned by {@link #setErrorRef}, which may be <code>null</code> */
    QName getErrorRef();

    /** @param signalRef  the BPMN error reference, or <code>null</code> to mark an error event without a reference */
    void setErrorRef(QName errorRef);

    /** @return whether {@link #setSignalRef} has been called on this instance */
    boolean isSignal();

    /** @return the value assigned by {@link #setSignalRef}, which may be <code>null</code> */
    QName getSignalRef();

    /** @param signalRef  the BPMN signal reference, or <code>null</code> to mark a signal event without a reference */
    void setSignalRef(QName signalRef);
}
