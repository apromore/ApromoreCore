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

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.data_object.DataState;

/**
 * Factory for Data Objects
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"DataObject"
})
public class DataObjectFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		
		String prop = shape.getProperty("input_output");
		
		AbstractDataObject dataObject = null;
		
		if(prop == null || prop.equals("None")) {
			dataObject = new DataObject();
			//this.setDataObjectAttributes((DataObject) dataObject, shape);
		} else if(prop.equals("Input")) {
			dataObject = new DataInput();
			// ((DataInput) dataObject).setName(shape.getProperty("name"));
		} else if(prop.equals("Output")) {
			dataObject = new DataOutput();
			// ((DataOutput) dataObject).setName(shape.getProperty("name"));
		}
		if(dataObject == null)
			throw new BpmnConverterException("Error while creating DataObject: null value");
		
		this.setCommonAttributes(dataObject, shape);
		this.setDataObjectAttributes(dataObject, shape);
		dataObject.setId(shape.getResourceId());		
		
		return dataObject;
	}
	
	/**
	 * This methods set the common properties of a data object based on its shape
	 * data.
	 * 
	 * @param dataObject
	 * @param shape
	 */
	private void setDataObjectAttributes(AbstractDataObject dataObject, GenericShape shape) {
		dataObject.setName(shape.getProperty("name"));
		
		/* Set isCollection attribute */
		String isCollection = shape.getProperty("iscollection");
		if(isCollection != null && isCollection.equalsIgnoreCase("true"))
			dataObject.setIsCollection(true);
		else
			dataObject.setIsCollection(false);
		
		/* Define DataState element */
		String dataStateName = shape.getProperty("state");
		if(dataStateName != null && !(dataStateName.length() == 0)) {
			DataState dataState = new DataState(dataStateName);
			dataObject.setDataState(dataState);
		}
		
//		/* Determine requirements of data input and output */
//		this.setRequiredForStartCompletionAttributes(dataObject, shape);
	}
	
	/**
	 * Checks for the required for start and completion attributes to determine
	 * whether a data object is necessary to start or complete an activity.
	 * 
	 * A further post process step is needed to setup the IOSpecifications of the
	 * related activity.
	 * 
	 * @param dataObject
	 * @param shape
	 */
//	private void setRequiredForStartCompletionAttributes(DataObject dataObject, GenericShape shape) {
//		
//		/* Handle required for start property */
////		String reqStartString = shape.getProperty("requiredforstart");
////		if(reqStartString != null && reqStartString.equalsIgnoreCase("true"))
////			dataObject.setIsRequiredForStart(true);
////		else
////			dataObject.setIsRequiredForStart(false);
////		
////		/* Handle required for completion */
////		String reqCompletionString = shape.getProperty("producedatcompletion");
////		if(reqCompletionString != null && reqCompletionString.equalsIgnoreCase("true"))
////			dataObject.setIsCollection(true);
////		else
////			dataObject.setIsRequiredForCompletion(false);
//	}

}
