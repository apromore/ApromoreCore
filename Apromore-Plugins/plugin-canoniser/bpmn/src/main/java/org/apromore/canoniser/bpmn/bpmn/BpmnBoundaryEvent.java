/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;

/**
 * BPMN Start Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnBoundaryEvent extends TBoundaryEvent {

    /** No-arg constructor. */
    public BpmnBoundaryEvent() { }

    /**
     * Construct a BPMN Boundary Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param attachedId  the CPF id of the task this boundary event is attached to
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the boundary event can't be constructed
     */
    public BpmnBoundaryEvent(final CpfEventType cpfEvent, final String attachedId, final Initializer initializer) throws CanoniserException {
        initializer.populateEvent(this, cpfEvent);

        setCancelActivity(cpfEvent.isInterrupting());

        initializer.defer(new Initialization() {
            public void initialize() {
                setAttachedToRef(new QName(initializer.getTargetNamespace(), initializer.findElement(attachedId).getId()));
            }
        });
    }
}
