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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyEdge;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyModel;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyNode;

/**
 * An implementation of the first stage of the Sugiyama layout. Straightforward
 * longest path calculation of layer assignment
 */
public class JGraphMinimumCycleRemover implements JGraphHierarchicalLayoutStep {

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

		final Set seenNodes = new HashSet();
		final Set unseenNodes = new HashSet(model.getVertexMapping().values());
		// Perform a dfs through the internal model. If a cycle is found,
		// reverse it.
		Object rootsArray[] = null;
		if (model.roots != null) {
			rootsArray = new Object[model.roots.length];
			for (int i = 0; i < model.roots.length; i++) {
				Object node = model.roots[i];
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) model
						.getVertexMapping().get(node);
				rootsArray[i] = internalNode;
			}
		}

		model.dfs(new JGraphFacade.CellVisitor() {
			public void visit(Object parent, Object cell,
					Object connectingEdge, int layer, int seen) {

				// there is a connectingEdge from parent to cell. If
				// we've seen cell before, this indicates a cycle.

				// Check if the cell is in it's own ancestor list, if so
				// invert the connecting edge and reverse the target/source
				// relationship to that edge in the parent and the cell
				if (((JGraphHierarchyNode) cell)
						.isAncestor((JGraphHierarchyNode) parent)) {
					((JGraphHierarchyEdge) connectingEdge).invert();
					((JGraphHierarchyNode) parent).connectsAsSource
							.remove(connectingEdge);
					((JGraphHierarchyNode) parent).connectsAsTarget
							.add(connectingEdge);
					((JGraphHierarchyNode) cell).connectsAsTarget
							.remove(connectingEdge);
					((JGraphHierarchyNode) cell).connectsAsSource
							.add(connectingEdge);
				}
				seenNodes.add(cell);
				unseenNodes.remove(cell);
			}
		}, rootsArray, true, null);

		Set possibleNewRoots = null;
		if (unseenNodes.size() > 0) {
			possibleNewRoots = new HashSet(unseenNodes);
		}
		// If there are any nodes that should be nodes that the dfs can miss
		// these need to be processed with the dfs and the roots assigned
		// correctly to form a correct internal model
		Set seenNodesCopy = new HashSet(seenNodes);

		// Pick a random cell and dfs from it
		model.dfs(new JGraphFacade.CellVisitor() {
			public void visit(Object parent, Object cell,
					Object connectingEdge, int layer, int seen) {
				// Check if the cell is in it's own ancestor list, if so
				// invert the connecting edge and reverse the target/source
				// relationship to that edge in the parent and the cell
				if (((JGraphHierarchyNode) cell)
						.isAncestor((JGraphHierarchyNode) parent)) {
					((JGraphHierarchyEdge) connectingEdge).invert();
					((JGraphHierarchyNode) parent).connectsAsSource
							.remove(connectingEdge);
					((JGraphHierarchyNode) parent).connectsAsTarget
							.add(connectingEdge);
					((JGraphHierarchyNode) cell).connectsAsTarget
							.remove(connectingEdge);
					((JGraphHierarchyNode) cell).connectsAsSource
							.add(connectingEdge);
				}
				seenNodes.add(cell);
				unseenNodes.remove(cell);
			}
		}, unseenNodes.toArray(), true, seenNodesCopy);

		if (possibleNewRoots != null && possibleNewRoots.size() > 0) {
			Iterator iter = possibleNewRoots.iterator();
			List roots = facade.getRoots();
			while (iter.hasNext()) {
				JGraphHierarchyNode node = (JGraphHierarchyNode) iter.next();
				Object realNode = node.cell;
				int numIncomingEdges = facade.getIncomingEdges(realNode, null,
						true, false).size();
				if (numIncomingEdges == 0) {
					roots.add(realNode);
				}
			}
		}
		return model;
	}
}
