package de.hpi.bpmn2_0.factory;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

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
