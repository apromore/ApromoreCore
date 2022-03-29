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
package org.apromore.processmining.models.graphbased.undirected;

import java.util.ArrayList;
import java.util.Collection;

import org.apromore.processmining.models.graphbased.AbstractGraph;

public abstract class AbstractUndirectedGraph<N extends UndirectedGraphNode, E extends UndirectedGraphEdge<? extends N>>
		extends AbstractGraph implements UndirectedGraph<N, E> {

	public AbstractUndirectedGraph() {
		super();
	}

	public AbstractUndirectedGraph<?, ?> getGraph() {
		return this;
	}

	protected abstract AbstractUndirectedGraph<N, E> getEmptyClone();

	protected abstract void cloneFrom(UndirectedGraph<N, E> graph);

	@SuppressWarnings("unchecked")
	public abstract void removeEdge(UndirectedGraphEdge edge);

	protected void checkAddEdge(N source, N target) {
		Collection<N> nodes = getNodes();
		if (!nodes.contains(source) && !nodes.contains(target)) {
			throw new IllegalArgumentException("Cannot add an arc between " + source.toString() + " and "
					+ target.toString() + ", since one of these nodes is not in the graph.");
		}

	}

	public Collection<E> getEdges(UndirectedGraphNode node) {
		Collection<E> edges = new ArrayList<E>();
		for (E edge : getEdges()) {
			if (edge.getTarget().equals(node) || edge.getSource().equals(node)) {
				edges.add(edge);
			}
		}
		return edges;
	}

	public int compareTo(UndirectedGraph<N, E> o) {
		if (!(o instanceof AbstractUndirectedGraph<?, ?>)) {
			return getLabel().compareTo(o.getLabel());
		}
		AbstractUndirectedGraph<?, ?> graph = (AbstractUndirectedGraph<?, ?>) o;
		return id.compareTo(graph.id);
	}

}
