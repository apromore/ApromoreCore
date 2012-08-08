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
package de.hbrs.oryx.yawl.util;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNetElement;
import org.yawlfoundation.yawl.util.JDOMUtil;

import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

public abstract class YAWLUtils {

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
		return !(yElement instanceof YCondition) || (yElement instanceof YCondition) && (!((YCondition) yElement).isImplicit()); // Explicit
																																	// means
																																	// visible
																																	// in
																																	// Oryx
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
			return yFlow.getNextElement().getPostsetElements().iterator().next();
		}
	}

	static public Document parseToElement(String source) throws ConversionException {
		// Use the YAWL method to parse String
		Document doc = JDOMUtil.stringToDocument(source);
		if (doc != null) {
			return doc;
		}
		throw new ConversionException("Could not parse " + source);
	}

	static public String elementToString(List elementList) {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		return out.outputString(elementList);
	}

	static public String elementToString(Element el) {
		return JDOMUtil.elementToString(el);
	}

	public static final String YAWL_NS = "http://www.yawlfoundation.org/yawlschema";

}
