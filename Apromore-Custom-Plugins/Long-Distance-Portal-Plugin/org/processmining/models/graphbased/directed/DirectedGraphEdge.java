package org.processmining.models.graphbased.directed;

import java.util.Collection;

public interface DirectedGraphEdge<S extends DirectedGraphNode, T extends DirectedGraphNode> extends
		DirectedGraphElement {

	/**
	 * Implementations of this class should also implement DirectedGraphEdge<N,
	 * ?>
	 * 
	 * @author bfvdonge
	 * 
	 * @param <N>
	 */
	public static interface MultipleSources<N extends DirectedGraphNode> {
		/**
		 * returns all target nodes of this DirectedGraphEdge, including the
		 * node returned by getSource() of DirectedGraphEdge.
		 * 
		 * @return
		 */
		Collection<N> getSources();
	}

	/**
	 * Implementations of this class should also implement DirectedGraphEdge<?,
	 * N>
	 * 
	 * @author bfvdonge
	 * 
	 * @param <N>
	 */
	public static interface MultipleTargets<N extends DirectedGraphNode> {

		/**
		 * returns all target nodse of this DirectedGraphEdge, including the
		 * node returned by getTarget() of DirectedGraphEdge.
		 * 
		 * @return
		 */
		Collection<N> getTargets();
	}

	S getSource();

	T getTarget();

}
