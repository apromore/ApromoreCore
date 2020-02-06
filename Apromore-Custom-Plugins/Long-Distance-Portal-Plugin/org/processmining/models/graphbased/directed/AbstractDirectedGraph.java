package org.processmining.models.graphbased.directed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.processmining.framework.util.Cast;
import org.processmining.models.graphbased.AbstractGraph;

public abstract class AbstractDirectedGraph<N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>>
		extends AbstractGraph implements DirectedGraph<N, E> {

	private final Map<DirectedGraphNode, Collection<E>> inEdgeMap = new LinkedHashMap<DirectedGraphNode, Collection<E>>();;
	private final Map<DirectedGraphNode, Collection<E>> outEdgeMap = new LinkedHashMap<DirectedGraphNode, Collection<E>>();;

	public AbstractDirectedGraph() {
		super();
	}

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

	public Collection<E> getInEdges(DirectedGraphNode node) {
		Collection<E> col = inEdgeMap.get(node);
		if (col == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList<E>(col);
		}
	}

	public Collection<E> getOutEdges(DirectedGraphNode node) {
		Collection<E> col = outEdgeMap.get(node);
		if (col == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList<E>(col);
		}
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

	public void graphElementChanged(Object element) {
		super.graphElementChanged(element);
	}

	public int compareTo(DirectedGraph<N, E> o) {
		if (!(o instanceof AbstractDirectedGraph<?, ?>)) {
			return getLabel().compareTo(o.getLabel());
		}
		AbstractDirectedGraph<?, ?> graph = (AbstractDirectedGraph<?, ?>) o;
		return id.compareTo(graph.id);
	}
}