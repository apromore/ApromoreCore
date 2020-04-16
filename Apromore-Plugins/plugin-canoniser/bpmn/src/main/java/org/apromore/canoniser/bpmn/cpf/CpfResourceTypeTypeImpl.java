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
import java.util.Collections;
import java.util.List;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ResourceTypeType;
import org.omg.spec.bpmn._20100524.model.TParticipant;

/**
 * CPF 1.0 resource type with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfResourceTypeTypeImpl extends ResourceTypeType implements CpfResourceTypeType {

    /** No-arg constructor. */
    public CpfResourceTypeTypeImpl() { }

    /**
     * Construct a CPF ResourceType corresponding to a BPMN Participant.
     *
     * @param participant  a BPMN Participant
     * @param initializer  global document construction state
     * @throws CanoniserException if the resource type can't be constructed
     */
    public CpfResourceTypeTypeImpl(final TParticipant participant, final Initializer initializer) throws CanoniserException {
        //initializer.populateBaseElement(this, participant);
        setId(initializer.newId(participant.getId()));

        // Handle @name
        setName(requiredName(participant.getName()));

        //initializer.addResourceType(this);
    }

    /**
     * @return every other resource type which has this one as a specialization
     */
    public List<CpfResourceTypeType> getGeneralizationRefs() {
        return Collections.emptyList();
    }
}
