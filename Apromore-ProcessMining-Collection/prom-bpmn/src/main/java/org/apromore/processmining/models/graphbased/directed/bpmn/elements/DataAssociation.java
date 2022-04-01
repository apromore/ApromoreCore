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
package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Graphics2D;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.AttributeMap.ArrowType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;

public class DataAssociation extends BPMNEdge<BPMNNode, BPMNNode> implements Decorated {

	private IGraphElementDecoration decorator = null;
	
	public DataAssociation(BPMNNode source, BPMNNode target, String label) {
		super(source, target);
		fillAttributes(label);
	}
	
	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_SIMPLE);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, false);
		getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { (float)2.0, (float)2.0 });
	}
	
	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (decorator != null) {
			decorator.decorate(g2d, x, y, width, height);
		}
	}
}
