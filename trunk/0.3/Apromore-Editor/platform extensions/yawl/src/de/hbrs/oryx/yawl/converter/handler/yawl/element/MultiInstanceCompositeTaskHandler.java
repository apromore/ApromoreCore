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

import org.yawlfoundation.yawl.elements.YNetElement;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;

/**
 *  Converts a YAWL composite task with multiple instances to a Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public class MultiInstanceCompositeTaskHandler extends CompositeTaskHandler {

	public MultiInstanceCompositeTaskHandler(YAWLConversionContext context,
			YNetElement netElement) {
		super(context, netElement);
		// Override setting of CompositeTaskHandler
		setTaskType("CompositeMultipleTask");
	}

	/* (non-Javadoc)
	 * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.AtomicTaskHandler#convertTaskProperties(de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
	 */
	@Override
	protected HashMap<String, String> convertTaskProperties(
			NetElementLayout layout) {
		HashMap<String, String> properties = super
				.convertTaskProperties(layout);
		
		YTask task = (YTask) getNetElement();
		properties.putAll(MultiInstancePropertiesConverter.convert(task));
		
		return properties;
	}



}
