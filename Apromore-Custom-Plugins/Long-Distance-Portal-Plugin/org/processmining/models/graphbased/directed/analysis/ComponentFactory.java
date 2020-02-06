package org.processmining.models.graphbased.directed.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

public class ComponentFactory {

	/**
	 * Computes strongly connected components for the given directed graph.
	 * 
	 * @param <N>
	 *            Node type of the graph
	 * @param <E>
	 *            Edge type of the graph
	 * @param graph
	 *            The graph to componentize
	 * @return The set of strongly connected components of the graph
	 */
	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> Collection<Collection<N>> componentize(
			DirectedGraph<N, E> graph) {
		/**
		 * The set of nodes to still componentize.
		 */
		Collection<N> toDo = new HashSet<N>(graph.getNodes());
		/**
		 * The set of components found so far.
		 */
		Collection<Collection<N>> components = new HashSet<Collection<N>>();
		/**
		 * Until no nodes need componentizing...
		 */
//		System.out.println("E: " + graph.getEdges().size());
//		System.out.println("N: " + graph.getNodes().size());
		while (!toDo.isEmpty()) {
			/**
			 * Take a node, and create a component for it.
			 */
			N node = toDo.iterator().next();
			toDo.remove(node);
			Collection<N> component = new TreeSet<N>();
			component.add(node);
			/**
			 * Add the successors of this node, if possible (that is, if there
			 * is some path from the successor to the selected node.
			 */
			HashSet<N> reDo = new HashSet<N>();
			Collection<E> edges = graph.getOutEdges(node);
			for (E edge : edges) {
				N succNode = edge.getTarget();
				checkForComponent(graph, succNode, component, toDo, reDo);
			}
			/**
			 * Add the component.
			 */
//			System.out.println("REDO: " + reDo.size() + "    " + System.currentTimeMillis());
			components.add(component);
			toDo.addAll(reDo);
//			System.out.println("TODO: " + toDo.size() + "    " + System.currentTimeMillis());
		}
		return components;
	}

	/**
	 * Add all nodes reachable from the given node from which some node in the
	 * given component is reachable.
	 * 
	 * @param <N>
	 *            The type of nodes in the graph
	 * @param <E>
	 *            The type of edges in the graph
	 * @param graph
	 *            The given graph
	 * @param node
	 *            The given node
	 * @param component
	 *            The component
	 * @param toDo
	 *            The nodes to consider
	 */
	private static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> void checkForComponent(
			DirectedGraph<N, E> graph, N node, Collection<N> component, Collection<N> toDo, Collection<N> reDo) {
		/**
		 * Check whether this node should be considered
		 */
		if (toDo.contains(node)) {
			/**
			 * Node is being considered now...
			 */
			toDo.remove(node);
			/**
			 * Check all successors...
			 */
			boolean added = false;
			Collection<E> edges = graph.getOutEdges(node);
			for (E edge : edges) {
				N succNode = edge.getTarget();
				if (component.contains(succNode)) {
					/**
					 * Found a successor with a path to the component. Hence
					 * there is a path from this node to the componetn as well.
					 * Add it.
					 */
					added = true;
					component.add(node);
				} else {
					checkForComponent(graph, succNode, component, toDo, reDo);
				}
			}
			/**
			 * Node has not been added to component. Hence, it should be
			 * reconsidered in the future.
			 */
			if (!added) {
				reDo.add(node);
			}
		}
	}

	/**
	 * Checks whether a component is terminal, that is, whether it has no
	 * outgoing edges.
	 * 
	 * @param <N>
	 *            The type of nodes in the graph
	 * @param <E>
	 *            The type of edges in the graph
	 * @param graph
	 *            The given graph
	 * @param component
	 *            The given component
	 * @return Whether the component is terminal
	 */
	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> boolean isTerminal(
			DirectedGraph<N, E> graph, Collection<N> component) {
		for (N node : component) {
			Collection<E> edges = graph.getOutEdges(node);
			for (E edge : edges) {
				if (!component.contains(edge.getTarget())) {
					/**
					 * Found an edge leaving the component, hence it is not
					 * terminal.
					 */
					return false;
				}
			}
		}
		/**
		 * No edges leave the component, hence it is temrinal.
		 */
		return true;
	}
}
