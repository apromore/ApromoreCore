/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import static org.apromore.cpf.DirectionEnum.INCOMING;
import static org.apromore.cpf.DirectionEnum.OUTGOING;
import org.apromore.cpf.MessageType;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TIntermediateCatchEvent;
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

        setDirection(INCOMING);
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
     * Construct a CPF Message corresponding to a BPMN Intermediate Catch Event.
     *
     * @param endEvent  a BPMN Intermediate Catch Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfMessageType(final TIntermediateCatchEvent intermediateCatchEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, intermediateCatchEvent, initializer);

        setDirection(INCOMING);
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
    public Set<CpfEdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getOutgoingEdges() {
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
    public boolean isInterrupting() {
        return super2.isInterrupting();
    }

    /** {@inheritDoc} */
    public void setInterrupting(final boolean value) {
        super2.setInterrupting(value);
    }

    /** {@inheritDoc} */
    public boolean isSignalCatcher() {
        return super2.isSignalCatcher();
    }

    /** {@inheritDoc} */
    public QName getSignalCaughtRef() {
        return super2.getSignalCaughtRef();
    }

    /** {@inheritDoc} */
    public void setSignalCaughtRef(final QName value) {
        super2.setSignalCaughtRef(value);
    }

    /** {@inheritDoc} */
    public boolean isSignalThrower() {
        return super2.isSignalThrower();
    }

    /** {@inheritDoc} */
    public QName getSignalThrownRef() {
        return super2.getSignalThrownRef();
    }

    /** {@inheritDoc} */
    public void setSignalThrownRef(final QName value) {
        super2.setSignalThrownRef(value);
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        return CpfEventTypeImpl.toBpmn(this, initializer);
    }
}
