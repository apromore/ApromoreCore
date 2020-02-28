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

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfTaskType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TTask;

/**
 * BPMN Task element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnTask extends TTask {

    /** No-arg constructor. */
    public BpmnTask() { }

    /**
     * Construct a BPMN Task corresponding to a CPF Task.
     *
     * @param task  a CPF Task with a defined subnet
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the subprocess can't be constructed
     */
    public BpmnTask(final CpfTaskType task,
                    final Initializer initializer) throws CanoniserException {

        initializer.populateActivity(this, task);
    }
}
