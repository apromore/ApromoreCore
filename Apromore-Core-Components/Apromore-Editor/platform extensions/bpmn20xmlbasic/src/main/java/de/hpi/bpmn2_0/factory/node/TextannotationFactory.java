/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;

/**
 * Factor to create {@link TextAnnotation}
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("TextAnnotation")
public class TextannotationFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected TextAnnotation createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		TextAnnotation text = new TextAnnotation();
		this.setCommonAttributes(text, shape);
		text.setId(shape.getResourceId());
		
		/* Text */
		String textAttr = shape.getProperty("text");
		if(textAttr != null && !(textAttr.length() == 0)) {
			text.setText(textAttr);
		}
		
		/* Text Format */
		String textFormat = shape.getProperty("textformat");
		if(textFormat != null && !(textFormat.length() == 0)) {
			text.setTextFormat(textFormat);
		}
		
		return text;
	}

}
