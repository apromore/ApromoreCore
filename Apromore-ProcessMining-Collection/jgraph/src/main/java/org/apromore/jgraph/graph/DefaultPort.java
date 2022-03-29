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
 * Copyright (c) 2001-2006 Gaudenz Alder
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple implementation for a port.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class DefaultPort extends DefaultGraphCell implements Port {

	/** Edges that are connected to the port */
	// TODO After Java 1.3 is EOL, this could be changed into a LinkHashSet
	// to retain ordering
	protected HashSet edges = new HashSet(4, 0.75f);

	/** Reference to the anchor of this port */
	protected Port anchor;

	/**
	 * Constructs an empty port.
	 */
	public DefaultPort() {
		this(null, null);
	}

	/**
	 * Constructs a port that holds a reference to the specified user object.
	 *
	 * @param userObject reference to the user object
	 */
	public DefaultPort(Object userObject) {
		this(userObject, null);
	}

	/**
	 * Constructs a port that holds a reference to the specified user object
	 * and a reference to the specified anchor.
	 *
	 * @param userObject reference to the user object
	 * @param anchor reference to a graphcell that constitutes the anchor
	 */
	public DefaultPort(Object userObject, Port anchor) {
		super(userObject);
		setAllowsChildren(false);
		this.anchor = anchor;
	}

	/**
	 * Returns an iterator of the edges connected
	 * to the port.
	 */
	public Iterator edges() {
		return edges.iterator();
	}

	/**
	 * Adds <code>edge</code> to the list of edges.
	 */
	public boolean addEdge(Object edge) {
		return edges.add(edge);
	}

	/**
	 * Removes <code>edge</code> from the list of edges.
	 */
	public boolean removeEdge(Object edge) {
		return edges.remove(edge);
	}

	/**
	 * Returns the collection of edges connected to this port.
	 */
	public Set getEdges() {
		return new HashSet(edges);
	}

	/**
	 * Sets the collection of edges connected to this port.
	 */
	public void setEdges(Set edges) {
		this.edges = new HashSet(edges);
	}

	/**
	 * Returns the anchor of this port.
	 */
	public Port getAnchor() {
		return anchor;
	}

	/**
	 * Sets the anchor of this port.
	 */
	public void setAnchor(Port port) {
		anchor = port;
	}

	/**
	 * Create a clone of the cell. The cloning of the
	 * user object is deferred to the cloneUserObject()
	 * method.
	 *
	 * @return Object  a clone of this object.
	 */
	public Object clone() {
		DefaultPort c = (DefaultPort) super.clone();
		c.edges = new HashSet();
		return c;
	}

}
