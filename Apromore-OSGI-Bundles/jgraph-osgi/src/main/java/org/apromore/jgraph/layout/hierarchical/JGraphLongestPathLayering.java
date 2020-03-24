/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.hierarchical;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyModel;

/**
 * An implementation of the first stage of the Sugiyama layout. Straightforward
 * longest path calculation of layer assignment
 */
public class JGraphLongestPathLayering implements JGraphHierarchicalLayoutStep {

	/**
	 * Produces the layer assignmment using the graph information specified
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 * @return the updated hierarchy model
	 */
	public JGraphHierarchyModel run(JGraphFacade facade,
			JGraphHierarchyModel model) {
		if (model == null) {
			model = new JGraphHierarchyModel(facade);
		}
		model.initialRank();
		model.fixRanks();
		return model;
	}
}
