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
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioDataObjectType;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioType;

/**
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("processparticipant")
public class ProcessParticipantFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		DataObject processParticipant = new DataObject();
		this.setCommonAttributes(processParticipant, shape);
		
		/* Set Process Participant flag */
		processParticipant.getOrCreateExtensionElements().getAny().add(new SignavioType(SignavioDataObjectType.PROCESSPARTICIPANT));
		
		processParticipant.setName(shape.getProperty("name"));
		processParticipant.setId(shape.getResourceId());
		
		return processParticipant;
	}

}
