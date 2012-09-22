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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.awt.Rectangle;

import org.oryxeditor.server.diagram.basic.BasicShape;
import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Handler for all kinds of shapes: Net, Tasks, Conditions, Flows ...
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class OryxShapeHandler extends OryxHandlerImpl {

	private final BasicShape shape;

	public OryxShapeHandler(OryxConversionContext context, BasicShape shape) {
		super(context);
		this.shape = shape;
	}

	/**
	 * @return the BasicShape to convert
	 */
	public BasicShape getShape() {
		return shape;
	}

	protected Rectangle convertShapeBounds(BasicShape shape) {
		return new Rectangle(shape.getUpperLeft().getX().intValue(), shape.getUpperLeft().getY().intValue(), (int) shape.getHeight(),
				(int) shape.getWidth());
	}

}
