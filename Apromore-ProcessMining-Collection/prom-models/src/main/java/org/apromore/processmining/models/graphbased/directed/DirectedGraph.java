/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.processmining.models.graphbased.directed;

import java.util.Collection;
import java.util.Set;
/**
 * 
 * @Modified Bruce Nguyen
 *      - 7 April 2020: Add Collection<E> getEdges(DirectedGraphNode source, DirectedGraphNode target);
 */
public interface DirectedGraph<N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>>
		extends DirectedGraphElement, Comparable<DirectedGraph<N, E>> {

	Set<N> getNodes();

	Set<E> getEdges();

	Collection<E> getInEdges(DirectedGraphNode node);

	Collection<E> getOutEdges(DirectedGraphNode node);
	
	Collection<E> getEdges(DirectedGraphNode source, DirectedGraphNode target);

	/**
	 * Removes the given edge from the graph.
	 * 
	 * @param edge
	 */
	@SuppressWarnings("unchecked")
	void removeEdge(DirectedGraphEdge edge);

	void removeNode(DirectedGraphNode cell);

}
