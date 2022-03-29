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
package org.apromore.processmining.models.graphbased.undirected;

import org.apromore.processmining.models.cast.Cast;
import org.apromore.processmining.models.graphbased.AbstractGraphEdge;
import org.apromore.processmining.models.graphbased.AttributeMap;

public abstract class AbstractUndirectedEdge<T extends UndirectedGraphNode> extends AbstractGraphEdge<T, T> implements
		UndirectedGraphEdge<T> {

	private final AbstractUndirectedGraph<?, ?> graph;

	public AbstractUndirectedEdge(T source, T target, String label) {
		super(source, target);
		assert (source.getGraph() == target.getGraph());
		assert (source.getGraph() instanceof AbstractUndirectedGraph<?, ?>);
		this.graph = Cast.<AbstractUndirectedGraph<?, ?>>cast(source.getGraph());
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	public UndirectedGraph<?, ?> getGraph() {
		return graph;
	}

}
