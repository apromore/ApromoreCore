package org.apromore.processmining.models.jgraph.listeners;

import java.util.Collection;

public interface SelectionListener<N, E> {

	void SelectionChanged(SelectionChangeEvent<N, E> event);

	public static class SelectionChangeEvent<N, E> {
		private final Collection<N> addedNodes;
		private final Collection<E> addedEdges;
		private final Collection<N> removedNodes;
		private final Collection<E> removedEdges;

		public SelectionChangeEvent(Collection<N> addedNodes, Collection<E> addedEdges, Collection<N> removedNodes,
				Collection<E> removedEdges) {
			this.addedNodes = addedNodes;
			this.addedEdges = addedEdges;
			this.removedNodes = removedNodes;
			this.removedEdges = removedEdges;
		}

		public Collection<N> getAddedNodes() {
			return addedNodes;
		}

		public Collection<E> getAddedEdges() {
			return addedEdges;
		}

		public Collection<N> getRemovedNodes() {
			return removedNodes;
		}

		public Collection<E> getRemovedEdges() {
			return removedEdges;
		}

		public String toString() {
			return "N:+" + addedNodes + " -" + removedNodes + "  E:+" + addedEdges + " -" + removedEdges;
		}

	}
}
