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

import java.awt.Dimension;

import org.apromore.processmining.models.graphbased.AbstractGraphNode;
import org.apromore.processmining.models.graphbased.AttributeMap;

public class AbstractDirectedHypergraphNode extends AbstractGraphNode implements DirectedHypergraphNode {

	private final AbstractDirectedHypergraph<?, ?, ?> graph;

	public AbstractDirectedHypergraphNode(String label, AbstractDirectedHypergraph<?, ?, ?> graph) {
		super();
		this.graph = graph;
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 50));
	}

	public DirectedHypergraph<?, ?, ?> getGraph() {
		return graph;
	}



	public int compareTo(DirectedHypergraphNode node) {
		if (node instanceof AbstractDirectedHypergraphNode) {
			return getId().compareTo(((AbstractDirectedHypergraphNode) node).getId());
		}
		return getLabel().compareTo(node.getLabel());

	}

}
