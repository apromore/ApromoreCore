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
 * @(#)Port.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.util.Iterator;

/**
 * Defines the requirements for an object that
 * represents a port in a graph model.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface Port extends GraphCell {

	/**
	 * Returns an iterator of the edges connected
	 * to the port.
	 */
	Iterator edges();

	/**
	 * Adds <code>edge</code> to the list of ports.
	 */
	boolean addEdge(Object edge);

	/**
	 * Removes <code>edge</code> from the list of ports.
	 */
	boolean removeEdge(Object edge);

	/**
	 * Returns the anchor of the port.
	 */
	Port getAnchor();

	/**
	 * Sets the anchor of the port.
	 */
	void setAnchor(Port port);

}
