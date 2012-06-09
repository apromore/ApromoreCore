/**
 * Copyright (c) 2012 Felix Mannhardt
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
import java.util.Map;

import org.yawlfoundation.yawl.elements.YMultiInstanceAttributes;
import org.yawlfoundation.yawl.elements.YTask;

public class MultiInstancePropertiesConverter {

	public static Map<String, String> convert(YTask task) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (task.isMultiInstance()) {
			YMultiInstanceAttributes m = task.getMultiInstanceAttributes();
			map.put("minimum", String.valueOf(m.getMinInstances()));
			map.put("maximum", String.valueOf(m.getMaxInstances()));
			map.put("threshold", String.valueOf(m.getThreshold()));
			map.put("creationmode", m.getCreationMode());
			map.put("miinputexpression", task.getPreSplittingMIQuery());
			map.put("miinputsplittingexpression", m.getMISplittingQuery());
			map.put("miinputformalinputparam", m.getMIFormalInputParam());
			if (m.getMIFormalOutputQuery() != null) {
				map.put("mioutputformaloutputexpression",
						m.getMIFormalOutputQuery());
				map.put("mioutputoutputjoiningexpression",
						m.getMIJoiningQuery());
				map.put("mioutputresultappliedtolocalvariable", task
						.getMIOutputAssignmentVar(m.getMIFormalOutputQuery()));
			}
		}

		return map;
	}
	

	

}
