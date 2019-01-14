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

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.factory.AbstractEdgesFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;

/**
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId("SequenceFlow")
public class SequenceFlowFactory extends AbstractEdgesFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape) {
		SequenceFlow seqFlow = new SequenceFlow();
		this.setCommonAttributes(seqFlow, shape);
		seqFlow.setId(shape.getResourceId());
		seqFlow.setName(shape.getProperty("name"));

		String conditionType = shape.getProperty("conditiontype");
		String conditionExpression = shape.getProperty("conditionexpression");

		/*
		if (!(conditionType == null || conditionType.equals("Default"))
				&& !(conditionExpression == null || conditionExpression
						.length() == 0)) {
			seqFlow.setConditionExpression(new FormalExpression(conditionExpression));
		}
		*/

		if (conditionType != null && conditionType.equals("Expression")) {
			seqFlow.setConditionExpression(conditionExpression == null ? null : new FormalExpression(conditionExpression));
		}

		if (conditionType != null && conditionType.equals("Default")) {
			seqFlow.setDefaultSequenceFlow(true);
		}
		
		/* Unnecessary since migration is doing the same */
//		/*
//		 * Copy condition expression to name attribute, because many tools only
//		 * display the name property
//		 */
//		if((seqFlow.getName() == null || seqFlow.getName().length() == 0)
//				&& seqFlow.getConditionExpression() != null) {
//			String condition = seqFlow.getConditionExpression().toExportString();
//			if(condition != null)
//				seqFlow.setName(condition);
//		}
		
		/* IsImmediate Property */
		String isImmediate = shape.getProperty("isimmediate");
		if(isImmediate != null && isImmediate.length() > 0) {
			if(isImmediate.equalsIgnoreCase("false"))
				seqFlow.setIsImmediate(false);
			else if(isImmediate.equalsIgnoreCase("true"))
				seqFlow.setIsImmediate(true);
		}

		assert this.state != null : "Null state at sequence flow creation";
		this.state.shapeToXMLObjectMap.put(shape, seqFlow);

		return seqFlow;
	}

}
