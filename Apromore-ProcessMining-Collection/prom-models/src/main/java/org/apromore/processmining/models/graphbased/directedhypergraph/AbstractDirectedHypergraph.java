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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apromore.processmining.models.graphbased.AbstractGraph;

public abstract class AbstractDirectedHypergraph<N extends DirectedHypergraphNode, I extends DirectedIncomingHyperedge<? extends N, ? extends N>, O extends DirectedOutgoingHyperedge<? extends N, ? extends N>>
		extends AbstractGraph implements DirectedHypergraph<N, I, O> {

	public AbstractDirectedHypergraph() {
		super();
	}

	public AbstractDirectedHypergraph<?, ?, ?> getGraph() {
		return this;
	}

	protected abstract AbstractDirectedHypergraph<N, I, O> getEmptyClone();

	protected abstract void cloneFrom(AbstractDirectedHypergraph<N, I, O> graph);

	@SuppressWarnings("unchecked")
	public abstract void removeEdge(DirectedIncomingHyperedge edge);

	@SuppressWarnings("unchecked")
	public abstract void removeEdge(DirectedOutgoingHyperedge edge);

	protected void checkAddEdge(N source, Set<N> targets) {
		Collection<N> nodes = getNodes();
		if (!nodes.contains(source) && !nodes.containsAll(targets)) {
			throw new IllegalArgumentException("Cannot add an edge between " + source.toString() + " and "
					+ targets.toString() + ", since one of these nodes is not in the graph.");
		}

	}

	protected void checkAddEdge(Set<N> sources, N target) {
		Collection<N> nodes = getNodes();
		if (!nodes.containsAll(sources) && !nodes.contains(target)) {
			throw new IllegalArgumentException("Cannot add an edge between " + sources.toString() + " and "
					+ target.toString() + ", since one of these nodes is not in the graph.");
		}

	}

	public Collection<I> getIncomingInEdges(DirectedHypergraphNode node) {
		Collection<I> edges = new ArrayList<I>();
		for (I edge : getInEdges()) {
			if (edge.getTarget().equals(node)) {
				edges.add(edge);
			}
		}
		return edges;
	}

	public Collection<O> getOutgoingOutEdges(DirectedHypergraphNode node) {
		Collection<O> edges = new ArrayList<O>();
		for (O edge : getOutEdges()) {
			if (edge.getSource().equals(node)) {
				edges.add(edge);
			}
		}
		return edges;

	}

	public Collection<O> getIncomingOutEdges(DirectedHypergraphNode node) {
		Collection<O> edges = new ArrayList<O>();
		for (O edge : getOutEdges()) {
			if (edge.getTargets().contains(node)) {
				edges.add(edge);
			}
		}
		return edges;
	}

	public Collection<I> getOutgoingInEdges(DirectedHypergraphNode node) {
		Collection<I> edges = new ArrayList<I>();
		for (I edge : getInEdges()) {
			if (edge.getSources().contains(node)) {
				edges.add(edge);
			}
		}
		return edges;
	}

	public int compareTo(DirectedHypergraph<N, I, O> o) {
		if (!(o instanceof AbstractDirectedHypergraph<?, ?, ?>)) {
			return getLabel().compareTo(o.getLabel());
		}
		AbstractDirectedHypergraph<?, ?, ?> graph = (AbstractDirectedHypergraph<?, ?, ?>) o;
		return id.compareTo(graph.id);
	}

}
