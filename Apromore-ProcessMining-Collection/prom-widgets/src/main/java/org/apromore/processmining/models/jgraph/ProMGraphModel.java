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

import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apromore.jgraph.event.GraphModelEvent;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.ConnectionSet;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.apromore.jgraph.graph.ParentMap;
import org.apromore.processmining.models.graphbased.directed.DirectedGraph;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;

public class ProMGraphModel extends DefaultGraphModel implements ModelOwner {

	private static final long serialVersionUID = 9097862538097193482L;
	private final DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> graph;

	public ProMGraphModel(
			DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> graph) {
		this.graph = graph;
	}

	public DirectedGraph<? extends DirectedGraphNode, ? extends DirectedGraphEdge<? extends DirectedGraphNode, ? extends DirectedGraphNode>> getGraph() {
		return graph;
	}

	public String toString() {
		return graph.toString();
	}

	public ProMGraphModel getModel() {
		return this;
	}

	/**
	 * Invoke this method after you've changed how the cells are to be
	 * represented in the graph.
	 */
	public void cellsChanged(final Object[] cells, final Rectangle2D dirtyRegion) {
		if (cells != null) {
			fireGraphChanged(this, new GraphModelEvent.GraphModelChange() {

				public Object[] getInserted() {
					return null;
				}

				public Object[] getRemoved() {
					return null;
				}

				public Map<?, ?> getPreviousAttributes() {
					return null;
				}

				public ConnectionSet getConnectionSet() {
					return null;
				}

				public ConnectionSet getPreviousConnectionSet() {
					return null;
				}

				public ParentMap getParentMap() {
					return null;
				}

				public ParentMap getPreviousParentMap() {
					return null;
				}

				public void putViews(GraphLayoutCache view, CellView[] cellViews) {
				}

				public CellView[] getViews(GraphLayoutCache view) {
					return null;
				}

				public Object getSource() {
					return this;
				}

				public Object[] getChanged() {
					return cells;
				}

				public Map<?, ?> getAttributes() {
					return null;
				}

				public Object[] getContext() {
					return null;
				}

				public Rectangle2D getDirtyRegion() {
					return dirtyRegion;
				}

				public void setDirtyRegion(Rectangle2D dirty) {
				}

			});
		}
	}

}
