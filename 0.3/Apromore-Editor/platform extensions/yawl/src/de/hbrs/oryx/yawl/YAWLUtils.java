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
package de.hbrs.oryx.yawl;

import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNetElement;

public class YAWLUtils {

	/**
	 * Date Format used in whole conversion. Can't store a DateFormat here,
	 * because it is not synchronized! Just use like this: <br />
	 * new SimpleDateFormat(YAWLUtils.DATE_FORMAT)
	 */
	public static final String DATE_FORMAT = "MM/dd/yy";

	/**
	 * There are always implicit conditions between tasks in YAWL. Those
	 * conditions are not visible and therefore this method returns false on
	 * them.
	 * 
	 * @param yElement
	 *            any YAWL element
	 * @return true if element is a invisible condition
	 */
	static public boolean isElementVisible(YNetElement yElement) {
		return !(yElement instanceof YCondition)
				|| (yElement instanceof YCondition)
				&& (!((YCondition) yElement).isImplicit()); // Explicit means
															// visible in Oryx
	}

	/**
	 * Returns the next visible task or condition of a YAWL flow.
	 * 
	 * @param yFlow
	 * @return
	 */
	static public YExternalNetElement getNextVisibleElement(YFlow yFlow) {
		if (isElementVisible(yFlow.getNextElement())) {
			return yFlow.getNextElement();
		} else {
			// There is a invisible condition with exact one successor
			return yFlow.getNextElement().getPostsetElements().iterator()
					.next();
		}
	}

}
