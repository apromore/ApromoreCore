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

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ResourceTypeType;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TLaneSet;

/**
 * BPMN Lane element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnLane extends TLane {

    /** No-arg constructor. */
    public BpmnLane() { }

    /**
     * Construct a BPMN Lane corresponding to a CPF Resource.
     *
     * @param resourceType  a CPF ResourceType
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the lane can't be constructed
     */
    public BpmnLane(final CpfResourceTypeType resourceType, final Initializer initializer) throws CanoniserException {

        initializer.populateBaseElement(this, resourceType);
        addChildLanes(this, initializer);
    }

    /**
     * Recursively populate a BPMN {@link TLane}'s child lanes.
     *
     * TODO - circular resource type chains cause non-termination!  Need to check for and prevent this.
     *
     * @throws CanoniserException  if the lane can't be constructed
     */
    private static void addChildLanes(final TLane parentLane, final Initializer initializer) throws CanoniserException {

        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : initializer.getResourceTypes()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().contains(parentLane.getId())) {
                laneSet.getLane().add(new BpmnLane(cpfResourceType, initializer));
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            parentLane.setChildLaneSet(laneSet);
        }
    }

}
