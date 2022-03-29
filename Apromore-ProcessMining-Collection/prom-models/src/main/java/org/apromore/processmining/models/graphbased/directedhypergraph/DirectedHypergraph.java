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
package org.apromore.processmining.models.graphbased.directedhypergraph;

import java.util.Collection;

public interface DirectedHypergraph<N extends DirectedHypergraphNode, I extends DirectedIncomingHyperedge<? extends N, ? extends N>, O extends DirectedOutgoingHyperedge<? extends N, ? extends N>>
		extends DirectedHypergraphElement, Comparable<DirectedHypergraph<N, I, O>> {

	Collection<N> getNodes();

	Collection<I> getInEdges();

	Collection<O> getOutEdges();

	Collection<I> getIncomingInEdges(DirectedHypergraphNode node);

	Collection<O> getOutgoingOutEdges(DirectedHypergraphNode node);

	Collection<O> getIncomingOutEdges(DirectedHypergraphNode node);

	Collection<I> getOutgoingInEdges(DirectedHypergraphNode node);

	@SuppressWarnings("unchecked")
	void removeEdge(DirectedIncomingHyperedge edge);

	@SuppressWarnings("unchecked")
	void removeEdge(DirectedOutgoingHyperedge edge);

}
