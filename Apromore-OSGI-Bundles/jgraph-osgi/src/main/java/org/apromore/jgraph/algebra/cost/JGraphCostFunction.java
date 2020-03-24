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
