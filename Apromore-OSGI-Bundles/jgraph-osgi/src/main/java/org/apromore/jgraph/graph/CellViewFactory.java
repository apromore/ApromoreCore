/*
 * @(#)CellViewFactory.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

/**
 * Defines the requirements for objects that may be used as a
 * cell view factory.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface CellViewFactory {

	/**
	 * Constructs a view for the specified cell and associates it
	 * with the specified object using the specified CellMapper.
	 *
	 * @param cell reference to the object in the model
	 */
	CellView createView(GraphModel model, Object cell);

}