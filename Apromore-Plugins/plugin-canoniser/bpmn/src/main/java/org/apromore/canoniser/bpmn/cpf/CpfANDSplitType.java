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

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;

/**
 * CPF 1.0 AND split routing with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfANDSplitType extends ANDSplitType implements CpfRoutingType {

    /** Secondary superclass. */
    private final CpfNodeTypeImpl super2;

    /** Constructor. */
    public CpfANDSplitType() {
        super2 = new CpfNodeTypeImpl();
    }

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getOutgoingEdges() {
        return super2.getOutgoingEdges();
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        TParallelGateway gateway = new TParallelGateway();
        initializer.populateGateway(gateway, this);
        return initializer.getFactory().createParallelGateway(gateway);
    }
}
