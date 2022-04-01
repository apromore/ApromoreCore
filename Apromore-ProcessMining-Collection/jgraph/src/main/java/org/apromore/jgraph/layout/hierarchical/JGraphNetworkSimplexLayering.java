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
