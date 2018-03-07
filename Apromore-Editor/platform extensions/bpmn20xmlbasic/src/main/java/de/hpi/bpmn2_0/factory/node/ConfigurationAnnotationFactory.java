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

import java.util.Map;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.BaseElement;

/**
 * Factory to create configuration annotation artifacts.
 *
 * Because these don't actually occur in BPMN, this factory simply returns <code>null</code>.
 *
 * @author Simon Raboczi
 */
@StencilId("ConfigurationAnnotation")
public class ConfigurationAnnotationFactory extends AbstractShapeFactory {

	/**
	 * @return <code>null</code> always, since configuration annotations don't actually exist in BPMN
	 */
	@Override public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent, State state) {
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override protected BaseElement createProcessElement(GenericShape shape) throws BpmnConverterException {
		throw new BpmnConverterException("Configuration annotation elements don't exist in BPMN");
	}
}
