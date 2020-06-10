/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012, 2014 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
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

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;

/**
 * CPF 1.0 node with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfNodeTypeImpl {

    // Internal state

    /** Incoming edges. */
    private Set<CpfEdgeType> incomingEdges = new HashSet<CpfEdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<CpfEdgeType> outgoingEdges = new HashSet<CpfEdgeType>();  // TODO - diamond operator

    // Accessor methods

    /** @return every edge which has this node as its target */
    public Set<CpfEdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /** @return every edge which has this node as its source */
    public Set<CpfEdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }
}
