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

package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TProcess;

/**
 * BPMN Process element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnProcess extends TProcess {

    /** No-arg constructor. */
    public BpmnProcess() { }

    /**
     * Construct a BPMN Process corresponding to a CPF Net.
     *
     * This constructor is only applicable to root processes.
     *
     * @param net  a CPF Net
     * @param initializer  BPMN document construction state
     * @param collaboration  element accumulating pool participants
     * @throws CanoniserException  if the process can't be constructed
     */
    public BpmnProcess(final CpfNetType     net,
                       final Initializer    initializer,
                       final TCollaboration collaboration) throws CanoniserException {

        // Add the BPMN Process element
        initializer.populateBaseElement(this, net);

        // Add the BPMN Participant element
        collaboration.getParticipant().add(new BpmnParticipant(this, initializer));

        // Populate the BPMN Process element
        initializer.populateProcess(new ProcessWrapper(this), net);
    }
}
