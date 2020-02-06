package org.processmining.models.graphbased.directed;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectedGraphElementWeights extends HashMap<Object, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6950324561008737936L;

	public DirectedGraphElementWeights() {
	}

	/**
	 * Add the given weight to the given object, which will typically be a node
	 * identifier.
	 * 
	 * @param nodeId
	 *            The node identifier
	 * @param weight
	 *            The weight to add
	 */
	public void add(Object nodeId, int weight) {
		if (keySet().contains(nodeId)) {
			put(nodeId, get(nodeId) + weight);
		} else {
			put(nodeId, weight);
		}
	}

	/**
	 * Returns the current weight of the given object, which will typically be a
	 * node identifier. Returns the given default weight if no current weight is
	 * found.
	 * 
	 * @param nodeId
	 *            The node identifier
	 * @param defaultWeight
	 *            The default weight
	 * @return The weight of the node with the given identifier
	 */
	public int get(Object nodeId, int defaultWeight) {
		if (keySet().contains(nodeId)) {
			return get(nodeId);
		}
		return defaultWeight;
	}

	/**
	 * Adds the given weight to the edge identified by its source identifier,
	 * target identifier, and its own identifier. Note that the combination of
	 * these three should be unique.
	 * 
	 * @param sourceId
	 *            The source node identifier
	 * @param targetId
	 *            The target node identifier
	 * @param edgeId
	 *            The edge identifier
	 * @param weight
	 *            The weight to add
	 */
	public void add(Object sourceId, Object targetId, Object edgeId, int weight) {
		ArrayList<Object> list = new ArrayList<Object>(3);
		list.add(sourceId);
		list.add(targetId);
		list.add(edgeId);
		if (keySet().contains(list)) {
			put(list, get(list) + weight);
		} else {
			put(list, weight);
		}
	}

	/**
	 * Returns the current weight of the edge identified by its source
	 * identifier, target identifier, and its own identifier. Returns the
	 * default weight if no current weight is found.
	 * 
	 * @param sourceId
	 *            The source node identifier
	 * @param targetId
	 *            The target node identifier
	 * @param edgeId
	 *            The edge identifier
	 * @param defaultWeight
	 *            The default weight
	 * @return The weight of the edge identified by its source identifier,
	 *         target identifier, and its own identifier
	 */
	public int get(Object sourceId, Object targetId, Object edgeId, int defaultWeight) {
		ArrayList<Object> list = new ArrayList<Object>(3);
		list.add(sourceId);
		list.add(targetId);
		list.add(edgeId);
		if (keySet().contains(list)) {
			return get(list);
		}
		return defaultWeight;
	}
}
