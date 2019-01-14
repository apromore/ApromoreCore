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

package de.hpi.bpmn2_0.factory.edge;

import java.util.Map;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractEdgesFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.di.MessageVisibleKind;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.data_object.Message;

/**
 * Factory that creates {@link MessageFlow}
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("MessageFlow")
public class MessageFlowFactory extends AbstractEdgesFactory {

	@Override
	public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent, State state)
			throws BpmnConverterException {
		BPMNElement element = super.createBpmnElement(shape, parent, state);
		
		for(GenericShape child : ((GenericShape<?,?>)shape).getChildShapesReadOnly()) {
			if(child.getStencilId().equals("Message")) {
				/*
				Message m = new Message();
				
				// Name value
				String name = child.getProperty("name");
				if(name != null && name.length() > 0) {
					m.setName(name);
				}
				
				((MessageFlow) element.getNode()).setMessageRef(m);
				
				// Initiating
				String initiating = shape.getProperty("initiating");
				if(initiating != null && initiating.equals("false")) {
					((BPMNEdge) element.getShape()).setMessageVisibleKind(MessageVisibleKind.NON_INITIATING);
				} else {
					((BPMNEdge) element.getShape()).setMessageVisibleKind(MessageVisibleKind.INITIATING);
				}
				*/
			}
		}
		
		return element;
	}
	
	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected MessageFlow createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		MessageFlow msgFlow = new MessageFlow();
		this.setCommonAttributes(msgFlow, shape);
		msgFlow.setId(shape.getResourceId());
		msgFlow.setName(shape.getProperty("name"));
		
		return msgFlow;
	}

}
