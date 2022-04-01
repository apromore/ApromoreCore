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
package org.apromore.processmining.models.graphbased.directed;

import lombok.NonNull;
import org.apromore.processmining.models.graphbased.AbstractGraphEdge;

public abstract class AbstractDirectedGraphEdge<S extends AbstractDirectedGraphNode, T extends AbstractDirectedGraphNode>
		extends AbstractGraphEdge<S, T> implements DirectedGraphEdge<S, T> {

	private final AbstractDirectedGraph<?, ?> graph;

	public AbstractDirectedGraphEdge(@NonNull S source, @NonNull T target) {
		super(source, target);
		assert (source.getGraph() == target.getGraph());
		this.graph = source.getGraph();
	}

	public AbstractDirectedGraphEdge(@NonNull String id, @NonNull S source, @NonNull T target) {
		super(id, source, target);
		assert (source.getGraph() == target.getGraph());
		this.graph = source.getGraph();
	}

	public AbstractDirectedGraph<?, ?> getGraph() {
		return graph;
	}

	public int compareTo(AbstractGraphEdge<S, T> edge) {
		int c;
		c = getClass().getName().compareTo(edge.getClass().getName());
		if (c != 0) {
			return c;
		}
		c = source.compareTo(edge.getSource());
		if (c != 0) {
			return c;
		}
		return target.compareTo(edge.getTarget());
	}

}
