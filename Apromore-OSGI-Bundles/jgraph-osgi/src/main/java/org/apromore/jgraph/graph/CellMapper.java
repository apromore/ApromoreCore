/*
 * @(#)CellMapper.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2005 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

/**
 * Defines the requirements for objects that may be used as a cell mapper. A
 * cell mapper is able to return the view of a cell, given a reference to that
 * cell object. It is basically a cell to cell view mapping
 */

public interface CellMapper {

	/**
	 * Returns the view that is associated with <code>cell</code>.
	 * 
	 * @param create
	 *            whether a new view should created if a view does not already
	 *            exist
	 */
	CellView getMapping(Object cell, boolean create);

	/**
	 * Inserts the association between <code>cell</code> and <code>view</code>.
	 * 
	 * @param cell
	 *            the cell that constitutes the model element
	 * @param view
	 *            the view that constitutes the view element
	 */
	void putMapping(Object cell, CellView view);
}