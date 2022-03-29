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
