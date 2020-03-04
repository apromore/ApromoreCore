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

// Local packages
import org.apromore.cpf.ResourceTypeRefType;
import org.omg.spec.bpmn._20100524.model.TLane;

/**
 * CPF 1.0 resource type reference with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfResourceTypeRefType extends ResourceTypeRefType implements Attributed {

    /** No-arg constructor. */
    public CpfResourceTypeRefType() { }

    /**
     * Construct a CPF Resource reference corresponding to a BPMN Lane.
     *
     * @param lane  the BPMN Lane
     * @param initializer  CPF document construction state
     */
    public CpfResourceTypeRefType(final TLane lane, final Initializer initializer) {
        setId(initializer.newId(null));
        //setOptional(false);  // redundant, since false is the default
        setQualifier(null);
        setResourceTypeId(lane.getId());
    }
}
