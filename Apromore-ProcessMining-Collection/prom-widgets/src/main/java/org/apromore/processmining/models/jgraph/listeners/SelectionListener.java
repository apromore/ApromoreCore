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
