/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
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
