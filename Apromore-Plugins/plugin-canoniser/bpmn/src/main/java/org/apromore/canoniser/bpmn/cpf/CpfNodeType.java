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

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Set;
import javax.xml.bind.JAXBElement;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.model.TFlowNode;

/**
 * CPF 1.0 node with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public interface CpfNodeType extends Attributed {

    // Methods already present in CPF NodeType

    /**
     * @return whether this element is configurable
     * @see {@link NodeType#getConfigurable}
     */
    Boolean isConfigurable();

    /** @return the identifier for this element, unique within the CPF document */
    String getId();

    /** @return the presentation name of this element */
    String getName();

    // Added convenience methods

    /** @return every edge which has this node as its target */
    Set<CpfEdgeType> getIncomingEdges();

    /** @return every edge which has this node as its source */
    Set<CpfEdgeType> getOutgoingEdges();

    /**
     * @param BPMN initializer  BPMN document's global construction state
     * @return a BPMN element corresponding to this CPF element
     * @throws CanoniserException if the BPMN element can't be created
     */
    JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException;
}
