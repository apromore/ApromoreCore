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
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.model.participant.ParticipantMultiplicity;

/**
 * Factory to create participants in a conversation
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("Participant")
public class ConversationParticipantFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected Participant createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		Participant participant = new Participant();
		this.setCommonAttributes(participant, shape);
		participant.setId(shape.getResourceId());
		participant.setName(shape.getProperty("name"));
		
		/* Participant Multiplicity */
		String isMultipleParticipant = shape.getProperty("multiinstance");
		if(isMultipleParticipant != null && isMultipleParticipant.equals("true")) {
			ParticipantMultiplicity multiplicit = new ParticipantMultiplicity();
			
			/* Maximum */
			String maximum = shape.getProperty("maximum");
			if(maximum != null) {
				multiplicit.setMaximum(Integer.valueOf(maximum));
			}
			
			/* Minimum */
			String minimum = shape.getProperty("minimum");
			if(minimum != null) {
				multiplicit.setMinimum(Integer.valueOf(minimum));
			}
			
			participant.setParticipantMultiplicity(multiplicit);
		}
		
		return participant;
	}

}
