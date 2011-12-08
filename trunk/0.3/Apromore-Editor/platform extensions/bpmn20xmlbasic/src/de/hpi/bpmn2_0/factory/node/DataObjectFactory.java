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
		
		BaseElement dataObject = null;
		
		if(prop == null || prop.equals("None")) {
			dataObject = new DataObject();
			this.setDataObjectAttributes((DataObject) dataObject, shape);
			
		} else if(prop.equals("Input")) {
			dataObject = new DataInput();
			((DataInput) dataObject).setName(shape.getProperty("name"));
		} else if(prop.equals("Output")) {
			dataObject = new DataOutput();
			((DataOutput) dataObject).setName(shape.getProperty("name"));
		}
		
		if(dataObject == null)
			throw new BpmnConverterException("Error while creating DataObject: null value");
		
		this.setCommonAttributes(dataObject, shape);
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
	private void setDataObjectAttributes(DataObject dataObject, GenericShape shape) {
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
