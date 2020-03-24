/*
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
package org.apromore.jgraph.layout.graph;

import org.apromore.jgraph.layout.organic.JGraphFastOrganicLayout;

/**
 * @deprecated use JGraphFastOrganicLayout instead
 */
public class JGraphFRLayout extends JGraphFastOrganicLayout {

	/**
	 * @deprecated use JGraphFastOrganicLayout instead
	 */
	public JGraphFRLayout() {
		super();
	}

	/**
	 * Returns <code>FR</code>, the name of this algorithm.
	 */
	public String toString() {
		return "FR";
	}
}