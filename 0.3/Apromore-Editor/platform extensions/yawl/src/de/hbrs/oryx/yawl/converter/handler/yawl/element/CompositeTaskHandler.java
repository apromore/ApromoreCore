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

import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YNetElement;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;

/**
 * Converst a YAWL composite task to an Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class CompositeTaskHandler extends TaskHandler {

	public CompositeTaskHandler(YAWLConversionContext context,
			YNetElement netElement) {
		super(context, netElement, "CompositeTask");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler#
	 * convertTaskProperties
	 * (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
	 */
	@Override
	protected HashMap<String, String> convertTaskProperties(
			NetElementLayout layout) {
		// First convert all common task properties
		HashMap<String, String> properties = super
				.convertTaskProperties(layout);
		if (getCompositeTask()
				.getDecompositionPrototype() != null) {
			properties.put("decomposesto", getCompositeTask()
					.getDecompositionPrototype().getID());	
		} else {
			getContext().addConversionWarnings("Composite Task without Decomposition "+getCompositeTask().getID(), null);
		}
		return properties;
	}

	private YCompositeTask getCompositeTask() {
		return (YCompositeTask) getNetElement();
	}

}
