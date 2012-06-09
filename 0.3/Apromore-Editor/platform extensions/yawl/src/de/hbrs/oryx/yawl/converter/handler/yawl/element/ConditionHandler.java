/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import java.util.HashMap;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNetElement;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

/**
 * Converts a YAWL condition to an Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class ConditionHandler extends NetElementHandler {

	public ConditionHandler(YAWLConversionContext context,
			YNetElement netElement) {
		super(context, netElement);
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
			getContext().addPostsetFlows(parentId,
					((YExternalNetElement) getNetElement()).getPostsetFlows());
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
		return props;
	}

	private Bounds getConditionLayout(String netId, YCondition condition) {
		return getContext().getVertexLayout(netId, condition.getID())
				.getBounds();
	}

	// Explicit means visible in Oryx
	private boolean isElementVisible(YNetElement yElement) {
		return !(yElement instanceof YCondition)
				|| (yElement instanceof YCondition)
				&& (!((YCondition) yElement).isImplicit());
	}

}
