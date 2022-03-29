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
package org.apromore.processmining.models.graphbased.directedhypergraph;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apromore.processmining.models.cast.Cast;
import org.apromore.processmining.models.graphbased.AbstractGraphEdge;
import org.apromore.processmining.models.graphbased.AttributeMap;

public abstract class AbstractDirectedOutgoingHyperedge<S extends AbstractDirectedHypergraphNode, T extends AbstractDirectedHypergraphNode>
		extends AbstractGraphEdge<S, Set<T>> implements DirectedOutgoingHyperedge<S, T> {

	private final AbstractDirectedHypergraph<?, ?, ?> graph;

	public AbstractDirectedOutgoingHyperedge(S source, Set<T> targets, String label) {
		super(source, targets);
		// DO NOT uncomment the next line. It is not valid,
		// but the idea is clear.
		// assert(for (source:sources) {source.getGraph() ==
		// target.getGraph()});
		assert (source.getGraph() instanceof AbstractDirectedHypergraph<?, ?, ?>);
		this.graph = Cast.<AbstractDirectedHypergraph<?, ?, ?>>cast(source.getGraph());
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public AbstractDirectedHypergraph<?, ?, ?> getGraph() {
		return graph;
	}

	public int compareTo(AbstractGraphEdge<S, Set<T>> edge) {
		int c;
		c = getClass().getName().compareTo(edge.getClass().getName());
		if (c != 0) {
			return c;
		}
		// Check for same source
		c = source.compareTo(edge.getSource());
		if (c != 0) {
			return c;
		}
		// Check for same sources size
		c = target.size() - edge.getTarget().size();
		if ((c != 0) || (target.size() == 0)) {
			return c;
		}
		SortedSet<T> targets = new TreeSet<T>(target);
		SortedSet<T> edgeTargets = new TreeSet<T>(edge.getTarget());
		Iterator<T> it = targets.iterator();
		Iterator<T> it2 = edgeTargets.iterator();
		do {
			c = (it.next().compareTo(it2.next()));
		} while ((c == 0) && it.hasNext());
		return c;
	}

	public Set<T> getTargets() {
		return getTarget();
	}
}
