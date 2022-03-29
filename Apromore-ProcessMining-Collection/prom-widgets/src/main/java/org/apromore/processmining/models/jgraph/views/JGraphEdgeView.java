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
package org.apromore.processmining.models.jgraph.views;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apromore.jgraph.graph.EdgeView;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.renderers.ProMEdgeRenderer;

public class JGraphEdgeView extends EdgeView implements Cleanable {

	private static final long serialVersionUID = -2874236692967529775L;
	private static ProMEdgeRenderer renderer;
	private DirectedGraphEdge<?, ?> edge;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;

	public transient Shape middleShape;

	@SuppressWarnings("unchecked")
	public JGraphEdgeView(ProMGraphEdge cell, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		super(cell);
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;
		edge = cell.getEdge();
		points = new ArrayList(2);
		points.add(cell.getSource().getView());
		points.addAll(cell.getInternalPoints());
		points.add(cell.getTarget().getView());

		groupBounds = null;
	}

	public void setPoints(List<Point2D> list) {
		points = list;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributeMap() {
		return viewSpecificAttributes;
	}

	@Override
	public ProMEdgeRenderer getRenderer() {
		if (renderer == null) {
			renderer = edge.getAttributeMap().get(AttributeMap.RENDERER, null);
			if (renderer == null) {
				renderer = new ProMEdgeRenderer();
			}
		}
		return renderer;
	}

	public void cleanUp() {
		edge = null;
		setCell(null);
		viewSpecificAttributes.clearViewSpecific(edge);
		source = null;
		target = null;
		if (renderer != null) {
			renderer.cleanUp();
			renderer = null;
		}
	}

	public DirectedGraphEdge<?, ?> getEdge() {
		return edge;
	}

	public boolean isPIP() {
		return isPIP;
	}

}
