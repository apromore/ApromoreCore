/**
 * 
 */
package org.processmining.models.graphbased.directed.analysis;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

/**
 * This class generate a shortest path from one node in a directed graph to
 * another.
 * 
 * @author arya
 * @email arya.adriansyah@gmail.com
 * @version Dec 12, 2008
 */
public class ShortestPathFactory {
	public static int UNLIMITEDMAX = Integer.MAX_VALUE;

	private ShortestPathFactory() {

	}

	/**
	 * Calculate shortest distance from a node to every other node in the graph.
	 * Algorithm in use is Dijkstra's algorithm written in Introduction to
	 * Algorithm, chapter 25.
	 * 
	 * @param source
	 * @param graph
	 * @return
	 */
	private static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> int[][] calculateShortestDistanceDijkstra(
			N source, DirectedGraph<N, E> graph, ShortestPathInfo<N, E> shortestPathInfo) {

		// start of Dijkstra's algorithm
		Set<N> S = new HashSet<N>(); // to store set of vertices whose final shortest-path weights from the source have already been determined
		Set<N> Q = new HashSet<N>(); // to store queue that contains all the vertices in V - S, V is total vertices in graph

		int[] d = new int[graph.getNodes().size()]; // to store shortest-path estimate
		Arrays.fill(d, ShortestPathFactory.UNLIMITEDMAX);

		int[] phi = new int[d.length];
		Arrays.fill(phi, -1); // -1 means that no node is before this node in a shortest path

		// initialize single source, as well as complete all mapping
		for (N node : graph.getNodes()) {
			Q.add(node);
		}

		// init source node
		d[shortestPathInfo.getIndexOf(source)] = 0;

		// start loop
		while (!Q.isEmpty()) {
			N currentNode = extractMin(d, Q, shortestPathInfo);
			if (currentNode == null) {
				break;
			} else {
				S.add(currentNode);
				Q.remove(currentNode);

				// update each vertex connected to indexMin 
				int currentNodeIndex = shortestPathInfo.getIndexOf(currentNode);
				Collection<E> edges = graph.getOutEdges(currentNode);
				for (E edge : edges) {
					// do RELAX. Please notice that the weight of an arc is always 1 in our case 
					if (d[shortestPathInfo.getIndexOf(edge.getTarget())] > (d[currentNodeIndex] + 1)) {
						d[shortestPathInfo.getIndexOf(edge.getTarget())] = d[currentNodeIndex] + 1;
						phi[shortestPathInfo.getIndexOf(edge.getTarget())] = currentNodeIndex;
					}

				}
			}
		}

		// result consist of 2 array : 1st array for shortestpath distance, 2nd array for nodes listing
		int[][] result = new int[2][];
		result[0] = d;
		result[1] = phi;
		return result;
	}

	private static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> N extractMin(
			int[] d, Set<N> q, ShortestPathInfo<N, E> shortestPathInfo) {

		int minValue = ShortestPathFactory.UNLIMITEDMAX;
		N resultNode = null;

		// find the minimum value
		for (N node : q) {
			int currentNodeIndex = shortestPathInfo.getIndexOf(node);
			if ((d[currentNodeIndex] <= minValue) && (d[currentNodeIndex] != ShortestPathFactory.UNLIMITEDMAX)) {
				minValue = d[currentNodeIndex];
				resultNode = node;
			}
		}
		return resultNode;
	}

	public static <N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> ShortestPathInfo<N, E> calculateAllShortestDistanceDijkstra(
			DirectedGraph<N, E> graph) {

		// create shortest path info
		ShortestPathInfo<N, E> shortestPathInfo = new ShortestPathInfo<N, E>(graph);

		// insert distances to shortest path info
		for (N node : graph.getNodes()) {
			int[][] temp = calculateShortestDistanceDijkstra(node, graph, shortestPathInfo);
			int currentNodeIndex = shortestPathInfo.getIndexOf(node);
			for (int i = 0; i < temp[0].length; i++) {
				if (temp[0][i] != ShortestPathFactory.UNLIMITEDMAX) {
					shortestPathInfo.setShortestPathLength(currentNodeIndex, i, temp[0][i]);
					shortestPathInfo.setLastOnShortestPath(currentNodeIndex, i, temp[1][i]);
				}
			}
			shortestPathInfo.setShortestPathLength(currentNodeIndex, currentNodeIndex, 0); // set distance to itself with zero
		}

		return shortestPathInfo;
	}
}
