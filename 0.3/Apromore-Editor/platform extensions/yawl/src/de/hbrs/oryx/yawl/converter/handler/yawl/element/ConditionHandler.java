/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import java.util.HashMap;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YNetElement;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

/**
 * Converts a YAWL condition to an Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class ConditionHandler extends NetElementHandler {

	public ConditionHandler(YAWLConversionContext context, YNetElement netElement) {
		super(context, netElement, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler#convert(java.lang
	 * .String)
	 */
	@Override
	public void convert(String parentId) {
		if (isElementVisible(getNetElement())) {
			BasicShape condition = convertCondition(parentId, "Condition");
			getContext().putShape(parentId, getNetElement().getID(), condition);
			super.convert(parentId);
		}
	}

	private YCondition getCondition() {
		return (YCondition) getNetElement();
	}

	protected BasicShape convertCondition(String netId, String stencilType) {
		BasicShape outputShape = new BasicNode(getCondition().getID(), stencilType);
		outputShape.setBounds(getConditionLayout(netId, getCondition()));
		outputShape.setProperties(convertProperties());
		return outputShape;
	}

	private HashMap<String, String> convertProperties() {
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("yawlid", getCondition().getID());
		if (getCondition().getName() != null) {
			props.put("name", getCondition().getName());
		}
		if (getCondition().getDocumentation() != null) {
			props.put("documentation", getCondition().getDocumentation());
		}
		return props;
	}

	private Bounds getConditionLayout(String netId, YCondition condition) {
		return getContext().getVertexLayout(netId, condition.getID()).getBounds();
	}

	// Explicit means visible in Oryx
	private boolean isElementVisible(YNetElement yElement) {
		return !(yElement instanceof YCondition) || (yElement instanceof YCondition) && (!((YCondition) yElement).isImplicit());
	}

}
