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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.util.List;

import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Abstract base class for all NetElement conversion
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public abstract class OryxNetElementHandler extends OryxShapeHandler {

	public OryxNetElementHandler(OryxConversionContext context, BasicShape shape) {
		super(context, shape);
	}

	/**
	 * Converting the incoming flowShapes to YFlows
	 * 
	 * @param element
	 *            with incoming flows, should have been added to YNet already!
	 */
	protected void rememberIncomings() {
		List<BasicShape> incomings = getShape().getIncomingsReadOnly();
		addFlows(incomings);
	}

	/**
	 * Converting the outgoing flowShapes to YFlows
	 * 
	 * @param element
	 *            with incoming flows, should have been added to YNet already!
	 */
	protected void rememberOutgoings() {
		List<BasicShape> outgoings = getShape().getOutgoingsReadOnly();
		addFlows(outgoings);
	}

	private void addFlows(List<BasicShape> outgoings) {
		for (BasicShape flowShape : outgoings) {
			if (flowShape instanceof BasicEdge) {
				getContext().addFlow(getShape().getParent(), (BasicEdge) flowShape);	
			} else {
				//TODO check if these Shapes are still needed
				getContext().addConversionWarnings("Edge was not added to FlowSet " + flowShape.toString(), null);
			}
		}
	}

}
