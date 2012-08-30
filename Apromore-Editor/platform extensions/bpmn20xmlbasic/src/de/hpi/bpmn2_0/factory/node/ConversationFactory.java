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

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.generic.GenericEdge;
import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.factory.configuration.Configuration;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Collaboration;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.conversation.CallConversation;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.conversation.SubConversation;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.util.DiagramHelper;

/**
 * Factory that creates communication and conversation elements
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "Communication", "SubConversation" })
public class ConversationFactory extends AbstractShapeFactory {

	public BPMNElement createBpmnElement(GenericShape shape, Configuration configuration) throws BpmnConverterException {
		BPMNElement bpmnElement = super.createBpmnElement(shape, configuration);
		
		if(bpmnElement != null && bpmnElement.getNode() != null) {
			handleLinkedDiagrams(bpmnElement.getNode(), shape, configuration);
		}
		
		return bpmnElement;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected ConversationNode createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			ConversationNode node = (ConversationNode) this
					.invokeCreatorMethod(shape);
			
			node.setName(shape.getProperty("name"));
			node.setId(shape.getResourceId());
			
			node = convertToCallConversation(shape, node);
			
			return node;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}

	}

	/**
	 * Creates the process element for a call conversation.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The {@link CallConversation}
	 */
	public ConversationNode convertToCallConversation(GenericShape shape, ConversationNode node) {
		String isCallConversation = shape.getProperty("iscallconversation");
		
		if(isCallConversation != null && isCallConversation.equals("true")) {
			return new CallConversation(node);
		} else {
			return node;
		}
		
	}

	@StencilId("Communication")
	public Conversation createConversation(GenericShape shape) {
		return new Conversation();
	}
	
	@StencilId("SubConversation")
	public SubConversation createSubConversation(GenericShape shape) {
		return new SubConversation();
	}

	private List<String> getParticipantIds(GenericShape<?,?> shape) {
		List<String> participantIds = new ArrayList<String>();

		/* Check outgoing conversation links */

		for (GenericEdge connector : DiagramHelper.getOutgoingEdges(shape)) {
			if (!connector.getStencilId().equals("ConversationLink"))
				continue;
			
			if (connector.getTarget() != null
					&& connector.getTarget().getStencilId().equals(
							"Participant"))
				participantIds.add(connector.getTarget().getResourceId());
		}

		/* Check incomming conversation links */
		for (GenericEdge<?,?> connector : DiagramHelper.getIncomingEdges(shape)) {
			if (!connector.getStencilId().equals("ConversationLink"))
				continue;

			for (GenericShape part : connector.getIncomingsReadOnly()) {
				if (part.getStencilId().equals("Participant"))
					participantIds.add(part.getResourceId());
			}
		}

		return participantIds;
	}
	
	/**
	 * Transforms linked diagrams of collapsed subprocess and event subprocess.
	 * 
	 * @param baseElement
	 * @param shape
	 * @param config
	 */
	private void handleLinkedDiagrams(BaseElement baseElement, GenericShape shape, Configuration config) {
		if(baseElement == null || !shape.getStencilId().matches(".*SubConversation.*")) {
			return;
		}
		
		/*
		 * Diagram Link
		 */
		String entry = shape.getProperty("entry");
		if(entry == null || entry.length() == 0) {
			return;
		}
		
		SignavioMetaData metaData = new SignavioMetaData("entry", entry);
		baseElement.getOrCreateExtensionElements().add(metaData);
		
		Definitions linkedDiagram = SubprocessFactory.retrieveDefinitionsOfLinkedDiagram(entry, config);
		
		if(linkedDiagram == null || linkedDiagram.getRootElement().size() == 0) {
			return;
		}
		
		for(BaseElement rootEl : linkedDiagram.getRootElement()) {
			if(rootEl instanceof Collaboration) {
				Collaboration linkedCon = (Collaboration) rootEl;
				
				/* Sub choreography */
				if(baseElement instanceof SubConversation) {
					SubConversation subConversation = (SubConversation) baseElement;
					
					/* 
					 * Add conversation nodes, links, participants, 
					 * artifacts, associations, message flows
					 */
					for(ConversationNode node : linkedCon.getConversationNode()) {
						subConversation.getConversationNode().add(node);
						subConversation._diagramElements.add(node._diagramElement);
					}
					
					for(ConversationLink link : linkedCon.getConversationLink()) {
						subConversation.getConversationLink().add(link);
						subConversation._diagramElements.add(link._diagramElement);
					}
					
					for(Artifact a : linkedCon.getArtifact()) {
						subConversation.getArtifact().add(a);
						subConversation._diagramElements.add(a._diagramElement);
					}
					
					for(MessageFlow m : linkedCon.getMessageFlow()) {
						subConversation.getMessageFlow().add(m);
						subConversation._diagramElements.add(m._diagramElement);
					}
					
					for(Participant p : linkedCon.getParticipant()) {
						subConversation.getParticipantRef().add(p);
						subConversation._diagramElements.add(p._diagramElement);
					}
					
					for(Association associ : linkedCon.getAssociation()) {
						subConversation.getAssociation().add(associ);
						subConversation._diagramElements.add(associ._diagramElement);
					}
				}
				
				/* Call choreography */
				else if(baseElement instanceof CallConversation) {
					CallConversation callConversation = (CallConversation) baseElement;
					callConversation.setCalledElementRef(linkedCon);
					
					for(BaseElement baseEl : linkedCon.getChilds()) {
						callConversation._diagramElements.add(baseEl._diagramElement);
					}
					
				}
			} 
		}
	}
}
