/*
 * $Id: JGraphAnnealingLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2005, Gaudenz Alder
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.graph;

import java.awt.geom.Rectangle2D;

import org.apromore.jgraph.layout.organic.JGraphOrganicLayout;

/**
 * @deprecated use JGraphOrganicLayout instead
 * 
 */
public class JGraphAnnealingLayout extends JGraphOrganicLayout {

	/**
	 * @deprecated use JGraphOrganicLayout instead
	 */
	public JGraphAnnealingLayout() {
		super();
	}

	/**
	 * Constructor for SimulatedAnnealingAlgorithm.
	 */
	public JGraphAnnealingLayout(Rectangle2D bounds) {
		super(bounds);
	}

	/**
	 * Returns <code>Annealing</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Annealing";
	}
}
