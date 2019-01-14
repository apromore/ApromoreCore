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
import de.hpi.bpmn2_0.model.data_object.DataState;
import de.hpi.bpmn2_0.model.data_object.DataStore;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory for DataStores 
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("DataStore")
public class DataStoreFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		DataStoreReference dataStoreRef = new DataStoreReference();
		this.setCommonAttributes(dataStoreRef, shape);
		dataStoreRef.setDataStoreRef(new DataStore());
		this.setDataStoreRefAttributes(dataStoreRef, shape);
		
		return dataStoreRef;
	}
	
	/**
	 * Sets the attributes related to a data store element.
	 * 
	 * @param dataStoreRef
	 * 		The @link {@link DataStoreReference}.
	 * @param shape
	 * 		The data store {@link GenericShape}
	 */
	private void setDataStoreRefAttributes(DataStoreReference dataStoreRef, GenericShape shape) {
		DataStore dataStore = dataStoreRef.getDataStoreRef();
		String dataStateName = shape.getProperty("state");
		/* Set attributes of the global data store */
		if(dataStore != null) {
			dataStore.setId(SignavioUUID.generate());
			dataStore.setName(shape.getProperty("name"));
			if(shape.getProperty("capacity") != null && !(shape.getProperty("capacity").length() == 0))
				dataStore.setCapacity(Integer.valueOf(shape.getProperty("capacity")).intValue());
			
			/* Set isUnlimited attribute */
			String isUnlimited = shape.getProperty("isunlimited");
			if(isUnlimited != null && isUnlimited.equalsIgnoreCase("true")) 
				dataStore.setUnlimited(true);
			else
				dataStore.setUnlimited(false);
			
			/* Define DataState element */
			if(dataStateName != null && !(dataStateName.length() == 0)) {
				DataState dataState = new DataState(dataStateName);
				dataStore.setDataState(dataState);
			}
		}
		
		/* Set attributes of the data store reference */
		dataStoreRef.setName(shape.getProperty("name"));
		dataStoreRef.setId(shape.getResourceId());
		
		/* Define DataState element */
		if(dataStateName != null && !(dataStateName.length() == 0)) {
			DataState dataState = new DataState(dataStateName);
			dataStoreRef.setDataState(dataState);
		}
	}

}
