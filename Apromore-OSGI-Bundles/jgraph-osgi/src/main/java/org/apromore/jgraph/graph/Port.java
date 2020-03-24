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