/*
 * $Id: JGraphConstantCostFunction.java,v 1.1 2009/09/25 15:14:15 david Exp $
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
 * This allows to recursively compose any number of abstract layouts into a compound
 * abstract layout. Note that this is more flexible than a decorator pattern,
 * because you can use different class hierarchies to implement the input (facade)
 * and the layout algorithms, while adhering to the rule that each layout
 * algorithm uses the output of the last layout as its input.
 */
public class JGraphConstantCostFunction implements JGraphCostFunction {

	protected double cost = 0;
	
	public JGraphConstantCostFunction(double cost) {
		this.cost = cost;
	}
	
	/**
	 *
	 */
	public double getCost(Object cell) {
		return cost;
	}

}