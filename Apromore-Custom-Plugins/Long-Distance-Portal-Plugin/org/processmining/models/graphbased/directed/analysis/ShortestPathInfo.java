package org.processmining.models.graphbased.directed.analysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

public class ShortestPathInfo<N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> {

	public final static int NOPATH = -1;

	private final DirectedGraph<N, E> graph;
	private final Map<N, Integer> map;
	private final int[][] lengths;
	private final int[][] lastNodeInShortestPath;

	ShortestPathInfo(DirectedGraph<N, E> graph) {
		this.graph = graph;
		int n = graph.getNodes().size();
		int i = 0;
		map = new HashMap<N, Integer>(n, 1F);
		for (N node : graph.getNodes()) {
			map.put(node, i++);
		}
		lengths = new int[n][];
		lastNodeInShortestPath = new int[n][];
		for (i = 0; i < n; i++) {
			lengths[i] = new int[n];
			lastNodeInShortestPath[i] = new int[n];
			Arrays.fill(lengths[i], ShortestPathInfo.NOPATH);
			Arrays.fill(lastNodeInShortestPath[i], -1);
		}
	}

	public int getShortestPathLength(N source, N target) {
		assert (map.get(source) != null);
		assert (map.get(target) != null);
		return lengths[map.get(source)][map.get(target)];
	}

	void setShortestPathLength(N source, N target, int length) {
		assert (map.get(source) != null);
		assert (map.get(target) != null);
		assert ((length == ShortestPathInfo.NOPATH) || (length >= 0));
		lengths[map.get(source)][map.get(target)] = length;
	}

	int getIndexOf(N node) {
		assert (map.get(node) != null);
		return map.get(node);
	}

	void setShortestPathLength(int sourceIndex, int targetIndex, int length) {
		assert (0 <= sourceIndex) && (sourceIndex < lengths.length);
		assert (0 <= targetIndex) && (targetIndex < lengths.length);
		assert ((length == ShortestPathInfo.NOPATH) || (length >= 0));
		lengths[sourceIndex][targetIndex] = length;
	}

	/**
	 * Returns the shortest path from the source to the target. If
	 * source.equals(target), then a list of length 1 is returned. Otherwise,
	 * the list returned contains the nodes from source to target that make up
	 * the path, including source as the first node and target as the last node.
	 * 
	 * If no path exist, an empty list is returned.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public List<N> getShortestPath(N source, N target) {
		int sourceIndex = getIndexOf(source);
		int targetIndex = getIndexOf(target);

		// check if target has a previous node in the shortest path
		List<N> result = new LinkedList<N>();
		result.add(target);
		while (lastNodeInShortestPath[sourceIndex][targetIndex] >= 0) {
			// find a node which is mapped to previousNodeInShortesPath[sourceIndex][targetIndex]
			N temp = getNodeFromIndex(lastNodeInShortestPath[sourceIndex][targetIndex]);
			if (temp != null) {
				result.add(temp);
				targetIndex = lastNodeInShortestPath[sourceIndex][targetIndex];
			} else {
				return Collections.emptyList();
			}
		}
		if (targetIndex == sourceIndex) {
			Collections.reverse(result);
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	private N getNodeFromIndex(int index) {
		if (!map.containsValue(index)) {
			return null;
		} else {
			for (N node : graph.getNodes()) {
				if (map.get(node) == index) {
					return node;
				}
			}
			return null;
		}
	}

	void setLastOnShortestPath(int fromNodeIndex, int toNodeIndex, int lastNodeIndex) {
		assert (0 <= fromNodeIndex) && (fromNodeIndex < lengths.length);
		assert (0 <= toNodeIndex) && (toNodeIndex < lengths.length);
		assert (-1 <= lastNodeIndex) && (lastNodeIndex < lengths.length);
		assert (lengths[fromNodeIndex][toNodeIndex] >= 0);
		assert ((lengths[fromNodeIndex][toNodeIndex] > 0) || (lastNodeIndex == -1));
		lastNodeInShortestPath[fromNodeIndex][toNodeIndex] = lastNodeIndex;
	}
}
