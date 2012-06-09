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

import java.util.UUID;

import org.oryxeditor.server.diagram.basic.BasicShape;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Base class implementation for a OryxHandler
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class OryxHandlerImpl implements OryxHandler {

	private final OryxConversionContext context;

	public OryxHandlerImpl(OryxConversionContext context) {
		this.context = context;
	}

	/**
	 * @return current conversion context
	 */
	protected OryxConversionContext getContext() {
		return context;
	}

	/**
	 * Checks if there is a YAWL ID (yawlid) property set either by the user or
	 * by a former import. If there is no such ID, a unique ID is generated and
	 * both returned and added to the properties of the supplied Shape.
	 * 
	 * @param shape
	 *            to get the yawlid property from
	 * @return either a UUID or a manually specified ID
	 */
	protected String convertYawlId(BasicShape shape) {
		String yawlId = shape.getProperty("yawlid");
		if (yawlId == null || yawlId.isEmpty()) {
			// Generate a globally unique identifier
			String uuid = "id"+UUID.randomUUID().toString();
			shape.setProperty("yawlid", uuid);
			return uuid;
		} else {
			return yawlId;
		}
	}

}
