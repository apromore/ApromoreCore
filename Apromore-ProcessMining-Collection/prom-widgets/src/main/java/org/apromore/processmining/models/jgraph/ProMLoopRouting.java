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
package org.apromore.processmining.models.jgraph;

import java.util.List;

import org.apromore.jgraph.graph.DefaultEdge;
import org.apromore.jgraph.graph.EdgeView;
import org.apromore.jgraph.graph.GraphLayoutCache;

public class ProMLoopRouting extends DefaultEdge.DefaultRouting {

	public static ProMLoopRouting ROUTER = new ProMLoopRouting();

	private static final long serialVersionUID = -1502015269578934172L;

	public List<?> route(GraphLayoutCache cache, EdgeView edge) {
		// No routing is performed "on the fly", i.e. all routing information
		// is introduced during the layout phase where all internal control points
		// of edges are set. Hence, the route should just return null.
		return null;
	}
}
