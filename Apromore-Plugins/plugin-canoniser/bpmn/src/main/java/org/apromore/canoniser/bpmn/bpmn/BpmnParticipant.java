/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

// Java 2 Standard packages
import javax.xml.namespace.QName;

// Local packages
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;

/**
 * BPMN Participant element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnParticipant extends TParticipant {

    /** No-arg constructor. */
    public BpmnParticipant() { }

    /**
     * Construct a BPMN Participant corresponding to (and referencing) a BPMN Process.
     *
     * @param process  the BPMN Process this participant references
     * @param initializer  BPMN document construction state
     */
    public BpmnParticipant(final TProcess process,
                           final Initializer initializer) {

        setId(initializer.newId(process.getId() + "_pool"));
        setName(process.getName());  // TODO - use an extension element for pool name if it exists
        setProcessRef(new QName(initializer.getTargetNamespace(), process.getId()));
    }
}
