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

import java.awt.geom.Point2D;

import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.PortView;
import org.apromore.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.apromore.processmining.models.jgraph.elements.Cleanable;
import org.apromore.processmining.models.jgraph.elements.ProMGraphPort;
import org.apromore.processmining.models.jgraph.renderers.ProMPortRenderer;

public class JGraphPortView extends PortView implements Cleanable {

	private static final long serialVersionUID = 5279360045457316828L;
	private static ProMPortRenderer renderer;
	private final boolean isPIP;
	private final ViewSpecificAttributeMap viewSpecificAttributes;
	private final ProMGraphPort cell;

	public JGraphPortView(ProMGraphPort cell, boolean isPIP, ViewSpecificAttributeMap viewSpecificAttributes) {
		super(cell);
		this.cell = cell;
		this.isPIP = isPIP;
		this.viewSpecificAttributes = viewSpecificAttributes;

		groupBounds = null;
	}

	@Override
	public ProMPortRenderer getRenderer() {
		if (renderer == null) {
			renderer = new ProMPortRenderer();
		}
		return renderer;
	}

	public ViewSpecificAttributeMap getViewSpecificAttributeMap() {
		return viewSpecificAttributes;
	}

	public void cleanUp() {
		setCell(null);
		parent = null;
		lastParent = null;
		if (renderer != null) {
			renderer.cleanUp();
			renderer = null;
		}
	}

	public boolean isPIP() {
		return isPIP;
	}

	@Override
	public Point2D getLocation(EdgeView edge, Point2D nearest) {
		Point2D pos = super.getLocation(edge, nearest);
		Point2D pos2 = pos;
		if (renderer != null && nearest != null && //
				cell.isBoundaryNode()) {
			pos2 = renderer.getPerimeterPoint(this, pos, nearest);
		}
		return pos2;
	}
}
