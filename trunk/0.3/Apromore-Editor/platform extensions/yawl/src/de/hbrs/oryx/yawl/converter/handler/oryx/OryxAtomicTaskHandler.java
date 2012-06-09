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

import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 *  Converts a Atomic Task
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public class OryxAtomicTaskHandler extends OryxTaskHandler {

	public OryxAtomicTaskHandler(OryxConversionContext context, BasicShape shape) {
		super(context, shape);
	}

	/* (non-Javadoc)
	 * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
	 */
	@Override
	public void convert() {
		final BasicShape shape = getShape();
		final YNet parentNet = getContext().getNet(shape.getParent());

		int joinType = convertConnectorType(shape.getProperty("join"));
		int splitType = convertConnectorType(shape.getProperty("split"));

		YAtomicTask task = new YAtomicTask(convertYawlId(shape), joinType,
				splitType, parentNet);

		task.setDecompositionPrototype(convertDecomposition(shape));

		parentNet.addNetElement(task);

		// Remember Flows for later conversion
		rememberOutgoings();
		rememberIncomings();
	}

	private YAWLServiceGateway convertDecomposition(final BasicShape shape) {
		final YSpecification specification = getContext().getSpecification();
		final YAWLServiceGateway taskDecomposition = new YAWLServiceGateway(
				shape.getProperty("yawlid"), specification);
		specification.setDecomposition(taskDecomposition);
		return taskDecomposition;
	}
}
