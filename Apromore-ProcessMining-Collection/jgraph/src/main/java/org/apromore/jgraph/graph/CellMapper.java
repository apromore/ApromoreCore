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
