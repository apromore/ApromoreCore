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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMessageName;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.model.participant.ParticipantMultiplicity;

/**
 * Factory to create participants
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId("ChoreographyParticipant")
public class ParticipantFactory extends AbstractShapeFactory {

	
	@Override
	public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent, State state)
			throws BpmnConverterException {
		BPMNElement bpmnElement = super.createBpmnElement(shape, parent, state);
		
		/*
		 * Check on associated messages
		 */
		setMessageVisibility(shape, bpmnElement);
		
		return bpmnElement;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected Participant createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		Participant p = new Participant();
		p._isChoreographyParticipant = true;
		this.setCommonAttributes(p, shape);
		p.setId(shape.getResourceId());
		p.setName(shape.getProperty("name"));

		/* Handle initiating property */
		String initiating = shape.getProperty("initiating");
		if (initiating != null)
			p.setInitiating(initiating.equalsIgnoreCase("true"));
		else
			p.setInitiating(false);

		/* Participant Multiplicity */
		String isMultipleParticipant = shape.getProperty("multiple_instance");
		if (isMultipleParticipant != null
				&& isMultipleParticipant.equals("true")) {
			ParticipantMultiplicity multiplicit = new ParticipantMultiplicity();

			/* Maximum */
			String maximum = shape.getProperty("maximum");
			if (maximum != null) {
				multiplicit.setMaximum(Integer.valueOf(maximum));
			}

			/* Minimum */
			String minimum = shape.getProperty("minimum");
			if (minimum != null) {
				multiplicit.setMinimum(Integer.valueOf(minimum));
			}

			p.setParticipantMultiplicity(multiplicit);
		}

		return p;
	}

	// @Override
	protected BPMNShape createDiagramElement(GenericShape shape) {
		BPMNShape bpmnShape = super.createDiagramElement(shape);
		return bpmnShape;
	}

	/**
	 * Checks whether the message of an participant is visible and assigns the
	 * name tag as a {@link SignavioMessageName} to the {@link Participant}.
	 */
	private GenericShape getTheVisibleMessage(GenericShape shape) {
		/* Navigate in both directions because of undirected association */
		List<GenericShape> connectedElements = new ArrayList<GenericShape>();
		connectedElements.addAll(shape.getOutgoingsReadOnly());
		connectedElements.addAll(shape.getIncomingsReadOnly());

		for (GenericShape conShape : connectedElements) {
			if (conShape.getStencilId().equals("Association_Undirected")) {
				List<GenericShape> shapeList = new ArrayList<GenericShape>();
				shapeList.addAll(conShape.getIncomingsReadOnly());
				shapeList.addAll(conShape.getOutgoingsReadOnly());

				for (GenericShape msgShape : shapeList) {
					if (msgShape.getStencilId().equals("Message")) {

						return msgShape;
					}
				}
			}
		}

		return null;
	}

	private void setMessageVisibility(GenericShape shape, BPMNElement bpmnElement) {
		GenericShape visibleMessage = getTheVisibleMessage(shape);
		if (visibleMessage != null) {
			((BPMNShape) bpmnElement.getShape()).setIsMessageVisible(true);
			
			/* Create Message element, which is later referenced by a message
			 * flow from connecting the participants of the choreography task */
			Message msg = new Message();
			
			msg.setInitiating("true".equalsIgnoreCase(visibleMessage.getProperty("initiating")));

			/*
			 * Set the message name tag as extension element of the participant
			 */
			String name = visibleMessage.getProperty("name");
			if (name != null && name.length() > 0) {
				bpmnElement.getNode().getOrCreateExtensionElements().add(
						new SignavioMessageName(name));
				msg.setName(name);
			}
			
			// Reference the message element
			if(bpmnElement.getNode() instanceof Participant) {
				Participant p = (Participant) bpmnElement.getNode();
				p._msgRef = msg;
			}
			
		} else {
			((BPMNShape) bpmnElement.getShape()).setIsMessageVisible(false);
		}
	}

}
