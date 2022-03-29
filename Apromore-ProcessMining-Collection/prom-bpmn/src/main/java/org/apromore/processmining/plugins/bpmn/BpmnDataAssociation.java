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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;

public class BpmnDataAssociation extends BpmnIdName {
	private static final Logger LOGGER = LoggerFactory.getLogger(BpmnDataAssociation.class);
	private String sourceRef;
	private String targetRef;
	
	public BpmnDataAssociation(String tag) {
		super(tag);
	}
	
	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}
	
	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}
	
	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			return true;
		}
		if (xpp.getName().equals("sourceRef")) {
			BpmnText sourceRefElement = new BpmnText("sourceRef");
			sourceRefElement.importElement(xpp, bpmn);
			sourceRef = sourceRefElement.getText();
			return true;
		} else if(xpp.getName().equals("targetRef")) {
			BpmnText targetRefElement = new BpmnText("targetRef");
			targetRefElement.importElement(xpp, bpmn);
			targetRef = targetRefElement.getText();
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if(sourceRef != null) {
			BpmnText sourceRefElement = new BpmnText("sourceRef");
			sourceRefElement.setText(sourceRef);
			s += sourceRefElement.exportElement();
		}
		if(targetRef != null) {
			BpmnText targetRefElement = new BpmnText("targetRef");
			targetRefElement.setText(targetRef);
			s += targetRefElement.exportElement();
		}
		return s;
	}

	/**
	 * @todo: 10/20/2021 Data association could have only sourceRef or targetRef, need to handle it properly
	 */
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node) {
		if (id2node.containsKey(sourceRef) && id2node.containsKey(targetRef)) {
			diagram.setNextId(id);
			diagram.addDataAssociation(id2node.get(sourceRef), id2node.get(targetRef), name);
		}
		else {
			if (!id2node.containsKey(sourceRef)) {
				LOGGER.error("Couldn't match sourceRef {} of data association {} with a corresponding element ID", sourceRef, id);
			}
			if (!id2node.containsKey(targetRef)) {
				LOGGER.error("Couldn't match targetRef {} of data association {} with a corresponding element ID", targetRef, id);
			}
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node) {
		if (elements.contains(sourceRef) && elements.contains(targetRef)) {
			this.unmarshall(diagram, id2node);
		}
	}	
}
