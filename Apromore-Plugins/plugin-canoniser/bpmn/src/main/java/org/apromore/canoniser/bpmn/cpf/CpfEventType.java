/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local classes
import org.apromore.cpf.DirectionEnum;

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

    /** {@link TypeAttribute#name} indicating that a BPMN BoundaryEvent cancels its attached task. */
    String INTERRUPTING = "interrupting";

    /** {@link TypeAttribute#name} indicating that a BPMN Catching Event is a signal and its associated signal reference. */
    String SIGNAL_CAUGHT = "signalCaught";

    /** {@link TypeAttribute#name} indicating that a BPMN Throwing Event is a signal and its associated signal reference. */
    String SIGNAL_THROWN = "signalThrown";

    // Accessors

    /** @return whether {@link #setCompensationActivityRef} has been called on this instance */
    boolean isCompensation();

    /** @return the value assigned by {@link #setCompensationActivityRef}, which may be <code>null</code> */
    QName getCompensationActivityRef();

    /** @param compensationActivityRef  the BPMN compensation reference, or <code>null</code> to mark a compensation event without a reference */
    void setCompensationActivityRef(QName compensationActivityRef);

    /** @return whether this catches incoming events, or throws outgoing ones */
    public DirectionEnum getDirection();

    /** @return whether {@link #setErrorRef} has been called on this instance */
    boolean isError();

    /** @return the value assigned by {@link #setErrorRef}, which may be <code>null</code> */
    QName getErrorRef();

    /** @param signalRef  the BPMN error reference, or <code>null</code> to mark an error event without a reference */
    void setErrorRef(QName errorRef);

    /** @return whether this corresponds to an interrupting BPMN boundary event */
    boolean isInterrupting();

    /** @param value  whether this event corresponds to an interrupting BPMN boundary event */
    void setInterrupting(boolean value);

    /** @return whether {@link #setSignalCaughtRef} has been called on this instance */
    boolean isSignalCatcher();

    /** @return the value assigned by {@link #setSignalCaughtRef}, which may be <code>null</code> */
    QName getSignalCaughtRef();

    /** @param signalRef  the BPMN signal reference, or <code>null</code> to mark a signal event without a reference */
    void setSignalCaughtRef(QName signalRef);

    /** @return whether {@link #setSignalThrownRef} has been called on this instance */
    boolean isSignalThrower();

    /** @return the value assigned by {@link #setSignalThrownRef}, which may be <code>null</code> */
    QName getSignalThrownRef();

    /** @param signalRef  the BPMN signal reference, or <code>null</code> to mark a signal event without a reference */
    void setSignalThrownRef(QName signalRef);
}
