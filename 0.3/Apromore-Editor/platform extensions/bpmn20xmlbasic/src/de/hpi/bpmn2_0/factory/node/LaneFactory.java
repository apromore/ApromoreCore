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
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.LaneSet;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.model.participant.ParticipantMultiplicity;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory to create lanes and pools
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "CollapsedPool", "VerticalPool", "CollapsedVerticalPool", "Pool", "Lane", "VerticalLane" })
public class LaneFactory extends AbstractShapeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {

		if (shape.getStencilId().equals("CollapsedPool") 
				|| shape.getStencilId().equals("Pool")
				|| shape.getStencilId().equals("CollapsedVerticalPool")
				|| shape.getStencilId().equals("VerticalPool")) {
			Participant participant = new Participant();
			
			/* Set name attribute */
			String name = shape.getProperty("name");
			if(name != null && !(name.length() == 0))
				participant.setName(name);
			
			participant.setId(shape.getResourceId());
			
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
			
			/* Process type */
			String processType = shape.getProperty("processtype");
			if(processType != null && !(processType.length() == 0)) {
				participant._processType = processType;
			}
			
			/* Process isClosed */
			String isClosed = shape.getProperty("isclosed");
			if(isClosed != null && !(isClosed.length() == 0))
				participant._isClosed = isClosed;
			
			/* Process isExecutable */
			String isExecutable = shape.getProperty("isexecutable");
			if(isExecutable != null && !(isExecutable.length() == 0))
				participant._isExecutable = isExecutable;
			
			this.setCommonAttributes(participant, shape);
			return participant;
		}

		if (shape.getStencilId().equals("Pool")) {
//			LaneSet poolLaneSet = new LaneSet();
//			this.setCommonAttributes(poolLaneSet, shape);
//			poolLaneSet.setId(shape.getResourceId());
//			
//			/* Name */
//			String name = shape.getProperty("name");
//			if(name != null && !(name.length() == 0)) {
//				poolLaneSet.setName(name);
//			}
//			
//			/* Process type */
//			String processType = shape.getProperty("processtype");
//			if(processType != null && !(processType.length() == 0)) {
//				poolLaneSet._processType = processType;
//			}
//			
//			/* Process isClosed */
//			String isClosed = shape.getProperty("isclosed");
//			if(isClosed != null && !(isClosed.length() == 0))
//				poolLaneSet._isClosed = isClosed;
//	
//			return poolLaneSet;
		}

		Lane lane = new Lane();
		this.setCommonAttributes(lane, shape);
		lane.setId(shape.getResourceId());
		
		/* Set name attribute */
		String name = shape.getProperty("name");
		if(name != null && !(name.length() == 0))
			lane.setName(name);
		
		lane.setLane(lane);

		if (this.hasChildLanes(shape)) {
			LaneSet laneSet = new LaneSet();
			laneSet.setParentLane(lane);
			laneSet.setId(SignavioUUID.generate());
			lane.setChildLaneSet(laneSet);
		}

		return lane;
	}

	private boolean hasChildLanes(GenericShape<?,?> shape) {
		for (GenericShape childShape : shape.getChildShapesReadOnly()) {
			if (childShape.getStencilId().endsWith("Lane")) {
				return true;
			}
		}
		return false;
	}
	
	// @Override
	protected BPMNShape createDiagramElement(GenericShape shape) {
		BPMNShape swimLaneShape = super.createDiagramElement(shape);
		if(shape.getStencilId().equals("Pool")
			|| shape.getStencilId().equals("CollapsedPool")
			|| shape.getStencilId().equals("Lane")) {
			swimLaneShape.setIsHorizontal(true);
		} else {
			swimLaneShape.setIsHorizontal(false);
		}
				
		return swimLaneShape;
	}
}
