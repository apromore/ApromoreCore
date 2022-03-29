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
