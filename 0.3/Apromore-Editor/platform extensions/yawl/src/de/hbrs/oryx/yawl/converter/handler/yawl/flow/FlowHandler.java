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
package de.hbrs.oryx.yawl.converter.handler.yawl.flow;

import java.util.HashMap;

import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YFlow;

import de.hbrs.oryx.yawl.YAWLUtils;
import de.hbrs.oryx.yawl.converter.OryxUUID;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerImpl;
import de.hbrs.oryx.yawl.converter.layout.FlowLayout;

/**
 * Converts the YAWL flows to Oryx edges
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class FlowHandler extends YAWLHandlerImpl {

	private final YFlow flow;

	public FlowHandler(YAWLConversionContext context, YFlow flow) {
		super(context);
		this.flow = flow;
	}

	@Override
	public void convert(String parentId) {
		if (YAWLUtils.isElementVisible(flow.getPriorElement())
				&& YAWLUtils.isElementVisible(flow.getNextElement())) {
			// Both elements are visible
			// -> draw a edge between them
			String priorElementID = flow.getPriorElement().getID();
			String nextElementID = flow.getNextElement().getID();
			getContext().getNet(parentId).addChildShape(
					convertFlow(parentId, priorElementID, nextElementID));
		} else if (YAWLUtils.isElementVisible(flow.getPriorElement())
				&& !YAWLUtils.isElementVisible(flow.getNextElement())) {
			// The next element is invisible (e.g. implicit condition)
			// -> draw a edge to the next element
			String priorElementID = flow.getPriorElement().getID();
			String nextElementID = YAWLUtils.getNextVisibleElement(flow)
					.getID();
			getContext().getNet(parentId).addChildShape(
					convertFlow(parentId, priorElementID, nextElementID));
		}
	}

	private BasicShape convertFlow(String netId, String priorElementID,
			String nextElementID) {

		// YAWL flows do not have an ID
		String generatedId = OryxUUID.generate();
		BasicEdge flowShape = new BasicEdge(generatedId, "Flow");

		BasicShape priorShape = getContext().getShape(priorElementID);
		BasicShape nextShape = getContext().getShape(nextElementID);

		// Incomings and Outgoings are automatically configured in Signavio
		flowShape.connectToASource(priorShape);
		flowShape.connectToATarget(nextShape);

		FlowLayout flowLayout = getContext().getFlowLayout(netId,
				priorElementID, nextElementID);
		flowShape.setBounds(flowLayout.getBounds());
		flowShape.setDockers(flowLayout.getDockers());

		HashMap<String, String> props = new HashMap<String, String>();
		props.put("yawlid", priorElementID + "|-|" + nextElementID);
		flowShape.setProperties(props);

		return flowShape;
	}

}
