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

import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.NoSubnetFoundException;

/**
 * Converts a composite task
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxCompositeTaskHandler extends OryxTaskHandler {

	public OryxCompositeTaskHandler(OryxConversionContext context,
			BasicShape shape) {
		super(context, shape);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
	 */
	@Override
	public void convert() {
		final BasicShape shape = getShape();
		final YNet parentNet = getContext().getNet(shape.getParent());

		int joinType = convertConnectorType(shape.getProperty("join"));
		int splitType = convertConnectorType(shape.getProperty("split"));

		YCompositeTask task = new YCompositeTask(convertYawlId(shape),
				joinType, splitType, parentNet);

		try {
			task.setDecompositionPrototype(convertDecomposition(shape));
		} catch (NoSubnetFoundException e) {
			getContext().addConversionWarnings(
					"Ignoring composite task " + task.getID(), e);
		}

		parentNet.addNetElement(task);

		// Remember Flows for later conversion
		rememberOutgoings();
		rememberIncomings();

	}

	private YDecomposition convertDecomposition(BasicShape shape)
			throws NoSubnetFoundException {
		BasicDiagram subnetDiagram = retrieveSubnetDiagram(shape);
		// Just find the contained subnet and ignore all Diagram properties
		BasicShape subnet = findSubnetNet(subnetDiagram);
		// Convert the Subnet with all its childShapes
		getContext().getHandlerFactory().createOryxConverter(subnet).convert();
		// Return to just created subnet, that should be already part of the
		// specification decompositions
		return getContext().getNet(subnet);
	}

	private BasicShape findSubnetNet(BasicDiagram diagramShape)
			throws NoSubnetFoundException {
		for (BasicShape shape : diagramShape.getChildShapesReadOnly()) {
			if (isSubNet(shape)) {
				return shape;
			}
		}
		// No subnet found
		throw new NoSubnetFoundException("Could not find subnet in Diagram "
				+ diagramShape.getResourceId());
	}

	private boolean isSubNet(BasicShape shape) {
		if (shape.hasProperty("isrootnet")) {
			return !(new Boolean(shape.getProperty("isrootnet")));
		} else {
			return false;
		}
	}

	/**
	 * Fetches the Subnet
	 * 
	 * @param BasicShape
	 * @return
	 * @throws NoSubnetFoundException
	 */
	private BasicDiagram retrieveSubnetDiagram(BasicShape shape)
			throws NoSubnetFoundException {
		BasicDiagram subnetDiagram = getContext().getSubnetDiagram(
				shape.getProperty("decomposesto"));
		if (subnetDiagram != null) {
			return subnetDiagram;
		} else {
			throw new NoSubnetFoundException(
					"Could not find a Diagram for Subnet with ID: "
							+ shape.getProperty("decomposesto"));
		}
	}

}
