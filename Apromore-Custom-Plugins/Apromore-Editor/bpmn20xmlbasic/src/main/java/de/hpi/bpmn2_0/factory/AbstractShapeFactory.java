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
package de.hpi.bpmn2_0.factory;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.generic.GenericShape;

/**
 * Abstract factory to handle all types {@link BPMNShape} objects.
 * 
 * @author Sven Wagner-Boysen
 *
 */
public abstract class AbstractShapeFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.common.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	// @Override
	public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent, AbstractBpmnFactory.State state)
			throws BpmnConverterException {
		
		BPMNShape diaElement = this.createDiagramElement(shape);
		BaseElement processElement = this.createProcessElement(shape);
		diaElement.setBpmnElement(processElement);
		
		super.setLabelPositionInfo(shape, processElement);
		
		setBgColor(shape, processElement);
		
		BPMNElement bpmnElement = new BPMNElement(diaElement, processElement, shape.getResourceId());

		// handle external extension elements like from Activiti
		try {
			super.reinsertExternalExtensionElements(shape, bpmnElement);
		} catch (Exception e) {
			
		} 

		return bpmnElement;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.common.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BPMNShape createDiagramElement(GenericShape shape) {
		BPMNShape bpmnShape = new BPMNShape();
		super.setVisualAttributes(bpmnShape, shape);
		
		/* Bounds */
		bpmnShape.setBounds(createBounds(shape));
		
		return bpmnShape;
	}
	
	/* Helper methods */
	
	/**
	 * Generates the BPMN Bounds out of a Shape.
	 */
	private de.hpi.bpmn2_0.model.bpmndi.dc.Bounds createBounds(GenericShape shape) {
		Bounds absBounds = shape.getAbsoluteBounds();
		
		de.hpi.bpmn2_0.model.bpmndi.dc.Bounds bpmnBounds = new de.hpi.bpmn2_0.model.bpmndi.dc.Bounds();
		bpmnBounds.setX(absBounds.getUpperLeft().getX());
		bpmnBounds.setY(absBounds.getUpperLeft().getY());
		bpmnBounds.setHeight(shape.getHeight());
		bpmnBounds.setWidth(shape.getWidth());
		
		return bpmnBounds;
	}
	
	/**
	 * Sets the bgcolor property as a {@link SignavioMetaData} extension
	 * element.
	 * 
	 * @param node
	 * @param element
	 */
	private void setBgColor(GenericShape node, BaseElement element) {
		String bgColor = node.getProperty("bgcolor");
		if(bgColor != null) {
			ExtensionElements extElements = element.getOrCreateExtensionElements();
			extElements.add(new SignavioMetaData("bgcolor", bgColor));
		}
	}
	

}
