/*
 * @(#)GraphCell.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.util.Map;


/**
 * Defines the requirements for objects that appear as
 * GraphCells. This is the base interface for all GraphCells.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface GraphCell {

	/**
	 * Returns the <code>attributes</code> of the cell.
	 */
	AttributeMap getAttributes();
	
	/**
	 * Changes the <code>attributes</code> of the cell.
	 * 
	 * @deprecated Use getAttributes().applyMap
	 */
	Map changeAttributes(Map change);

	/**
	 * Sets the attributes
	 */
	public void setAttributes(AttributeMap map);

}