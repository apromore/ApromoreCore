/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.service;

import java.util.Set;

import org.apromore.dao.model.Edge;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.Resource;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.INode;
import org.apromore.service.helper.OperationContext;

/**
 * Interface for the Content Service. Defines all the methods that will do the majority of the work for
 * adding new content into the repository.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ContentService {

    /**
     * Updates the Cancel Nodes and Edges with the correct Information.
     * @param operationContext the current Operational Context.
     */
    public void updateCancelNodes(OperationContext operationContext);

    /**
     * Adds a Single node to the Repository.
     * @param cpfNode the cpfNode we are persisting.
     * @param graphType the Graph Type (Bond, Rigid, Polygon)
     * @param objects any objects attached to this node
     * @param resources any resources attached to this node
     * @return the saved Node.
     */
    Node addNode(final INode cpfNode, final String graphType, Set<org.apromore.dao.model.Object> objects,
        Set<Resource> resources);

    /**
     * Add a single Edge to the Repository.
     *
     *
     * @param cpfEdge the cpfEdge we are persisting.
     * @param fv
     * @param op
     * @return the saved Edge.
     */
    Edge addEdge(final CPFEdge cpfEdge, FragmentVersion fv, OperationContext op);
}
