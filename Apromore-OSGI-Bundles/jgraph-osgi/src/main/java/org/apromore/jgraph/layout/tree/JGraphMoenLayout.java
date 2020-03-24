/*
 * $Id: JGraphMoenLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.tree;

/**
 * @deprecated use JGraphCompactTreeLayout instead
 */
public class JGraphMoenLayout extends JGraphCompactTreeLayout {

	/**
	 * @deprecated use JGraphCompactTreeLayout instead
	 */
	public JGraphMoenLayout() {
		super();
	}

	/**
	 * Returns <code>Moen</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Moen";
	}
}