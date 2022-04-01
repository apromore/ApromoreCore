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
 * $Id: JGraphCostFunction.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.algebra.cost;

/**
 * The cost function takes a cell and returns it's cost as a double. Two typical
 * examples of cost functions are the euclidian length of edges or a constant
 * number for each edge. To use one of the built-in cost functions, use either
 * <code>new JGraphDistanceCostFunction(graph.getGraphLayoutCache())</code> or
 * <code>new JGraphConstantCostFunction(1)</code>.
 * 
 */
public interface JGraphCostFunction {

	/**
	 * Evaluates the cost of <code>cell</code>.
	 * 
	 * @param cell
	 * 		the cell to be evaluated
	 * 
	 * @return Returns the cost to traverse <code>cell</code>
	 */
	double getCost(Object cell);

}
