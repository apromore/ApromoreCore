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

public abstract class AbstractDirectedIncomingHyperedge<S extends AbstractDirectedHypergraphNode, T extends AbstractDirectedHypergraphNode>
		extends AbstractGraphEdge<Set<S>, T> implements DirectedIncomingHyperedge<S, T> {

	private final AbstractDirectedHypergraph<?, ?, ?> graph;

	public AbstractDirectedIncomingHyperedge(Set<S> sources, T target, String label) {
		super(sources, target);
		// DO NOT uncomment the next line. It is not valid,
		// but the idea is clear.
		// assert(for (source:sources) {source.getGraph() ==
		// target.getGraph()});
		assert (target.getGraph() instanceof AbstractDirectedHypergraph<?, ?, ?>);
		this.graph = Cast.<AbstractDirectedHypergraph<?, ?, ?>>cast(target.getGraph());
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public AbstractDirectedHypergraph<?, ?, ?> getGraph() {
		return graph;
	}

	public int compareTo(AbstractGraphEdge<Set<S>, T> edge) {
		int c;
		c = getClass().getName().compareTo(edge.getClass().getName());
		if (c != 0) {
			return c;
		}
		// Check for same target
		c = target.compareTo(edge.getTarget());
		if (c != 0) {
			return c;
		}
		// Check for same sources size
		c = source.size() - edge.getSource().size();
		if ((c != 0) || (source.size() == 0)) {
			return c;
		}
		SortedSet<S> sources = new TreeSet<S>(source);
		SortedSet<S> edgeSources = new TreeSet<S>(edge.getSource());
		Iterator<S> it = sources.iterator();
		Iterator<S> it2 = edgeSources.iterator();
		do {
			c = (it.next().compareTo(it2.next()));
		} while ((c == 0) && it.hasNext());
		return c;
	}

	public Set<S> getSources() {
		return getSource();
	}
}
