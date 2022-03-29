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
package org.apromore.processmining.models.jgraph.factory;

import java.util.ArrayList;
import java.util.List;

import org.apromore.jgraph.graph.DefaultCellViewFactory;
import org.apromore.jgraph.graph.Edge;
import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.Port;
import org.apromore.jgraph.graph.PortView;
import org.apromore.jgraph.graph.VertexView;
import org.apromore.processmining.models.cast.Cast;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.jgraph.elements.ProMGraphCell;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.elements.ProMGraphPort;
import org.apromore.processmining.models.jgraph.views.JGraphEdgeView;
import org.apromore.processmining.models.jgraph.views.JGraphPortView;
import org.apromore.processmining.models.jgraph.views.JGraphShapeView;

public class ProMCellViewFactory extends DefaultCellViewFactory {

	private static final long serialVersionUID = -2424217390990685801L;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;

	public ProMCellViewFactory(boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;
	}

	@Override
	protected VertexView createVertexView(Object v) {
		ProMGraphCell cell = (ProMGraphCell) v;
		JGraphShapeView view = new JGraphShapeView(cell, isPIP, viewSpecificAttributes);
		cell.setView(view);
		return view;
	}

	@Override
	@Deprecated
	protected EdgeView createEdgeView(Edge e) {
		return createEdgeView((Object) e);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected EdgeView createEdgeView(Object e) {
		ProMGraphEdge cell = Cast.<ProMGraphEdge>cast(e);

		List list = new ArrayList(cell.getInternalPoints());
		list.add(0, cell.getSource().getView());
		list.add(cell.getTarget().getView());
		GraphConstants.setPoints(cell.getAttributes(), list);

		JGraphEdgeView view = new JGraphEdgeView(cell, isPIP, viewSpecificAttributes);
		cell.setView(view);
		return view;
	}

	@Override
	@Deprecated
	protected PortView createPortView(Port e) {
		return createPortView((Object) e);
	}

	@Override
	protected PortView createPortView(Object e) {
		ProMGraphPort cell = Cast.<ProMGraphPort>cast(e);
		JGraphPortView view = new JGraphPortView(cell, isPIP, viewSpecificAttributes);
		cell.setView(view);
		cell.updateViewsFromMap();
		return view;
	}

}
