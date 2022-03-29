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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apromore.processmining.models.cast.Cast;
import org.apromore.processmining.models.graphbased.AbstractGraph;

/**
 * 
 * @Modified Bruce Nguyen
 *      - 7 April 2020: Add Collection<E> getEdge(DirectedGraphNode source, DirectedGraphNode target);
 */
public abstract class AbstractDirectedGraph<N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>>
		extends AbstractGraph implements DirectedGraph<N, E> {

	private final Map<DirectedGraphNode, Collection<E>> inEdgeMap = new LinkedHashMap<DirectedGraphNode, Collection<E>>();;
	private final Map<DirectedGraphNode, Collection<E>> outEdgeMap = new LinkedHashMap<DirectedGraphNode, Collection<E>>();;

	public AbstractDirectedGraph() {
		super();
	}

	@Override
    public AbstractDirectedGraph<?, ?> getGraph() {
		return this;
	}

	protected abstract AbstractDirectedGraph<N, E> getEmptyClone();

	/**
	 * The returned mapping satisfies:
	 * map.keySet().containsAll(graph.getNodes()) and
	 * map.keySet().containsAll(graph.getEdges())
	 * 
	 * @param graph
	 * @return
	 */
	protected abstract Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
			DirectedGraph<N, E> graph);

	@Override
    @SuppressWarnings("unchecked")
	public abstract void removeEdge(DirectedGraphEdge edge);

	protected void removeSurroundingEdges(N node) {
		for (E edge : getInEdges(node)) {
			removeEdge(edge);
		}
		for (E edge : getOutEdges(node)) {
			removeEdge(edge);
		}
	}

	protected void checkAddEdge(N source, N target) {
		Collection<N> nodes = getNodes();
		if (!nodes.contains(source) && !nodes.contains(target)) {
			throw new IllegalArgumentException("Cannot add an arc between " + source.toString() + " and "
					+ target.toString() + ", since one of these nodes is not in the graph.");
		}

	}

	@Override
    public Collection<E> getInEdges(DirectedGraphNode node) {
		Collection<E> col = inEdgeMap.get(node);
		if (col == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList<E>(col);
		}
	}

	@Override
    public Collection<E> getOutEdges(DirectedGraphNode node) {
		Collection<E> col = outEdgeMap.get(node);
		if (col == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList<E>(col);
		}
	}
	
	@Override
    public Collection<E> getEdges(DirectedGraphNode source, DirectedGraphNode target) {
	    if (!inEdgeMap.containsKey(target) || !outEdgeMap.containsKey(source)) {
            throw new IllegalArgumentException("Cannot get an arc between " + source.toString() + " and "
                    + target.toString() + ", since one of these nodes is not in the graph.");
	    }
	    Collection<E> arcs = new ArrayList<>(inEdgeMap.get(target));
	    arcs.retainAll(outEdgeMap.get(source));
	    return arcs;
	}

	@Override
	public void graphElementAdded(Object element) {
		if (element instanceof DirectedGraphNode) {
			DirectedGraphNode node = (DirectedGraphNode) element;
			synchronized (inEdgeMap) {
				inEdgeMap.put(node, new LinkedHashSet<E>());
			}
			synchronized (outEdgeMap) {
				outEdgeMap.put(node, new LinkedHashSet<E>());
			}
		}
		if (element instanceof DirectedGraphEdge<?, ?>) {
			E edge = Cast.<E>cast(element);
			synchronized (inEdgeMap) {
				Collection<E> collection = inEdgeMap.get(edge.getTarget());
				collection.add(edge);
			}
			synchronized (outEdgeMap) {
				Collection<E> collection = outEdgeMap.get(edge.getSource());
				collection.add(edge);
			}
		}
		super.graphElementAdded(element);
	}

	@Override
    public void graphElementRemoved(Object element) {
		if (element instanceof DirectedGraphNode) {
			DirectedGraphNode node = (DirectedGraphNode) element;
			synchronized (inEdgeMap) {
				inEdgeMap.remove(node);
			}
			synchronized (outEdgeMap) {
				outEdgeMap.remove(node);
			}
		}
		if (element instanceof DirectedGraphEdge<?, ?>) {
			E edge = Cast.<E>cast(element);
			synchronized (inEdgeMap) {
				Collection<E> collection = inEdgeMap.get(edge.getTarget());
				collection.remove(element);
			}
			synchronized (outEdgeMap) {
				Collection<E> collection = outEdgeMap.get(edge.getSource());
				collection.remove(element);
			}
		}
		super.graphElementRemoved(element);
	}

	@Override
    public void graphElementChanged(Object element) {
		super.graphElementChanged(element);
	}

	@Override
    public int compareTo(DirectedGraph<N, E> o) {
		if (!(o instanceof AbstractDirectedGraph<?, ?>)) {
			return getLabel().compareTo(o.getLabel());
		}
		AbstractDirectedGraph<?, ?> graph = (AbstractDirectedGraph<?, ?>) o;
		return id.compareTo(graph.id);
	}
}
