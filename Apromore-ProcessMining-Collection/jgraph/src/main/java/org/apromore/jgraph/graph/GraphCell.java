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
