package org.processmining.models.graphbased.directed.utils;

import java.util.Collection;
import java.util.HashSet;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

public class GraphIterator {

	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> Collection<N> getDirectSuccessors(
			final N node, DirectedGraph<N, E> graph) {
		return getDepthFirstSuccessors(node, graph, new EdgeAcceptor<N, E>() {
			public boolean acceptEdge(E edge, int depth) {
				return depth == 0;
			}
		}, new NodeAcceptor<N>() {
			public boolean acceptNode(N node, int depth) {
				return true;
			}
		});
	}

	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> Collection<N> getDepthFirstSuccessors(
			N node, DirectedGraph<N, E> graph, EdgeAcceptor<N, E> edgeAcceptor, NodeAcceptor<N> nodeAcceptor) {
		Collection<N> result = new HashSet<N>();
		HashSet<N> seen = new HashSet<N>();
		getDepthFirstSuccessors(node, graph, result, edgeAcceptor, nodeAcceptor, 0, seen);
		return result;

	}

	private static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> void getDepthFirstSuccessors(
			N node, DirectedGraph<N, E> graph, Collection<N> result, EdgeAcceptor<N, E> edgeAcceptor,
			NodeAcceptor<N> nodeAcceptor, final int depth, final HashSet<N> seen) {

		for (E edge : graph.getOutEdges(node)) {
			if (edgeAcceptor.acceptEdge(edge, depth)) {
				N target = edge.getTarget();
				if (nodeAcceptor.acceptNode(target, depth)) {
					result.add(target);
				}
				if (!seen.contains(target)) {
					seen.add(target);
					getDepthFirstSuccessors(target, graph, result, edgeAcceptor, nodeAcceptor, depth + 1, seen);
					seen.remove(target);
				}
			}
		}
	}

	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> Collection<N> getDirectPredecessors(
			final N node, DirectedGraph<N, E> graph) {
		return getDepthFirstPredecessors(node, graph, new EdgeAcceptor<N, E>() {
			public boolean acceptEdge(E edge, int depth) {
				return depth == 0;
			}
		}, new NodeAcceptor<N>() {
			public boolean acceptNode(N node, int depth) {
				return true;
			}
		});
	}

	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> Collection<N> getDepthFirstPredecessors(
			N node, DirectedGraph<N, E> graph, EdgeAcceptor<N, E> edgeAcceptor, NodeAcceptor<N> nodeAcceptor) {
		Collection<N> result = new HashSet<N>();
		HashSet<N> seen = new HashSet<N>();
		getDepthFirstPredecessors(node, graph, result, edgeAcceptor, nodeAcceptor, 0, seen);
		return result;

	}

	private static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> void getDepthFirstPredecessors(
			N node, DirectedGraph<N, E> graph, Collection<N> result, EdgeAcceptor<N, E> edgeAcceptor,
			NodeAcceptor<N> nodeAcceptor, final int depth, final HashSet<N> seen) {

		for (E edge : graph.getInEdges(node)) {
			if (edgeAcceptor.acceptEdge(edge, depth)) {
				N source = edge.getSource();
				if (nodeAcceptor.acceptNode(source, depth)) {
					result.add(source);
				}
				if (!seen.contains(source)) {
					seen.add(source);
					getDepthFirstPredecessors(source, graph, result, edgeAcceptor, nodeAcceptor, depth + 1, seen);
					seen.remove(source);
				}
			}
		}
	}

	public interface EdgeAcceptor<N, E extends DirectedGraphEdge<? extends N, ? extends N>> {
		/**
		 * Used while searching. If this method returns false for a certain
		 * edge, then this edge is not traversed in the search.
		 * 
		 * @param edge
		 * @return
		 */
		public boolean acceptEdge(E edge, int depth);
	}

	public interface NodeAcceptor<T extends DirectedGraphNode> {
		/**
		 * Used while searching. If this method returns false for a certain
		 * node, then this node is not added to the search result and the search
		 * does not continue along this node.
		 * 
		 * @param edge
		 * @return
		 */
		public boolean acceptNode(T node, int depth);
	}

}
