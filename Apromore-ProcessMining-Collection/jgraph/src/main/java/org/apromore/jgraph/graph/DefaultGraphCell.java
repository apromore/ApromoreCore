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
/*
 * @(#)AbstractGraphCell.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2006 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * The default implementation for the GraphCell interface.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class DefaultGraphCell extends DefaultMutableTreeNode implements
		GraphCell, Cloneable {

	/** Hashtable for properties. Initially empty */
	protected AttributeMap attributes = null;

	/**
	 * Creates an empty cell.
	 */
	public DefaultGraphCell() {
		this(null);
	}

	/**
	 * Creates a graph cell and initializes it with the specified user object.
	 *
	 * @param userObject an Object provided by the user that constitutes
	 *                   the cell's data
	 */
	public DefaultGraphCell(Object userObject) {
		this(userObject, null);
	}

	/**
	 * Constructs a cell that holds a reference to the specified user object
	 * and contains the specified array of children and sets default values
	 * for the bounds attribute.
	 *
	 * @param userObject reference to the user object
	 * @param storageMap the storage attribute map for this cell
	 */
	public DefaultGraphCell(Object userObject, AttributeMap storageMap) {
		this(userObject, storageMap, null);

	}

	/**
	 * Creates a graph cell and initializes it with the specified user object.
	 * The GraphCell allows children only if specified.
	 *
	 * @param userObject an Object provided by the user that constitutes
	 *                   the cell's data
	 * @param storageMap the storage attribute map for this cell
	 * @param children array of children
	 */
	public DefaultGraphCell(Object userObject, AttributeMap storageMap,
			MutableTreeNode[] children) {
		super(userObject, true);
		setAttributes(storageMap);
		if (children != null)
			for (int i = 0; i < children.length; i++)
				add(children[i]);
	}

	/**
	 * Provides access to the children list to change ordering.
	 * This method returns a <code>Collections.EMPTY_LIST</code>
	 * if the list of childrenpoints to <code>null</code>.
	 */
	public List getChildren() {
		if (children == null)
			return Collections.EMPTY_LIST;
		return children;
	}

	/**
	 * Returns the properies of the cell.
	 */
	public AttributeMap getAttributes() {
		return attributes;
	}

	/**
	 * Changes the <code>attributes</code> of the cell.
	 * 
	 * @deprecated Use getAttributes().applyMap
	 */
	public Map changeAttributes(Map change) {
		return getAttributes().applyMap(change);
	}

	/**
	 * Sets the attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(AttributeMap attributes) {
		if (attributes == null)
			attributes = new AttributeMap();
		this.attributes = attributes;
	}

	/**
	 * Utility method to create a port for this cell. This method adds
	 * a floating port.
	 * @return the port created
	 */
	public Object addPort() {
		return addPort(null);
	}

	/**
	 * Utility method to create a port for this cell. The method adds a port
	 * at a fixed relative offset within the cell. If the offset is null
	 * then a floating port is added.
	 * @param offset the offset of the port within the cell
	 * @return the port created
	 */
	public Object addPort(Point2D offset) {
		return addPort(offset, null);
	}

	/**
	 * Utility method to create a port for this cell. The method adds a port
	 * at a fixed relative offset within the cell. If the offset is null
	 * then a floating port is added.
	 * @param offset the offset of the port within the cell
	 * @param userObject the user object of the port cell
	 * @return the port created
	 */
	public Object addPort(Point2D offset, Object userObject) {
		DefaultPort port = new DefaultPort(userObject);
		if (offset == null) {
			add(port);
		} else {
			GraphConstants.setOffset(port.getAttributes(), offset);
			add(port);
		}
		return port;
	}

	/**
	 * Create a clone of the cell. This method uses the superclass
	 * implementation (which does not clone the children), then
	 * uses clone on the attribute map. This method does not
	 * clone the user object. You should override the 
	 * cloneUserObject in the graph model to implement cloning
	 * of custom user objects.
	 *
	 * @return Object  a clone of this object.
	 */
	public Object clone() {
		DefaultGraphCell c = (DefaultGraphCell) super.clone();
		c.attributes = (AttributeMap) attributes.clone();
		return c;
	}

}
