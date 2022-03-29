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
package org.apromore.processmining.models.graphbased.directed.bpmn;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;

public abstract class BPMNEdge<S extends BPMNNode, T extends BPMNNode> extends AbstractDirectedGraphEdge<S, T>
		implements ContainableDirectedGraphElement {

	private static final String NO_LABEL = "no label";
	
	private ContainingDirectedGraphNode parent;

	public BPMNEdge(S source, T target) {
		super(((BPMNDiagram)source.getGraph()).getNextId(LocalIDGenerator.ID_PREFIX_EDGE), source, target);
	}

	public BPMNEdge(S source, T target, ContainingDirectedGraphNode parent) {
		this(source, target);
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	public int compareTo(BPMNEdge<S, T> edge) {
		return edge.id.compareTo(id);
	}

	public int hashCode() {
		// Hashcode not based on source and target, which
		// respects contract that this.equals(o) implies
		// this.hashCode()==o.hashCode()
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (!(this.getClass().equals(o.getClass()))) {
			return false;
		}
		BPMNEdge<?, ?> edge = (BPMNEdge<?, ?>) o;

		return edge.id.equals(id);
	}

	public ContainingDirectedGraphNode getParent() {
		return parent;
	}
	
	public void setParent(ContainingDirectedGraphNode node) {
		this.parent=node;
	}
	
	@Override
	public String getLabel() {
		String label = super.getLabel();
		return NO_LABEL.equals(label)? "" : label;
	}
	
	public void setLabel(String newLabel) {
		getAttributeMap().put(AttributeMap.LABEL, newLabel);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
	}
}
