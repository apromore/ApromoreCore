/*
 * $Id: JGraphDistanceCostFunction.java,v 1.1 2009/09/25 15:14:15 david Exp $
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

import java.awt.geom.Point2D;

import org.apromore.jgraph.graph.CellMapper;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.EdgeView;

/**
 * This class implements a priority queue.
 */
public class JGraphDistanceCostFunction implements JGraphCostFunction {
	
	protected CellMapper mapper = null;

	public JGraphDistanceCostFunction(CellMapper mapper) {
		this.mapper = mapper;
	}
	
	/**
	 *
	 */
	public double getCost(Object cell) {
		if (mapper != null) {
			CellView view = mapper.getMapping(cell, false);
			return getLength(view);
		} else {
			return 1.0;
		}
	}

	/**
	 *
	 */
	public static double getLength(CellView view) {
		double cost = 1.0;
		if (view instanceof EdgeView) {
			EdgeView edge = (EdgeView) view;
			Point2D last = null, current = null;
			for (int i = 0; i < edge.getPointCount(); i++) {
				current = edge.getPoint(i);
				if (last != null)
					cost += last.distance(current);
				last = current;
			}
		}
		return cost;
	}
}