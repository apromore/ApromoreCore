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
 * The specific layout interface for hierarchical layouts. It adds a
 * <code>run</code> method with a parameter for the hierarchical layout model
 * that is shared between the layout stages.
 */
public interface JGraphHierarchicalLayoutStep {

	/**
	 * Takes the graph detail and configuration information within the facade
	 * and creates the resulting laid out graph within that facade for further
	 * use
	 * 
	 * @param graph
	 *            The layout facade that the layout will use as input
	 * @param model
	 *            The internal model of the hierarchical layout created to store
	 *            details of inverted edges and dummy node placement. The model
	 *            persists for the duration of the layout object to enable
	 *            incremental layouts.
	 * @return the updated hierarchy model
	 */
	public JGraphHierarchyModel run(JGraphFacade graph, JGraphHierarchyModel model);
}
