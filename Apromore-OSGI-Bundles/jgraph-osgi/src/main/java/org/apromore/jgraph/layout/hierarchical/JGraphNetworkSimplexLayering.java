/*
 * Copyright (c) 2005, David Benson
 * 
 * All rights reserved.
 * 
 * This file is licensed under the JGraph software license, a copy of which will
 * have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please contact
 * JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.hierarchical;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyModel;

/**
 * A network simplex layering algorithm as described by Gansner et al 1993
 */
public class JGraphNetworkSimplexLayering implements
		JGraphHierarchicalLayoutStep {

	/**
	 * Assigns rank using a network simplex formulation
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 * @return the updated hierarchy model
	 */
	public JGraphHierarchyModel run(JGraphFacade facade,
			JGraphHierarchyModel model) {
		feasibleTree(facade, model);

		normalize();
		balance();
		return model;
	}

	/**
	 * 
	 */
	private void balance() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void normalize() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void feasibleTree(JGraphFacade facade, JGraphHierarchyModel model) {
		if (model == null) {
			model = new JGraphHierarchyModel(facade, facade.getVertices()
					.toArray(), false, false, true);
		}

	}
}