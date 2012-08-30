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
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNetElement;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.DecompositionHandler;

/**
 * Base class for conversion of YAWL NetElements
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class NetElementHandler extends DecompositionHandler {

	private final YNetElement netElement;

	public NetElementHandler(YAWLConversionContext context, YNetElement netElement, YDecomposition decomposition) {
		super(context, decomposition);
		this.netElement = netElement;
	}

	/**
	 * @return the YNetElement that is converted
	 */
	protected YNetElement getNetElement() {
		return netElement;
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
		getContext().addPostsetFlows(parentId, ((YExternalNetElement) getNetElement()).getPostsetFlows());

	}

}
