/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpmnSequenceFlow extends BpmnFlow {
	private static final Logger LOGGER = LoggerFactory.getLogger(BpmnSequenceFlow.class);

	protected String conditionExpression;
	
	public BpmnSequenceFlow(String tag) {
		super(tag);
	}
	
	public Flow unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		if (id2node.containsKey(sourceRef) && id2node.containsKey(targetRef)) {
			diagram.setNextId(id);
			Flow flow = diagram.addFlow(id2node.get(sourceRef), id2node.get(targetRef), name);
			flow.setConditionExpression(conditionExpression);
			id2node.put(id, flow.getTarget());
			return flow;
		}
		else {
			if (!id2node.containsKey(sourceRef)) {
				LOGGER.error("Couldn't match sourceRef {} of sequence flow {} with a corresponding node ID", sourceRef, id);
			}
			if (!id2node.containsKey(targetRef)) {
				LOGGER.error("Couldn't match targetRef {} of sequence flow {} with a corresponding node ID", targetRef, id);
			}
			return null;
		}
	}

	public Flow unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(sourceRef) && elements.contains(targetRef)) {
			return this.unmarshall(diagram, id2node);
		}
		return null;
	}
	
	public void marshall(Flow flow) {
		super.marshall(flow);
		conditionExpression = flow.getConditionExpression();
	}
	
	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
	
	public String getConditionExpression() {
		return conditionExpression;
	}
}
