/* 
 * $Id: RichTextGraphModel.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved.
 * 
 * See LICENSE file for license details. If you are unable to locate
 * this file please contact info (at) jgraph (dot) com.
 */
package org.apromore.jgraph.components.labels;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apromore.jgraph.graph.AttributeMap;
import org.apromore.jgraph.graph.ConnectionSet;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.Edge;

/**
 * GraphModel that supports cloning of {@link JGraphpadBusinessObject} and
 * handles custom entries in nested maps to add/change/remove properties for
 * business object (using the model's insert and edit methods).
 */
public class RichTextGraphModel extends DefaultGraphModel {

	/**
	 * Defines the EMPTY_VALUE constant to be used in nested maps in order to
	 * remove properties from business objects.
	 */
	public static final Object VALUE_EMPTY = new Object();

	/**
	 * Constructs a new empty graph model.
	 */
	public RichTextGraphModel() {
		super();
	}

	public boolean acceptsSource(Object edge, Object port) {
		// Source only Valid if not Equal Target
		return (((Edge) edge).getTarget() != port);
	}

	// Override Superclass Method
	public boolean acceptsTarget(Object edge, Object port) {
		// Target only Valid if not Equal Source
		return (((Edge) edge).getSource() != port);
	}

	/**
	 * Constructs a new graph model using the specified root cells and
	 * attributes (for the model) and establishes the connections defined in the
	 * specified connection set between the cells.
	 * 
	 * @param roots
	 *            The roots to be inserted into the model.
	 * @param attributes
	 *            The model's attributes.
	 * @param cs
	 *            The connections to be established.
	 */
	public RichTextGraphModel(List roots, AttributeMap attributes,
			ConnectionSet cs) {
		super(roots, attributes, cs);
	}

	/**
	 * Extends the parent implementation to support cloning of
	 * {@link JGraphpadBusinessObject}.
	 * 
	 * @param userObject
	 *            The user object to be cloned.
	 * @return Returns the cloned user object.
	 */
	protected Object cloneUserObject(Object userObject) {
		if (userObject instanceof RichTextBusinessObject)
			return ((RichTextBusinessObject) userObject).clone();
		return super.cloneUserObject(userObject);
	}

	/**
	 * Extends the parent implementation to support changing the value on
	 * {@link JGraphpadBusinessObject}. This implementation supports setting
	 * the value to String, {@link JGraphpadRichTextValue} or Map. If the new
	 * value is a map the complete properties of the user object are replaced
	 * with the specified map.
	 * 
	 * @param cell
	 *            The cell to change the value for.
	 * @param newValue
	 *            The new value to use for the cell.
	 * @return Returns the old value of the cell.
	 */
	public Object valueForCellChanged(Object cell, Object newValue) {
		Object userObject = getValue(cell);
		if (userObject instanceof RichTextBusinessObject) {
			RichTextBusinessObject businessObject = (RichTextBusinessObject) userObject;
			if (newValue instanceof String
					|| newValue instanceof RichTextValue
					|| newValue instanceof Component) {
				Object oldValue = businessObject.getValue();
				businessObject.setValue(newValue);
				return oldValue; // exit
			} else if (newValue instanceof Map) {
				Map oldProperties = businessObject.getProperties();
				businessObject.setProperties((Map) newValue);
				return oldProperties; // exit
			}
		}
		return super.valueForCellChanged(cell, newValue);
	}

	/**
	 * Extends the parent implementation to support changing properties on
	 * {@link JGraphpadBusinessObject} by adding a map for the business object
	 * to the nested map which is passed to an insert or edit call. The special
	 * {@link #VALUE_EMPTY} is used to remove a property from a business object.
	 * 
	 * @param attributes
	 *            The attributes to be processed.
	 * @return Returns the attributes used to undo the change.
	 */
	protected Map handleAttributes(Map attributes) {
		Map undo = super.handleAttributes(attributes);
		if (attributes != null) {

			// Creates the undo map if the superclass returned null
			if (undo == null)
				undo = new Hashtable();

			// Iterates through all entries in the nested map
			Iterator it = attributes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object cell = entry.getKey();
				Map properties = (Map) entry.getValue();

				// If the key is a business object then we have to take
				// the properties and apply them to the business object.
				if (cell instanceof RichTextBusinessObject) {
					RichTextBusinessObject bo = (RichTextBusinessObject) cell;
					Map deltaOld = new Hashtable();
					Iterator it2 = properties.entrySet().iterator();
					while (it2.hasNext()) {
						Map.Entry property = (Map.Entry) it2.next();
						Object key = property.getKey();
						Object value = property.getValue();

						// Handles the special VALUE_EMPTY to remove the
						// respective value from the properties.
						Object oldValue = (value == VALUE_EMPTY) ? bo
								.getProperties().remove(key) : bo.putProperty(
								key, value);

						// Uses the special VALUE_EMPTY in the undo
						// datastructure if the there was no property for
						// the specified key.
						if (oldValue != null)
							deltaOld.put(key, oldValue);
						else
							deltaOld.put(key, VALUE_EMPTY);
					}
					undo.put(cell, deltaOld);
				}
			}
		}
		return undo;
	}

}
